<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { mdiStore, mdiChevronDown, mdiChevronUp, mdiPlus, mdiMagnify, mdiDelete } from '@mdi/js'

import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'

import { api } from '@/plugins/axios.js'

/* State */
const shops = ref([])
const loadingShops = ref(false)
const activeShopId = ref(null)
const shopItems = reactive({})
const shopLoading = reactive({})
const employees = ref([])
const addShopModal = ref(false)
const notifyModal = ref(false)
const notifyTitle = ref('Notification')
const notifyMessage = ref('')
const deleteModal = ref(false)
const selectedShop = ref(null)
const newShop = ref({ name: '', location: '', managerId: null })
const searchQuery = ref('')

/* Auth header */
const getAuthHeaders = () => {
  const token = localStorage.getItem('accessToken') || ''
  return { Authorization: `Bearer ${token}` }
}

/* Notify helper */
const showNotify = (title, message) => {
  notifyTitle.value = title
  notifyMessage.value = message
  notifyModal.value = true
}

/* Toggle shop */
const toggleShop = async (shopId) => {
  if (activeShopId.value === shopId) {
    activeShopId.value = null
  } else {
    activeShopId.value = shopId
    await fetchShopItems(shopId)
  }
}

/* Fetch shop items */
const fetchShopItems = async (shopId) => {
  if (shopLoading[shopId]) return
  shopLoading[shopId] = true

  try {
    const resProducts = await api.get('/products', {
      headers: getAuthHeaders(),
      params: { page: 0, size: 1000 },
    })

    const products = resProducts.data?.data?.content || []

    const items = await Promise.all(
      products.map(async (p) => {
        try {
          const resStock = await api.get(`/products/${p.id}/stores`, {
            headers: getAuthHeaders(),
          })

          const stockAtShop = resStock.data?.data?.find((s) => s.store_id === shopId)

          return {
            id: p.id,
            name: p.name,
            sku: p.sku,
            quantity: stockAtShop?.quantity_in_stock || 0,
            batch: p.batch || '',
            expiry: p.expiry || '',
          }
        } catch {
          return {
            id: p.id,
            name: p.name,
            sku: p.sku,
            quantity: 0,
            batch: '',
            expiry: '',
          }
        }
      }),
    )

    shopItems[shopId] = items
  } catch (err) {
    console.warn(`Cannot fetch products for shop ${shopId}`, err)
    shopItems[shopId] = []
  } finally {
    shopLoading[shopId] = false
  }
}

/* Fetch shops */
const fetchShops = async () => {
  loadingShops.value = true
  try {
    const res = await api.get('/stores', {
      headers: getAuthHeaders(),
      params: { page: 0, size: 100 },
    })
    shops.value = res.data?.data?.content || []
  } catch (err) {
    console.warn('Cannot fetch shops', err)
    shops.value = []
  } finally {
    loadingShops.value = false
  }
}

/* Fetch employees */
const fetchEmployees = async () => {
  try {
    const res = await api.get('/employees', {
      headers: getAuthHeaders(),
      params: { page: 0, size: 1000 },
    })
    employees.value = res.data?.data?.content || []
  } catch (err) {
    console.warn('Cannot fetch employees', err)
    employees.value = []
  }
}

/* Submit new shop */
const submitNewShop = async () => {
  if (!newShop.value.name || !newShop.value.location || !newShop.value.managerId) {
    showNotify('Error', 'Please fill in all shop information!')
    return
  }

  try {
    const res = await api.post(
      '/stores',
      {
        name: newShop.value.name,
        location: newShop.value.location,
        managerId: newShop.value.managerId,
      },
      { headers: getAuthHeaders() },
    )

    if (res.data?.success) {
      showNotify('Success', 'Shop added successfully!')
      addShopModal.value = false
      newShop.value = { name: '', location: '', managerId: null }
      fetchShops()
    } else {
      showNotify('Error', 'Failed to add shop!')
    }
  } catch (err) {
    showNotify('Error', err.response?.data?.message || 'Cannot add shop!')
  }
}

/* Delete shop */
const confirmDeleteShop = (shop) => {
  selectedShop.value = shop
  deleteModal.value = true
}

const deleteShop = async () => {
  if (!selectedShop.value) return
  try {
    await api.delete(`/stores/${selectedShop.value.id}`, {
      headers: getAuthHeaders(),
    })
    showNotify('Success', 'Shop deleted successfully!')
    fetchShops()
  } catch (err) {
    showNotify('Error', 'Cannot delete shop: ' + err)
  } finally {
    deleteModal.value = false
  }
}

/* Computed */
const employeeOptions = computed(() =>
  employees.value.map((e) => ({
    value: e.id,
    label: `${e.first_name} ${e.last_name}`,
  })),
)

const filteredShops = computed(() => {
  if (!searchQuery.value) return shops.value
  return shops.value.filter(
    (shop) =>
      shop.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      shop.id.toString().includes(searchQuery.value) ||
      shop.location.toLowerCase().includes(searchQuery.value.toLowerCase()),
  )
})

/* Init */
onMounted(() => {
  fetchShops()
  fetchEmployees()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <!-- Title -->
      <SectionTitleLine :icon="mdiStore" title="Quản lý cửa hàng" main :has-button="false">
        <button
          @click="addShopModal = true"
          class="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all hover:bg-blue-700"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path :d="mdiPlus" />
          </svg>
          Thêm cửa hàng
        </button>
      </SectionTitleLine>

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm cửa hàng</h3>
            <p class="text-sm text-gray-600">Tìm theo mã cửa hàng, tên hoặc vị trí</p>
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
                placeholder="Nhập ID, tên hoặc vị trí..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Stores Card -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách cửa hàng</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredShops.length }} cửa hàng
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
                  Tên cửa hàng
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Vị trí
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Quản lý
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody class="divide-y divide-gray-200 bg-white">
              <template v-for="shop in filteredShops" :key="shop.id">
                <!-- Shop row -->
                <tr class="cursor-pointer transition hover:bg-blue-50" @click="toggleShop(shop.id)">
                  <td class="font-sm px-6 py-4 text-center text-gray-900">{{ shop.id }}</td>
                  <td class="flex items-center justify-center gap-2 px-6 py-4 text-center">
                    <span>{{ shop.name }}</span>
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-4 w-4 text-gray-500"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        :d="activeShopId === shop.id ? mdiChevronUp : mdiChevronDown"
                      />
                    </svg>
                  </td>
                  <td class="px-6 py-4 text-center text-gray-600">{{ shop.location }}</td>
                  <td class="px-6 py-4 text-center text-gray-700">
                    {{ shop.manager_name || 'Chưa có' }}
                  </td>
                  <td class="px-6 py-4 text-center">
                    <button
                      @click.stop="confirmDeleteShop(shop)"
                      class="cursor-pointer rounded-lg bg-red-100 p-2 text-red-600 transition-colors hover:bg-red-200"
                      title="Xóa cửa hàng"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiDelete" />
                      </svg>
                    </button>
                  </td>
                </tr>

                <!-- Product row -->
                <tr v-if="activeShopId === shop.id">
                  <td colspan="5" class="bg-gray-50 px-6 py-4">
                    <div v-if="shopLoading[shop.id]" class="text-center text-gray-500">
                      Đang tải sản phẩm...
                    </div>
                    <table v-else class="w-full border border-gray-200 bg-white text-base">
                      <thead class="bg-gray-100 text-gray-700">
                        <tr>
                          <th class="px-4 py-2 text-center text-sm font-medium text-gray-500">
                            Tên sản phẩm
                          </th>
                          <th class="px-4 py-2 text-center text-sm font-medium text-gray-500">
                            SKU
                          </th>
                          <th class="px-4 py-2 text-center text-sm font-medium text-gray-500">
                            Số lượng
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr
                          v-for="item in shopItems[shop.id] || []"
                          :key="item.id"
                          class="hover:bg-gray-50"
                        >
                          <td class="px-4 py-2 text-left text-gray-800">
                            {{ item.name }}
                          </td>
                          <td class="px-4 py-2 text-left text-gray-800">{{ item.sku }}</td>
                          <td class="px-4 py-2 text-center text-gray-800">{{ item.quantity }}</td>
                        </tr>
                        <tr v-if="(shopItems[shop.id] || []).length === 0">
                          <td colspan="3" class="px-4 py-2 text-center text-gray-500">
                            Không có sản phẩm
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </td>
                </tr>
              </template>

              <!-- Empty State -->
              <tr v-if="filteredShops.length === 0">
                <td colspan="6" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không tìm thấy cửa hàng</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery"
                        >Không có cửa hàng nào phù hợp với "{{ searchQuery }}"</span
                      >
                      <span v-else>Không có cửa hàng nào trong hệ thống</span>
                    </p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Add Store Modal -->
      <CardBoxModal
        v-model="addShopModal"
        title="Thêm cửa hàng mới"
        button="info"
        button-label="Thêm cửa hàng"
        @confirm="submitNewShop"
        has-cancel
      >
        <div class="space-y-4">
          <FormField label="Tên cửa hàng">
            <FormControl v-model="newShop.name" />
          </FormField>

          <FormField label="Vị trí">
            <FormControl v-model="newShop.location" />
          </FormField>

          <FormField label="Quản lý">
            <FormControl v-model="newShop.managerId" :options="employeeOptions" />
          </FormField>
        </div>
      </CardBoxModal>

      <!-- Delete Store Modal -->
      <CardBoxModal
        v-model="deleteModal"
        title="Xác nhận xoá"
        button="danger"
        button-label="Xóa"
        @confirm="deleteShop"
        has-cancel
      >
        <p>
          Bạn có chắc muốn xoá cửa hàng:
          <b>{{ selectedShop?.name }}</b> không?
        </p>
      </CardBoxModal>

      <!-- Notify Modal -->
      <CardBoxModal v-model="notifyModal" :title="notifyTitle" button="OK" button-label="Đóng">
        <p>{{ notifyMessage }}</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
