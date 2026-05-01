<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'
import { mdiMagnify, mdiEye, mdiPrinter } from '@mdi/js'
import { api } from '@/plugins/axios.js'

const searchQuery = ref('')
const salesHistory = ref([])
const loading = ref(false)

const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

const fetchSalesHistory = async () => {
  loading.value = true
  try {
    const res = await api.get('/orders', {
      headers: getTokenHeader(),
      params: { page: 0, size: 100 },
    })
    const data = res.data?.success ? res.data.data.content : []
    salesHistory.value = data.map((sale) => ({
      id: sale.id,
      invoiceCode: `HD${sale.id}`,
      time: new Date(sale.created_at).toLocaleString('vi-VN'),
      customer: sale.customer_name || 'Khách lẻ',
      items: sale.total_items,
      total: sale.total_money,
      status: sale.status,
    }))
  } catch (err) {
    console.error('Lỗi tải lịch sử bán hàng:', err)
  } finally {
    loading.value = false
  }
}

const filteredHistory = computed(() => {
  if (!searchQuery.value) return salesHistory.value
  const query = searchQuery.value.toLowerCase()
  return salesHistory.value.filter(
    (sale) =>
      sale.invoiceCode.toLowerCase().includes(query) || sale.customer.toLowerCase().includes(query),
  )
})

const totalRevenue = computed(() =>
  filteredHistory.value.reduce((sum, sale) => sum + sale.total, 0),
)

const viewDetails = (sale) => {
  alert(`Chi tiết hóa đơn: ${sale.invoiceCode}`)
}

const printInvoice = (sale) => {
  alert(`In hóa đơn: ${sale.invoiceCode}`)
}

onMounted(() => {
  fetchSalesHistory()
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
          <h2 class="mb-2 text-2xl font-bold text-gray-800 dark:text-white">Lịch Sử Bán Hàng</h2>
          <p class="text-gray-600 dark:text-gray-400">Xem lại các giao dịch đã thực hiện</p>
        </div>

        <FormField class="mb-6">
          <FormControl
            v-model="searchQuery"
            :icon="mdiMagnify"
            placeholder="Tìm kiếm theo mã hóa đơn hoặc tên khách hàng..."
          />
        </FormField>

        <CardBox has-table>
          <table>
            <thead>
              <tr>
                <th>Mã HĐ</th>
                <th>Thời gian</th>
                <th>Khách hàng</th>
                <th>SL sản phẩm</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="sale in filteredHistory" :key="sale.id">
                <td data-label="Mã HĐ">{{ sale.invoiceCode }}</td>
                <td data-label="Thời gian">{{ sale.time }}</td>
                <td data-label="Khách hàng">{{ sale.customer }}</td>
                <td data-label="SL sản phẩm">{{ sale.items }} món</td>
                <td data-label="Tổng tiền">
                  <span class="font-semibold text-blue-600 dark:text-blue-400">
                    {{ sale.total.toLocaleString('vi-VN') }}₫
                  </span>
                </td>
                <td data-label="Trạng thái">
                  <span
                    class="rounded-full px-2 py-1 text-xs"
                    :class="{
                      'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200':
                        sale.status === 'COMPLETED',
                      'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200':
                        sale.status !== 'COMPLETED',
                    }"
                  >
                    {{ sale.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </CardBox>

        <div
          class="mt-4 flex items-center justify-between border-t border-gray-200 pt-4 dark:border-gray-700"
        >
          <span class="text-sm text-gray-600 dark:text-gray-400">
            Tổng: {{ filteredHistory.length }} giao dịch
          </span>
          <span class="text-lg font-semibold text-blue-600 dark:text-blue-400">
            Tổng doanh thu: {{ totalRevenue.toLocaleString('vi-VN') }}₫
          </span>
        </div>

        <div v-if="loading" class="py-12 text-center text-gray-500 dark:text-gray-400">
          Đang tải dữ liệu...
        </div>

        <div
          v-if="!loading && filteredHistory.length === 0"
          class="py-12 text-center text-gray-500 dark:text-gray-400"
        >
          Không tìm thấy giao dịch nào
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
