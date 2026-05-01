<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiAccount, mdiMagnify, mdiEye } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

/* State */
const searchQuery = ref('')
const customers = ref([])
const modalActive = ref(false)
const selectedCustomer = ref(null)
const loadingDetail = ref(false)

/* Fetch customers */
const fetchCustomers = async () => {
  try {
    const accessToken = localStorage.getItem('accessToken')
    const res = await api.get('/customers', { headers: { Authorization: `Bearer ${accessToken}` } })
    customers.value = res.data.data.content.map((c) => ({
      id: `${c.id}`,
      name: `${c.first_name} ${c.last_name}`,
      email: c.email,
      phone: c.phone,
      address: c.address,
      points: c.loyalty_points,
      totalOrders: c.total_orders,
      totalSpent: c.totalSpent,
    }))
  } catch (err) {
    console.warn('Không thể load khách hàng từ API', err)
  }
}

/* Fetch customer details */
const fetchCustomerDetail = async (id) => {
  loadingDetail.value = true
  selectedCustomer.value = null
  try {
    const accessToken = localStorage.getItem('accessToken')
    const res = await api.get(`/customers/${id}`, {
      headers: { Authorization: `Bearer ${accessToken}` },
    })
    const c = res.data.data
    selectedCustomer.value = {
      id: `${c.id}`,
      name: `${c.first_name} ${c.last_name}`,
      email: c.email,
      phone: c.phone,
      address: c.address,
      points: c.loyalty_points,
      totalOrders: c.total_orders,
      totalSpent: c.total_spent,
      createdAt: c.created_at,
    }
  } catch (err) {
    console.warn('Không thể load chi tiết, dùng dữ liệu mẫu', err)
    selectedCustomer.value = customers.value.find((c) => c.id === id)
  } finally {
    loadingDetail.value = false
  }
}

/* Actions */
const viewCustomer = (customer) => {
  modalActive.value = true
  fetchCustomerDetail(customer.id)
}

/* Computed */
const filteredCustomers = computed(() => {
  if (!searchQuery.value) return customers.value
  return customers.value.filter(
    (customer) =>
      customer.id?.toString().includes(searchQuery.value) ||
      customer.name?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      customer.email?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      customer.phone?.toLowerCase().includes(searchQuery.value.toLowerCase()),
  )
})

/* Init */
onMounted(fetchCustomers)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Title -->
      <SectionTitleLine :icon="mdiAccount" title="Tra cứu khách hàng" main :has-button="false" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm khách hàng</h3>
            <p class="text-sm text-gray-600">
              Tìm theo mã khách hàng, tên, email hoặc số điện thoại
            </p>
          </div>
          <div class="w-full md:w-96">
            <div class="relative">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-gray-400"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path :d="mdiMagnify" />
                </svg>
              </div>
              <FormControl
                v-model="searchQuery"
                placeholder="Nhập ID, tên, email hoặc điện thoại..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Results Table -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách khách hàng</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredCustomers.length }} khách hàng
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã số
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Họ và tên
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Email
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Số điện thoại
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Địa chỉ
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody class="divide-y divide-gray-200 bg-white">
              <!-- Customer rows -->
              <tr
                v-for="c in filteredCustomers"
                :key="c.id"
                class="transition-colors duration-200 hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">{{ c.id }}</td>
                <td class="px-6 py-4">{{ c.name }}</td>
                <td class="px-6 py-4">{{ c.email }}</td>
                <td class="px-6 py-4 text-center">{{ c.phone }}</td>
                <td class="px-6 py-4">{{ c.address }}</td>
                <td class="px-6 py-4 text-center">
                  <button
                    @click.stop="viewCustomer(c)"
                    class="cursor-pointer rounded-lg bg-blue-100 p-2 text-blue-600 transition-colors hover:bg-blue-200"
                    title="Xem chi tiết"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-4 w-4"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path :d="mdiEye" />
                    </svg>
                  </button>
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredCustomers.length === 0">
                <td colspan="6" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <h3 class="mt-4 text-lg font-medium text-gray-900">
                      Không tìm thấy khách hàng
                    </h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery"
                        >Không có khách hàng nào phù hợp với "{{ searchQuery }}"</span
                      >
                      <span v-else>Không có khách hàng nào trong hệ thống</span>
                    </p>
                  </div>
                </td>
              </tr>

              <!-- Loading state -->
              <tr v-if="loading">
                <td colspan="7" class="px-6 py-12 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <div
                      class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
                    ></div>
                    <p class="mt-4 text-sm text-gray-600">Đang tải danh sách ưu đãi...</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Customer Detail Modal -->
      <CardBoxModal v-model="modalActive" title="Chi tiết khách hàng" button-label="Đóng">
        <div v-if="selectedCustomer" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ selectedCustomer.name }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                Mã số: {{ selectedCustomer.id }} • Ngày tạo tài khoản:
                {{
                  selectedCustomer.createdAt
                    ? new Date(selectedCustomer.createdAt).toLocaleString()
                    : 'Lỗi hiển thị'
                }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiAccount" />
              </svg>
            </div>
          </div>

          <!-- Grid layout -->
          <div class="grid gap-6 sm:grid-cols-2">
            <!-- Basic info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin cơ bản
              </h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Email</span
                  ><span class="text-sm text-gray-900">{{ selectedCustomer.email }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Số điện thoại</span
                  ><span class="text-sm text-gray-900">{{ selectedCustomer.phone }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Địa chỉ</span
                  ><span class="text-sm text-gray-900">{{ selectedCustomer.address }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Điểm thưởng</span
                  ><span class="text-sm text-gray-900">{{ selectedCustomer.points }}</span>
                </div>
              </div>
            </div>

            <!-- Transaction info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Giao dịch & chi tiêu
              </h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Tổng đơn hàng</span
                  ><span class="text-sm text-gray-900">{{ selectedCustomer.totalOrders }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Tổng chi tiêu</span
                  ><span class="text-sm text-gray-900">{{ selectedCustomer.totalSpent }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Loading -->
        <div v-else class="flex flex-col items-center justify-center py-12">
          <div
            class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết...</p>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
