<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white">User Accounts</h2>
      <Button variant="primary">Add New User</Button>
    </div>
    
    <Table :columns="columns" :data="accounts">
      <template #status="{ row }">
        <Badge :variant="row.status === 'Active' ? 'verified' : 'pending'">
          {{ row.status }}
        </Badge>
      </template>
      <template #actions>
        <Button variant="outline" size="sm">Edit</Button>
      </template>
    </Table>
  </div>
</template>

<script setup>
import Table from '../../../components/ui/Table.vue'
import Badge from '../../../components/ui/Badge.vue'
import Button from '../../../components/ui/Button.vue'

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'name', label: 'Name' },
  { key: 'role', label: 'Role' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Actions' }
]

// Mock Data placeholder for API
const accounts = [
  { id: 'USR-001', name: 'Alice Smith', role: 'Admin', status: 'Active' },
  { id: 'USR-002', name: 'Bob Johnson', role: 'Inspector', status: 'Active' },
  { id: 'USR-003', name: 'Charlie Davis', role: 'Farmer', status: 'Pending' }
]
</script>
