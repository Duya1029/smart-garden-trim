package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.mapper.SysClientMapper;
import com.garden.smarttrim.mapper.SysUserMapper;
import com.garden.smarttrim.mapper.TreeMapper;
import com.garden.smarttrim.mapper.TrimRecordMapper;
import com.garden.smarttrim.mapper.TrimTaskMapper;
import com.garden.smarttrim.service.StatService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.AreaStatVO;
import com.garden.smarttrim.vo.SpeciesStatVO;
import com.garden.smarttrim.vo.StatOverviewVO;
import com.garden.smarttrim.vo.TrendStatVO;
import com.garden.smarttrim.vo.WorkerStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计看板服务实现（管理员）
 */
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final TreeMapper treeMapper;
    private final TrimTaskMapper taskMapper;
    private final TrimRecordMapper recordMapper;
    private final SysClientMapper clientMapper;
    private final SysUserMapper userMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public StatOverviewVO overview(String startDate, String endDate) {
        checkAdmin();
        long treeTotal = treeMapper.countTreeTotal();
        long trimmedCount = recordMapper.countTrimmedTree(startDate, endDate);
        long taskTotal = taskMapper.countTaskTotal(startDate, endDate);
        long taskDone = taskMapper.countTaskByStatuses(List.of("DONE"), startDate, endDate);
        long taskPending = taskMapper.countTaskByStatuses(List.of("PENDING", "ACCEPTED"), startDate, endDate);
        long taskProcessing = taskMapper.countTaskByStatuses(List.of("PROCESSING", "SUBMITTED"), startDate, endDate);
        Double avg = recordMapper.avgScore(startDate, endDate);

        StatOverviewVO vo = new StatOverviewVO();
        vo.setTreeTotal(treeTotal);
        vo.setTrimmedCount(trimmedCount);
        vo.setTaskTotal(taskTotal);
        vo.setTaskDone(taskDone);
        vo.setTaskPending(taskPending);
        vo.setTaskProcessing(taskProcessing);
        vo.setCompletionRate(taskTotal == 0 ? 0.0 : round2(taskDone * 100.0 / taskTotal));
        vo.setAvgScore(avg == null ? 0.0 : round2(avg));
        vo.setClientTotal(clientMapper.countAll());
        vo.setWorkerTotal(userMapper.countByRole("WORKER"));
        return vo;
    }

    @Override
    public List<AreaStatVO> area() {
        checkAdmin();
        return treeMapper.selectAreaStat();
    }

    @Override
    public List<TrendStatVO> trend(Integer days) {
        checkAdmin();
        int d = days == null || days < 1 ? 30 : days;
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(d - 1L);
        String startStr = start.format(DATE_FORMATTER);
        String endStr = end.format(DATE_FORMATTER);

        List<TrendStatVO> raw = recordMapper.selectDailyTrend(startStr, endStr);
        Map<String, Long> countMap = raw.stream()
                .collect(Collectors.toMap(TrendStatVO::getDate, t -> t.getCount() == null ? 0L : t.getCount(),
                        (a, b) -> a));

        // 缺失日期补 0
        List<TrendStatVO> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String key = date.format(DATE_FORMATTER);
            TrendStatVO vo = new TrendStatVO();
            vo.setDate(key);
            vo.setCount(countMap.getOrDefault(key, 0L));
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<SpeciesStatVO> species() {
        checkAdmin();
        return treeMapper.selectSpeciesStat();
    }

    @Override
    public List<WorkerStatVO> worker(String startDate, String endDate) {
        checkAdmin();
        List<WorkerStatVO> list = recordMapper.selectWorkerStat(startDate, endDate);
        for (WorkerStatVO vo : list) {
            vo.setAvgScore(vo.getAvgScore() == null ? 0.0 : round2(vo.getAvgScore()));
        }
        return list;
    }

    /** 保留两位小数 */
    private double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
