package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.TrimTaskQueryDTO;
import com.garden.smarttrim.dto.TrimTaskSubmitDTO;
import com.garden.smarttrim.entity.SysUser;
import com.garden.smarttrim.entity.TrimRecord;
import com.garden.smarttrim.entity.TrimRecordImage;
import com.garden.smarttrim.entity.TrimTask;
import com.garden.smarttrim.mapper.SysUserMapper;
import com.garden.smarttrim.mapper.TrimRecordImageMapper;
import com.garden.smarttrim.mapper.TrimRecordMapper;
import com.garden.smarttrim.mapper.TrimTaskMapper;
import com.garden.smarttrim.service.TrimTaskService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.TrimRecordVO;
import com.garden.smarttrim.vo.TrimTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 修剪任务服务实现
 */
@Service
@RequiredArgsConstructor
public class TrimTaskServiceImpl implements TrimTaskService {

    private final TrimTaskMapper taskMapper;
    private final TrimRecordMapper recordMapper;
    private final TrimRecordImageMapper imageMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResult<TrimTaskVO> page(TrimTaskQueryDTO query) {
        checkAdmin();
        long total = taskMapper.countTaskPage(query);
        List<TrimTaskVO> list = taskMapper.selectTaskPage(query);
        return PageResult.of(total, list);
    }

    @Override
    public List<TrimTaskVO> myTasks(String status) {
        if (!SecurityUtil.isWorker()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return taskMapper.selectMyTasks(SecurityUtil.getUserId(), status);
    }

    @Override
    public TrimTaskVO detail(Long id) {
        SecurityUtil.getRequired();
        TrimTaskVO vo = taskMapper.selectTaskVoById(id);
        if (vo == null) {
            throw new BusinessException("任务不存在");
        }
        TrimRecordVO record = recordMapper.selectRecordVoByTaskId(id);
        if (record != null) {
            record.setImages(imageMapper.selectByRecordId(record.getId()));
        }
        vo.setRecord(record);
        return vo;
    }

    @Override
    public void accept(Long id) {
        TrimTask task = loadOwnTask(id);
        if (!"PENDING".equals(task.getStatus())) {
            throw new BusinessException("当前状态不可接单");
        }
        taskMapper.updateStatus(id, "ACCEPTED");
    }

    @Override
    public void start(Long id) {
        TrimTask task = loadOwnTask(id);
        if (!"ACCEPTED".equals(task.getStatus())) {
            throw new BusinessException("当前状态不可开始修剪");
        }
        taskMapper.updateStatus(id, "PROCESSING");
    }

    @Override
    @Transactional
    public Map<String, Object> submit(Long id, TrimTaskSubmitDTO dto) {
        TrimTask task = loadOwnTask(id);
        if (!"PROCESSING".equals(task.getStatus())) {
            throw new BusinessException("当前状态不可提交记录");
        }
        // 新增修剪记录（task_id / tree_id / worker_id / client_id 取自任务）
        TrimRecord record = new TrimRecord();
        record.setTaskId(task.getId());
        record.setTreeId(task.getTreeId());
        record.setWorkerId(task.getWorkerId());
        record.setClientId(task.getClientId());
        record.setTrimType(dto.getTrimType());
        record.setActualTrimTime(dto.getActualTrimTime());
        record.setCutBranchCount(dto.getCutBranchCount() == null ? 0 : dto.getCutBranchCount());
        record.setKeepBranchCount(dto.getKeepBranchCount() == null ? 0 : dto.getKeepBranchCount());
        record.setWorkContent(dto.getWorkContent());
        record.setAiSuggestion(dto.getAiSuggestion());
        record.setStatus("SUBMITTED");
        recordMapper.insert(record);

        // 批量插入图片
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<TrimRecordImage> images = new ArrayList<>();
            for (TrimTaskSubmitDTO.ImageItem item : dto.getImages()) {
                TrimRecordImage image = new TrimRecordImage();
                image.setRecordId(record.getId());
                image.setTaskId(task.getId());
                image.setImageType(item.getImageType() == null ? "SCENE" : item.getImageType());
                image.setImageUrl(item.getUrl());
                images.add(image);
            }
            imageMapper.batchInsert(images);
        }

        // 任务状态置为 SUBMITTED 并写完成时间
        taskMapper.updateAfterSubmit(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", record.getId());
        return result;
    }

    @Override
    public void assign(Long id, Long workerId) {
        checkAdmin();
        if (workerId == null) {
            throw new BusinessException("请选择养护人员");
        }
        SysUser worker = userMapper.selectById(workerId);
        if (worker == null || !"WORKER".equals(worker.getRole())) {
            throw new BusinessException("养护人员不存在");
        }
        if (taskMapper.selectById(id) == null) {
            throw new BusinessException("任务不存在");
        }
        taskMapper.updateWorker(id, workerId);
    }

    @Override
    public void finish(Long id) {
        checkAdmin();
        TrimTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!"SUBMITTED".equals(task.getStatus())) {
            throw new BusinessException("当前状态不可结单");
        }
        taskMapper.finishTask(id);
    }

    /** 校验任务存在且属于当前养护人员 */
    private TrimTask loadOwnTask(Long id) {
        if (!SecurityUtil.isWorker()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        TrimTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!SecurityUtil.getUserId().equals(task.getWorkerId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return task;
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
