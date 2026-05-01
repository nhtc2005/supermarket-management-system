<script setup>
import { reactive, ref, onMounted } from 'vue'
import { mdiTruckDelivery, mdiPlus, mdiDelete } from '@mdi/js'
import { api } from '@/plugins/axios'

import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseDivider from '@/components/BaseDivider.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'

/* =========================
   STATE
========================= */
const warehouses = ref([])
const stores = ref([])
const batches = ref([]) // tất cả lô từ kho API

const form = reactive({
  employee_id: '',
  source_warehouse: null,
  destination_store: null,
  transfer_date: '',
  notes: '',
  batches: [],
})

const formModalActive = ref(false)
const formModalOkActive = ref(false)
const formMessage = ref('')

/* =========================
   AUTH HEADER
========================= */
const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

/* =========================
   LOAD DATA
========================= */
const fetchWarehouses = async () => {
  const res = await api.get('/warehouses', {
    headers: getTokenHeader(),
    params: { page: 0, size: 1000 },
  })
  warehouses.value = res.data.data.content.map((w) => ({ id: w.id, label: w.name }))
}

const fetchStores = async () => {
  const res = await api.get('/stores', {
    headers: getTokenHeader(),
    params: { page: 0, size: 1000 },
  })
  stores.value = res.data.data.content.map((s) => ({ id: s.id, label: s.name }))
}

const fetchBatches = async (warehouseId) => {
  if (!warehouseId) return
  const res = await api.get('/batches', {
    headers: getTokenHeader(),
    params: { page: 0, size: 1000, warehouseId },
  })
  batches.value = res.data.data.content.map((b) => ({
    batch_id: b.id,
    product_id: b.product_id,
    product_name: b.product_name,
    available_quantity: b.quantity_available,
    warehouse_id: b.warehouse_id,
  }))
  console.log(batches.value)
}

/* =========================
   BATCH HANDLER
========================= */
const addBatch = () => {
  form.batches.push({
    batch_id: null,
    product_id: null,
    product_name: '',
    available_quantity: 0,
    quantity: 0,
  })
}

const removeBatch = (i) => form.batches.splice(i, 1)

const onBatchChange = (batch) => {
  const selected = batch.batch_id
  if (selected) {
    batch.product_id = selected.productId
    batch.product_name = selected.productName
    batch.available_quantity = selected.availableQuantity
  }
}

/* =========================
   SUBMIT
========================= */
const submit = async () => {
  if (!form.employee_id || !form.source_warehouse || !form.destination_store) {
    formMessage.value = 'Vui lòng nhập đầy đủ thông tin: nhân viên, kho nguồn, cửa hàng nhận.'
    formModalActive.value = true
    return
  }

  if (!form.batches.length) {
    formMessage.value = 'Vui lòng thêm ít nhất 1 lô hàng.'
    formModalActive.value = true
    return
  }

  for (const [i, batch] of form.batches.entries()) {
    if (!batch.batch_id) {
      formMessage.value = `Lô #${i + 1} chưa chọn!`
      formModalActive.value = true
      return
    }
    if (!batch.quantity || batch.quantity <= 0) {
      formMessage.value = `Lô #${i + 1}: số lượng chuyển phải > 0!`
      formModalActive.value = true
      return
    }
    if (batch.quantity > batch.available_quantity) {
      formMessage.value = `Lô #${i + 1}: số lượng chuyển không được lớn hơn tồn kho (${batch.available_quantity})!`
      formModalActive.value = true
      return
    }
  }

  try {
    const payload = {
      fromWarehouseId: form.source_warehouse.id,
      toStoreId: form.destination_store.id,
      employeeId: form.employee_id,
      notes: form.notes,
      items: form.batches.map((b) => ({
        batchId: b.batch_id.id,
        quantity: b.quantity,
      })),
    }

    console.log(payload)

    await api.post('/warehouse-operations/transfers/to-store', payload, {
      headers: getTokenHeader(),
    })

    formMessage.value = 'Phiếu chuyển hàng đã được tạo thành công!'
    formModalOkActive.value = true

    // Reset form
    form.employee_id = ''
    form.source_warehouse = null
    form.destination_store = null
    form.transfer_date = ''
    form.notes = ''
    form.batches = []
  } catch (err) {
    console.error(err)
    formMessage.value = err.response?.data?.message || 'Lỗi khi tạo phiếu chuyển hàng!'
    formModalActive.value = true
  }
}

/* =========================
   WATCH SOURCE WAREHOUSE
========================= */
const onWarehouseChange = async () => {
  form.batches = []
  await fetchBatches(form.source_warehouse?.id)
}

/* =========================
   MOUNT
========================= */
onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  form.employee_id = user?.id || ''
  await fetchWarehouses()
  await fetchStores()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine
        :icon="mdiTruckDelivery"
        title="Chuyển hàng đến cửa hàng"
        main
        :has-button="false"
      />

      <CardBox is-form @submit.prevent="submit">
        <FormField label="Nhân viên lập phiếu">
          <FormControl v-model="form.employee_id" readonly />
        </FormField>

        <FormField label="Kho nguồn">
          <FormControl
            v-model="form.source_warehouse"
            :options="warehouses"
            @change="onWarehouseChange"
          />
        </FormField>

        <FormField label="Cửa hàng nhận">
          <FormControl v-model="form.destination_store" :options="stores" />
        </FormField>

        <FormField label="Ngày chuyển">
          <FormControl type="date" v-model="form.transfer_date" />
        </FormField>

        <FormField label="Ghi chú">
          <FormControl v-model="form.notes" placeholder="Nhập ghi chú (tùy chọn)" />
        </FormField>

        <BaseDivider />

        <div class="mb-4 flex items-center justify-between">
          <h2 class="text-lg font-bold">Danh sách lô hàng chuyển</h2>
          <BaseButton :icon="mdiPlus" color="info" label="Thêm lô" @click="addBatch" />
        </div>

        <div
          v-for="(batch, index) in form.batches"
          :key="index"
          class="mb-6 rounded-lg border bg-gray-50 p-4"
        >
          <div class="mb-2 flex items-center justify-between">
            <h3 class="font-semibold">Lô #{{ index + 1 }}</h3>
            <BaseButton
              color="danger"
              :icon="mdiDelete"
              label="Xóa lô"
              @click="removeBatch(index)"
            />
          </div>

          <FormField label="Chọn lô hàng">
            <FormControl
              v-model="batch.batch_id"
              :options="
                batches
                  .filter(
                    (b) =>
                      form.source_warehouse &&
                      b.warehouse_id === form.source_warehouse.id &&
                      b.available_quantity !== 0,
                  )
                  .map((b) => ({
                    id: b.batch_id,
                    label: `${b.batch_id} - ${b.product_name} (Tồn kho: ${b.available_quantity})`,
                    productId: b.product_id,
                    productName: b.product_name,
                    availableQuantity: b.available_quantity,
                  }))
              "
              @change="onBatchChange(batch)"
            />
          </FormField>

          <FormField label="Mã sản phẩm">
            <FormControl v-model="batch.product_id" readonly />
          </FormField>

          <FormField label="Tên sản phẩm">
            <FormControl v-model="batch.product_name" readonly />
          </FormField>

          <FormField label="Tồn kho">
            <FormControl type="number" v-model="batch.available_quantity" readonly />
          </FormField>

          <FormField label="Số lượng chuyển">
            <FormControl type="number" v-model="batch.quantity" />
          </FormField>
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
