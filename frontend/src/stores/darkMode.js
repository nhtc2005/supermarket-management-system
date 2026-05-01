import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDarkModeStore = defineStore('darkMode', () => {
  const saved = typeof localStorage !== 'undefined' ? localStorage.getItem('darkMode') : null

  const isEnabled = ref(saved === '1')
  const isInProgress = ref(false)

  function set(payload = null) {
    isInProgress.value = true
    isEnabled.value = payload !== null ? payload : !isEnabled.value

    if (typeof document !== 'undefined') {
      document.body.classList[isEnabled.value ? 'add' : 'remove']('dark-scrollbars')

      document.documentElement.classList[isEnabled.value ? 'add' : 'remove'](
        'dark',
        'dark-scrollbars-compat',
      )
    }

    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('darkMode', isEnabled.value ? '1' : '0')
    }

    setTimeout(() => {
      isInProgress.value = false
    }, 200)
  }

  return {
    isEnabled,
    isInProgress,
    set,
  }
})
