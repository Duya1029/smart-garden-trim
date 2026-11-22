<template>
  <div class="error-page">
    <el-result icon="info" title="404" sub-title="抱歉，您访问的页面不存在">
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 返回当前角色的首页
function goHome() {
  router.replace(userStore.homePath || '/login')
}
</script>

<style scoped>
.error-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--sg-page-bg);
}
</style>
