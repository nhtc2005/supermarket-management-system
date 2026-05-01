<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitle from '@/components/SectionTitle.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import { mdiMagnify, mdiCartPlus, mdiStar } from '@mdi/js'

const searchQuery = ref('')
const selectedCategory = ref('all')
const products = ref([])
const cart = ref([])

const categories = [
  { value: 'all', label: 'Tất cả' },
  { value: 'food', label: 'Thực phẩm' },
  { value: 'drink', label: 'Đồ uống' },
  { value: 'household', label: 'Gia dụng' },
]

const fetchProducts = async () => {
  try {
    products.value = [
      {
        id: 1,
        name: 'Bánh mì',
        price: 15000,
        category: 'food',
        image: '',
        rating: 4.5,
        reviews: 120,
        stock: 50,
      },
      {
        id: 2,
        name: 'Sữa tươi',
        price: 25000,
        category: 'drink',
        image: '',
        rating: 4.8,
        reviews: 85,
        stock: 30,
      },
      {
        id: 3,
        name: 'Trứng gà',
        price: 35000,
        category: 'food',
        image: '',
        rating: 4.7,
        reviews: 200,
        stock: 100,
      },
      {
        id: 4,
        name: 'Nước ngọt',
        price: 12000,
        category: 'drink',
        image: '',
        rating: 4.3,
        reviews: 150,
        stock: 80,
      },
      {
        id: 5,
        name: 'Gạo',
        price: 25000,
        category: 'food',
        image: '',
        rating: 4.6,
        reviews: 95,
        stock: 40,
      },
    ]
  } catch (error) {
    console.error('Lỗi tải sản phẩm:', error)
  }
}

const filteredProducts = computed(() => {
  let filtered = products.value

  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter((p) => p.category === selectedCategory.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter((p) => p.name.toLowerCase().includes(query))
  }

  return filtered
})

const addToCart = (product) => {
  const existingItem = cart.value.find((item) => item.id === product.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    cart.value.push({
      ...product,
      quantity: 1,
    })
  }
  alert(`Đã thêm ${product.name} vào giỏ hàng!`)
}

const cartCount = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.quantity, 0)
})

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitle first>Mua sắm</SectionTitle>

      <div class="mb-6 flex items-center justify-between">
        <CardBox class="flex-1 mr-4">
          <FormField>
            <FormControl
              v-model="searchQuery"
              :icon="mdiMagnify"
              placeholder="Tìm kiếm sản phẩm..."
            />
          </FormField>
        </CardBox>
        <CardBox>
          <FormField>
            <FormControl v-model="selectedCategory" :options="categories" />
          </FormField>
        </CardBox>
        <div class="ml-4 relative">
          <BaseButton :icon="mdiCartPlus" color="info" label="Giỏ hàng" />
          <span
            v-if="cartCount > 0"
            class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
          >
            {{ cartCount }}
          </span>
        </div>
      </div>

      <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <CardBox
          v-for="product in filteredProducts"
          :key="product.id"
          class="hover:shadow-lg transition-shadow"
        >
          <div class="text-center">
            <div class="mb-4 h-48 bg-gray-200 rounded-lg flex items-center justify-center dark:bg-slate-700">
              <span class="text-gray-400">Hình ảnh</span>
            </div>
            <h3 class="font-semibold text-lg text-gray-800 dark:text-white mb-2">
              {{ product.name }}
            </h3>
            <div class="flex items-center justify-center space-x-1 mb-2">
              <BaseIcon :path="mdiStar" size="16" class="text-yellow-500" />
              <span class="text-sm text-gray-600 dark:text-gray-400">
                {{ product.rating }} ({{ product.reviews }} đánh giá)
              </span>
            </div>
            <p class="text-2xl font-bold text-blue-600 dark:text-blue-400 mb-4">
              {{ product.price.toLocaleString('vi-VN') }}₫
            </p>
            <p class="text-xs text-gray-500 dark:text-gray-500 mb-4">
              Còn lại: {{ product.stock }} sản phẩm
            </p>
            <BaseButton
              :icon="mdiCartPlus"
              label="Thêm vào giỏ"
              color="info"
              class="w-full"
              @click="addToCart(product)"
            />
          </div>
        </CardBox>
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>


