import request from './request'

/** 用户登录 */
export const login = (data) => request.post('/auth/login', data)

/** 获取当前用户信息 */
export const getUserInfo = () => request.get('/auth/info')

/** 退出登录 */
export const logout = () => request.post('/auth/logout')
