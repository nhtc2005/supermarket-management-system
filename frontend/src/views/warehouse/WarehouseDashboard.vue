<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/plugins/axios'

import {
  mdiChartTimelineVariant,
  mdiReload,
  mdiChartPie,
  mdiArrowDownBoldBox,
  mdiArrowUpBoldBox,
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

const chartData = ref(null)

const dashboardData = ref({
  nhapHomNay: 0,
  xuatHomNay: 0,
  tongTonKho: 0,
  canhBao: 0,
})

const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

const getTestChartData = () => ({
  labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
  datasets: [
    {
      label: 'Nhập kho',
      data: [120, 150, 180, 200, 170, 190],
      borderColor: '#4ade80',
      fill: false,
      tension: 0.4,
    },
    {
      label: 'Xuất kho',
      data: [80, 95, 130, 160, 140, 150],
      borderColor: '#60a5fa',
      fill: false,
      tension: 0.4,
    },
  ],
})

const fetchDashboard = async () => {
  try {
    const productsRes = await api.get('/products/low-stock', {
      headers: getTokenHeader(),
    })
    const lowStockProducts = productsRes.data.data.content || []
    dashboardData.value.canhBao = lowStockProducts.length
    dashboardData.value.tongTonKho = lowStockProducts.reduce(
      (sum, p) => sum + (p.available_quantity || 0),
      0,
    )

    const expiredRes = await api.get('/batches/expired', {
      headers: getTokenHeader(),
      params: { page: 0, size: 1000 },
    })
    const expiredBatches = expiredRes.data.data.content || []
    dashboardData.value.canhBao += expiredBatches.length

    dashboardData.value.nhapHomNay = 0
    dashboardData.value.xuatHomNay = 0
  } catch (err) {
    console.error('Lỗi load dashboard:', err)
    dashboardData.value = {
      nhapHomNay: 0,
      xuatHomNay: 0,
      tongTonKho: 0,
      canhBao: 0,
    }
  }
}

const fetchChart = async () => {
  try {
    chartData.value = getTestChartData()
  } catch (e) {
    console.error('Lỗi load chart:', e)
    chartData.value = getTestChartData()
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
      <SectionTitleLine :icon="mdiChartTimelineVariant" title="Chung" main :has-button="false" />

      <div class="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <CardBoxWidget
          :number="dashboardData.nhapHomNay"
          label="Nhập hôm nay"
          :icon="mdiArrowDownBoldBox"
          color="text-green-500"
        />

        <CardBoxWidget
          :number="dashboardData.xuatHomNay"
          label="Xuất hôm nay"
          :icon="mdiArrowUpBoldBox"
          color="text-blue-500"
        />

        <CardBoxWidget
          :number="dashboardData.tongTonKho"
          label="Tổng tồn kho"
          :icon="mdiCube"
          color="text-gray-500"
        />

        <CardBoxWidget
          :number="dashboardData.canhBao"
          label="Cảnh báo"
          :icon="mdiAlertCircle"
          color="text-red-500"
        />
      </div>

      <SectionTitleLine :icon="mdiChartPie" title="Số liệu" main>
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
