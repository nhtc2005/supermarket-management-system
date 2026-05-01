import axios from 'axios'
import { useMainStore } from '@/stores/main'

export const api = axios.create({
  baseURL: "/api/v1",
  withCredentials: true,
})

api.interceptors.request.use(async (config) => {
  const store = useMainStore()

  if (store.isAccessTokenExpired() && store.refreshToken) {
    await store.refreshAccessToken()
  }

  if (store.accessToken) {
    config.headers.Authorization = `Bearer ${store.accessToken}`
  }

  return config
}, (error) => Promise.reject(error))

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const store = useMainStore()
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry && store.refreshToken) {
      originalRequest._retry = true
      const refreshed = await store.refreshAccessToken()
      if (refreshed) {
        originalRequest.headers.Authorization = `Bearer ${store.accessToken}`
        return api(originalRequest)
      }
    }
    return Promise.reject(error)
  }
)
