<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'
import { mdiMagnify } from '@mdi/js'
import { api } from '@/plugins/axios.js'

const searchQuery = ref('')
const products = ref([])
const loading = ref(false)

const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value
  const query = searchQuery.value.toLowerCase()
  return products.value.filter(
    (p) =>
      p.name.toLowerCase().includes(query) ||
      p.code.toLowerCase().includes(query) ||
      (p.barcode && p.barcode.includes(query)),
  )
})

const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await api.get('/products', {
      headers: getTokenHeader(),
      params: { page: 0, size: 1000 },
    })
    const data = res.data?.success ? res.data.data.content : []
    products.value = data.map((p) => ({
      id: p.id,
      name: p.name,
      code: p.sku || p.barcode || `P${String(p.id).padStart(3, '0')}`,
      price: p.price || 0,
      barcode: p.barcode || '',
    }))
  } catch (err) {
    console.error('Lỗi tải sản phẩm:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="mb-2 text-3xl font-bold text-gray-800 dark:text-white">Dashboard Bán Hàng</h1>
        <p class="text-gray-600 dark:text-gray-400">Thực hiện giao dịch và tra cứu sản phẩm</p>
      </div>

      <SalesNavTabs />

      <CardBox>
        <div class="mb-6">
          <h2 class="mb-2 text-2xl font-bold text-gray-800 dark:text-white">Tra Cứu Sản Phẩm</h2>
          <p class="text-gray-600 dark:text-gray-400">
            Tìm kiếm thông tin sản phẩm, giá và mã sản phẩm
          </p>
        </div>

        <FormField class="mb-6">
          <FormControl
            v-model="searchQuery"
            :icon="mdiMagnify"
            placeholder="Tìm kiếm theo tên, mã sản phẩm, hoặc mã vạch..."
          />
        </FormField>

        <div v-if="loading" class="py-12 text-center text-gray-500 dark:text-gray-400">
          Đang tải sản phẩm...
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="product in filteredProducts"
            :key="product.id"
            class="rounded-lg border border-gray-200 bg-white p-4 dark:border-gray-700 dark:bg-slate-800"
          >
            <div class="mb-3 flex items-start justify-between">
              <div class="flex-1">
                <h3 class="mb-2 text-lg font-semibold text-gray-800 dark:text-white">
                  {{ product.name }}
                </h3>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >SKU: {{ product.code }}</span
                >
              </div>
              <div class="text-right">
                <p class="text-lg font-bold text-blue-600 dark:text-blue-400">
                  {{ product.price.toLocaleString('vi-VN') }}₫
                </p>
              </div>
            </div>

            <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
              <span class="font-medium">Mã vạch:</span> {{ product.barcode }}
            </p>
          </div>
        </div>

        <div v-if="filteredProducts.length === 0 && !loading" class="py-12 text-center">
          <p class="text-gray-500 dark:text-gray-400">Không tìm thấy sản phẩm nào</p>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
