<template>
  <el-container class="app-wrapper">
    <!-- 侧边菜单 -->
    <el-aside
      class="app-wrapper__aside"
      :width="collapsed ? 'var(--sg-sidebar-collapsed-width)' : 'var(--sg-sidebar-width)'"
    >
      <Sidebar />
    </el-aside>

    <el-container>
      <!-- 顶栏 -->
      <el-header class="app-wrapper__header" height="var(--sg-header-height)">
        <Navbar />
      </el-header>
      <!-- 主内容区 -->
      <el-main class="app-wrapper__main">
        <router-view v-slot="{ Component }">
          <keep-alive :max="10">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { storeToRefs } from 'pinia'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import { useAppStore } from '@/store/app'

const appStore = useAppStore()
const { sidebarCollapsed: collapsed } = storeToRefs(appStore)
</script>

<style scoped>
.app-wrapper {
  height: 100%;
}
.app-wrapper__aside {
  transition: width 0.2s;
  overflow: hidden;
}
.app-wrapper__header {
  padding: 0;
}
.app-wrapper__main {
  background: var(--sg-page-bg);
  padding: 0;
  overflow-y: auto;
}
</style>
