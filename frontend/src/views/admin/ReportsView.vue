<script setup>
import { ref, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitle from '@/components/SectionTitle.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import { mdiFileExport, mdiChartBar } from '@mdi/js'

const reportType = ref('sales')
const dateRange = ref({
  start: '',
  end: '',
})

const reports = ref([
  {
    id: 1,
    type: 'sales',
    name: 'Báo cáo doanh thu',
    description: 'Thống kê doanh thu theo thời gian',
    generatedAt: '2024-03-15 10:30',
  },
  {
    id: 2,
    type: 'products',
    name: 'Báo cáo sản phẩm',
    description: 'Thống kê sản phẩm bán chạy',
    generatedAt: '2024-03-14 15:20',
  },
  {
    id: 3,
    type: 'users',
    name: 'Báo cáo người dùng',
    description: 'Thống kê người dùng mới',
    generatedAt: '2024-03-13 09:15',
  },
])

const generateReport = () => {
  // Logic tạo báo cáo
  alert('Đang tạo báo cáo...')
}

const exportReport = (report) => {
  // Logic xuất báo cáo
  alert(`Đang xuất báo cáo: ${report.name}`)
}

onMounted(() => {
  const today = new Date()
  const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate())
  dateRange.value.start = lastMonth.toISOString().split('T')[0]
  dateRange.value.end = today.toISOString().split('T')[0]
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitle first>Báo cáo và thống kê</SectionTitle>

      <CardBox class="mb-6">
        <SectionTitle>Tạo báo cáo mới</SectionTitle>
        <div class="grid grid-cols-1 gap-4 md:grid-cols-3">
          <FormField label="Loại báo cáo">
            <FormControl
              v-model="reportType"
              :options="[
                { value: 'sales', label: 'Báo cáo doanh thu' },
                { value: 'products', label: 'Báo cáo sản phẩm' },
                { value: 'users', label: 'Báo cáo người dùng' },
                { value: 'inventory', label: 'Báo cáo tồn kho' },
              ]"
            />
          </FormField>
          <FormField label="Từ ngày">
            <FormControl v-model="dateRange.start" type="date" />
          </FormField>
          <FormField label="Đến ngày">
            <FormControl v-model="dateRange.end" type="date" />
          </FormField>
        </div>
        <div class="mt-4">
          <BaseButton
            :icon="mdiChartBar"
            label="Tạo báo cáo"
            color="info"
            @click="generateReport"
          />
        </div>
      </CardBox>

      <CardBox>
        <SectionTitle>Báo cáo đã tạo</SectionTitle>
        <div class="space-y-4">
          <div
            v-for="report in reports"
            :key="report.id"
            class="flex items-center justify-between p-4 rounded-lg bg-gray-50 dark:bg-slate-800"
          >
            <div class="flex-1">
              <h3 class="font-semibold text-gray-800 dark:text-white">{{ report.name }}</h3>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ report.description }}</p>
              <p class="text-xs text-gray-500 dark:text-gray-500 mt-1">
                Tạo lúc: {{ report.generatedAt }}
              </p>
            </div>
            <BaseButton
              :icon="mdiFileExport"
              label="Xuất"
              color="info"
              small
              @click="exportReport(report)"
            />
          </div>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>

