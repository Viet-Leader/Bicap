export default {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
  },
  ADMIN: {
    ACCOUNTS: '/accounts',
    FARMS: '/farms',
    CROPS: '/crops',
    PRODUCTS: '/products',
    ORDERS: '/orders',
    NOTIFICATIONS: '/notifications'
  },
  FARM: {
    PROFILE: '/farm/profile',
    SEASONS: '/farm/seasons'
  },
  TRACEABILITY: {
    GET_BATCH: (code) => `/trace/${code}`
  }
}
