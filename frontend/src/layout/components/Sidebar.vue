<template>
  <div class="sidebar">
    <!-- 顶部 Logo -->
    <div class="sidebar__logo">
      <el-icon :size="24" color="#4caf50"><Sunny /></el-icon>
      <span v-show="!collapsed" class="sidebar__logo-text">智慧园林修剪平台</span>
    </div>

    <el-scrollbar class="sidebar__scroll">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        background-color="#1f2d3d"
        text-color="#c0c4cc"
        active-text-color="#ffffff"
        router
        unique-opened
      >
        <template v-for="menu in menus" :key="menu.path">
          <el-sub-menu :index="menu.path">
            <template #title>
              <el-icon><component :is="menu.meta.icon" /></el-icon>
              <span>{{ menu.meta.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.path"
              :index="resolvePath(menu.path, child.path)"
            >
              <el-icon><component :is="child.meta.icon" /></el-icon>
              <template #title>{{ child.meta.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { Sunny } from '@element-plus/icons-vue'
import { asyncRoutes } from '@/router'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'

const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()
const { sidebarCollapsed: collapsed } = storeToRefs(appStore)

// 按当前角色过滤菜单：父级与子级均需命中 meta.roles，且排除 hidden 项
const menus = computed(() => {
  const role = userStore.role
  return asyncRoutes
    .filter((r) => !r.meta?.hidden && (!r.meta?.roles || r.meta.roles.includes(role)))
    .map((r) => ({
      ...r,
      children: (r.children || []).filter(
        (c) => !c.meta?.hidden && (!c.meta?.roles || c.meta.roles.includes(role))
      )
    }))
    .filter((r) => r.children.length > 0)
})

// 拼接子路由完整路径
function resolvePath(parent, child) {
  if (child.startsWith('/')) return child
  return `${parent}/${child}`.replace(/\/+/g, '/')
}

// 当前激活菜单（详情页通过 meta.activeMenu 指定）
const activeMenu = computed(() => route.meta?.activeMenu || route.path)
</script>

<style scoped>
.sidebar {
  height: 100%;
  background: #1f2d3d;
  display: flex;
  flex-direction: column;
}
.sidebar__logo {
  height: var(--sg-header-height);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  color: #fff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
}
.sidebar__logo-text {
  font-size: 14px;
}
.sidebar__scroll {
  flex: 1;
}
.sidebar :deep(.el-menu) {
  border-right: none;
}
.sidebar :deep(.el-menu-item.is-active) {
  background-color: var(--sg-primary) !important;
}
</style>
