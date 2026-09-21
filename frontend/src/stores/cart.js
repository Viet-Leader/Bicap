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
        this.cart = data
      } catch (error) {
        this.error = error.response?.data?.message || 'Unable to load cart'
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
    async removeItem(cartItemId) {
      await axiosClient.delete(`/carts/items/${cartItemId}`)
      await this.load()
    },
    async checkout() {
      const { data } = await axiosClient.post('/orders/checkout')
      await this.load()
      return data
    },
  },
})
