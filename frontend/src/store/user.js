import { defineStore } from 'pinia'
import { login as loginApi, getInfo, logout as logoutApi } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

const USER_INFO_KEY = 'smart_garden_user'

// 从 localStorage 读取已持久化的用户信息
function readUserInfo() {
  try {
    const raw = localStorage.getItem(USER_INFO_KEY)
    return raw ? JSON.parse(raw) : null
  } catch (e) {
    return null
  }
}

// 写入/清除用户信息
function writeUserInfo(info) {
  if (info) {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
  } else {
    localStorage.removeItem(USER_INFO_KEY)
  }
}

// 各角色登录后的首页
export const ROLE_HOME = {
  ADMIN: '/admin/dashboard',
  WORKER: '/worker/task',
  CLIENT: '/client/tree'
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: readUserInfo()
  }),
  getters: {
    role: (state) => state.userInfo?.role || '',
    userId: (state) => state.userInfo?.userId ?? null,
    realName: (state) => state.userInfo?.realName || state.userInfo?.username || '',
    clientId: (state) => state.userInfo?.clientId ?? null,
    deptId: (state) => state.userInfo?.deptId ?? null,
    // 当前角色对应的首页
    homePath: (state) => ROLE_HOME[state.userInfo?.role] || '/login'
  },
  actions: {
    // 登录：保存 token 与用户信息
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      setToken(data.token)
      const info = { ...data }
      delete info.token
      this.userInfo = info
      writeUserInfo(info)
      return info
    },
    // 拉取当前登录用户信息
    async fetchInfo() {
      const info = await getInfo()
      this.userInfo = info
      writeUserInfo(info)
      return info
    },
    // 退出登录：后端无状态，本地清除即可
    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // 忽略退出接口异常
      }
      this.reset()
    },
    // 清空登录态
    reset() {
      this.token = ''
      this.userInfo = null
      removeToken()
      writeUserInfo(null)
    }
  }
})
