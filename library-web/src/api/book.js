import request from './request'

/** 分页查询图书 */
export const getBookList = (params) => request.get('/book/list', { params })

/** 查询图书详情 */
export const getBookById = (id) => request.get(`/book/${id}`)

/** 新增图书 */
export const addBook = (data) => request.post('/book', data)

/** 更新图书 */
export const updateBook = (id, data) => request.put(`/book/${id}`, data)

/** 删除图书 */
export const deleteBook = (id) => request.delete(`/book/${id}`)

/** 更新图书状态 */
export const updateBookStatus = (id, status) => request.put(`/book/${id}/status`, { status })

/** 查询热门图书 */
export const getHotBooks = (limit = 10) => request.get('/book/hot', { params: { limit } })
