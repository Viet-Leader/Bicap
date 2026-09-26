import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import LoginView from './LoginView.vue'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', component: { template: '<div>home</div>' } },
  { path: '/admin', component: { template: '<div>admin</div>' } },
  { path: '/farm', component: { template: '<div>farm</div>' } },
  { path: '/retailer', component: { template: '<div>retailer</div>' } },
  { path: '/login', component: { template: '<div>login</div>' } },
]

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('clicking login with valid credentials updates auth state and redirects', async () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes,
    })

    const wrapper = mount(LoginView, {
      global: {
        plugins: [router],
      },
    })

    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('admin@bicap.vn')
    await inputs[1].setValue('secret123')

    await wrapper.findAll('button')[0].trigger('click')
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 0))

    const auth = useAuthStore()
    expect(auth.role).toBe('ADMIN')
    expect(auth.isAuthenticated).toBe(true)
    expect(router.currentRoute.value.path).toBe('/admin')
  })
})
