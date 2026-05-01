<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiWarehouse, mdiMagnify, mdiFilterVariant, mdiPackageVariant } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLine.vue'
import FormControl from '@/components/FormControl.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { api } from '@/plugins/axios'

/* =========================
   STATE
========================= */
const products = ref([])
const batches = ref([])
const warehouses = ref([])
const filteredLots = ref([])

const searchQuery = ref('')
const showFilters = ref(false)

const filterStatus = ref('all')
const filterWarehouse = ref('all')

const modalActive = ref(false)
const selectedLot = ref(null)

/* =========================
   OPTIONS
========================= */
const statusOptions = [
  { value: 'all', label: 'Tất cả trạng thái' },
  { value: 'normal', label: 'Bình thường' },
  { value: 'low_stock', label: 'Tồn kho thấp' },
  { value: 'expiring', label: 'Có lô hết hạn' },
]

/* =========================
   AUTH
========================= */
const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

/* =========================
   FETCH WAREHOUSES ✅
========================= */
const fetchWarehouses = async () => {
  const res = await api.get('/warehouses', {
    headers: getTokenHeader(),
    params: { page: 0, size: 1000 },
  })
  warehouses.value = res.data.data.content || []
}

/* =========================
   FETCH PRODUCTS ✅
========================= */
const fetchProducts = async () => {
  const res = await api.get('/products', {
    headers: getTokenHeader(),
    params: { page: 0, size: 1000 },
  })
  products.value = res.data.data.content || []
}

/* =========================
   FETCH BATCHES ✅
========================= */
const fetchBatches = async () => {
  const res = await api.get('/batches', {
    headers: getTokenHeader(),
    params: { page: 0, size: 1000 },
  })
  batches.value = res.data.data.content || []
}

/* =========================
   MERGE PRODUCTS + BATCHES ✅
========================= */
const mergeData = () => {
  const map = {}

  products.value.forEach((p) => {
    map[p.id] = {
      productId: p.id,
      product: p.name,
      totalQuantity: p.available_quantity,
      hasExpired: false,
      warehouses: new Set(),
      batches: [],
      status: p.available_quantity === 0 ? 'low_stock' : 'normal',
    }
  })

  batches.value.forEach((b) => {
    if (!map[b.product_id]) return

    map[b.product_id].batches.push(b)
    map[b.product_id].warehouses.add(b.warehouse_name)

    if (b.is_expired && b.quantity_available > 0) {
      map[b.product_id].hasExpired = true
      map[b.product_id].status = 'expiring'
    }
  })

  products.value = Object.values(map).map((p) => ({
    ...p,
    warehouses: Array.from(p.warehouses),
  }))

  filterLots()
}

/* =========================
   FILTER
========================= */
const filterLots = () => {
  let results = products.value

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    results = results.filter(
      (p) => p.productId.toString().includes(q) || p.product.toLowerCase().includes(q),
    )
  }

  if (filterWarehouse.value !== 'all') {
    results = results.filter((p) => p.warehouses.includes(filterWarehouse.value))
  }

  if (filterStatus.value !== 'all') {
    results = results.filter((p) => p.status === filterStatus.value)
  }

  filteredLots.value = results
}

/* =========================
   STATUS UI
========================= */
const getWarning = (p) => {
  if (p.status === 'low_stock') return 'Tồn kho thấp'
  if (p.status === 'expiring') return 'Có lô hết hạn'
  return 'Bình thường'
}

const getWarningClass = (p) => {
  if (p.status === 'normal') return 'status-normal'
  if (p.status === 'low_stock') return 'status-warning'
  return 'status-danger'
}

const getBatchStatus = (b) => {
  if (b.is_expired) return 'Hết hạn'
  if (b.quantity_available < 10) return 'Tồn kho thấp'
  return 'Bình thường'
}

const getBatchStatusClass = (b) => {
  if (b.is_expired) return 'status-danger'
  if (b.quantity_available < 10) return 'status-warning'
  return 'status-normal'
}

/* =========================
   VIEW
========================= */
const viewLot = (lot) => {
  selectedLot.value = lot
  modalActive.value = true
}

/* =========================
   COMPUTED
========================= */
const activeFiltersCount = computed(() => {
  let count = 0
  if (filterStatus.value !== 'all') count++
  if (filterWarehouse.value !== 'all') count++
  return count
})

/* =========================
   MOUNT ✅
========================= */
onMounted(async () => {
  await fetchWarehouses()
  await fetchProducts()
  await fetchBatches()
  mergeData()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton
        :icon="mdiWarehouse"
        title="Quản lý kho"
        main
        :has-button="false"
      />

      <!-- SEARCH + FILTER -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <!-- Left content -->
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm tồn kho theo sản phẩm</h3>
            <p class="text-sm text-gray-600">Tìm theo mã sản phẩm hoặc tên sản phẩm</p>
          </div>

          <!-- Right actions -->
          <div class="flex flex-col gap-2 md:flex-row md:items-center md:gap-4">
            <!-- Search -->
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
                @input="filterLots"
                placeholder="Nhập mã hoặc tên sản phẩm..."
                class="pl-10"
              />
            </div>

            <!-- Filter Button -->
            <button
              class="flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm transition hover:bg-gray-50"
              @click="showFilters = !showFilters"
            >
              <BaseIcon :path="mdiFilterVariant" class="h-4 w-4" />
              Bộ lọc

              <span
                v-if="activeFiltersCount"
                class="ml-1 rounded-full bg-red-500 px-2 text-xs text-white"
              >
                {{ activeFiltersCount }}
              </span>
            </button>
          </div>
        </div>

        <!-- FILTER PANEL -->
        <div
          v-if="showFilters"
          class="mt-4 grid grid-cols-1 gap-4 border-t border-gray-200 pt-4 md:grid-cols-2"
        >
          <!-- Trạng thái -->
          <div>
            <label class="text-sm font-medium text-gray-700">Trạng thái</label>
            <select
              v-model="filterStatus"
              @change="filterLots"
              class="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-sm"
            >
              <option v-for="s in statusOptions" :key="s.value" :value="s.value">
                {{ s.label }}
              </option>
            </select>
          </div>

          <!-- Kho -->
          <div>
            <label class="text-sm font-medium text-gray-700">Kho</label>
            <select
              v-model="filterWarehouse"
              @change="filterLots"
              class="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-sm"
            >
              <option value="all">Tất cả kho</option>
              <option v-for="w in warehouses" :key="w.id" :value="w.name">
                {{ w.name }}
              </option>
            </select>
          </div>
        </div>
      </CardBox>

      <!-- Inventory Table -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
        <!-- Header -->
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách tồn kho theo sản phẩm</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredLots.length }} sản phẩm
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã SP
                </th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Sản phẩm
                </th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Kho</th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Tổng SL
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Trạng thái
                </th>
              </tr>
            </thead>

            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="p in filteredLots"
                :key="p.productId"
                @click="viewLot(p)"
                class="cursor-pointer transition-colors duration-200 hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">
                  {{ p.productId }}
                </td>

                <td class="px-6 py-4 text-left font-medium text-gray-900">
                  {{ p.product }}
                </td>

                <td class="px-6 py-4 text-left text-gray-700">
                  {{ p.warehouses.join(', ') }}
                </td>

                <td class="px-6 py-4 text-center font-semibold text-green-600">
                  {{ p.totalQuantity }}
                </td>

                <td class="px-6 py-4 text-center">
                  <span :class="['status-badge', getWarningClass(p)]">
                    {{ getWarning(p) }}
                  </span>
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredLots.length === 0">
                <td colspan="5" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không có dữ liệu</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery">
                        Không có sản phẩm nào phù hợp với "{{ searchQuery }}"
                      </span>
                      <span v-else>Hiện chưa có dữ liệu tồn kho</span>
                    </p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- MODAL -->
      <CardBoxModal v-model="modalActive" title="Chi tiết sản phẩm" button-label="Đóng">
        <!-- Loading -->
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div
            class="h-10 w-10 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
        </div>

        <!-- Content -->
        <div v-else-if="selectedLot" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">
                {{ selectedLot.product }}
              </h3>
              <p class="mt-1 text-sm text-gray-500">Mã SP: {{ selectedLot.productId }}</p>
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

          <!-- Product Info -->
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Thông tin sản phẩm
            </h4>

            <div class="grid gap-4 sm:grid-cols-2">
              <!-- Left -->
              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Mã sản phẩm:</span>
                  <span>{{ selectedLot.productId }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Tên sản phẩm:</span>
                  <span class="text-right font-medium">{{ selectedLot.product }}</span>
                </div>

                <div class="flex items-start gap-4">
                  <span class="flex-shrink-0 text-gray-700">Kho:</span>
                  <span class="flex-1 text-right break-words text-gray-800">
                    {{ selectedLot.warehouses.join(', ') }}
                  </span>
                </div>
              </div>

              <!-- Right -->
              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Tổng tồn:</span>
                  <span class="font-semibold text-green-600">
                    {{ selectedLot.totalQuantity }}
                  </span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Trạng thái:</span>
                  <span class="status-badge" :class="getWarningClass(selectedLot)">
                    {{ getWarning(selectedLot) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Batch List -->
          <div class="space-y-4">
            <div class="flex items-center justify-between rounded-t-md px-2 py-3">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Danh sách lô hàng
              </h4>
            </div>

            <table class="w-full rounded border border-gray-300 text-sm">
              <thead class="bg-gray-100">
                <tr>
                  <th class="border border-gray-300 p-2 text-center">Mã lô</th>
                  <th class="border border-gray-300 p-2 text-center">Kho</th>
                  <th class="border border-gray-300 p-2 text-center">SL khả dụng</th>
                  <th class="border border-gray-300 p-2 text-center">Hạn sử dụng</th>
                  <th class="border border-gray-300 p-2 text-center">Trạng thái</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="b in selectedLot.batches" :key="b.id">
                  <td class="border border-gray-300 p-2 text-center">{{ b.id }}</td>

                  <td class="border border-gray-300 p-2 text-center">
                    {{ b.warehouse_name }}
                  </td>

                  <td class="border border-gray-300 p-2 text-center font-semibold">
                    {{ b.quantity_available }}
                  </td>

                  <td class="border border-gray-300 p-2 text-center">
                    {{ b.expiry_date }}
                  </td>

                  <td class="border border-gray-300 p-2 text-center">
                    <span class="status-badge" :class="getBatchStatusClass(b)">
                      {{ getBatchStatus(b) }}
                    </span>
                  </td>
                </tr>

                <tr v-if="!selectedLot.batches.length">
                  <td colspan="5" class="py-6 text-center text-gray-500">Không có lô hàng</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
.table-wrapper {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}
.inventory-table {
  width: 100%;
  border-collapse: collapse;
}
.inventory-table th,
.inventory-table td {
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}
.table-row:hover {
  background: #f9fafb;
  cursor: pointer;
}
.status-badge {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}
.status-normal {
  background: #dcfce7;
  color: #16a34a;
}
.status-warning {
  background: #fef3c7;
  color: #d97706;
}
.status-danger {
  background: #fee2e2;
  color: #dc2626;
}
</style>
