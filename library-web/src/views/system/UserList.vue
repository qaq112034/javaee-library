<template>
  <div class="user-list">
    <el-card>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索用户名/姓名" clearable @keyup.enter="fetchData"
          style="width: 240px; margin-right: 8px" />
        <el-button type="primary" @click="fetchData">搜索</el-button>
        <el-button type="success" icon="Plus" @click="openDialog()">新增用户</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ fmt(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? null : 'password'">
          <el-input v-model="form.password" type="password"
            :placeholder="isEdit ? '留空则不修改' : '请输入密码'" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
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
import request from '@/api/request'

const keyword = ref(''); const loading = ref(false); const tableData = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(null); const submitting = ref(false)
const form = reactive({ username: '', password: '', realName: '', phone: '', email: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

function fmt(d) { return d ? d.replace('T', ' ').substring(0, 16) : '-' }

async function fetchData() {
  loading.value = true
  try {
    const res = await request.get('/user/list', { params: { keyword: keyword.value, current: page.current, size: page.size } })
    tableData.value = res.data.records; page.total = res.data.total
  } catch { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  form.username = ''; form.password = ''; form.realName = ''; form.phone = ''; form.email = ''
  if (row) {
    isEdit.value = true; editId.value = row.id
    Object.assign(form, { username: row.username, realName: row.realName, phone: row.phone, email: row.email })
    form.password = ''
  } else { isEdit.value = false; editId.value = null }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/user/${editId.value}`, form)
    } else {
      await request.post('/user', form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false; fetchData()
  } catch { /* ignore */ }
  submitting.value = false
}

async function handleDelete(id) { await request.delete(`/user/${id}`); ElMessage.success('删除成功'); fetchData() }

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 8px; }
</style>
