<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/plugins/axios'
import {
  mdiChartTimelineVariant,
  mdiReload,
  mdiChartPie,
  mdiCash,
  mdiClipboardList,
  mdiCube,
  mdiAlertCircle,
} from '@mdi/js'

import LineChart from '@/components/Charts/LineChart.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBoxWidget from '@/components/CardBoxWidget.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'

// Dashboard data
const dashboardData = ref({
  doanhThuHomNay: 0,
  donHangHomNay: 0,
  tongTonKho: 0,
  canhBaoTonKho: 0,
  canhBaoLoHetHan: 0,
})

// Chart data
const chartData = ref(null)

// Lấy header token
const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

// Lấy dashboard data
const fetchDashboard = async () => {
  const today = new Date().toISOString()
  try {
    // 1. Đơn hàng & doanh thu hôm nay
    const ordersRes = await api.get('/orders/statistics', {
      headers: getTokenHeader(),
      params: { fromDate: today, toDate: today },
    })
    const stats = ordersRes.data.data || {}
    dashboardData.value.doanhThuHomNay = stats.revenue || 0
    dashboardData.value.donHangHomNay = stats.orders || 0

    // 2. Tổng tồn kho & cảnh báo tồn kho
    const productsRes = await api.get('/products/low-stock', {
      headers: getTokenHeader(),
      params: { threshold: 10, page: 0, size: 1000 },
    })
    const products = productsRes.data.data.content || []
    dashboardData.value.canhBaoTonKho = products.length
    dashboardData.value.tongTonKho = products.reduce(
      (sum, p) => sum + (p.available_quantity || 0),
      0,
    )

    // 3. Cảnh báo lô hết hạn
    const expiredRes = await api.get('/batches/expired', {
      headers: getTokenHeader(),
      params: { page: 0, size: 1000 },
    })
    const batches = expiredRes.data.data.content || []
    dashboardData.value.canhBaoLoHetHan = batches.length
  } catch (err) {
    console.error('Lỗi load dashboard:', err)
    // fallback dữ liệu mẫu
    dashboardData.value = {
      doanhThuHomNay: 3500000,
      donHangHomNay: 45,
      tongTonKho: 540,
      canhBaoTonKho: 5,
      canhBaoLoHetHan: 2,
    }
  }
}

// Chart tuần (mock nếu chưa có API)
const getMockChartData = () => ({
  labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
  datasets: [
    {
      label: 'Doanh thu',
      data: [1200, 1500, 1800, 2000, 1700, 1900, 2200],
      borderColor: '#4ade80',
      fill: false,
      tension: 0.4,
    },
    {
      label: 'Đơn hàng',
      data: [80, 95, 130, 160, 140, 150, 170],
      borderColor: '#60a5fa',
      fill: false,
      tension: 0.4,
    },
  ],
})

const fetchChart = async () => {
  try {
    chartData.value = getMockChartData()
  } catch (err) {
    console.error('Lỗi load chart:', err)
    chartData.value = getMockChartData()
  }
}

onMounted(() => {
  fetchDashboard()
  fetchChart()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine
        :icon="mdiChartTimelineVariant"
        title="Tổng quan"
        main
        :has-button="false"
      />

      <div class="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
        <CardBoxWidget
          :number="dashboardData.doanhThuHomNay"
          label="Doanh thu hôm nay"
          :icon="mdiCash"
          color="text-green-500"
          :formatNumber="true"
        />

        <CardBoxWidget
          :number="dashboardData.donHangHomNay"
          label="Đơn hàng hôm nay"
          :icon="mdiClipboardList"
          color="text-blue-500"
        />

        <CardBoxWidget
          :number="dashboardData.tongTonKho"
          label="Tổng tồn kho"
          :icon="mdiCube"
          color="text-gray-500"
        />

        <CardBoxWidget
          :number="dashboardData.canhBaoTonKho"
          label="Cảnh báo tồn kho"
          :icon="mdiAlertCircle"
          color="text-red-500"
        />

        <CardBoxWidget
          :number="dashboardData.canhBaoLoHetHan"
          label="Cảnh báo lô hết hạn"
          :icon="mdiAlertCircle"
          color="text-yellow-500"
        />
      </div>

      <SectionTitleLine :icon="mdiChartPie" title="Biểu đồ tuần" main>
        <BaseButton :icon="mdiReload" color="whiteDark" @click="fetchChart" />
      </SectionTitleLine>

      <CardBox class="mb-6">
        <div v-if="chartData">
          <LineChart :data="chartData" class="h-96" />
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
