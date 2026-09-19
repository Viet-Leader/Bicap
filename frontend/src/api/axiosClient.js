import axios from 'axios'

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('bicap_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('bicap_token')
      localStorage.removeItem('bicap_user')
      window.dispatchEvent(new Event('bicap:unauthorized'))
    }
    return Promise.reject(error)
  },
)

export default axiosClient
