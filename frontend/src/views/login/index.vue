<template>
  <div class="login">
    <div class="login__panel">
      <div class="login__brand">
        <el-icon :size="38" color="#fff"><Sunny /></el-icon>
        <h1>智慧园林</h1>
        <p>树木智能修剪决策平台</p>
        <ul class="login__features">
          <li>一树一码 · 档案数字化</li>
          <li>任务派发 · 流程闭环化</li>
          <li>AI 识别 · 修剪决策智能化</li>
          <li>客户评价 · 效果可视化</li>
        </ul>
      </div>

      <div class="login__form-wrap">
        <h2 class="login__title">欢迎登录</h2>
        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="submit">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login__btn" :loading="loading" @click="submit">
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            <div class="login__demo">
              <span>演示账号（密码均为 123456）：</span>
              <div class="login__demo-list">
                <el-tag v-for="acc in demoAccounts" :key="acc.username" size="small" @click="fillAccount(acc)">
                  {{ acc.label }}：{{ acc.username }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Sunny } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 演示账号提示
const demoAccounts = [
  { label: '管理员', username: 'admin' },
  { label: '养护人员', username: 'worker01' },
  { label: '客户', username: 'client01' }
]

// 点击演示账号快速填充
function fillAccount(acc) {
  form.username = acc.username
  form.password = '123456'
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const info = await userStore.login({ username: form.username, password: form.password })
    ElMessage.success(`欢迎回来，${info.realName || info.username}`)
    // 按角色跳转对应首页
    router.replace(userStore.homePath)
  } catch (e) {
    // 错误已由请求拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 45%, #66bb6a 100%);
}
.login__panel {
  display: flex;
  width: 860px;
  max-width: 94vw;
  min-height: 460px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.25);
}
.login__brand {
  width: 380px;
  padding: 44px 36px;
  color: #fff;
  background: linear-gradient(160deg, #2e7d32, #1b5e20);
  display: flex;
  flex-direction: column;
}
.login__brand h1 {
  margin: 12px 0 4px;
  font-size: 26px;
  letter-spacing: 2px;
}
.login__brand p {
  margin: 0 0 24px;
  font-size: 14px;
  opacity: 0.85;
}
.login__features {
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 14px;
  line-height: 2.2;
  opacity: 0.92;
}
.login__features li::before {
  content: '◆';
  color: #a5d6a7;
  margin-right: 8px;
  font-size: 10px;
}
.login__form-wrap {
  flex: 1;
  padding: 52px 44px;
}
.login__title {
  margin: 0 0 28px;
  font-size: 22px;
  color: var(--sg-text-primary);
}
.login__btn {
  width: 100%;
  letter-spacing: 4px;
}
.login__demo {
  line-height: 1.8;
}
.login__demo-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 6px;
}
.login__demo-list .el-tag {
  cursor: pointer;
}
/* 手机端：隐藏左侧品牌栏，只保留登录表单，避免横向溢出 */
@media (max-width: 640px) {
  .login__panel {
    width: 94vw;
    min-height: auto;
  }
  .login__brand {
    display: none;
  }
  .login__form-wrap {
    padding: 36px 24px;
  }
}
</style>
