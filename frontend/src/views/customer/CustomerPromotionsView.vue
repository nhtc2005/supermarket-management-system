<script setup>
import { ref, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CustomerNavTabs from '@/components/CustomerNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { useMainStore } from '@/stores/main'
import {
  mdiMedal,
  mdiCashRegister,
  mdiGift,
  mdiTrendingUp,
  mdiLightningBolt,
  mdiStar,
} from '@mdi/js'
import { api } from '@/plugins/axios.js'

const mainStore = useMainStore()

const points = ref(0)
const currentRank = ref('Silver')
const nextRank = ref('Gold')
const pointsToNextRank = ref(50)
const currentProgress = ref(0)

const benefits = ref([
  {
    id: 1,
    icon: mdiTrendingUp,
    iconColor: 'text-blue-500',
    title: 'Tích điểm tăng',
    value: '+15%',
  },
  {
    id: 2,
    icon: mdiGift,
    iconColor: 'text-purple-500',
    title: 'Voucher tháng',
    value: '3 voucher',
  },
  {
    id: 3,
    icon: mdiLightningBolt,
    iconColor: 'text-green-500',
    title: 'Ưu tiên hỗ trợ',
    value: '24/7',
  },
])

const getRankLabel = (rank) => {
  const ranks = {
    Gold: 'Hạng Vàng',
    Silver: 'Hạng Bạc',
    Bronze: 'Hạng Đồng',
  }
  return ranks[rank] || rank
}

// Lấy thông tin khách hàng hiện tại
const fetchCustomer = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await api.get('/customers/me', {
      headers: { Authorization: `Bearer ${token}` },
    })
    if (res.data.success) {
      const data = res.data.data
      points.value = data.loyalty_points || 0
      mainStore.userName = `${data.first_name || ''} ${data.last_name || ''}`

      // Cập nhật tiến độ rank (hardcode logic demo)
      if (points.value >= 2500) {
        currentRank.value = 'Gold'
        nextRank.value = 'Platinum'
        pointsToNextRank.value = 500
        currentProgress.value = Math.min(100, (points.value / 3000) * 100)
      } else if (points.value >= 1500) {
        currentRank.value = 'Silver'
        nextRank.value = 'Gold'
        pointsToNextRank.value = 1000 - points.value
        currentProgress.value = Math.min(100, ((points.value - 1500) / 1000) * 100)
      } else {
        currentRank.value = 'Bronze'
        nextRank.value = 'Silver'
        pointsToNextRank.value = 1500 - points.value
        currentProgress.value = Math.min(100, (points.value / 1500) * 100)
      }
    }
  } catch (err) {
    console.error('Lỗi tải thông tin khách hàng:', err)
  }
}

onMounted(() => {
  fetchCustomer()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="mb-2 text-3xl font-bold text-gray-800 dark:text-white">
          Chào mừng, {{ mainStore.userName || 'Khách hàng' }}!
        </h1>
        <p class="text-gray-600 dark:text-gray-400">Xem điểm tích lũy và ưu đãi của bạn</p>
      </div>

      <!-- Summary Cards -->
      <div class="mb-6 grid grid-cols-1 gap-6 lg:grid-cols-3">
        <CardBox class="relative">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="mb-2 text-sm text-gray-500 dark:text-gray-400">Điểm Tích Lũy</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">
                {{ points.toLocaleString('vi-VN') }}
              </h1>
              <p class="mt-2 text-sm text-yellow-600 dark:text-yellow-400">
                {{ getRankLabel(currentRank) }}
              </p>
            </div>
            <BaseIcon :path="mdiMedal" size="48" class="text-yellow-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="mb-2 text-sm text-gray-500 dark:text-gray-400">Tổng Đơn Hàng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">47</h1>
              <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">Lịch sử mua hàng</p>
            </div>
            <BaseIcon :path="mdiCashRegister" size="48" class="text-blue-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="mb-2 text-sm text-gray-500 dark:text-gray-400">Voucher Khả Dụng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">5</h1>
              <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">Ưu đãi khuyến mãi</p>
            </div>
            <BaseIcon :path="mdiGift" size="48" class="text-purple-500" />
          </div>
        </CardBox>
      </div>

      <CustomerNavTabs />

      <!-- Points Progress Section -->
      <CardBox class="mb-6 bg-gradient-to-r from-blue-500 to-purple-600 text-white">
        <div class="mb-4 flex items-start justify-between">
          <div>
            <h3 class="mb-2 text-sm text-white/80">Điểm tích lũy</h3>
            <h1 class="mb-2 text-4xl font-bold text-white">
              {{ points.toLocaleString('vi-VN') }} điểm
            </h1>
            <p class="text-white/90">Tiến độ lên {{ getRankLabel(nextRank) }}</p>
          </div>
          <div class="flex items-center space-x-2">
            <BaseIcon :path="mdiStar" size="24" class="text-white" />
            <span class="font-medium text-white">{{ getRankLabel(currentRank) }}</span>
          </div>
        </div>

        <div class="mt-4">
          <div class="mb-2 flex items-center justify-between">
            <span class="text-sm text-white/90">Tiến độ</span>
            <span class="font-medium text-white">{{ pointsToNextRank }} điểm nữa</span>
          </div>
          <div class="h-3 w-full rounded-full bg-white/20">
            <div
              class="h-3 rounded-full bg-white transition-all duration-300"
              :style="{ width: currentProgress + '%' }"
            ></div>
          </div>
        </div>
      </CardBox>

      <!-- Membership Benefits -->
      <CardBox>
        <div class="mb-6">
          <h2 class="mb-2 text-2xl font-bold text-gray-800 dark:text-white">
            Quyền Lợi {{ getRankLabel(currentRank) }}
          </h2>
          <p class="text-gray-600 dark:text-gray-400">Ưu đãi dành riêng cho bạn</p>
        </div>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div
            v-for="benefit in benefits"
            :key="benefit.id"
            class="rounded-lg border border-gray-200 bg-white p-6 text-center dark:border-gray-700 dark:bg-slate-800"
          >
            <div class="mb-4 flex justify-center">
              <div class="rounded-full bg-gray-100 p-3 dark:bg-slate-700">
                <BaseIcon :path="benefit.icon" size="32" :class="benefit.iconColor" />
              </div>
            </div>
            <h3 class="mb-2 text-lg font-semibold text-gray-800 dark:text-white">
              {{ benefit.title }}
            </h3>
            <p class="text-2xl font-bold text-blue-600 dark:text-blue-400">{{ benefit.value }}</p>
          </div>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
