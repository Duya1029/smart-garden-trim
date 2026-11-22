package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.TreeDTO;
import com.garden.smarttrim.dto.TreeQueryDTO;
import com.garden.smarttrim.entity.Tree;
import com.garden.smarttrim.mapper.TreeMapper;
import com.garden.smarttrim.mapper.TrimRecordImageMapper;
import com.garden.smarttrim.mapper.TrimRecordMapper;
import com.garden.smarttrim.service.TreeService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.TreeArchiveRecordVO;
import com.garden.smarttrim.vo.TreeArchiveVO;
import com.garden.smarttrim.vo.TreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 树木档案服务实现
 */
@Service
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {

    private final TreeMapper treeMapper;
    private final TrimRecordMapper recordMapper;
    private final TrimRecordImageMapper imageMapper;

    @Override
    public PageResult<TreeVO> page(TreeQueryDTO query) {
        if (!SecurityUtil.isAdmin() && !SecurityUtil.isWorker()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        long total = treeMapper.countTreePage(query);
        List<TreeVO> list = treeMapper.selectTreePage(query);
        return PageResult.of(total, list);
    }

    @Override
    public TreeVO detail(Long id) {
        SecurityUtil.getRequired();
        TreeVO vo = treeMapper.selectTreeVoById(id);
        if (vo == null) {
            throw new BusinessException("树木不存在");
        }
        return vo;
    }

    @Override
    public List<TreeVO> myTrees(String keyword) {
        if (!SecurityUtil.isClient()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Long clientId = SecurityUtil.currentClientId();
        if (clientId == null) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return treeMapper.selectMyTrees(clientId, keyword);
    }

    @Override
    public TreeArchiveVO archive(Long id) {
        SecurityUtil.getRequired();
        TreeVO tree = treeMapper.selectTreeVoById(id);
        if (tree == null) {
            throw new BusinessException("树木不存在");
        }
        TreeArchiveVO archive = new TreeArchiveVO();
        // 基础信息
        TreeArchiveVO.TreeInfo info = new TreeArchiveVO.TreeInfo();
        info.setId(tree.getId());
        info.setTreeCode(tree.getTreeCode());
        info.setSpecies(tree.getSpecies());
        info.setTreeAge(tree.getTreeAge());
        archive.setTree(info);

        // 修剪记录时间线（逐条补充图片地址）
        List<TreeArchiveRecordVO> records = recordMapper.selectArchiveRecords(id);
        for (TreeArchiveRecordVO record : records) {
            record.setImages(imageMapper.selectImageUrlsByRecordId(record.getRecordId()));
        }
        archive.setRecords(records);

        // 评分趋势
        archive.setScoreTrend(recordMapper.selectTreeScoreTrend(id));
        return archive;
    }

    @Override
    public void create(TreeDTO dto) {
        checkAdmin();
        if (treeMapper.countByTreeCode(dto.getTreeCode(), null) > 0) {
            throw new BusinessException("树木编号已存在");
        }
        Tree tree = new Tree();
        copyProps(tree, dto);
        tree.setHealthStatus(dto.getHealthStatus() == null ? "HEALTHY" : dto.getHealthStatus());
        treeMapper.insert(tree);
    }

    @Override
    public void update(Long id, TreeDTO dto) {
        checkAdmin();
        if (treeMapper.selectById(id) == null) {
            throw new BusinessException("树木不存在");
        }
        if (treeMapper.countByTreeCode(dto.getTreeCode(), id) > 0) {
            throw new BusinessException("树木编号已存在");
        }
        Tree tree = new Tree();
        tree.setId(id);
        copyProps(tree, dto);
        tree.setHealthStatus(dto.getHealthStatus());
        treeMapper.update(tree);
    }

    @Override
    public void delete(Long id) {
        checkAdmin();
        if (treeMapper.selectById(id) == null) {
            throw new BusinessException("树木不存在");
        }
        treeMapper.deleteById(id);
    }

    /** 复制 DTO 字段到实体 */
    private void copyProps(Tree tree, TreeDTO dto) {
        tree.setTreeCode(dto.getTreeCode());
        tree.setSpecies(dto.getSpecies());
        tree.setVariety(dto.getVariety());
        tree.setArea(dto.getArea());
        tree.setLocation(dto.getLocation());
        tree.setLongitude(dto.getLongitude());
        tree.setLatitude(dto.getLatitude());
        tree.setTreeAge(dto.getTreeAge());
        tree.setPlantTime(dto.getPlantTime());
        tree.setHeight(dto.getHeight());
        tree.setCrownDiameter(dto.getCrownDiameter());
        tree.setClientId(dto.getClientId());
        tree.setDeptId(dto.getDeptId());
        tree.setImageUrl(dto.getImageUrl());
        tree.setRemark(dto.getRemark());
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
