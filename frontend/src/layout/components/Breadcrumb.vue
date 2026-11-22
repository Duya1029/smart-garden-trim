<template>
  <div class="breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item v-for="(item, index) in items" :key="index">
        {{ item }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 依据 matched 路由记录生成面包屑（父级 + 当前页）
const items = computed(() => {
  const list = (route.matched || [])
    .filter((r) => r.meta?.title)
    .map((r) => r.meta.title)
  return list.length ? list : ['首页']
})
</script>

<style scoped>
.breadcrumb {
  display: inline-block;
}
</style>
