<template>
  <!-- 通用 ECharts 容器：传入 option 即可渲染 -->
  <div ref="chartRef" class="base-chart" :style="{ height: height }"></div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  // ECharts 配置项
  option: { type: Object, default: () => ({}) },
  // 图表高度
  height: { type: String, default: '320px' }
})

const chartRef = ref(null)
let chart = null

function render() {
  if (!chart) return
  chart.setOption(props.option || {}, true)
}

function resize() {
  chart && chart.resize()
}

onMounted(() => {
  chart = echarts.init(chartRef.value)
  render()
  window.addEventListener('resize', resize)
})

// option 变化时重新渲染
watch(
  () => props.option,
  () => {
    nextTick(render)
  },
  { deep: true }
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.base-chart {
  width: 100%;
}
</style>
