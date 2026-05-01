<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { mdiMagnify, mdiPackageVariant, mdiEye } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

/* Search & data */
const searchQuery = ref('')
const batches = ref([])
const loading = ref(false)

const filteredBatches = computed(() => {
  if (!searchQuery.value) return batches.value
  const q = searchQuery.value.toLowerCase()
  return batches.value.filter(
    (b) =>
      b.id?.toString().includes(q) ||
      b.product_name?.toLowerCase().includes(q) ||
      b.barcode?.toLowerCase().includes(q),
  )
})

/* Modal detail */
const modalActive = ref(false)
const selectedBatch = ref(null)
const loadingDetail = ref(false)

/* Auth header */
const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

/* Fetch batch list */
const fetchBatches = async () => {
  loading.value = true
  try {
    const res = await api.get('/batches', {
      headers: getTokenHeader(),
      params: {
        keyword: searchQuery.value || undefined,
        page: 0,
        size: 1000,
      },
    })

    batches.value = res.data.data.content || []
  } finally {
    loading.value = false
  }
}

/* Fetch batch detail */
const fetchBatchDetail = async (id) => {
  loadingDetail.value = true
  selectedBatch.value = null
  try {
    const res = await api.get(`/batches/${id}`, { headers: getTokenHeader() })
    selectedBatch.value = res.data.data
  } finally {
    loadingDetail.value = false
  }
}

/* View only */
const viewBatch = (batch) => {
  modalActive.value = true
  fetchBatchDetail(batch.id)
}

watch(searchQuery, fetchBatches)
onMounted(fetchBatches)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine
        :icon="mdiPackageVariant"
        title="Tra cứu lô hàng"
        main
        :has-button="false"
      />

      <!-- Search -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm lô hàng</h3>
            <p class="text-sm text-gray-600">Tìm theo mã lô hoặc tên sản phẩm</p>
          </div>

          <div class="relative w-full md:w-96">
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
              placeholder="Nhập mã lô, barcode hoặc sản phẩm..."
              class="pl-10"
            />
          </div>
        </div>
      </CardBox>

      <!-- Batch Table -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách lô hàng</h3>
          <p class="mt-1 text-sm text-gray-600">Tìm thấy {{ filteredBatches.length }} lô</p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full table-fixed">
            <thead class="bg-gray-50">
              <tr>
                <th class="w-24 px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã lô
                </th>
                <th class="w-64 px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Sản phẩm
                </th>
                <th class="w-40 px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  NSX
                </th>
                <th class="w-40 px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  HSD
                </th>
                <th class="w-32 px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Tồn kho
                </th>
                <th class="w-24 px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="b in filteredBatches"
                :key="b.id"
                class="transition-colors duration-200 hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">{{ b.id }}</td>
                <td class="px-6 py-4">{{ b.product_name }}</td>
                <td class="px-6 py-4 text-center">{{ b.manufacture }}</td>
                <td class="px-6 py-4 text-center">{{ b.expiry_date }}</td>
                <td class="px-6 py-4 text-center font-semibold text-green-600">
                  {{ b.quantity_available }}
                </td>
                <td class="px-6 py-4 text-center">
                  <button
                    @click="viewBatch(b)"
                    class="rounded-lg bg-blue-100 p-2 text-blue-600 hover:bg-blue-200"
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

              <tr v-if="filteredBatches.length === 0 && !loading">
                <td colspan="6" class="px-6 py-16 text-center text-gray-500">
                  Không có lô hàng nào
                </td>
              </tr>

              <tr v-if="loading">
                <td colspan="6" class="px-6 py-12 text-center">
                  <div
                    class="mx-auto h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
                  ></div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Batch Details Modal -->
      <CardBoxModal v-model="modalActive" title="Chi tiết lô hàng" button-label="Đóng">
        <!-- Loading -->
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div
            class="h-10 w-10 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
        </div>

        <!-- Content -->
        <div v-else-if="selectedBatch" class="space-y-6">
          <!-- ===== HEADER ===== -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">
                {{ selectedBatch.product_name }}
              </h3>
              <p class="mt-1 text-sm text-gray-500">
                Lô #{{ selectedBatch.id }} • Kho {{ selectedBatch.warehouse_name }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiPackageVariant" />
              </svg>
            </div>
          </div>

          <!-- ===== THÔNG TIN LÔ ===== -->
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Thông tin lô hàng
            </h4>

            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Mã lô:</span>
                  <span>{{ selectedBatch.id }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Nhà sản xuất:</span>
                  <span>{{ selectedBatch.manufacture }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Nhà cung cấp:</span>
                  <span>{{ selectedBatch.supplier }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Kho:</span>
                  <span>{{ selectedBatch.warehouse_name }}</span>
                </div>
              </div>

              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Tổng số lượng:</span>
                  <span class="font-semibold">
                    {{ selectedBatch.quantity_total }}
                  </span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Còn khả dụng:</span>
                  <span class="font-semibold text-green-600">
                    {{ selectedBatch.quantity_available }}
                  </span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Ngày nhập:</span>
                  <span>{{ selectedBatch.create_date }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Hạn sử dụng:</span>
                  <span class="font-medium text-red-600">
                    {{ selectedBatch.expiry_date }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- ===== TRẠNG THÁI ===== -->
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Trạng thái lô
            </h4>

            <div class="flex items-center gap-3 rounded-lg border px-4 py-3">
              <div
                class="h-3 w-3 rounded-full"
                :class="selectedBatch.is_expired ? 'bg-red-500' : 'bg-green-500'"
              ></div>
              <span class="text-sm font-medium">
                {{ selectedBatch.is_expired ? 'Đã hết hạn' : 'Còn hạn sử dụng' }}
              </span>
            </div>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
