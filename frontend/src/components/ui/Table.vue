<template>
  <div class="overflow-x-auto w-full glass-panel rounded-lg">
    <table class="w-full text-left border-collapse">
      <thead>
        <tr class="bg-white/5 border-b border-white/10">
          <th
            v-for="(col, index) in columns"
            :key="index"
            class="p-4 font-mono text-sm text-gray-300 uppercase tracking-wider"
          >
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(row, rowIndex) in data"
          :key="rowIndex"
          class="border-b border-white/5 last:border-b-0 hover:bg-white/5 transition-colors"
        >
          <td
            v-for="(col, colIndex) in columns"
            :key="colIndex"
            class="p-4 text-gray-100 font-sans"
          >
            <slot :name="col.key" :row="row">
              {{ row[col.key] }}
            </slot>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
defineProps({
  columns: {
    type: Array,
    required: true,
    // { key: 'name', label: 'Name' }
  },
  data: {
    type: Array,
    required: true,
  }
})
</script>
