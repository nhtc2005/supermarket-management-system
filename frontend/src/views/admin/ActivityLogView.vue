<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import AdminNavTabs from '@/components/AdminNavTabs.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import {
  mdiMagnify,
  mdiFileExport,
} from '@mdi/js'

const searchQuery = ref('')
const typeFilter = ref('all')
const logs = ref([
  {
    id: 1,
    time: '2025-10-25 14:23:15',
    user: 'Nguyễn Văn Admin',
    action: 'Thêm nhân viên mới',
    type: 'create',
    object: 'Nhân viên #E006',
    status: 'success',
  },
  {
    id: 2,
    time: '2025-10-25 13:45:30',
    user: 'Trần Thị Bán Hàng',
    action: 'Tạo hóa đơn bán hàng',
    type: 'sales',
    object: 'Hóa đơn #HD1234',
    status: 'success',
  },
  {
    id: 3,
    time: '2025-10-25 12:30:20',
    user: 'Lê Văn Kho',
    action: 'Nhập kho',
    type: 'import',
    object: 'Phiếu nhập #PN567',
    status: 'success',
  },
  {
    id: 4,
    time: '2025-10-25 11:15:45',
    user: 'Hoàng Văn Quản Lý',
    action: 'Cập nhật giá sản phẩm',
    type: 'update',
    object: 'Sản phẩm #P001',
    status: 'success',
  },
  {
    id: 5,
    time: '2025-10-25 10:00:10',
    user: 'Nguyễn Văn Admin',
    action: 'Xuất báo cáo doanh thu',
    type: 'report',
    object: 'Báo cáo tháng 10',
    status: 'warning',
  },
  {
    id: 6,
    time: '2025-10-25 09:30:55',
    user: 'Trần Thị Bán Hàng',
    action: 'Hủy hóa đơn',
    type: 'delete',
    object: 'Hóa đơn #HD1233',
    status: 'success',
  },
  {
    id: 7,
    time: '2025-10-25 08:20:30',
    user: 'Lê Văn Kho',
    action: 'Xuất kho',
    type: 'export',
    object: 'Phiếu xuất #PX234',
    status: 'success',
  },
  {
    id: 8,
    time: '2025-10-25 07:10:15',
    user: 'Nguyễn Văn Admin',
    action: 'Đăng nhập hệ thống',
    type: 'login',
    object: 'Tài khoản admin',
    status: 'success',
  },
])

const filteredLogs = computed(() => {
  let filtered = logs.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (log) =>
        log.user.toLowerCase().includes(query) || log.action.toLowerCase().includes(query),
    )
  }

  if (typeFilter.value !== 'all') {
    filtered = filtered.filter((log) => log.type === typeFilter.value)
  }

  return filtered
})

const getTypeLabel = (type) => {
  const labels = {
    create: 'Tạo mới',
    sales: 'Bán hàng',
    import: 'Nhập kho',
    update: 'Cập nhật',
    report: 'Báo cáo',
    delete: 'Xóa',
    export: 'Xuất kho',
    login: 'Đăng nhập',
  }
  return labels[type] || type
}

const getTypeColor = (type) => {
  const colors = {
    create: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
    sales: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200',
    import: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
    update: 'bg-cyan-100 text-cyan-800 dark:bg-cyan-900 dark:text-cyan-200',
    report: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
    delete: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
    export: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
    login: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
  }
  return colors[type] || 'bg-gray-100 text-gray-800'
}

const getStatusLabel = (status) => {
  return status === 'success' ? 'Thành công' : 'Cảnh báo'
}

const getStatusColor = (status) => {
  return status === 'success'
    ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    : 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
}

const exportReport = () => {
  alert('Đang xuất báo cáo...')
}

onMounted(() => {
  // Fetch logs
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

      <CardBox>
        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">Nhật Ký Hoạt Động</h2>
          <p class="text-gray-600 dark:text-gray-400">Theo dõi mọi thao tác trong hệ thống</p>
        </div>

        <div class="flex space-x-4 mb-6">
          <FormField class="flex-1">
            <FormControl
              v-model="searchQuery"
              :icon="mdiMagnify"
              placeholder="Tìm kiếm theo người dùng, hành động..."
            />
          </FormField>
          <FormField>
            <FormControl
              v-model="typeFilter"
              :options="[
                { value: 'all', label: 'Tất cả loại' },
                { value: 'create', label: 'Tạo mới' },
                { value: 'sales', label: 'Bán hàng' },
                { value: 'import', label: 'Nhập kho' },
                { value: 'update', label: 'Cập nhật' },
                { value: 'report', label: 'Báo cáo' },
                { value: 'delete', label: 'Xóa' },
                { value: 'export', label: 'Xuất kho' },
                { value: 'login', label: 'Đăng nhập' },
              ]"
            />
          </FormField>
          <BaseButton :icon="mdiFileExport" label="Xuất báo cáo" color="info" @click="exportReport" />
        </div>

        <CardBox has-table>
          <table>
            <thead>
              <tr>
                <th>Thời gian</th>
                <th>Người dùng</th>
                <th>Hành động</th>
                <th>Loại</th>
                <th>Đối tượng</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in filteredLogs" :key="log.id">
                <td data-label="Thời gian">{{ log.time }}</td>
                <td data-label="Người dùng">{{ log.user }}</td>
                <td data-label="Hành động">{{ log.action }}</td>
                <td data-label="Loại">
                  <span class="px-2 py-1 text-xs rounded-full" :class="getTypeColor(log.type)">
                    {{ getTypeLabel(log.type) }}
                  </span>
                </td>
                <td data-label="Đối tượng">{{ log.object }}</td>
                <td data-label="Trạng thái">
                  <span class="px-2 py-1 text-xs rounded-full" :class="getStatusColor(log.status)">
                    {{ getStatusLabel(log.status) }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </CardBox>

        <div class="mt-4 text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ filteredLogs.length }} kết quả
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>

