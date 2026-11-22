package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.TrimAuditDTO;
import com.garden.smarttrim.dto.TrimRequestDTO;
import com.garden.smarttrim.entity.SysUser;
import com.garden.smarttrim.entity.Tree;
import com.garden.smarttrim.entity.TrimRequest;
import com.garden.smarttrim.entity.TrimTask;
import com.garden.smarttrim.mapper.SysUserMapper;
import com.garden.smarttrim.mapper.TreeMapper;
import com.garden.smarttrim.mapper.TrimRequestMapper;
import com.garden.smarttrim.mapper.TrimTaskMapper;
import com.garden.smarttrim.service.TrimRequestService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.TrimRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 修剪需求服务实现
 */
@Service
@RequiredArgsConstructor
public class TrimRequestServiceImpl implements TrimRequestService {

    private final TrimRequestMapper requestMapper;
    private final TrimTaskMapper taskMapper;
    private final TreeMapper treeMapper;
    private final SysUserMapper userMapper;

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional
    public Map<String, Object> create(TrimRequestDTO dto) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Long clientId = SecurityUtil.currentClientId();
        Tree tree = treeMapper.selectById(dto.getTreeId());
        if (tree == null) {
            throw new BusinessException("树木不存在");
        }
        if (!tree.getClientId().equals(clientId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        TrimRequest request = new TrimRequest();
        request.setRequestNo(generateRequestNo());
        request.setClientId(clientId);
        request.setTreeId(dto.getTreeId());
        request.setTrimType(dto.getTrimType());
        request.setAppointTime(dto.getAppointTime());
        // 地点缺省取树木位置
        request.setLocation(dto.getLocation() == null || dto.getLocation().isBlank()
                ? tree.getLocation() : dto.getLocation());
        request.setRemark(dto.getRemark());
        request.setStatus("PENDING");
        requestMapper.insert(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("requestId", request.getId());
        result.put("requestNo", request.getRequestNo());
        return result;
    }

    @Override
    public PageResult<TrimRequestVO> page(String status, Long clientId, String keyword,
                                          Integer pageNum, Integer pageSize) {
        checkAdmin();
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (num - 1) * size;
        long total = requestMapper.countRequestPage(status, clientId, keyword);
        List<TrimRequestVO> list = requestMapper.selectRequestPage(status, clientId, keyword, offset, size);
        return PageResult.of(total, list);
    }

    @Override
    public List<TrimRequestVO> myRequests(String status) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Long clientId = SecurityUtil.currentClientId();
        if (clientId == null) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return requestMapper.selectMyRequests(clientId, status);
    }

    @Override
    @Transactional
    public Map<String, Object> audit(Long id, TrimAuditDTO dto) {
        checkAdmin();
        TrimRequest request = requestMapper.selectById(id);
        if (request == null) {
            throw new BusinessException("需求不存在");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new BusinessException("当前需求状态不可审核");
        }
        String status = dto.getStatus();
        if ("REJECTED".equals(status)) {
            // 驳回：仅置状态并写审核信息
            request.setStatus("REJECTED");
            request.setAuditorId(SecurityUtil.getUserId());
            request.setAuditRemark(dto.getAuditRemark());
            request.setAuditTime(LocalDateTime.now());
            requestMapper.updateAudit(request);
            return null;
        }
        if (!"AUDITED".equals(status)) {
            throw new BusinessException("审核结果不合法");
        }
        // 通过并派单：必须指定养护人员
        if (dto.getWorkerId() == null) {
            throw new BusinessException("请选择养护人员");
        }
        SysUser worker = userMapper.selectById(dto.getWorkerId());
        if (worker == null || !"WORKER".equals(worker.getRole())) {
            throw new BusinessException("养护人员不存在");
        }
        // 自动创建修剪任务
        TrimTask task = new TrimTask();
        task.setTaskNo(generateTaskNo());
        task.setRequestId(request.getId());
        task.setTreeId(request.getTreeId());
        task.setWorkerId(worker.getId());
        task.setClientId(request.getClientId());
        task.setTrimType(request.getTrimType());
        task.setPlanTime(dto.getPlanTime() != null ? dto.getPlanTime() : request.getAppointTime());
        task.setStatus("PENDING");
        task.setPriority(1);
        task.setDescription(request.getRemark());
        taskMapper.insert(task);

        // 需求状态置为 DISPATCHED
        request.setStatus("DISPATCHED");
        request.setAuditorId(SecurityUtil.getUserId());
        request.setAuditRemark(dto.getAuditRemark());
        request.setAuditTime(LocalDateTime.now());
        requestMapper.updateAudit(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("taskId", task.getId());
        result.put("taskNo", task.getTaskNo());
        return result;
    }

    @Override
    public void cancel(Long id) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        TrimRequest request = requestMapper.selectById(id);
        if (request == null) {
            throw new BusinessException("需求不存在");
        }
        if (!request.getClientId().equals(SecurityUtil.currentClientId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        if (!"PENDING".equals(request.getStatus()) && !"AUDITED".equals(request.getStatus())) {
            throw new BusinessException("当前状态不可取消");
        }
        requestMapper.updateStatus(id, "CANCELLED");
    }

    @Override
    public void delete(Long id) {
        checkAdmin();
        if (requestMapper.selectById(id) == null) {
            throw new BusinessException("需求不存在");
        }
        requestMapper.deleteById(id);
    }

    /** 生成需求单号：TR + yyyyMMdd + 4 位流水 */
    private String generateRequestNo() {
        String prefix = "TR" + LocalDateTime.now().format(DAY_FORMATTER);
        long seq = requestMapper.countByNoPrefix(prefix) + 1;
        return prefix + String.format("%04d", seq);
    }

    /** 生成任务单号：TK + yyyyMMdd + 4 位流水 */
    private String generateTaskNo() {
        String prefix = "TK" + LocalDateTime.now().format(DAY_FORMATTER);
        long seq = taskMapper.countByNoPrefix(prefix) + 1;
        return prefix + String.format("%04d", seq);
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
