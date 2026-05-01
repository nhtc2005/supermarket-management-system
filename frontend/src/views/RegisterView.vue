<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { mdiAccount, mdiEmail, mdiPhone, mdiHome, mdiAsterisk } from '@mdi/js'

import logo from '@/assets/logo.png'
import { api } from '@/plugins/axios.js'

import LayoutGuest from '@/layouts/LayoutGuest.vue'
import SectionFullScreen from '@/components/SectionFullScreen.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'

const router = useRouter()

/* Modal */
const modalActive = ref(false)
const modalTitle = ref('Thông báo')
const modalMessage = ref('')

const showModal = (title, message) => {
  modalTitle.value = title
  modalMessage.value = message
  modalActive.value = true
}

/* Form */
const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  address: '',
  password: '',
  passwordConfirmation: '',
})

/* Validators */
const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)

const validatePhone = (phone) => /^[0-9]{10,20}$/.test(phone)

/* Submit */
const submit = async () => {
  if (!form.firstName || !form.lastName || !form.email || !form.password) {
    return showModal('Lỗi', 'Vui lòng điền đầy đủ thông tin bắt buộc!')
  }

  if (!validateEmail(form.email)) {
    return showModal('Lỗi', 'Email không hợp lệ!')
  }

  if (form.password !== form.passwordConfirmation) {
    return showModal('Lỗi', 'Mật khẩu xác nhận không khớp!')
  }

  if (form.phone && !validatePhone(form.phone)) {
    return showModal('Lỗi', 'Số điện thoại phải từ 10–20 chữ số!')
  }

  const content = {
    firstName: form.firstName,
    lastName: form.lastName,
    email: form.email,
    phone: form.phone || null,
    address: form.address || null,
    password: form.password,
  }

  try {
    const res = await api.post('/auth/register', content)

    if (res.data?.success) {
      showModal('Thành công', 'Đăng ký thành công! Vui lòng đăng nhập.')
      setTimeout(() => router.push('/login'), 1200)
    } else {
      showModal('Lỗi', res.data?.message || 'Đăng ký thất bại!')
    }
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Đã có lỗi xảy ra!')
  }
}
</script>

<template>
  <LayoutGuest>
    <SectionFullScreen bg="softBlue">
      <CardBox
        is-form
        @submit.prevent="submit"
        class="w-11/12 rounded-xl bg-white/90 p-6 shadow-lg transition-shadow duration-300 hover:shadow-2xl sm:w-10/12 md:w-8/12 lg:w-6/12 xl:w-5/12 dark:bg-slate-900/80"
      >
        <!-- Logo -->
        <div class="mb-2 flex justify-center">
          <img :src="logo" alt="Logo" class="h-24 w-auto" />
        </div>

        <!-- Title -->
        <h2 class="mb-4 text-center text-2xl font-bold text-gray-800 md:text-3xl dark:text-white">
          Tạo tài khoản mới
        </h2>

        <!-- Form -->
        <div class="grid grid-cols-1 gap-2 sm:grid-cols-2">
          <FormField label="Họ">
            <FormControl v-model="form.firstName" :icon="mdiAccount" />
          </FormField>

          <FormField label="Tên">
            <FormControl v-model="form.lastName" :icon="mdiAccount" />
          </FormField>

          <FormField label="Email" class="sm:col-span-2">
            <FormControl v-model="form.email" :icon="mdiEmail" type="email" autocomplete="email" />
          </FormField>

          <FormField label="Số điện thoại" help="Tùy chọn">
            <FormControl v-model="form.phone" :icon="mdiPhone" type="tel" autocomplete="tel" />
          </FormField>

          <FormField label="Địa chỉ" help="Tùy chọn">
            <FormControl
              v-model="form.address"
              :icon="mdiHome"
              type="text"
              autocomplete="street-address"
            />
          </FormField>

          <FormField label="Mật khẩu">
            <FormControl
              v-model="form.password"
              :icon="mdiAsterisk"
              type="password"
              autocomplete="new-password"
            />
          </FormField>

          <FormField label="Xác nhận mật khẩu">
            <FormControl
              v-model="form.passwordConfirmation"
              :icon="mdiAsterisk"
              type="password"
              autocomplete="new-password"
            />
          </FormField>
        </div>

        <!-- Submit -->
        <div class="mt-4 flex justify-center">
          <BaseButton type="submit" color="info" label="Đăng ký" class="w-full sm:w-1/2" />
        </div>

        <!-- Footer -->
        <template #footer>
          <div class="mt-2 text-center">
            <p class="text-sm text-gray-800 dark:text-white">
              Bạn đã có tài khoản?
              <span
                class="cursor-pointer text-blue-500 hover:underline"
                @click="router.push('/login')"
              >
                Đăng nhập
              </span>
            </p>
          </div>
        </template>
      </CardBox>

      <!-- Error -->
      <CardBoxModal v-model="modalActive" :title="modalTitle" button="OK" button-label="Đóng">
        <p>{{ modalMessage }}</p>
      </CardBoxModal>
    </SectionFullScreen>
  </LayoutGuest>
</template>
