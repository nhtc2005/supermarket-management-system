<script setup>
import { ref } from 'vue'
import CardBoxModal from '@/components/CardBoxModal.vue'

const modalActive = ref(false)
const modalTitle = ref('Thông báo')
const modalMessage = ref('')

const showModal = (title, message) => {
  modalTitle.value = title
  modalMessage.value = message
  modalActive.value = true
}

showModal('Lỗi', 'Số điện thoại không hợp lệ!')
</script>

<template>
  <CardBoxModal v-model="modalActive" :title="modalTitle" button="OK">
    <p>{{ modalMessage }}</p>
  </CardBoxModal>

  <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div class="flex-1">
        <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm ưu đãi</h3>
        <p class="text-sm text-gray-600">Tìm kiếm theo mã ưu đãi, tên ưu đãi hoặc trạng thái</p>
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
            placeholder="Nhập mã, tên ưu đãi hoặc trạng thái..."
            class="pl-10"
          />
        </div>
      </div>
    </div>
  </CardBox>

  <SectionTitleLineWithButton :icon="mdiSale" title="Quản lý ưu đãi giảm giá" main>
    <button
      @click="addDiscount"
      class="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:bg-blue-700 hover:shadow-md"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-5 w-5"
        viewBox="0 0 24 24"
        fill="currentColor"
      >
        <path :d="mdiPlus" />
      </svg>
      Thêm ưu đãi
    </button>
  </SectionTitleLineWithButton>
</template>
