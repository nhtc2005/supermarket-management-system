<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiClipboardCheck, mdiMagnify, mdiEye } from '@mdi/js'

import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios.js'

/* ================= STATE ================= */
const searchQuery = ref('')
const filterType = ref('')
const receipts = ref([])
const modalActive = ref(false)
const selectedReceipt = ref(null)
const loadingDetail = ref(false)
const loading = ref(false)

/* ================= TOKEN ================= */
const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}`,
})

/* ================= LOAD IMPORT + EXPORT ================= */
const fetchAllReceipts = async () => {
  loading.value = true
  try {
    const [importRes, exportRes] = await Promise.all([
      api.get('/warehouse-operations/imports', { headers: getTokenHeader() }),
      api.get('/warehouse-operations/exports', { headers: getTokenHeader() }),
    ])

    const imports = (importRes.data.data || []).map((r) => ({
      id: r.details_id,
      type: 'import',
      warehouse: r.warehouse_name,
      employee: r.employee_name,
      date: r.import_date,
      totalQuantity: r.total_quantity,
      items: r.items || [],
    }))

    const exports = (exportRes.data.data || []).map((r) => ({
      id: r.details_id,
      type: 'export',
      warehouse: r.warehouse_name,
      employee: r.employee_name,
      date: r.export_date,
      totalQuantity: r.total_quantity,
      items: r.items || [],
    }))

    receipts.value = [...imports, ...exports]
  } catch (e) {
    console.error('Lỗi tải phiếu:', e)
    receipts.value = []
  } finally {
    loading.value = false
  }
}

/* ================= FILTER ================= */
const filteredReceipts = computed(() => {
  return receipts.value.filter((r) => {
    const q = searchQuery.value.toLowerCase()

    const matchSearch =
      r.id.toString().toLowerCase().includes(q) ||
      r.employee.toLowerCase().includes(q) ||
      r.warehouse.toLowerCase().includes(q)

    const matchType = !filterType.value || r.type === filterType.value

    return matchSearch && matchType
  })
})

/* ================= FETCH BATCH DETAIL ================= */
const fetchBatchDetail = async (batchId) => {
  const res = await api.get(`/batches/${batchId}`, {
    headers: getTokenHeader(),
  })
  return res.data.data
}

/* ================= VIEW DETAIL ================= */
const viewReceipt = async (receipt) => {
  modalActive.value = true
  loadingDetail.value = true

  try {
    const itemsWithBatch = []

    for (const item of receipt.items) {
      if (item.batch_id) {
        const batch = await fetchBatchDetail(item.batch_id)

        itemsWithBatch.push({
          ...item,
          product_name: batch.product_name,
          manufacture: batch.manufacture,
          expiry_date: batch.expiry_date,
          warehouse: batch.warehouse_name,
        })
      } else {
        itemsWithBatch.push(item)
      }
    }

    selectedReceipt.value = {
      ...receipt,
      items: itemsWithBatch,
    }
  } catch (e) {
    console.error('Lỗi chi tiết phiếu:', e)
    selectedReceipt.value = null
  } finally {
    loadingDetail.value = false
  }
}

onMounted(fetchAllReceipts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine
        :icon="mdiClipboardCheck"
        title="Phiếu Nhập / Xuất Kho"
        main
        :has-button="false"
      />

      <!-- SEARCH + FILTER -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm phiếu kho</h3>
            <p class="text-sm text-gray-600">Tìm theo mã phiếu, nhân viên hoặc kho</p>
          </div>

          <div class="flex w-full flex-col gap-3 md:w-auto md:flex-row md:items-center md:gap-4">
            <select
              v-model="filterType"
              class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm md:w-40"
            >
              <option value="">Tất cả</option>
              <option value="import">Phiếu nhập</option>
              <option value="export">Phiếu xuất</option>
            </select>

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
                placeholder="Nhập mã phiếu, nhân viên hoặc kho..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- TABLE -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách phiếu</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredReceipts.length }} phiếu
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã số phiếu
                </th>
                <th class="w-28 px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Loại
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Kho
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Nhân viên
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thời gian
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="r in filteredReceipts"
                :key="r.id"
                class="transition-colors duration-200 hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">{{ r.id }}</td>
                <td class="w-28 px-6 py-4 text-center">
                  {{ r.type === 'import' ? 'Nhập' : 'Xuất' }}
                </td>
                <td class="px-6 py-4 text-center">{{ r.warehouse }}</td>
                <td class="px-6 py-4 text-center">{{ r.employee }}</td>
                <td class="px-6 py-4 text-center">
                  {{ new Date(r.date).toLocaleString('vi-VN') }}
                </td>
                <td class="px-6 py-4 text-center">
                  <button
                    @click="viewReceipt(r)"
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

              <tr v-if="filteredReceipts.length === 0 && !loading">
                <td colspan="6" class="px-6 py-16 text-center text-gray-500">Không có dữ liệu</td>
              </tr>

              <tr v-if="loading">
                <td colspan="6" class="px-6 py-12 text-center">Đang tải...</td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- MODAL -->
      <CardBoxModal v-model="modalActive" title="Chi tiết phiếu" button-label="Đóng">
        <!-- Loading -->
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div
            class="h-10 w-10 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
        </div>

        <!-- Content -->
        <div v-else-if="selectedReceipt" class="space-y-6">
          <!-- HEADER -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">
                {{ selectedReceipt.type === 'import' ? 'Phiếu nhập kho' : 'Phiếu xuất kho' }}
              </h3>
              <p class="mt-1 text-sm text-gray-500">
                #{{ selectedReceipt.id }} •
                {{ new Date(selectedReceipt.date).toLocaleString('vi-VN') }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiClipboardCheck" />
              </svg>
            </div>
          </div>

          <!-- THÔNG TIN CHUNG -->
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Thông tin phiếu
            </h4>

            <div class="grid gap-4 text-sm sm:grid-cols-2">
              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Mã phiếu:</span>
                  <span>{{ selectedReceipt.id }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Loại:</span>
                  <span>
                    {{ selectedReceipt.type === 'import' ? 'Nhập kho' : 'Xuất kho' }}
                  </span>
                </div>
                <div class="flex justify-between sm:col-span-2">
                  <span class="text-gray-700">Thời gian:</span>
                  <span>{{ new Date(selectedReceipt.date).toLocaleString('vi-VN') }}</span>
                </div>
              </div>

              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Kho:</span>
                  <span>{{ selectedReceipt.warehouse }}</span>
                </div>

                <div class="flex justify-between">
                  <span class="text-gray-700">Nhân viên:</span>
                  <span>{{ selectedReceipt.employee }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- DANH SÁCH SẢN PHẨM
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Danh sách sản phẩm
            </h4>

            <table class="w-full rounded border border-gray-300 text-sm">
              <thead class="bg-gray-100">
                <tr>
                  <th class="border border-gray-300 px-2 py-2 text-center">Sản phẩm</th>
                  <th class="border border-gray-300 px-2 py-2 text-center">Lô</th>
                  <th class="border border-gray-300 px-2 py-2 text-center">Số lượng</th>
                  <th class="border border-gray-300 px-2 py-2 text-center">NSX</th>
                  <th class="border border-gray-300 px-2 py-2 text-center">HSD</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="(i, idx) in selectedReceipt.items" :key="idx" class="hover:bg-gray-50">
                  <td class="border border-gray-300 px-2 py-1">
                    {{ i.product_name }}
                  </td>
                  <td class="border border-gray-300 px-2 py-1 text-center">
                    {{ i.batch_id }}
                  </td>
                  <td class="border border-gray-300 px-2 py-1 text-center font-semibold">
                    {{ i.quantity }}
                  </td>
                  <td class="border border-gray-300 px-2 py-1">
                    {{ i.manufacture }}
                  </td>
                  <td class="border border-gray-300 px-2 py-1">
                    {{ i.expiry_date }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div> -->
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
