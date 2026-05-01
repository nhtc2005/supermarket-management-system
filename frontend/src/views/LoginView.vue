<script setup>
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { mdiAccount, mdiAsterisk } from '@mdi/js'

import logo from '@/assets/logo.png'
import { useMainStore } from '@/stores/main'

import LayoutGuest from '@/layouts/LayoutGuest.vue'
import SectionFullScreen from '@/components/SectionFullScreen.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'

const router = useRouter()
const mainStore = useMainStore()

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
  username: '',
  password: '',
})

/* Submit */
const submit = async () => {
  try {
    const success = await mainStore.login(form.username, form.password)

    if (success) {
      switch (mainStore.role) {
        case 'admin':
          router.push('/admin-dashboard')
          break
        case 'manager':
          router.push('/manager/dashboard')
          break
        case 'sales':
          router.push('/sales-dashboard')
          break
        case 'warehouse':
          router.push('/warehouse/dashboard')
          break
        case 'customer':
        default:
          router.push('/customer-dashboard')
      }
    } else {
      showModal('Lỗi', 'Tên đăng nhập hoặc mật khẩu không đúng!')
    }
  } catch (error) {
    showModal('Lỗi', 'Đã có lỗi xảy ra, vui lòng thử lại!')
    console.error(error)
  }
}
</script>

<template>
  <LayoutGuest>
    <SectionFullScreen bg="softBlue">
      <CardBox
        is-form
        @submit.prevent="submit"
        class="w-11/12 rounded-xl bg-white/90 p-4 shadow-lg transition-shadow duration-300 hover:shadow-2xl sm:w-8/12 md:w-6/12 lg:w-5/12 xl:w-3/12 dark:bg-slate-900/80"
      >
        <!-- Logo -->
        <div class="mb-4 flex justify-center">
          <img :src="logo" alt="Logo" class="h-30 w-auto" />
        </div>

        <!-- Title -->
        <h2 class="mb-6 text-center text-2xl font-bold text-gray-800 md:text-3xl dark:text-white">
          Đăng nhập
        </h2>

        <!-- Username -->
        <FormField label="Tên đăng nhập" help="Nhập tên đăng nhập hoặc email">
          <FormControl
            v-model="form.username"
            :icon="mdiAccount"
            name="login"
            autocomplete="username"
          />
        </FormField>

        <!-- Password -->
        <FormField label="Mật khẩu" help="Nhập mật khẩu tài khoản">
          <FormControl
            v-model="form.password"
            :icon="mdiAsterisk"
            type="password"
            name="password"
            autocomplete="current-password"
          />
        </FormField>

        <!-- Forgot password -->
        <div class="-mt-8 mb-4 text-right">
          <span class="text-sm text-blue-500 hover:underline"> Quên mật khẩu? </span>
        </div>

        <!-- Submit -->
        <div class="flex justify-center">
          <BaseButton type="submit" color="info" label="Đăng nhập" class="w-full" />
        </div>

        <!-- Footer -->
        <template #footer>
          <div class="text-center">
            <p class="text-sm text-gray-800 dark:text-white">
              Bạn chưa có tài khoản?
              <RouterLink to="/register" class="text-sm text-blue-500 hover:underline">
                Tạo tài khoản mới
              </RouterLink>
            </p>
          </div>
        </template>
      </CardBox>

      <!-- Modal -->
      <CardBoxModal v-model="modalActive" :title="modalTitle" button="OK" button-label="Đóng">
        <p>{{ modalMessage }}</p>
      </CardBoxModal>
    </SectionFullScreen>
  </LayoutGuest>
</template>
