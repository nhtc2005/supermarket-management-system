<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitle from '@/components/SectionTitle.vue'
import CardBoxWidget from '@/components/CardBoxWidget.vue'
import CardBox from '@/components/CardBox.vue'
import BaseIcon from '@/components/BaseIcon.vue'

const route = useRoute()
import {
  mdiPackageVariant,
  mdiAccountGroup,
  mdiStore,
  mdiShieldAlert,
  mdiAccountPlus,
  mdiTag,
  mdiShieldAccount,
  mdiCog,
  mdiHome,
} from '@mdi/js'

const stats = ref({
  products: 2547,
  employees: 84,
  stores: 32,
  warnings: 7,
})

const recentActivities = ref([
  {
    id: 1,
    icon: mdiAccountPlus,
    iconColor: 'text-green-500',
    title: 'Thêm nhân viên mới',
    user: 'Admin',
    time: '10 phút',
  },
  {
    id: 2,
    icon: mdiTag,
    iconColor: 'text-blue-500',
    title: 'Cập nhật giá sản phẩm',
    user: 'NV Kho',
    time: '1 giờ',
  },
  {
    id: 3,
    icon: mdiShieldAccount,
    iconColor: 'text-purple-500',
    title: 'Phân quyền người dùng',
    user: 'Admin',
    time: '2 giờ',
  },
  {
    id: 4,
    icon: mdiCog,
    iconColor: 'text-orange-500',
    title: 'Thay đổi cài đặt hệ thống',
    user: 'Admin',
    time: '3 giờ',
  },
])

const fetchDashboardData = async () => {
  try {
    // Giả lập dữ liệu - thay thế bằng API thực tế
  } catch (error) {
    console.error('Lỗi tải dữ liệu dashboard:', error)
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">Quản Trị Hệ Thống</h1>
        <p class="text-gray-600 dark:text-gray-400">Chào mừng, Nguyễn Văn Admin</p>
      </div>

      <!-- Navigation Tabs -->
      <AdminNavTabs />

      <!-- Widget Cards -->
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-2 xl:grid-cols-4 mb-6">
        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Sản phẩm</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">{{ stats.products.toLocaleString() }}</h1>
              <p class="text-sm text-green-600 dark:text-green-400 mt-2">+12% tháng này</p>
            </div>
            <BaseIcon :path="mdiPackageVariant" size="48" class="text-blue-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Nhân viên</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">{{ stats.employees }}</h1>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">3 vai trò khác nhau</p>
            </div>
            <BaseIcon :path="mdiAccountGroup" size="48" class="text-green-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Gian hàng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">{{ stats.stores }}</h1>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">28 hoạt động</p>
            </div>
            <BaseIcon :path="mdiStore" size="48" class="text-purple-500" />
          </div>
        </CardBox>

        <CardBox class="relative bg-orange-50 dark:bg-orange-900/20">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-orange-600 dark:text-orange-400 text-sm mb-2">Cảnh báo</h3>
              <h1 class="text-3xl font-bold text-orange-600 dark:text-orange-400">{{ stats.warnings }}</h1>
              <p class="text-sm text-orange-600 dark:text-orange-400 mt-2">Cần xem xét</p>
            </div>
            <BaseIcon :path="mdiShieldAlert" size="48" class="text-orange-500" />
          </div>
        </CardBox>
      </div>

      <!-- Recent Activities -->
      <CardBox>
        <SectionTitle>Hoạt động gần đây</SectionTitle>
        <div class="space-y-4">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="flex items-center justify-between p-4 rounded-lg bg-gray-50 dark:bg-slate-800"
          >
            <div class="flex items-center space-x-4">
              <div class="p-2 rounded-lg bg-white dark:bg-slate-700">
                <BaseIcon :path="activity.icon" size="24" :class="activity.iconColor" />
              </div>
              <div>
                <p class="font-medium text-gray-800 dark:text-white">{{ activity.title }}</p>
                <p class="text-sm text-gray-600 dark:text-gray-400">{{ activity.user }}</p>
              </div>
            </div>
            <span class="text-sm text-gray-500 dark:text-gray-400">{{ activity.time }}</span>
          </div>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
