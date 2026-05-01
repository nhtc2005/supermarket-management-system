<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BaseIcon from '@/components/BaseIcon.vue'
import {
  mdiCart,
  mdiMagnify,
  mdiCashRegister,
} from '@mdi/js'

const route = useRoute()

const tabs = [
  { path: '/sales/pos', icon: mdiCart, label: 'Bán Hàng' },
  { path: '/sales/search', icon: mdiMagnify, label: 'Tra Cứu' },
  { path: '/sales/history', icon: mdiCashRegister, label: 'Lịch Sử' },
]

const isActive = (tabPath) => {
  return route.path === tabPath || route.path.startsWith(tabPath + '/')
}
</script>

<template>
  <div class="flex space-x-2 mb-6 border-b border-gray-200 dark:border-gray-700">
    <router-link
      v-for="tab in tabs"
      :key="tab.path"
      :to="tab.path"
      class="px-4 py-2 rounded-t-lg flex items-center space-x-2 transition-colors"
      :class="
        isActive(tab.path)
          ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 font-medium'
          : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800'
      "
    >
      <BaseIcon :path="tab.icon" size="20" />
      <span>{{ tab.label }}</span>
    </router-link>
  </div>
</template>

