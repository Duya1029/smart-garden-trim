package com.garden.smarttrim.common;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页响应体
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageResult<T> implements Serializable {

    /** 总记录数 */
    private Long total;
    /** 当前页数据 */
    private List<T> list;

    public PageResult() {
        this.total = 0L;
        this.list = new ArrayList<>();
    }

    public PageResult(Long total, List<T> list) {
        this.total = total == null ? 0L : total;
        this.list = list == null ? new ArrayList<>() : list;
    }

    /** 构造分页结果 */
    public static <T> PageResult<T> of(Long total, List<T> list) {
        return new PageResult<>(total, list);
    }

    /** 空分页结果 */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, new ArrayList<>());
    }
}
