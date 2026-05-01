<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { useMainStore } from '@/stores/main'
import { mdiMedal, mdiCashRegister, mdiGift } from '@mdi/js'

const router = useRouter()
const mainStore = useMainStore()

const stats = ref({
  points: 2450,
  totalOrders: 47,
  availableVouchers: 5,
  rank: 'Gold',
})

const getRankLabel = (rank) => {
  const ranks = {
    Gold: 'Hạng Vàng',
    Silver: 'Hạng Bạc',
    Bronze: 'Hạng Đồng',
  }
  return ranks[rank] || rank
}

// Redirect to orders page
onMounted(() => {
  router.replace('/customer/orders')
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">
          Chào mừng, {{ mainStore.userName || 'Khách hàng' }}!
        </h1>
        <p class="text-gray-600 dark:text-gray-400">Xem lịch sử mua hàng và khuyến mãi của bạn</p>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3 mb-6">
        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Điểm Tích Lũy</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">
                {{ stats.points.toLocaleString('vi-VN') }}
              </h1>
              <p class="text-sm text-yellow-600 dark:text-yellow-400 mt-2">
                {{ getRankLabel(stats.rank) }}
              </p>
            </div>
            <BaseIcon :path="mdiMedal" size="48" class="text-yellow-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Tổng Đơn Hàng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">{{ stats.totalOrders }}</h1>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">Lịch sử mua hàng</p>
            </div>
            <BaseIcon :path="mdiCashRegister" size="48" class="text-blue-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Voucher Khả Dụng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">
                {{ stats.availableVouchers }}
              </h1>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">Ưu đãi khuyến mãi</p>
            </div>
            <BaseIcon :path="mdiGift" size="48" class="text-purple-500" />
          </div>
        </CardBox>
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>
