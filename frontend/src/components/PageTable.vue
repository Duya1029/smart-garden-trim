<template>
  <div class="page-table">
    <el-table v-loading="loading" :data="data" border stripe :height="height">
      <el-table-column
        v-for="col in columns"
        :key="col.prop || col.label"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
        :fixed="col.fixed"
        :align="col.align || 'left'"
        :sortable="col.sortable"
        show-overflow-tooltip
      >
        <template v-if="col.slot" #default="scope">
          <slot :name="col.slot" v-bind="scope" />
        </template>
      </el-table-column>
      <template #empty>
        <el-empty :description="emptyText" />
      </template>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
// 分页表格封装：columns 配置 + el-pagination，绑定 pageNum / pageSize / total
const props = defineProps({
  // 列配置 [{ prop, label, width, minWidth, align, slot, fixed, sortable }]
  columns: { type: Array, default: () => [] },
  // 表格数据
  data: { type: Array, default: () => [] },
  // 加载态
  loading: { type: Boolean, default: false },
  // 总数
  total: { type: Number, default: 0 },
  // 当前页
  pageNum: { type: Number, default: 1 },
  // 每页条数
  pageSize: { type: Number, default: 10 },
  // 可选每页条数
  pageSizes: { type: Array, default: () => [10, 20, 50] },
  // 空数据文案
  emptyText: { type: String, default: '暂无数据' },
  // 表格高度（可选）
  height: { type: [String, Number], default: undefined }
})
const emit = defineEmits(['update:pageNum', 'update:pageSize', 'change'])

function onPageChange(page) {
  emit('update:pageNum', page)
  emit('change')
}

function onSizeChange(size) {
  emit('update:pageSize', size)
  emit('update:pageNum', 1)
  emit('change')
}
</script>
