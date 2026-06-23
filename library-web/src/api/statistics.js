import request from './request'

export const getDashboard = () => request.get('/statistics/dashboard')
export const getBorrowTrend = (days = 30) => request.get('/statistics/trend', { params: { days } })
export const getCategoryStats = () => request.get('/statistics/category')
