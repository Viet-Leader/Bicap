export default {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
  },
  ADMIN: {
    ACCOUNTS: '/admin/accounts',
    FARMS: '/admin/farms'
  },
  FARM: {
    PROFILE: '/farm/profile',
    SEASONS: '/farm/seasons'
  },
  TRACEABILITY: {
    GET_BATCH: (code) => `/trace/${code}`
  }
}
