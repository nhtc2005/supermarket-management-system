<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BaseIcon from '@/components/BaseIcon.vue'
import {
  mdiCashRegister,
  mdiGift,
} from '@mdi/js'

const route = useRoute()

const tabs = [
  { path: '/customer/orders', icon: mdiCashRegister, label: 'Lịch Sử Mua Hàng' },
  { path: '/customer/promotions', icon: mdiGift, label: 'Khuyến Mãi & Tích Điểm' },
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

