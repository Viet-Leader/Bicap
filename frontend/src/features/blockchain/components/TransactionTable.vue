<template>
  <div>
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
      <h2 class="text-2xl font-bold text-white">Blockchain Explorer</h2>
      <div class="flex gap-2 w-full md:w-auto">
        <Input v-model="searchQuery" placeholder="Search Hash / Block..." class="w-full md:w-64" />
        <Button variant="primary" @click="applySearch">Search</Button>
      </div>
    </div>
    
    <Table :columns="columns" :data="filteredTransactions">
      <template #hash="{ row }">
        <span class="font-mono text-xs text-primary truncate max-w-[150px] inline-block" :title="row.hash">
          {{ row.hash }}
        </span>
      </template>
      <template #type="{ row }">
        <Badge variant="default">{{ row.type }}</Badge>
      </template>
      <template #timestamp="{ row }">
        <span class="font-mono text-sm">{{ row.timestamp }}</span>
      </template>
      <template #status="{ row }">
        <Badge variant="verified">Success</Badge>
      </template>
    </Table>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import Table from '../../../components/ui/Table.vue'
import Badge from '../../../components/ui/Badge.vue'
import Button from '../../../components/ui/Button.vue'
import Input from '../../../components/ui/Input.vue'

const searchQuery = ref('')
const activeSearch = ref('')

const applySearch = () => {
  activeSearch.value = searchQuery.value.trim().toLowerCase()
}

const columns = [
  { key: 'block', label: 'Block' },
  { key: 'hash', label: 'Tx Hash' },
  { key: 'type', label: 'Type' },
  { key: 'timestamp', label: 'Timestamp' },
  { key: 'status', label: 'Status' }
]

// Mock Data placeholder for API
const transactions = [
  { block: '1044231', hash: '0x9f8b...3a21', type: 'Farm Registration', timestamp: '2 mins ago' },
  { block: '1044230', hash: '0x7a2c...1f99', type: 'Product Harvest', timestamp: '5 mins ago' },
  { block: '1044229', hash: '0x3b11...8e44', type: 'Quality Check', timestamp: '12 mins ago' },
  { block: '1044228', hash: '0x1c44...2d55', type: 'Ownership Transfer', timestamp: '1 hour ago' },
]

const filteredTransactions = computed(() => {
  if (!activeSearch.value) return transactions
  return transactions.filter((transaction) => Object.values(transaction).some((value) => String(value).toLowerCase().includes(activeSearch.value)))
})
</script>
