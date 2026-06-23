import request from './request'

export const getNoticeList = (params) => request.get('/notice/list', { params })
export const getNoticeById = (id) => request.get(`/notice/${id}`)
export const addNotice = (data) => request.post('/notice', data)
export const updateNotice = (id, data) => request.put(`/notice/${id}`, data)
export const deleteNotice = (id) => request.delete(`/notice/${id}`)
export const publishNotice = (id) => request.put(`/notice/${id}/publish`)
