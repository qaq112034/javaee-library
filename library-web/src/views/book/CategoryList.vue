<template>
  <div class="category-page">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" icon="Plus" @click="openDialog()">新增分类</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" row-key="id" border
        default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="categoryName" label="分类名称" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="无（顶级分类）" clearable style="width:100%">
            <el-option v-for="c in flatCategories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
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

const loading = ref(false)
const tableData = ref([])
const flatCategories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const form = reactive({ categoryName: '', parentId: null, sortOrder: 0 })
const rules = { categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

async function fetchData() {
  loading.value = true
  try {
    tableData.value = (await request.get('/category/tree')).data || []
    const res = await request.get('/category/list')
    flatCategories.value = res.data || []
  } catch { /* ignore */ }
  loading.value = false
}

function openDialog(row) {
  form.categoryName = ''; form.parentId = null; form.sortOrder = 0
  if (row) {
    isEdit.value = true; editId.value = row.id
    Object.assign(form, { categoryName: row.categoryName, parentId: row.parentId || null, sortOrder: row.sortOrder })
  } else {
    isEdit.value = false; editId.value = null
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/category/${editId.value}`, form)
    } else {
      await request.post('/category', form)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchData()
  } catch { /* ignore */ }
  submitting.value = false
}

async function handleDelete(id) {
  await request.delete(`/category/${id}`)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
