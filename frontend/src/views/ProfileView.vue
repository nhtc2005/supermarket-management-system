<script setup>
import { reactive, ref, computed } from 'vue'
import { mdiAccount, mdiMail, mdiPhone, mdiHome, mdiCalendar, mdiLock } from '@mdi/js'
import { useMainStore } from '@/stores/main'
import { api } from '@/plugins/axios.js'

import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import BaseButton from '@/components/BaseButton.vue'

/* Store */
const mainStore = useMainStore()

/* Profile Form */
const profileForm = reactive({
  name: mainStore.userName || '',
  email: mainStore.userEmail || '',
  phone: mainStore.userPhone || '',
  address: mainStore.userAddress || '',
  hireDate: mainStore.role !== 'customer' ? mainStore.hiredDate : undefined,
  points: mainStore.role === 'customer' ? mainStore.points : undefined,
  role: mainStore.role,
})

/* Labels */
const roleLabel = computed(() => {
  switch (profileForm.role) {
    case 'admin':
      return 'Quản trị viên'
    case 'manager':
      return 'Quản lý'
    case 'warehouse':
      return 'Nhân viên kho'
    case 'sales':
      return 'Nhân viên bán hàng'
    case 'customer':
      return 'Khách hàng'
    default:
      return 'Không xác định'
  }
})

const idLabel = computed(() =>
  profileForm.role === 'customer' ? 'Mã số khách hàng' : 'Mã số nhân viên',
)

const formattedHireDate = computed(() => {
  if (!profileForm.hireDate) return ''
  return profileForm.hireDate.split('T')[0]
})

/* Modal */
const modalActive = ref(false)
const modalTitle = ref('Thông báo')
const modalMessage = ref('')
const editPasswordMode = ref(false)

/* Helpers */
const showModal = (title, message) => {
  modalTitle.value = title
  modalMessage.value = message
  modalActive.value = true
}

/* Submit Profile */
const submitProfile = async () => {
  if (profileForm.phone && !/^\d{9,15}$/.test(profileForm.phone))
    return showModal('Lỗi', 'Số điện thoại không hợp lệ!')

  try {
    const res = await api.put('/user/profile', { ...profileForm })

    if (!res.data?.success) return showModal('Lỗi', res.data?.message || 'Cập nhật thất bại!')

    mainStore.setUser(res.data.data)
    showModal('Thành công', res.data?.message || 'Cập nhật thành công!')
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Cập nhật thất bại!')
  }
}

/* Password Form */
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  passwordConfirmation: '',
})

/* Change Password */
const submitPassword = async () => {
  if (
    !passwordForm.currentPassword ||
    !passwordForm.newPassword ||
    !passwordForm.passwordConfirmation
  )
    return showModal('Lỗi', 'Vui lòng điền đầy đủ mật khẩu!')

  if (passwordForm.newPassword !== passwordForm.passwordConfirmation)
    return showModal('Lỗi', 'Mật khẩu xác nhận không khớp!')

  try {
    const token = localStorage.getItem('accessToken') || ''

    const res = await api.put(
      '/auth/change-password',
      { ...passwordForm },
      { headers: { Authorization: `Bearer ${token}` } },
    )

    if (!res.data?.success) return showModal('Lỗi', res.data?.message || 'Đổi mật khẩu thất bại!')

    showModal('Thành công', 'Đổi mật khẩu thành công!')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.passwordConfirmation = ''
    editPasswordMode.value = false
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Đổi mật khẩu thất bại!')
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain class="flex flex-col gap-6 lg:flex-row">
      <!-- Avatar -->
      <div class="flex-shrink-0 text-center">
        <img
          :src="mainStore.userAvatar"
          alt="avatar"
          class="mx-auto h-32 w-32 rounded-full border shadow-sm"
        />

        <p class="mt-3 text-sm text-gray-500 dark:text-slate-400">
          {{ idLabel }}:
          <span class="font-medium text-gray-700 dark:text-slate-300">
            {{ mainStore.userId }}
          </span>
          –
          <span class="font-medium text-gray-500 dark:text-slate-300">
            {{ roleLabel }}
          </span>
        </p>
      </div>

      <!-- Profile Form -->
      <div class="flex-1">
        <CardBox>
          <FormField label="Họ và tên">
            <FormControl v-model="profileForm.name" :icon="mdiAccount" readonly />
          </FormField>

          <FormField v-if="profileForm.role === 'customer'" label="Email">
            <FormControl v-model="profileForm.email" :icon="mdiMail" readonly />
          </FormField>

          <FormField v-if="profileForm.role === 'customer'" label="Số điện thoại">
            <FormControl v-model="profileForm.phone" :icon="mdiPhone" />
          </FormField>

          <FormField v-if="profileForm.role === 'customer'" label="Địa chỉ">
            <FormControl v-model="profileForm.address" :icon="mdiHome" />
          </FormField>

          <FormField v-if="profileForm.role !== 'customer'" label="Ngày vào làm">
            <FormControl :model-value="formattedHireDate" :icon="mdiCalendar" readonly />
          </FormField>

          <FormField v-if="profileForm.points !== undefined" label="Điểm tích lũy">
            <FormControl v-model="profileForm.points" readonly />
          </FormField>

          <template #footer>
            <BaseButtons class="mt-4">
              <!-- <BaseButton color="success" label="Lưu thông tin" @click="submitProfile" /> -->
              <BaseButton color="info" label="Đổi mật khẩu" @click="editPasswordMode = true" />
            </BaseButtons>
          </template>
        </CardBox>
      </div>
    </SectionMain>

    <!-- Change Password Modal -->
    <CardBoxModal
      v-model="editPasswordMode"
      title="Đổi mật khẩu"
      has-cancel
      button="info"
      button-label="Đổi mật khẩu"
      @confirm="submitPassword"
    >
      <FormField label="Mật khẩu hiện tại">
        <FormControl v-model="passwordForm.currentPassword" type="password" :icon="mdiLock" />
      </FormField>

      <FormField label="Mật khẩu mới">
        <FormControl v-model="passwordForm.newPassword" type="password" :icon="mdiLock" />
      </FormField>

      <FormField label="Xác nhận mật khẩu">
        <FormControl v-model="passwordForm.passwordConfirmation" type="password" :icon="mdiLock" />
      </FormField>
    </CardBoxModal>

    <!-- Notification Modal -->
    <CardBoxModal v-model="modalActive" :title="modalTitle" button="OK" button-label="Đóng">
      <p>{{ modalMessage }}</p>
    </CardBoxModal>
  </LayoutAuthenticated>
</template>
