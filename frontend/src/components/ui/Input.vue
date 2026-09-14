<template>
  <div class="flex flex-col gap-1 w-full">
    <label v-if="label" class="font-mono text-xs font-semibold text-gray-300 uppercase tracking-wider">{{ label }}</label>
    <div class="relative">
      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        :type="type"
        :placeholder="placeholder"
        :class="[
          'w-full px-3 py-2 bg-white/5 backdrop-blur-md text-white border rounded outline-none transition-all placeholder:text-gray-500',
          error ? 'border-error focus:ring-2 focus:ring-error focus:border-error shadow-[0_0_10px_rgba(239,68,68,0.2)]' : 'border-white/20 focus:ring-2 focus:ring-primary focus:border-primary focus:bg-white/10',
          icon ? 'pl-10' : ''
        ]"
      />
      <span v-if="icon" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
        <component :is="icon" class="w-4 h-4" />
      </span>
    </div>
    <span v-if="error" class="text-xs text-error font-semibold mt-1">{{ error }}</span>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  placeholder: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  },
  icon: {
    type: Object,
    default: null
  }
})

defineEmits(['update:modelValue'])
</script>
