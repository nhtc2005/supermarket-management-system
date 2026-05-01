<script setup>
import { mdiForwardburger, mdiBackburger, mdiMenu } from '@mdi/js'
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import menuAside from '@/menuAside.js'
import menuNavBar from '@/menuNavBar.js'
import { useDarkModeStore } from '@/stores/darkMode.js'
import BaseIcon from '@/components/BaseIcon.vue'
import NavBar from '@/components/NavBar.vue'
import NavBarItemPlain from '@/components/NavBarItemPlain.vue'
import AsideMenu from '@/components/AsideMenu.vue'
import FooterBar from '@/components/FooterBar.vue'
import { api } from '@/plugins/axios'

const layoutAsidePadding = 'xl:pl-80'

const darkModeStore = useDarkModeStore()

const router = useRouter()

const isAsideMobileExpanded = ref(false)
const isAsideLgActive = ref(false)

const isNotificationOpen = ref(false)
const notificationRef = ref(null)

const savedUser = JSON.parse(localStorage.getItem('user') || '{}')
const role = savedUser.role || ''

const navBarMenu = menuNavBar[role] || []
const asideMenu = menuAside[role] || []

router.beforeEach(() => {
  isAsideMobileExpanded.value = false
  isAsideLgActive.value = false
})

const notifications = ref([])

const fetchNotifications = async () => {
  // try {
  //   const res = await api.get(`/notifications?role=${role}`)
  //   notifications.value = res.data
  // } catch (e) {
  //   console.error('Lỗi load thông báo:', e)
  // }
}

const menuClick = (event, item) => {
  if (item.isToggleLightDark) {
    darkModeStore.set()
  }

  if (item.isLogout) {
    localStorage.removeItem('user')
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    router.push('/login')
  }

  if (item.label === 'Thông báo') {
    isNotificationOpen.value = !isNotificationOpen.value
  }
}

const handleClickOutside = (event) => {
  if (notificationRef.value && !notificationRef.value.contains(event.target)) {
    isNotificationOpen.value = false
  }
}

onMounted(() => {
  fetchNotifications()
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div
    :class="{
      'overflow-hidden lg:overflow-visible': isAsideMobileExpanded,
    }"
  >
    <div
      :class="[layoutAsidePadding, { 'ml-80 lg:ml-0': isAsideMobileExpanded }]"
      class="min-h-screen w-screen bg-gray-50 pt-14 transition-(--transition-position) lg:w-auto dark:bg-slate-800 dark:text-slate-100"
    >
      <NavBar
        :menu="navBarMenu"
        :class="[layoutAsidePadding, { 'ml-80 lg:ml-0': isAsideMobileExpanded }]"
        @menu-click="menuClick"
      >
        <NavBarItemPlain
          display="flex lg:hidden"
          @click.prevent="isAsideMobileExpanded = !isAsideMobileExpanded"
        >
          <BaseIcon :path="isAsideMobileExpanded ? mdiBackburger : mdiForwardburger" size="24" />
        </NavBarItemPlain>
        <NavBarItemPlain display="hidden lg:flex xl:hidden" @click.prevent="isAsideLgActive = true">
          <BaseIcon :path="mdiMenu" size="24" />
        </NavBarItemPlain>
      </NavBar>

      <transition name="fade">
        <div
          v-if="isNotificationOpen"
          class="absolute top-14 right-6 z-50 w-80 rounded-lg bg-white p-4 shadow-lg dark:bg-slate-900"
        >
          <div class="mb-2 flex items-center justify-between">
            <h3 class="font-bold text-gray-800 dark:text-white">Thông báo mới</h3>
            <button
              @click="isNotificationOpen = false"
              class="text-gray-500 hover:text-gray-700 dark:hover:text-white"
            >
              ✕
            </button>
          </div>
          <ul class="max-h-64 divide-y divide-gray-200 overflow-y-auto dark:divide-slate-700">
            <li
              v-for="(note, index) in notifications"
              :key="index"
              class="py-2 text-sm text-gray-700 dark:text-gray-200"
            >
              {{ note }}
            </li>
          </ul>
        </div>
      </transition>

      <AsideMenu
        :is-aside-mobile-expanded="isAsideMobileExpanded"
        :is-aside-lg-active="isAsideLgActive"
        :menu="asideMenu"
        @menu-click="menuClick"
        @aside-lg-close-click="isAsideLgActive = false"
      />
      <slot />
      <FooterBar> Hệ thống quản lý siêu thị </FooterBar>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
