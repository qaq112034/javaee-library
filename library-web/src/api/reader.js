import request from './request'

export const getReaderList = (params) => request.get('/reader/list', { params })
export const getReaderById = (id) => request.get(`/reader/${id}`)
export const addReader = (data) => request.post('/reader', data)
export const updateReader = (id, data) => request.put(`/reader/${id}`, data)
export const deleteReader = (id) => request.delete(`/reader/${id}`)
export const updateReaderStatus = (id, status) => request.put(`/reader/${id}/status`, { status })
