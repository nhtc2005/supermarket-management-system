<script setup>
import { reactive, ref, onMounted } from 'vue'
import { mdiBallotOutline, mdiPlus, mdiDelete } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseDivider from '@/components/BaseDivider.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import { api } from '@/plugins/axios.js'

/* =========================
=        DATA CƠ BẢN       =
========================= */
const receiptTypes = [
  { id: 'import', label: 'Phiếu nhập hàng' },
  { id: 'export', label: 'Phiếu xuất hàng' },
]

const products = ref([])
const warehouses = ref([])
const batches = ref([]) // Danh sách lô hàng hiện có trong kho (cho xuất kho)

/* Form */
const form = reactive({
  type: receiptTypes[0],
  employee_id: '',
  warehouse_id: '',
  supplier: '', // Chỉ nhập kho
  unitPrice: 0, // Chỉ nhập kho
  export_reason: '', // Chỉ xuất kho
  batches: [], // Danh sách lô thêm mới hoặc xuất
})

/* =========================
=        LOAD USER        =
========================= */
onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user'))
  form.employee_id = user?.id || ''

  loadProducts()
  loadWarehouses()
  loadBatches()
})

/* =========================
=     LOAD PRODUCT LIST   =
========================= */
const loadProducts = async () => {
  const res = await api.get('/products', {
    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` },
    params: { page: 0, size: 1000 },
  })

  products.value = res.data.data.content.map((p) => ({
    id: p.id,
    label: `#${p.id} - ${p.name}`,
  }))
}

/* =========================
=      LOAD VARIANTS      =
========================= */
const loadVariants = async (batch) => {
  if (!batch.product_id?.id) return

  const res = await api.get(`/products/${batch.product_id.id}/variants`, {
    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` },
  })

  batch.variants = res.data.data.map((v) => ({
    id: v.id,
    label: Object.values(v.variant_attributes).join(' - '),
  }))
}

/* =========================
=     LOAD WAREHOUSES     =
========================= */
const loadWarehouses = async () => {
  const res = await api.get('/warehouses', {
    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` },
    params: { page: 0, size: 1000 },
  })

  warehouses.value = res.data.data.content.map((w) => ({
    id: w.id,
    label: w.name,
  }))
}

/* =========================
=       LOAD BATCHES      =
========================= */
const loadBatches = async () => {
  const res = await api.get('/batches', {
    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` },
    params: { page: 0, size: 1000 },
  })

  batches.value = res.data.data.content.map((b) => ({
    id: b.id,
    label: `Lô #${b.id} - ${b.product_name}`,
  }))
}

/* =========================
=        BATCH CRUD       =
========================= */
const addBatch = () => {
  form.batches.push({
    product_id: '',
    variant_id: '',
    quantity: 1,
    variants: [],
    manufacture: '',
    expiryDate: '',
    batch_id: '', // Chỉ xuất kho
  })
}

const removeBatch = (i) => {
  form.batches.splice(i, 1)
}

/* =========================
=        MODAL            =
========================= */
const formModalActive = ref(false)
const formModalOkActive = ref(false)
const formMessage = ref('')

/* =========================
=        SUBMIT API       =
========================= */
const submit = async () => {
  try {
    const accessToken = localStorage.getItem('accessToken')

    if (form.type.id === 'import') {
      const payload = {
        warehouseId: form.warehouse_id.id,
        supplier: form.supplier,
        unitPrice: form.unitPrice,
        employeeImportId: form.employee_id,
        items: form.batches.map((b) => ({
          productId: b.product_id?.id || b.product_id,
          variantId: b.variant_id.id,
          quantity: b.quantity,
          manufacture: b.manufacture,
          expiryDate: b.expiryDate || null,
        })),
      }
      console.log(payload)

      await api.post('/warehouse-operations/imports', payload, {
        headers: { Authorization: `Bearer ${accessToken}` },
      })
    } else {
      const payload = {
        warehouseId: form.warehouse_id.id,
        reason: form.export_reason.label,
        employeeExportId: form.employee_id,
        items: form.batches.map((b) => ({
          batchId: b.batch_id?.id || b.batch_id,
          quantity: b.quantity,
        })),
      }
      console.log(payload)

      await api.post('/warehouse-operations/exports', payload, {
        headers: { Authorization: `Bearer ${accessToken}` },
      })
    }

    formMessage.value = 'Tạo phiếu thành công!'
    formModalOkActive.value = true
    form.batches = []
  } catch (err) {
    console.error(err)
    formMessage.value = err.response?.data?.message || 'Lỗi hệ thống!'
    formModalActive.value = true
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine
        :icon="mdiBallotOutline"
        title="Tạo Phiếu Nhập / Xuất Kho"
        main
        :has-button="false"
      />

      <CardBox is-form @submit.prevent="submit">
        <!-- Loại phiếu -->
        <FormField label="Loại phiếu">
          <FormControl v-model="form.type" :options="receiptTypes" />
        </FormField>

        <!-- Nhân viên -->
        <FormField label="Nhân viên">
          <FormControl v-model="form.employee_id" readonly />
        </FormField>

        <!-- Kho -->
        <FormField label="Kho">
          <FormControl v-model="form.warehouse_id" :options="warehouses" />
        </FormField>

        <!-- Nhập kho: Supplier & Unit Price -->
        <template v-if="form.type.id === 'import'">
          <FormField label="Nhà cung cấp">
            <FormControl v-model="form.supplier" />
          </FormField>

          <FormField label="Đơn giá">
            <FormControl type="number" v-model="form.unitPrice" />
          </FormField>
        </template>

        <!-- Xuất kho: Lý do -->
        <FormField v-if="form.type.id === 'export'" label="Lý do xuất">
          <FormControl
            type="select"
            v-model="form.export_reason"
            :options="[
              { id: 'sale', label: 'Bán hàng' },
              { id: 'transfer', label: 'Chuyển kho' },
              { id: 'damage', label: 'Hàng hỏng' },
              { id: 'return', label: 'Khách trả' },
            ]"
          />
        </FormField>

        <BaseDivider />

        <!-- Danh sách lô -->
        <div class="mb-4 flex justify-between">
          <h2 class="text-lg font-bold">Danh sách lô</h2>
          <BaseButton :icon="mdiPlus" color="info" label="Thêm lô" @click="addBatch" />
        </div>

        <div
          v-for="(batch, index) in form.batches"
          :key="index"
          class="mb-6 rounded-lg border bg-gray-50 p-4"
        >
          <div class="mb-2 flex justify-between">
            <h3>Lô #{{ index + 1 }}</h3>
            <BaseButton :icon="mdiDelete" color="danger" @click="removeBatch(index)" />
          </div>

          <!-- Nhập kho -->
          <template v-if="form.type.id === 'import'">
            <FormField label="Sản phẩm">
              <FormControl
                v-model="batch.product_id"
                :options="products"
                @change="loadVariants(batch)"
              />
            </FormField>

            <FormField label="Biến thể">
              <FormControl v-model="batch.variant_id" :options="batch.variants" />
            </FormField>

            <FormField label="Số lượng">
              <FormControl type="number" v-model="batch.quantity" />
            </FormField>

            <FormField label="Nhà sản xuất">
              <FormControl v-model="batch.manufacture" />
            </FormField>

            <FormField label="Ngày hết hạn">
              <FormControl type="date" v-model="batch.expiryDate" />
            </FormField>
          </template>

          <!-- Xuất kho -->
          <template v-else>
            <FormField label="Chọn lô">
              <FormControl v-model="batch.batch_id" :options="batches" />
            </FormField>

            <FormField label="Số lượng">
              <FormControl type="number" v-model="batch.quantity" />
            </FormField>
          </template>
        </div>

        <template #footer>
          <BaseButton type="submit" color="info" label="Tạo phiếu" />
        </template>
      </CardBox>

      <CardBoxModal v-model="formModalActive" title="Thông báo" button-label="OK">
        <p>{{ formMessage }}</p>
      </CardBoxModal>

      <CardBoxModal v-model="formModalOkActive" title="Thành công" button-label="OK">
        <p>{{ formMessage }}</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
