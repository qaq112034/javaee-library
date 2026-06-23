<template>
  <div class="role-list">
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showPermissions(row)">查看权限</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 权限查看弹窗 -->
    <el-dialog v-model="permDialogVisible" :title="`${currentRole?.roleName} - 权限列表`" width="500px">
      <el-tree :data="permTree" default-expand-all node-key="id" :props="{ label: 'permName' }">
        <template #default="{ data }">
          <span>
            {{ data.permName }}
            <el-tag size="small" style="margin-left:8px">{{ data.permCode }}</el-tag>
            <el-tag v-if="hasPerm(data.id)" type="success" size="small" style="margin-left:4px">✓</el-tag>
          </span>
        </template>
      </el-tree>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const permDialogVisible = ref(false)
const currentRole = ref(null)
const permTree = ref([])
const rolePermIds = ref([])

function hasPerm(permId) { return rolePermIds.value.includes(permId) }

async function fetchRoles() {
  loading.value = true
  try {
    tableData.value = (await request.get('/role/list')).data || []
  } catch { /* ignore */ }
  loading.value = false
}

async function showPermissions(role) {
  currentRole.value = role
  try {
    const [treeRes, permRes] = await Promise.all([
      request.get('/role/permissions/tree'),
      request.get(`/role/${role.id}/permissions`)
    ])
    permTree.value = treeRes.data || []
    rolePermIds.value = (permRes.data || []).map(p => p.id)
  } catch { /* ignore */ }
  permDialogVisible.value = true
}

onMounted(fetchRoles)
</script>
