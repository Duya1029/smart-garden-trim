package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.EvaluateDTO;
import com.garden.smarttrim.dto.TrimRecordQueryDTO;
import com.garden.smarttrim.entity.TrimRecord;
import com.garden.smarttrim.mapper.TrimRecordImageMapper;
import com.garden.smarttrim.mapper.TrimRecordMapper;
import com.garden.smarttrim.mapper.TrimTaskMapper;
import com.garden.smarttrim.service.TrimRecordService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.ScoreTrendVO;
import com.garden.smarttrim.vo.TrimRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 修剪记录服务实现
 */
@Service
@RequiredArgsConstructor
public class TrimRecordServiceImpl implements TrimRecordService {

    private final TrimRecordMapper recordMapper;
    private final TrimRecordImageMapper imageMapper;
    private final TrimTaskMapper taskMapper;

    @Override
    public PageResult<TrimRecordVO> page(TrimRecordQueryDTO query) {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        long total = recordMapper.countRecordPage(query);
        List<TrimRecordVO> list = recordMapper.selectRecordPage(query);
        fillImages(list);
        return PageResult.of(total, list);
    }

    @Override
    public TrimRecordVO detail(Long id) {
        SecurityUtil.getRequired();
        TrimRecordVO vo = recordMapper.selectRecordVoById(id);
        if (vo == null) {
            throw new BusinessException("记录不存在");
        }
        vo.setImages(imageMapper.selectByRecordId(id));
        return vo;
    }

    @Override
    public List<TrimRecordVO> myRecords(String startDate, String endDate) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Long clientId = SecurityUtil.currentClientId();
        if (clientId == null) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        List<TrimRecordVO> list = recordMapper.selectMyRecords(clientId, startDate, endDate);
        fillImages(list);
        return list;
    }

    @Override
    @Transactional
    public void evaluate(Long id, EvaluateDTO dto) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        TrimRecord record = recordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        if (record.getClientId() == null || !record.getClientId().equals(SecurityUtil.currentClientId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        TrimRecord update = new TrimRecord();
        update.setId(id);
        update.setScore(dto.getScore());
        update.setComment(dto.getComment());
        update.setEvaluateTime(LocalDateTime.now());
        update.setStatus("EVALUATED");
        recordMapper.evaluate(update);

        // 关联任务置为 DONE
        if (record.getTaskId() != null) {
            taskMapper.updateStatus(record.getTaskId(), "DONE");
        }
    }

    @Override
    public List<ScoreTrendVO> myTrend(Integer months) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Long clientId = SecurityUtil.currentClientId();
        if (clientId == null) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        int m = months == null || months < 1 ? 6 : months;
        return recordMapper.selectClientScoreTrend(clientId, m);
    }

    /** 逐条补充图片列表 */
    private void fillImages(List<TrimRecordVO> list) {
        for (TrimRecordVO vo : list) {
            vo.setImages(imageMapper.selectByRecordId(vo.getId()));
        }
    }
}
