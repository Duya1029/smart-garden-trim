import { defineStore } from 'pinia'

// 应用级 UI 状态：侧边栏折叠、页面标题
export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false,
    pageTitle: '智慧园林·树木智能修剪决策平台'
  }),
  actions: {
    // 切换侧边栏折叠状态
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    // 设置页面标题
    setPageTitle(title) {
      this.pageTitle = title || ''
      document.title = title ? `${title} - 智慧园林·树木智能修剪决策平台` : '智慧园林·树木智能修剪决策平台'
    }
  }
})
