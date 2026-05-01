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
const products = ref([])
const loading = ref(false)

const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value
  const q = searchQuery.value.toLowerCase()
  return products.value.filter(
    (p) =>
      p.id?.toString().includes(q) ||
      p.sku?.toLowerCase().includes(q) ||
      p.name?.toLowerCase().includes(q) ||
      p.barcode?.toLowerCase().includes(q),
  )
})

/* Modal detail */
const modalActive = ref(false)
const selectedProduct = ref(null)
const loadingDetail = ref(false)

/* Auth header */
const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

/* Fetch product list */
const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await api.get('/products', {
      headers: getTokenHeader(),
      params: {
        name: searchQuery.value || undefined,
        sku: searchQuery.value || undefined,
        page: 0,
        size: 1000,
        sortBy: 'id',
        sortDirection: 'ASC',
      },
    })

    products.value = (res.data.data?.content || []).map((p) => ({
      id: p.id,
      name: p.name,
      sku: p.sku,
      barcode: p.barcode,
      stock: p.available_quantity || 0,
      price: p.price || 0,
      description: p.description || '',
      variants: p.variants || [],
    }))
  } finally {
    loading.value = false
  }
}

/* Fetch product detail */
const fetchProductDetail = async (id) => {
  loadingDetail.value = true
  selectedProduct.value = null
  try {
    const res = await api.get(`/products/${id}`, { headers: getTokenHeader() })
    const data = res.data.data || {}
    const resVariant = await api.get(`/products/${id}/variants`, {
      headers: getTokenHeader(),
    })

    data.variants = resVariant.data.data || []
    selectedProduct.value = data
  } finally {
    loadingDetail.value = false
  }
}

/* View only */
const viewProduct = (product) => {
  modalActive.value = true
  fetchProductDetail(product.id)
}

// watch(searchQuery, fetchProducts)
onMounted(fetchProducts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Header + Add Product -->
      <SectionTitleLine
        :icon="mdiPackageVariant"
        title="Tra cứu hàng hóa"
        main
        :has-button="false"
      />

      <!-- Search -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm hàng hóa</h3>
            <p class="text-sm text-gray-600">Tìm kiếm theo mã số, mã vạch, SKU hoặc tên hàng hóa</p>
          </div>
          <div class="flex gap-2 md:items-center md:gap-4">
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
                placeholder="Nhập SKU, barcode hoặc tên sản phẩm..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Product Table -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách sản phẩm</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ products.length }} sản phẩm
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
                  SKU
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Tên sản phẩm
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã vạch
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Tồn kho
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="p in filteredProducts"
                :key="p.id"
                class="transition-colors duration-200 hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">{{ p.id }}</td>
                <td class="px-6 py-4 text-left">{{ p.sku }}</td>
                <td class="px-6 py-4 text-left">{{ p.name }}</td>
                <td class="px-6 py-4 text-center">{{ p.barcode }}</td>
                <td class="px-6 py-4 text-center font-semibold text-green-600">{{ p.stock }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center justify-center gap-2">
                    <button
                      @click="viewProduct(p)"
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
                  </div>
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredProducts.length === 0 && !loading">
                <td colspan="6" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không tìm thấy sản phẩm</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery"
                        >Không có sản phẩm nào phù hợp với "{{ searchQuery }}"</span
                      >
                      <span v-else>Không có sản phẩm nào trong hệ thống</span>
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

      <!-- Product Details Modal -->
      <CardBoxModal v-model="modalActive" title="Chi tiết sản phẩm" button-label="Đóng">
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div
            class="h-10 w-10 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
        </div>

        <div v-else-if="selectedProduct" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ selectedProduct.name }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ selectedProduct.sku }} • {{ selectedProduct.barcode }}
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

          <!-- Product Details -->
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Thông tin sản phẩm
            </h4>
            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">SKU:</span><span>{{ selectedProduct.sku }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-gray-700">Mã vạch:</span
                  ><span>{{ selectedProduct.barcode }}</span>
                </div>
                <div v-if="selectedProduct.description" class="flex items-start gap-4">
                  <span class="flex-shrink-0 text-gray-700">Mô tả:</span>
                  <span class="flex-1 text-right break-words text-gray-800">
                    {{ selectedProduct.description }}
                  </span>
                </div>
              </div>
              <div class="space-y-2">
                <div class="flex justify-between">
                  <span class="text-gray-700">Tồn kho:</span
                  ><span class="font-semibold text-green-600">{{
                    selectedProduct.available_quantity
                  }}</span>
                </div>
                <div v-if="selectedProduct.price" class="flex justify-between">
                  <span class="text-gray-700">Giá:</span
                  ><span
                    >{{ new Intl.NumberFormat('vi-VN').format(selectedProduct.price) }} VND</span
                  >
                </div>
              </div>
            </div>
          </div>

          <!-- Variants -->
          <div class="space-y-4">
            <div class="flex items-center justify-between rounded-t-md px-2 py-3">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Loại / Variants
              </h4>
            </div>

            <table class="w-full rounded border border-gray-300 text-sm">
              <thead class="bg-gray-100">
                <tr>
                  <th class="w-20 border border-gray-300 px-1 py-2 text-center">Mã số</th>
                  <th class="w-64 border border-gray-300 px-1 py-2 text-center">Thuộc tính</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="v in selectedProduct.variants" :key="v.id">
                  <td class="w-20 border border-gray-300 p-2 text-center">
                    {{ v.id }}
                  </td>

                  <td class="w-64 border border-gray-300 p-2 text-center break-words">
                    <div v-for="(val, key) in v.variant_attributes" :key="key">
                      <strong>{{ key }}:</strong> {{ val }}
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
