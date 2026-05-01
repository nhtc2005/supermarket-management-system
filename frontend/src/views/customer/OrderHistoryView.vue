<script setup>
import { ref, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CustomerNavTabs from '@/components/CustomerNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { mdiMedal, mdiCashRegister, mdiGift } from '@mdi/js'
import { useMainStore } from '@/stores/main'
import { api } from '@/plugins/axios.js'

const mainStore = useMainStore()

const orders = ref([])
const loyaltyPoints = ref(0)
const totalOrders = ref(0)
const totalSpent = ref(0)

const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

const fetchCustomerInfo = async () => {
  try {
    const res = await api.get(`/customers/me`, { headers: getTokenHeader() })
    const data = res.data?.success ? res.data.data : {}
    loyaltyPoints.value = data.loyalty_points || 0
    totalOrders.value = data.total_orders || 0
    totalSpent.value = data.total_spent || 0
    mainStore.userName = `${data.first_name || ''} ${data.last_name || ''}`
  } catch (err) {
    console.error('Lỗi tải thông tin khách hàng:', err)
  }
}

const fetchOrders = async () => {
  try {
    const res = await api.get('/orders/my-orders', {
      headers: getTokenHeader(),
      params: {
        page: 0,
        size: 100,
      },
    })
    const data = res.data?.success ? res.data.data.content : []
    orders.value = data.map((order) => ({
      id: order.id,
      orderCode: `HD${order.id}`,
      date: new Date(order.created_at).toLocaleString('vi-VN'),
      totalItems: order.total_items,
      total: order.total_money,
      status: order.status,
    }))
  } catch (err) {
    console.error('Lỗi tải lịch sử đơn hàng:', err)
  }
}

onMounted(() => {
  fetchCustomerInfo()
  fetchOrders()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="mb-2 text-3xl font-bold text-gray-800 dark:text-white">
          Chào mừng, {{ mainStore.userName || 'Khách hàng' }}!
        </h1>
        <p class="text-gray-600 dark:text-gray-400">Xem lịch sử mua hàng và ưu đãi của bạn</p>
      </div>

      <!-- Summary Cards -->
      <div class="mb-6 grid grid-cols-1 gap-6 lg:grid-cols-3">
        <CardBox class="relative">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="mb-2 text-sm text-gray-500 dark:text-gray-400">Điểm Tích Lũy</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">{{ loyaltyPoints }}</h1>
              <p class="mt-2 text-sm text-yellow-600 dark:text-yellow-400">Hạng Vàng</p>
            </div>
            <BaseIcon :path="mdiMedal" size="48" class="text-yellow-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="mb-2 text-sm text-gray-500 dark:text-gray-400">Tổng Đơn Hàng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">{{ totalOrders }}</h1>
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

      <!-- Order History -->
      <CardBox>
        <div class="mb-6">
          <h2 class="mb-2 text-2xl font-bold text-gray-800 dark:text-white">Lịch Sử Đơn Hàng</h2>
          <p class="text-gray-600 dark:text-gray-400">Theo dõi các đơn hàng đã mua tại siêu thị</p>
        </div>

        <div class="space-y-4">
          <div
            v-for="order in orders"
            :key="order.id"
            class="rounded-lg border border-gray-200 bg-white p-6 dark:border-gray-700 dark:bg-slate-800"
          >
            <div class="mb-4">
              <h3 class="mb-1 text-lg font-semibold text-gray-800 dark:text-white">
                {{ order.orderCode }}
              </h3>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ order.date }}</p>
            </div>

            <div class="mb-4">
              <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
                {{ order.totalItems }} sản phẩm
              </p>
            </div>

            <div
              class="flex items-center justify-between border-t border-gray-200 pt-4 dark:border-gray-700"
            >
              <span class="text-xl font-bold text-blue-600 dark:text-blue-400">
                {{ order.total.toLocaleString('vi-VN') }}₫
              </span>
            </div>
          </div>

          <div v-if="orders.length === 0" class="py-12 text-center">
            <p class="text-gray-500 dark:text-gray-400">Chưa có đơn hàng nào</p>
          </div>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
