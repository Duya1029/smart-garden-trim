package com.garden.smarttrim.mapper;

import com.garden.smarttrim.dto.TreeQueryDTO;
import com.garden.smarttrim.entity.Tree;
import com.garden.smarttrim.vo.AreaStatVO;
import com.garden.smarttrim.vo.SpeciesStatVO;
import com.garden.smarttrim.vo.TreeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 树木档案 Mapper（SQL 见 resources/mapper/TreeMapper.xml）
 */
public interface TreeMapper {

    /** 树木分页总数 */
    long countTreePage(TreeQueryDTO query);

    /** 树木分页列表（联查客户 / 养护队名称，统计修剪次数与最近修剪时间） */
    List<TreeVO> selectTreePage(TreeQueryDTO query);

    /** 根据ID查询树木 VO */
    TreeVO selectTreeVoById(@Param("id") Long id);

    /** 名下树木（按客户过滤） */
    List<TreeVO> selectMyTrees(@Param("clientId") Long clientId, @Param("keyword") String keyword);

    /** 根据ID查询树木实体 */
    Tree selectById(@Param("id") Long id);

    /** 新增树木 */
    int insert(Tree tree);

    /** 修改树木 */
    int update(Tree tree);

    /** 删除树木 */
    int deleteById(@Param("id") Long id);

    /** 树木编号唯一性校验（excludeId 用于修改时排除自身） */
    long countByTreeCode(@Param("treeCode") String treeCode, @Param("excludeId") Long excludeId);

    /** 树木总数（统计看板） */
    long countTreeTotal();

    /** 区域分布统计 */
    List<AreaStatVO> selectAreaStat();

    /** 树种分布统计 */
    List<SpeciesStatVO> selectSpeciesStat();
}
