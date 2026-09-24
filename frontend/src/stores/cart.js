import { defineStore } from 'pinia'
import axiosClient from '../api/axiosClient'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cart: null,
    isLoading: false,
    error: '',
  }),
  getters: {
    items: (state) => state.cart?.items || [],
    totalAmount: (state) => state.cart?.totalAmount || 0,
    farmId: (state) => state.cart?.farmId || null,
    isEmpty: (state) => !state.cart?.items?.length,
  },
  actions: {
    async load() {
      this.isLoading = true
      this.error = ''
      try {
        const { data } = await axiosClient.get('/carts/me')
        // Nếu API trả về cart rỗng (không có items) thì set null để isEmpty = true
        if (!data?.items?.length) {
          this.cart = null
        } else {
          this.cart = data
        }
      } catch (error) {
        if (error.response?.status === 404) {
          // Cart không tồn tại → giỏ hàng trống
          this.cart = null
        } else {
          this.error = error.response?.data?.message || 'Unable to load cart'
        }
      } finally {
        this.isLoading = false
      }
    },
    async addItem(batchId, quantity, productFarmId) {
      if (this.cart && this.cart.farmId && productFarmId && this.cart.farmId !== productFarmId) {
        throw new Error('Bạn chỉ có thể đặt hàng các sản phẩm từ cùng một Trang trại. Vui lòng thanh toán giỏ hàng hiện tại trước (Lỗi BR02).')
      }
      const { data } = await axiosClient.post('/carts/items', { batchId, quantity })
      this.cart = data
    },
    async updateItem(cartItemId, quantity) {
      const { data } = await axiosClient.put(`/carts/items/${cartItemId}`, { quantity })
      this.cart = data
    },
    async updateQuantity(cartItemId, quantity) {
      return this.updateItem(cartItemId, quantity)
    },
    async removeItem(cartItemId) {
      try {
        await axiosClient.delete(`/carts/items/${cartItemId}`)
        await this.load()
      } catch (error) {
        if (error.response?.status === 404) {
          this.cart = null
        } else {
          throw error
        }
      }
    },
    async checkout() {
      const { data } = await axiosClient.post('/orders/checkout')
      this.cart = null
      return data
    },
  },
})
