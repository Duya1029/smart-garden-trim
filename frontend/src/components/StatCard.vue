<template>
  <!-- 指标卡片 -->
  <div class="stat-card">
    <div class="stat-card__icon" :style="{ background: color }">
      <el-icon :size="22"><component :is="icon" /></el-icon>
    </div>
    <div class="stat-card__body">
      <div class="stat-card__value">
        {{ value }}<span v-if="suffix" class="stat-card__suffix">{{ suffix }}</span>
      </div>
      <div class="stat-card__title">{{ title }}</div>
    </div>
  </div>
</template>

<script setup>
// 看板指标卡片：图标 + 数值 + 标题
defineProps({
  title: { type: String, default: '' },
  value: { type: [String, Number], default: '-' },
  suffix: { type: String, default: '' },
  // Element Plus 图标组件名，如 'DataLine'
  icon: { type: String, default: 'DataLine' },
  // 图标底色
  color: { type: String, default: '#2e7d32' }
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 6px;
  padding: 18px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.stat-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-card__value {
  font-size: 24px;
  font-weight: 700;
  color: var(--sg-text-primary);
  line-height: 1.2;
}
.stat-card__suffix {
  font-size: 13px;
  font-weight: 400;
  color: var(--sg-text-secondary);
  margin-left: 4px;
}
.stat-card__title {
  font-size: 13px;
  color: var(--sg-text-secondary);
  margin-top: 4px;
}
</style>
