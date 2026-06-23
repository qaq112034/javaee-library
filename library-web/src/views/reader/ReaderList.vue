<template>
  <div class="reader-list">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索姓名/编号/手机" clearable
          @keyup.enter="fetchData" style="width: 260px; margin-right: 10px" />
        <el-button type="primary" @click="fetchData">搜索</el-button>
        <el-button type="success" icon="Plus" @click="openDialog()">新增读者</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="readerNo" label="读者编号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column label="性别" width="60">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.readerType === 1 ? '学生' : row.readerType === 2 ? '教师' : '校外' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最大借阅" width="80">
          <template #default="{ row }">{{ row.maxBorrowCount }}本/{{ row.maxBorrowDays }}天</template>
        </el-table-column>
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="warning" link size="small"
              @click="handleToggleStatus(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑读者' : '新增读者'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="读者类型">
              <el-select v-model="form.readerType" style="width:100%">
                <el-option label="学生" :value="1" />
                <el-option label="教师" :value="2" />
                <el-option label="校外" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大借书">
              <el-input-number v-model="form.maxBorrowCount" :min="1" :max="50" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="借阅天数">
              <el-input-number v-model="form.maxBorrowDays" :min="1" :max="365" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { getReaderList, getReaderById, addReader, updateReader, deleteReader, updateReaderStatus } from '@/api/reader'

const keyword = ref('')
const loading = ref(false)
const tableData = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const form = reactive({ realName: '', gender: 1, phone: '', email: '', address: '', readerType: 1, maxBorrowCount: 5, maxBorrowDays: 30 })
const rules = { realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }], phone: [{ required: true, message: '请输入电话', trigger: 'blur' }] }

async function fetchData() {
  loading.value = true
  try {
    const res = await getReaderList({ keyword: keyword.value, current: page.current, size: page.size })
    tableData.value = res.data.records; page.total = res.data.total
  } catch { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  if (row) {
    // 编辑模式：逐字段赋值（避免 Object.assign 与 Vue reactive Proxy 的兼容问题）
    isEdit.value = true; editId.value = row.id
    form.realName = row.realName || ''
    form.gender = row.gender ?? 1
    form.phone = row.phone || ''
    form.email = row.email || ''
    form.address = row.address || ''
    form.readerType = row.readerType ?? 1
    form.maxBorrowCount = row.maxBorrowCount ?? 5
    form.maxBorrowDays = row.maxBorrowDays ?? 30
  } else {
    // 新增模式：重置所有字段
    isEdit.value = false; editId.value = null
    form.realName = ''
    form.gender = 1
    form.phone = ''
    form.email = ''
    form.address = ''
    form.readerType = 1
    form.maxBorrowCount = 5
    form.maxBorrowDays = 30
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value) { await updateReader(editId.value, form) }
    else { await addReader(form) }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false; fetchData()
  } catch { /* ignore */ }
  submitting.value = false
}

async function handleDelete(id) { await deleteReader(id); ElMessage.success('删除成功'); fetchData() }

async function handleToggleStatus(row) {
  const s = row.status === 1 ? 0 : 1
  await updateReaderStatus(row.id, s)
  ElMessage.success(s === 1 ? '已启用' : '已停用')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 8px; }
</style>
