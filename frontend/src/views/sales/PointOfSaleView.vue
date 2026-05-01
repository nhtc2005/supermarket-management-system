<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'
import FormCheckRadio from '@/components/FormCheckRadio.vue'
import { mdiCart, mdiCash, mdiDelete, mdiPlus, mdiMinus } from '@mdi/js'
import { api } from '@/plugins/axios.js'

/* =========================
=        STATE
========================= */
const searchQuery = ref('')
const cart = ref([])
const customers = ref([])
const selectedCustomer = ref(null)
const usePoints = ref(false)
const popularProducts = ref([])
const loading = ref(false)
const formModalOkActive = ref(false)
const formModalMessage = ref('')

/* =========================
=        COMPUTED
========================= */
const filteredProducts = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  if (!query) return popularProducts.value
  return popularProducts.value.filter(
    (p) => p.name.toLowerCase().includes(query) || (p.code && p.code.toLowerCase().includes(query)),
  )
})

const cartTotal = computed(() =>
  cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
)

/* =========================
=        FETCH DATA
========================= */
const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await api.get('/products', { headers: getTokenHeader() })
    const data = res.data?.success ? res.data.data.content : []
    popularProducts.value = data.map((p) => ({
      id: p.id,
      name: p.name || '',
      price: p.price || 0,
      code: p.sku || p.barcode || `P${String(p.id).padStart(3, '0')}`,
      barcode: p.barcode || '',
    }))
  } catch (err) {
    console.error('Lỗi tải sản phẩm:', err)
  } finally {
    loading.value = false
  }
}

const fetchCustomers = async () => {
  try {
    const res = await api.get('/customers', {
      headers: getTokenHeader(),
      params: { page: 0, size: 1000 },
    })
    const data = res.data?.success ? res.data.data.content : []
    customers.value = data.map((c) => ({ id: c.id, label: `${c.first_name} ${c.last_name}` }))
  } catch (err) {
    console.error('Lỗi tải khách hàng:', err)
  }
}

onMounted(() => {
  fetchProducts()
  fetchCustomers()
})

/* =========================
=        CART METHODS
========================= */
const addToCart = (product) => {
  const existing = cart.value.find((i) => i.id === product.id)
  if (existing) existing.quantity++
  else cart.value.push({ ...product, quantity: 1 })
}

const removeFromCart = (productId) => {
  const item = cart.value.find((i) => i.id === productId)
  if (item) {
    if (item.quantity > 1) item.quantity--
    else cart.value = cart.value.filter((i) => i.id !== productId)
  }
}

const deleteFromCart = (productId) => {
  cart.value = cart.value.filter((i) => i.id !== productId)
}

const clearCart = () => {
  cart.value = []
  selectedCustomer.value = null
  usePoints.value = false
}

/* =========================
=        CHECKOUT
========================= */
const checkout = async () => {
  if (!selectedCustomer.value) {
    alert('Vui lòng chọn khách hàng!')
    return
  }
  if (cart.value.length === 0) {
    alert('Giỏ hàng trống!')
    return
  }

  try {
    const payload = {
      customerId: selectedCustomer.value.id,
      usePoints: usePoints.value,
      items: cart.value.map((i) => ({ productId: i.id, quantity: i.quantity })),
    }

    const res = await api.post('/orders', payload, { headers: getTokenHeader() })
    if (res.status === 200 || res.status === 201) {
      formModalMessage.value = `Đơn hàng đã tạo thành công! Tổng: ${cartTotal.value.toLocaleString('vi-VN')}₫`
      formModalOkActive.value = true
      clearCart()
    } else throw new Error('Tạo đơn hàng thất bại')
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || 'Lỗi tạo đơn hàng!')
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="mb-2 text-3xl font-bold">Dashboard Bán Hàng</h1>
        <p class="text-gray-600">Thực hiện giao dịch và tra cứu sản phẩm</p>
      </div>

      <SalesNavTabs />

      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <!-- Left Column: Products -->
        <div class="space-y-6 lg:col-span-2">
          <CardBox>
            <h3 class="mb-4 text-lg font-semibold">Tìm Sản Phẩm</h3>
            <div class="flex space-x-2">
              <FormField class="flex-1">
                <FormControl v-model="searchQuery" placeholder="Nhập tên hoặc mã" />
              </FormField>
            </div>
          </CardBox>

          <CardBox>
            <h3 class="mb-2 text-lg font-semibold">Sản Phẩm</h3>
            <div class="grid grid-cols-2 gap-4 md:grid-cols-3">
              <div
                v-for="product in filteredProducts"
                :key="product.id"
                class="cursor-pointer rounded-lg border p-4 text-center hover:bg-gray-50"
                @click="addToCart(product)"
              >
                <h4 class="mb-1 font-medium">{{ product.name }}</h4>
                <p class="text-lg font-bold text-blue-600">
                  {{ product.price.toLocaleString('vi-VN') }}₫
                </p>
              </div>
            </div>
          </CardBox>
        </div>

        <!-- Right Column: Cart -->
        <div>
          <CardBox>
            <div class="mb-4 flex items-center justify-between">
              <div class="flex items-center space-x-2">
                <BaseIcon :path="mdiCart" size="24" />
                <h3 class="text-lg font-semibold">Giỏ Hàng</h3>
              </div>
              <span class="rounded-full bg-gray-100 px-3 py-1 text-sm">
                {{ cart.length }} sản phẩm
              </span>
            </div>

            <div v-if="cart.length === 0" class="py-12 text-center">
              <BaseIcon :path="mdiCart" size="64" class="mx-auto mb-4 text-gray-300" />
              <p class="text-gray-500">Chưa có sản phẩm</p>
            </div>

            <div v-else class="mb-4 max-h-96 space-y-3 overflow-y-auto">
              <div v-for="item in cart" :key="item.id" class="rounded-lg bg-gray-50 p-3">
                <div class="mb-2 flex items-start justify-between">
                  <div class="flex-1">
                    <h4 class="font-medium">{{ item.name }}</h4>
                    <p class="text-sm text-gray-600">
                      {{ item.price.toLocaleString('vi-VN') }}₫ × {{ item.quantity }}
                    </p>
                  </div>
                  <BaseButton
                    :icon="mdiDelete"
                    color="danger"
                    small
                    @click="deleteFromCart(item.id)"
                  />
                </div>
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-2">
                    <BaseButton
                      :icon="mdiMinus"
                      color="lightDark"
                      small
                      @click="removeFromCart(item.id)"
                    />
                    <span class="w-8 text-center font-semibold">{{ item.quantity }}</span>
                    <BaseButton :icon="mdiPlus" color="lightDark" small @click="addToCart(item)" />
                  </div>
                  <span class="font-bold text-blue-600">
                    {{ (item.price * item.quantity).toLocaleString('vi-VN') }}₫
                  </span>
                </div>
              </div>
            </div>

            <div v-if="cart.length > 0" class="space-y-4 border-t pt-4">
              <!-- Chọn khách hàng -->
              <FormField label="Khách hàng">
                <FormControl
                  v-model="selectedCustomer"
                  :options="customers"
                  placeholder="Chọn khách hàng"
                />
              </FormField>

              <!-- Sử dụng điểm thưởng -->
              <FormField label="Sử dụng điểm thưởng">
                <FormCheckRadio
                  v-model="usePoints"
                  name="Dùng điểm thưởng"
                  :input-value="true"
                  label="Dùng điểm thưởng"
                  type="checkbox"
                />
              </FormField>

              <!-- Tổng tiền -->
              <div class="flex justify-between text-xl font-bold text-blue-600">
                <span>Tổng cộng:</span>
                <span>{{ cartTotal.toLocaleString('vi-VN') }}₫</span>
              </div>

              <!-- Thanh toán -->
              <BaseButton
                :icon="mdiCash"
                label="Tiền mặt"
                color="lightDark"
                class="w-full"
                @click="checkout"
              />
            </div>
          </CardBox>
        </div>
      </div>

      <!-- Modal thông báo -->
      <CardBoxModal v-model="formModalOkActive" title="Thông báo" button-label="OK">
        <p>{{ formModalMessage }}</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
