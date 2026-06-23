import request from './request'

export const getBorrowList = (params) => request.get('/borrow/list', { params })
export const borrowBook = (data) => request.post('/borrow', data)
export const returnBook = (id) => request.put(`/borrow/${id}/return`)
export const renewBook = (id) => request.put(`/borrow/${id}/renew`)
export const getReaderHistory = (readerId) => request.get(`/borrow/reader/${readerId}`)
export const getOverdueRecords = () => request.get('/borrow/overdue')
