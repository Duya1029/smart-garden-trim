<template>
  <div class="navbar">
    <!-- 左侧：折叠按钮 + 面包屑 -->
    <div class="navbar__left">
      <el-icon class="navbar__toggle" :size="20" @click="appStore.toggleSidebar()">
        <Fold v-if="!collapsed" />
        <Expand v-else />
      </el-icon>
      <Breadcrumb />
    </div>

    <!-- 右侧：用户信息 + 操作 -->
    <div class="navbar__right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="navbar__user">
          <el-avatar :size="30" :src="joinFileUrl(userStore.userInfo?.avatar)">
            {{ shortName }}
          </el-avatar>
          <span class="navbar__name">{{ userStore.realName }}</span>
          <el-tag size="small" type="success" effect="plain">{{ roleText(userStore.role) }}</el-tag>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="password">
              <el-icon><Key /></el-icon>修改密码
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdVisible" title="修改密码" width="420px" @closed="resetPwdForm">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6~20 位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="submitPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Fold, Expand, ArrowDown, Key, SwitchButton } from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'
import { updatePassword } from '@/api/auth'
import { joinFileUrl, roleText } from '@/utils'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()
const { sidebarCollapsed: collapsed } = storeToRefs(appStore)

// 头像展示名称（取姓名首字）
const shortName = computed(() => (userStore.realName || '用').charAt(0))

// 下拉菜单命令处理
function handleCommand(command) {
  if (command === 'password') {
    pwdVisible.value = true
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 退出登录
function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await userStore.logout()
      router.replace('/login')
    })
    .catch(() => {})
}

// ===== 修改密码 =====
const pwdVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '新密码长度需为 6~20 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function resetPwdForm() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdFormRef.value?.clearValidate()
}

async function submitPwd() {
  await pwdFormRef.value.validate()
  pwdLoading.value = true
  try {
    await updatePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    pwdVisible.value = false
    await userStore.logout()
    router.replace('/login')
  } catch (e) {
    // 错误已由请求拦截器提示
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.navbar {
  height: var(--sg-header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  border-bottom: 1px solid var(--sg-border-color);
}
.navbar__left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.navbar__toggle {
  cursor: pointer;
  color: #606266;
}
.navbar__right {
  display: flex;
  align-items: center;
}
.navbar__user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.navbar__name {
  font-size: 14px;
  color: var(--sg-text-primary);
}
</style>
