package com.garden.smarttrim.service;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.dto.TreeDTO;
import com.garden.smarttrim.dto.TreeQueryDTO;
import com.garden.smarttrim.vo.TreeArchiveVO;
import com.garden.smarttrim.vo.TreeVO;

import java.util.List;

/**
 * 树木档案服务
 */
public interface TreeService {

    /** 树木分页查询（管理员 / 养护人员） */
    PageResult<TreeVO> page(TreeQueryDTO query);

    /** 树木详情 */
    TreeVO detail(Long id);

    /** 名下树木（客户） */
    List<TreeVO> myTrees(String keyword);

    /** 生长档案 */
    TreeArchiveVO archive(Long id);

    /** 新增树木（管理员） */
    void create(TreeDTO dto);

    /** 修改树木（管理员） */
    void update(Long id, TreeDTO dto);

    /** 删除树木（管理员） */
    void delete(Long id);
}
