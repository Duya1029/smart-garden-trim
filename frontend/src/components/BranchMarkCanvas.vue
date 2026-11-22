<template>
  <div class="branch-canvas">
    <!-- 工具栏：原图 / AI标注图 切换 + 图例 -->
    <div class="branch-canvas__bar">
      <el-radio-group v-model="mode" size="small">
        <el-radio-button label="origin">原图</el-radio-button>
        <el-radio-button label="marked" :disabled="!markedImageUrl">AI标注图</el-radio-button>
      </el-radio-group>
      <div class="legend">
        <span class="legend__item">
          <i class="legend__line legend__line--cut"></i>建议剪除
        </span>
        <span class="legend__item">
          <i class="legend__line legend__line--keep"></i>建议保留
        </span>
      </div>
    </div>

    <!-- 图片 + SVG 归一化坐标覆盖层 -->
    <div v-if="currentImage" class="branch-canvas__stage">
      <img :src="currentImage" alt="树木照片" />
      <!-- 仅在展示原图时叠加矢量标注层 -->
      <svg
        v-if="showOverlay"
        class="branch-canvas__svg"
        viewBox="0 0 1 1"
        preserveAspectRatio="none"
      >
        <!-- 建议剪除：红线 -->
        <line
          v-for="(b, i) in cutBranches"
          :key="'cut-' + i"
          :x1="num(b.x1)"
          :y1="num(b.y1)"
          :x2="num(b.x2)"
          :y2="num(b.y2)"
          stroke="#f5222d"
          stroke-width="3"
          vector-effect="non-scaling-stroke"
        />
        <!-- 建议保留：绿线 -->
        <line
          v-for="(b, i) in keepBranches"
          :key="'keep-' + i"
          :x1="num(b.x1)"
          :y1="num(b.y1)"
          :x2="num(b.x2)"
          :y2="num(b.y2)"
          stroke="#52c41a"
          stroke-width="3"
          vector-effect="non-scaling-stroke"
        />
      </svg>
      <!-- 线段标签：用绝对定位的 HTML 渲染，避免 SVG 归一化 viewBox 拉伸文字 -->
      <template v-if="showOverlay">
        <span
          v-for="(item, i) in cutLabels"
          :key="'cut-label-' + i"
          class="branch-label branch-label--cut"
          :style="item.style"
          >{{ item.label }}</span
        >
        <span
          v-for="(item, i) in keepLabels"
          :key="'keep-label-' + i"
          class="branch-label branch-label--keep"
          :style="item.style"
          >{{ item.label }}</span
        >
      </template>
    </div>
    <el-empty v-else description="暂无图片" :image-size="80" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { joinFileUrl } from '@/utils'

// 组件props：图片地址 + 剪除/保留枝条线段（均为归一化坐标 0~1）
const props = defineProps({
  // 原图地址（相对路径，自动拼接 VITE_FILE_BASE）
  imageUrl: { type: String, default: '' },
  // AI 标注后合成图地址（可空）
  markedImageUrl: { type: String, default: '' },
  // 建议剪除枝条 [{x1,y1,x2,y2,label}]
  cutBranches: { type: Array, default: () => [] },
  // 建议保留枝条 [{x1,y1,x2,y2,label}]
  keepBranches: { type: Array, default: () => [] }
})

const mode = ref('origin')

// 有标注图时默认展示标注图，否则展示原图
watch(
  () => props.markedImageUrl,
  (val) => {
    mode.value = val ? 'marked' : 'origin'
  },
  { immediate: true }
)

const currentImage = computed(() => {
  const url = mode.value === 'marked' ? props.markedImageUrl || props.imageUrl : props.imageUrl
  return joinFileUrl(url)
})

// 只有展示原图时才绘制矢量覆盖层（标注图自身已包含线段）
const showOverlay = computed(() => mode.value === 'origin')

// 坐标合法性保护
function num(v) {
  const n = Number(v)
  return Number.isFinite(n) ? n : 0
}

// 依据线段中点计算标签位置（百分比）
function buildLabels(list, cls) {
  return (list || []).map((b) => {
    const x = (num(b.x1) + num(b.x2)) / 2
    const y = (num(b.y1) + num(b.y2)) / 2
    return {
      label: b.label || '',
      style: {
        left: `${x * 100}%`,
        top: `${y * 100}%`
      }
    }
  })
}

const cutLabels = computed(() => buildLabels(props.cutBranches))
const keepLabels = computed(() => buildLabels(props.keepBranches))
</script>

<style scoped>
.branch-canvas {
  width: 100%;
}
.branch-canvas__bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  flex-wrap: wrap;
  gap: 8px;
}
.legend {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #606266;
}
.legend__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.legend__line {
  display: inline-block;
  width: 20px;
  height: 3px;
  border-radius: 2px;
}
.legend__line--cut {
  background: #f5222d;
}
.legend__line--keep {
  background: #52c41a;
}
.branch-canvas__stage {
  position: relative;
  width: 100%;
  line-height: 0;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f7fa;
}
.branch-canvas__stage img {
  display: block;
  width: 100%;
  height: auto;
}
/* SVG 覆盖层铺满图片区域，preserveAspectRatio="none" 使归一化坐标直接对应图片宽高 */
.branch-canvas__svg {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
/* 标签以图片百分比定位 */
.branch-label {
  position: absolute;
  transform: translate(-50%, -50%);
  padding: 1px 6px;
  font-size: 12px;
  line-height: 18px;
  border-radius: 10px;
  color: #fff;
  white-space: nowrap;
  pointer-events: none;
}
.branch-label--cut {
  background: rgba(245, 34, 45, 0.9);
}
.branch-label--keep {
  background: rgba(82, 196, 26, 0.9);
}
</style>
