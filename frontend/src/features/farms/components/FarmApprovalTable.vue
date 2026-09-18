<template>
  <div>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white">Farm Approvals</h2>
    </div>
    
    <Table :columns="columns" :data="farms">
      <template #certification="{ row }">
        <Badge v-if="row.certification" variant="verified">{{ row.certification }}</Badge>
        <span v-else class="text-gray-400 font-mono text-sm">None</span>
      </template>
      <template #status="{ row }">
        <Badge :variant="row.status === 'Approved' ? 'verified' : (row.status === 'Rejected' ? 'error' : 'pending')">
          {{ row.status }}
        </Badge>
      </template>
      <template #actions="{ row }">
        <div class="flex gap-2">
          <Button v-if="row.status === 'Pending'" variant="primary" size="sm">Approve</Button>
          <Button v-if="row.status === 'Pending'" variant="danger" size="sm">Reject</Button>
          <Button variant="outline" size="sm">View Details</Button>
        </div>
      </template>
    </Table>
  </div>
</template>

<script setup>
import Table from '../../../components/ui/Table.vue'
import Badge from '../../../components/ui/Badge.vue'
import Button from '../../../components/ui/Button.vue'

const columns = [
  { key: 'farmId', label: 'Farm ID' },
  { key: 'owner', label: 'Owner' },
  { key: 'location', label: 'Location' },
  { key: 'certification', label: 'Cert' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Actions' }
]

// Mock Data placeholder for API
const farms = [
  { farmId: 'FRM-8821', owner: 'Green Valley Co.', location: 'Da Lat, VN', certification: 'Organic', status: 'Pending' },
  { farmId: 'FRM-8822', owner: 'Sunrise Ag', location: 'Dak Lak, VN', certification: 'GlobalGAP', status: 'Approved' },
  { farmId: 'FRM-8823', owner: 'Highland Beans', location: 'Gia Lai, VN', certification: null, status: 'Rejected' },
]
</script>
