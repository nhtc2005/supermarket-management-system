import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as jwtDecode from 'jwt-decode'
import { api } from '@/plugins/axios.js'

export const useMainStore = defineStore('main', () => {
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}')

  const userId = ref(savedUser.id || null)
  const userType = ref(savedUser.userType || '')
  const role = ref(savedUser.role || '')
  const userName = ref(savedUser.name || '')
  const userEmail = ref(savedUser.email || '')
  const userPhone = ref(savedUser.phone || '')
  const userAddress = ref(savedUser.address || '')
  const hiredDate = ref(savedUser.hiredDate || '')
  const points = ref(savedUser.points || 0)

  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')

  const userAvatar = computed(() => {
    const seed = userEmail.value || userName.value || 'default'
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed.replace(/[^a-z0-9]+/gi, '-')}`
  })

  function saveToLocalStorage() {
    const userData = {
      id: userId.value,
      userType: userType.value,
      role: role.value,
      name: userName.value,
      email: userEmail.value,
      phone: userPhone.value,
      address: userAddress.value,
      hiredDate: hiredDate.value,
      points: points.value,
    }
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('accessToken', accessToken.value)
    localStorage.setItem('refreshToken', refreshToken.value)
  }

  function setUser(data) {
    const user = data.user

    userId.value = user.id
    userType.value = data.userType.toLowerCase()
    role.value = data.userType === 'EMPLOYEE' ? user.employee_type.toLowerCase() : 'customer'
    userName.value = `${user.first_name} ${user.last_name}`
    userEmail.value = user.email || ''
    userPhone.value = user.phone || ''
    userAddress.value = user.address || ''
    hiredDate.value = user.hired_at || ''
    points.value = user.loyalty_points || 0

    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken

    saveToLocalStorage()
  }

  async function login(identifier, password) {
    try {
      const response = await api.post('/auth/login', {
        identifier,
        password,
      })

      if (response.data?.success) {
        setUser(response.data.data)
        return true
      } else {
        return false
      }
    } catch (error) {
      console.error('Login error:', error)
      return false
    }
  }

  function logout() {
    userId.value = null
    userType.value = ''
    role.value = ''
    userName.value = ''
    userEmail.value = ''
    userPhone.value = ''
    userAddress.value = ''
    hiredDate.value = ''
    points.value = 0
    accessToken.value = ''
    refreshToken.value = ''

    localStorage.removeItem('user')
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  function isAccessTokenExpired() {
    if (!accessToken.value) return true
    try {
      const decoded = jwtDecode(accessToken.value)
      return Date.now() >= decoded.exp * 1000
    } catch {
      return true
    }
  }

  async function refreshAccessToken() {
    if (!refreshToken.value) throw new Error('No refresh token available')
    try {
      const res = await api.post('/auth/refresh', { refreshToken: refreshToken.value })

      if (res.data?.success) {
        accessToken.value = res.data.data.accessToken
        refreshToken.value = res.data.data.refreshToken
        saveToLocalStorage()
        return true
      } else {
        return false
      }
    } catch (err) {
      console.error('Refresh token failed', err)
      logout()
      return false
    }
  }

  return {
    userId,
    userType,
    role,
    userName,
    userEmail,
    userPhone,
    userAddress,
    hiredDate,
    points,
    userAvatar,
    setUser,
    login,
    logout,
    isAccessTokenExpired,
    refreshAccessToken,
  }
})
