package com.garden.smarttrim.mapper;

import com.garden.smarttrim.entity.TrimRecordImage;
import com.garden.smarttrim.vo.TrimRecordImageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 修剪记录图片 Mapper（SQL 见 resources/mapper/TrimRecordImageMapper.xml）
 */
public interface TrimRecordImageMapper {

    /** 批量新增图片 */
    int batchInsert(@Param("list") List<TrimRecordImage> list);

    /** 查询记录下的图片列表 */
    List<TrimRecordImageVO> selectByRecordId(@Param("recordId") Long recordId);

    /** 查询记录下的图片地址列表（生长档案用） */
    List<String> selectImageUrlsByRecordId(@Param("recordId") Long recordId);
}
