<script setup>
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'

// Redirect to POS page
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  router.replace('/sales/pos')
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">Dashboard Bán Hàng</h1>
        <p class="text-gray-600 dark:text-gray-400">Thực hiện giao dịch và tra cứu sản phẩm</p>
      </div>
      <SalesNavTabs />
    </SectionMain>
  </LayoutAuthenticated>
</template>
