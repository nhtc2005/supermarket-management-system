<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitle from '@/components/SectionTitle.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import AdminNavTabs from '@/components/AdminNavTabs.vue'
import { mdiPlus, mdiMagnify, mdiPencil, mdiDelete } from '@mdi/js'

const searchQuery = ref('')
const isModalActive = ref(false)
const isDeleteModalActive = ref(false)
const selectedProduct = ref(null)
const form = ref({
  id: null,
  code: '',
  name: '',
  barcode: '',
  category: '',
  price: '',
  unit: '',
  stock: '',
})

const products = ref([
  {
    id: 1,
    code: 'P001',
    name: 'Sữa tươi Vinamilk',
    barcode: '8936036011234',
    category: 'Thực phẩm',
    price: 32000,
    unit: 'Hộp',
    stock: 245,
  },
  {
    id: 2,
    code: 'P002',
    name: 'Gạo ST25 5kg',
    barcode: '8936036022341',
    category: 'Lương thực',
    price: 125000,
    unit: 'Túi',
    stock: 89,
  },
  {
    id: 3,
    code: 'P003',
    name: 'Nước ngọt Coca Cola',
    barcode: '8936036033452',
    category: 'Đồ uống',
    price: 10000,
    unit: 'Lon',
    stock: 543,
  },
  {
    id: 4,
    code: 'P004',
    name: 'Bánh mì sandwich',
    barcode: '8936036044563',
    category: 'Thực phẩm',
    price: 25000,
    unit: 'Gói',
    stock: 67,
  },
  {
    id: 5,
    code: 'P005',
    name: 'Dầu ăn Simply 1L',
    barcode: '8936036055674',
    category: 'Gia vị',
    price: 45000,
    unit: 'Chai',
    stock: 156,
  },
])

const categories = ['Thực phẩm', 'Lương thực', 'Đồ uống', 'Gia vị', 'Gia dụng']

const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value
  const query = searchQuery.value.toLowerCase()
  return products.value.filter(
    (p) =>
      p.name.toLowerCase().includes(query) ||
      p.code.toLowerCase().includes(query) ||
      p.barcode.includes(query),
  )
})

const openAddModal = () => {
  form.value = {
    id: null,
    code: '',
    name: '',
    barcode: '',
    category: '',
    price: '',
    unit: '',
    stock: '',
  }
  selectedProduct.value = null
  isModalActive.value = true
}

const openEditModal = (product) => {
  form.value = { ...product }
  selectedProduct.value = product
  isModalActive.value = true
}

const saveProduct = () => {
  if (form.value.id) {
    const index = products.value.findIndex((p) => p.id === form.value.id)
    if (index !== -1) {
      products.value[index] = { ...form.value }
    }
  } else {
    const newProduct = {
      ...form.value,
      id: products.value.length + 1,
      price: Number(form.value.price),
      stock: Number(form.value.stock),
    }
    products.value.push(newProduct)
  }
  isModalActive.value = false
}

const openDeleteModal = (product) => {
  selectedProduct.value = product
  isDeleteModalActive.value = true
}

const deleteProduct = () => {
  if (selectedProduct.value) {
    products.value = products.value.filter((p) => p.id !== selectedProduct.value.id)
    isDeleteModalActive.value = false
    selectedProduct.value = null
  }
}

const isLowStock = (stock) => {
  return stock < 100
}

onMounted(() => {
  // Fetch products
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
        <div class="flex justify-between items-center mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">Quản Lý Sản Phẩm</h2>
            <p class="text-gray-600 dark:text-gray-400">Thêm, sửa, xóa sản phẩm và quản lý mã vạch</p>
          </div>
          <BaseButton :icon="mdiPlus" label="Thêm Sản Phẩm" color="info" @click="openAddModal" />
        </div>

        <FormField class="mb-6">
          <FormControl
            v-model="searchQuery"
            :icon="mdiMagnify"
            placeholder="Tìm kiếm theo tên, mã sản phẩm, mã vạch..."
          />
        </FormField>

        <CardBox has-table>
          <table>
            <thead>
              <tr>
                <th>Mã SP</th>
                <th>Tên sản phẩm</th>
                <th>Mã vạch</th>
                <th>Danh mục</th>
                <th>Giá bán</th>
                <th>Đơn vị</th>
                <th>Tồn kho</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in filteredProducts" :key="product.id">
                <td data-label="Mã SP">{{ product.code }}</td>
                <td data-label="Tên sản phẩm">{{ product.name }}</td>
                <td data-label="Mã vạch">{{ product.barcode }}</td>
                <td data-label="Danh mục">
                  <span
                    class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200"
                  >
                    {{ product.category }}
                  </span>
                </td>
                <td data-label="Giá bán">{{ product.price.toLocaleString('vi-VN') }}₫</td>
                <td data-label="Đơn vị">{{ product.unit }}</td>
                <td data-label="Tồn kho">
                  <span
                    :class="{
                      'text-orange-600 dark:text-orange-400 font-semibold': isLowStock(product.stock),
                      'text-gray-800 dark:text-white': !isLowStock(product.stock),
                    }"
                  >
                    {{ product.stock }}
                  </span>
                </td>
                <td class="before:hidden lg:w-1 whitespace-nowrap">
                  <BaseButtons type="justify-start lg:justify-end" no-wrap>
                    <BaseButton
                      :icon="mdiPencil"
                      color="info"
                      small
                      @click="openEditModal(product)"
                    />
                    <BaseButton
                      :icon="mdiDelete"
                      color="danger"
                      small
                      @click="openDeleteModal(product)"
                    />
                  </BaseButtons>
                </td>
              </tr>
            </tbody>
          </table>
        </CardBox>
      </CardBox>

      <!-- Modal thêm/sửa -->
      <CardBoxModal
        v-model="isModalActive"
        :title="form.id ? 'Sửa sản phẩm' : 'Thêm sản phẩm'"
      >
        <FormField label="Mã sản phẩm">
          <FormControl v-model="form.code" placeholder="Nhập mã sản phẩm" />
        </FormField>
        <FormField label="Tên sản phẩm">
          <FormControl v-model="form.name" placeholder="Nhập tên sản phẩm" />
        </FormField>
        <FormField label="Mã vạch">
          <FormControl v-model="form.barcode" placeholder="Nhập mã vạch" />
        </FormField>
        <FormField label="Danh mục">
          <FormControl v-model="form.category" :options="categories.map(c => ({value: c, label: c}))" />
        </FormField>
        <FormField label="Giá bán">
          <FormControl v-model="form.price" type="number" placeholder="Nhập giá bán" />
        </FormField>
        <FormField label="Đơn vị">
          <FormControl v-model="form.unit" placeholder="Nhập đơn vị" />
        </FormField>
        <FormField label="Tồn kho">
          <FormControl v-model="form.stock" type="number" placeholder="Nhập số lượng tồn kho" />
        </FormField>
        <template #footer>
          <BaseButton label="Hủy" color="lightDark" @click="isModalActive = false" />
          <BaseButton label="Lưu" color="info" @click="saveProduct" />
        </template>
      </CardBoxModal>

      <!-- Modal xóa -->
      <CardBoxModal
        v-model="isDeleteModalActive"
        title="Xác nhận xóa"
        button="danger"
        has-cancel
        @confirm="deleteProduct"
      >
        <p>Bạn có chắc chắn muốn xóa sản phẩm <strong>{{ selectedProduct?.name }}</strong>?</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>

