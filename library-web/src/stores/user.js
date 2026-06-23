import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'

/**
 * 用户状态管理 (Pinia)
 *
 * 管理登录状态、Token、用户信息、权限列表。
 * 采用 Composition API 风格（Setup Store）。
 */
export const useUserStore = defineStore('user', () => {
  // ==================== 状态 ====================
  const token = ref(localStorage.getItem('accessToken') || '')
  const userId = ref('')
  const username = ref('')
  const realName = ref('')
  const avatar = ref('')
  const roles = ref([])
  const permissions = ref([])

  // ==================== 计算属性 ====================
  const isLoggedIn = computed(() => !!token.value)

  // ==================== 方法 ====================

  /** 登录 */
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    const data = res.data
    // 保存 Token
    token.value = data.accessToken
    localStorage.setItem('accessToken', data.accessToken)
    // 保存用户信息
    userId.value = data.userId
    username.value = data.username
    realName.value = data.realName
    avatar.value = data.avatar
    roles.value = data.roles || []
    permissions.value = data.permissions || []
  }

  /** 退出登录 */
  function logout() {
    token.value = ''
    userId.value = ''
    username.value = ''
    realName.value = ''
    avatar.value = ''
    roles.value = []
    permissions.value = []
    localStorage.clear()
  }

  /** 判断是否有某个权限 */
  function hasPermission(permCode) {
    return permissions.value.includes(permCode)
  }

  /** 判断是否有某个角色 */
  function hasRole(roleCode) {
    return roles.value.includes(roleCode)
  }

  return {
    token, userId, username, realName, avatar,
    roles, permissions, isLoggedIn,
    login, logout, hasPermission, hasRole
  }
})
