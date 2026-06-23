<template>
  <div class="notice-list">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" icon="Plus" @click="openDialog()">新增公告</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.noticeType === 1 ? '' : 'warning'" size="small">
              {{ row.noticeType === 1 ? '系统公告' : '催还通知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ fmt(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="row.status !== 1" type="success" link size="small"
              @click="handlePublish(row.id)">发布</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" link size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page.current" v-model:page-size="page.size"
        :total="page.total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @change="fetchData" style="margin-top:20px;justify-content:flex-end" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.noticeType">
            <el-radio :value="1">系统公告</el-radio>
            <el-radio :value="2">催还通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNoticeList, getNoticeById, addNotice, updateNotice, deleteNotice, publishNotice } from '@/api/notice'

const loading = ref(false); const tableData = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(null); const submitting = ref(false)
const form = reactive({ title: '', content: '', noticeType: 1 })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }] }

function fmt(d) { return d ? d.replace('T', ' ').substring(0, 16) : '-' }

async function fetchData() {
  loading.value = true
  try {
    const res = await getNoticeList({ current: page.current, size: page.size })
    tableData.value = res.data.records; page.total = res.data.total
  } catch { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  form.title = ''; form.content = ''; form.noticeType = 1
  if (row) { isEdit.value = true; editId.value = row.id; Object.assign(form, { title: row.title, content: row.content, noticeType: row.noticeType }) }
  else { isEdit.value = false; editId.value = null }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value) { await updateNotice(editId.value, form) } else { await addNotice(form) }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false; fetchData()
  } catch { /* ignore */ }
  submitting.value = false
}

async function handleDelete(id) { await deleteNotice(id); ElMessage.success('删除成功'); fetchData() }
async function handlePublish(id) { await publishNotice(id); ElMessage.success('发布成功'); fetchData() }

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
