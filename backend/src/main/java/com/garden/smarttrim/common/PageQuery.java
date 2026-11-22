package com.garden.smarttrim.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基类
 *
 * <p>所有分页查询 DTO 继承本类，可直接在 MyBatis XML 中使用
 * #{offset} 与 #{pageSize} 拼接 SQL Server 的
 * {@code OFFSET ... ROWS FETCH NEXT ... ROWS ONLY} 分页语句。</p>
 */
@Data
public class PageQuery implements Serializable {

    /** 页码，从 1 开始 */
    private Integer pageNum = 1;
    /** 每页条数 */
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return pageSize > 500 ? 500 : pageSize;
    }

    /** SQL Server OFFSET 需要跳过的行数 */
    public Integer getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }
}
