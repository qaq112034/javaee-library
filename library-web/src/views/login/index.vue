<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
      <div class="shape shape-5"></div>
    </div>

    <div class="login-card">
      <div class="card-header">
        <div class="logo-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="8" y="6" width="32" height="36" rx="3" fill="#409EFF" opacity="0.15"/>
            <rect x="12" y="10" width="24" height="28" rx="2" fill="#409EFF" opacity="0.3"/>
            <line x1="18" y1="17" x2="30" y2="17" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
            <line x1="18" y1="22" x2="28" y2="22" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
            <line x1="18" y1="27" x2="26" y2="27" stroke="#409EFF" stroke-width="2" stroke-linecap="round"/>
            <line x1="18" y1="32" x2="24" y2="32" stroke="#E6A23C" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="title">图书管理系统</h1>
        <p class="subtitle">Enjoy Reading, Enjoy Life</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"
            :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password"
            placeholder="请输入密码" :prefix-icon="Lock"
            show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading"
            class="login-btn" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <p class="tip">默认账号: admin / admin123</p>
    </div>

    <p class="footer-text">Library Management System © 2024</p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({ username: 'admin', password: 'admin123' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #e8f4fd 0%, #f0f7ff 30%, #fef9f0 60%, #e8f4fd 100%);
  overflow: hidden;
  position: relative;
}

/* ==================== 背景装饰形状 ==================== */
.bg-shapes { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }
.shape {
  position: absolute; border-radius: 50%; opacity: 0.35;
  animation: drift 18s infinite ease-in-out;
}
.shape-1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, #a8d8ff 0%, transparent 70%);
  top: -15%; right: -8%; animation-delay: 0s;
}
.shape-2 {
  width: 350px; height: 350px;
  background: radial-gradient(circle, #ffe0a8 0%, transparent 70%);
  bottom: -10%; left: -5%; animation-delay: -5s;
}
.shape-3 {
  width: 250px; height: 250px;
  background: radial-gradient(circle, #b8f0d8 0%, transparent 70%);
  top: 50%; left: 10%; animation-delay: -10s;
}
.shape-4 {
  width: 200px; height: 200px;
  background: radial-gradient(circle, #ffd4b8 0%, transparent 70%);
  top: 20%; right: 20%; animation-delay: -14s;
}
.shape-5 {
  width: 180px; height: 180px;
  background: radial-gradient(circle, #c8e0ff 0%, transparent 70%);
  bottom: 20%; right: 30%; animation-delay: -8s;
}
@keyframes drift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(20px, -25px) scale(1.08); }
  50% { transform: translate(-15px, -10px) scale(0.95); }
  75% { transform: translate(-25px, 20px) scale(1.04); }
}

/* ==================== 登录卡片 ==================== */
.login-card {
  width: 420px;
  padding: 44px 44px 36px;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16px);
  border-radius: 20px;
  box-shadow: 0 8px 40px rgba(64,158,255,0.08), 0 2px 8px rgba(0,0,0,0.04);
  border: 1px solid rgba(255,255,255,0.6);
  position: relative;
  z-index: 1;
}
.card-header { text-align: center; margin-bottom: 32px; }
.logo-icon {
  width: 64px; height: 64px; margin: 0 auto 16px;
  background: rgba(64,158,255,0.08);
  border-radius: 18px; display: flex; align-items: center; justify-content: center;
}
.logo-icon svg { width: 40px; height: 40px; }
.title {
  font-size: 26px; font-weight: 700; color: #1d2b3a; margin: 0 0 6px;
  letter-spacing: 1px;
}
.subtitle {
  font-size: 13px; color: #909399; margin: 0;
  letter-spacing: 2px; font-weight: 400;
}

/* ==================== 表单 ==================== */
:deep(.el-input__wrapper) {
  border-radius: 10px; background: #f8fafc;
  border: 1px solid #e8edf2; box-shadow: none;
  transition: all 0.25s;
}
:deep(.el-input__wrapper:hover) { border-color: #b3cef0; background: #fff; }
:deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64,158,255,0.1); background: #fff;
}

.login-btn {
  width: 100%; height: 46px; font-size: 16px;
  letter-spacing: 4px; border-radius: 10px;
  background: linear-gradient(135deg, #409EFF, #5b8def);
  border: none; margin-top: 4px;
  transition: all 0.3s; font-weight: 500;
}
.login-btn:hover {
  background: linear-gradient(135deg, #5b8def, #79a6f5);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(64,158,255,0.35);
}

.tip { text-align: center; color: #b0b8c1; font-size: 12px; margin: 20px 0 0; }

/* ==================== 底部 ==================== */
.footer-text {
  position: absolute; bottom: 24px;
  color: #bcc4d0; font-size: 12px;
  letter-spacing: 1px; z-index: 1;
}

@media (max-width: 500px) {
  .login-card { width: 92%; padding: 32px 24px 28px; }
}
</style>
