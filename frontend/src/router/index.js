import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },

  // Login
  {
    path: '/login',
    name: 'login',
    meta: { title: 'Login' },
    component: () => import('@/views/LoginView.vue'),
  },

  // Register
  {
    path: '/register',
    name: 'register',
    meta: { title: 'Register' },
    component: () => import('@/views/RegisterView.vue'),
  },

  // Profile
  {
    path: '/profile',
    name: 'profile',
    meta: { title: 'Profile', requiresAuth: true },
    component: () => import('@/views/ProfileView.vue'),
  },

  // Admin routes
  {
    path: '/admin-dashboard',
    name: 'admin-dashboard',
    meta: { title: 'Trang chủ - Quản trị viên', requiresAuth: true },
    component: () => import('@/views/admin/AdminDashboardView.vue'),
  },
  {
    path: '/admin/employees',
    name: 'admin-employees',
    meta: { title: 'Quản lý nhân viên', requiresAuth: true },
    component: () => import('@/views/admin/EmployeeManagementView.vue'),
  },
  {
    path: '/admin/settings',
    name: 'admin-settings',
    meta: { title: 'Cài đặt hệ thống', requiresAuth: true },
    component: () => import('@/views/admin/SettingsView.vue'),
  },
  {
    path: '/admin/logs',
    name: 'admin-logs',
    meta: { title: 'Nhật ký hoạt động', requiresAuth: true },
    component: () => import('@/views/admin/ActivityLogView.vue'),
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    meta: { title: 'Quản lý người dùng', requiresAuth: true },
    component: () => import('@/views/admin/UserManagementView.vue'),
  },
  {
    path: '/admin/reports',
    name: 'admin-reports',
    meta: { title: 'Báo cáo và thống kê', requiresAuth: true },
    component: () => import('@/views/admin/ReportsView.vue'),
  },

  // Sales routes
  {
    path: '/sales-dashboard',
    name: 'sales-dashboard',
    meta: { title: 'Dashboard Bán Hàng', requiresAuth: true },
    component: () => import('@/views/sales/SalesDashboardView.vue'),
  },
  {
    path: '/sales/pos',
    name: 'sales-pos',
    meta: { title: 'Bán Hàng', requiresAuth: true },
    component: () => import('@/views/sales/PointOfSaleView.vue'),
  },
  {
    path: '/sales/search',
    name: 'sales-search',
    meta: { title: 'Tra Cứu Sản Phẩm', requiresAuth: true },
    component: () => import('@/views/sales/ProductSearchView.vue'),
  },
  {
    path: '/sales/history',
    name: 'sales-history',
    meta: { title: 'Lịch Sử Bán Hàng', requiresAuth: true },
    component: () => import('@/views/sales/SalesHistoryView.vue'),
  },

  // Customer routes
  {
    path: '/customer-dashboard',
    name: 'customer-dashboard',
    meta: { title: 'Trang chủ - Khách hàng', requiresAuth: true },
    component: () => import('@/views/customer/CustomerDashboardView.vue'),
  },
  {
    path: '/customer/orders',
    name: 'customer-orders',
    meta: { title: 'Lịch Sử Mua Hàng', requiresAuth: true },
    component: () => import('@/views/customer/OrderHistoryView.vue'),
  },
  {
    path: '/customer/promotions',
    name: 'customer-promotions',
    meta: { title: 'Khuyến Mãi & Tích Điểm', requiresAuth: true },
    component: () => import('@/views/customer/CustomerPromotionsView.vue'),
  },

  // Manager routes
  {
    path: '/manager/dashboard',
    name: 'manager-dashboard',
    meta: { title: 'Tổng quan', requiresAuth: true },
    component: () => import('@/views/manager/ManagerDashboard.vue'),
  },
  {
    path: '/manager/employees',
    name: 'manage-employees',
    meta: { title: 'Quản lý nhân viên', requiresAuth: true },
    component: () => import('@/views/manager/EmployeeManagement.vue'),
  },
  {
    path: '/manager/products',
    name: 'manage-products',
    meta: { title: 'Quản lý hàng hóa', requiresAuth: true },
    component: () => import('@/views/manager/ProductManagement.vue'),
  },
  {
    path: '/manager/stores',
    name: 'manage-stores',
    meta: { title: 'Quản lý cửa hàng', requiresAuth: true },
    component: () => import('@/views/manager/StoreManagement.vue'),
  },
  {
    path: '/manager/discounts',
    name: 'manage-discounts',
    meta: { title: 'Quản lý ưu đãi giảm giá', requiresAuth: true },
    component: () => import('@/views/manager/DiscountManagement.vue'),
  },
  {
    path: '/manager/customers',
    name: 'search-customer',
    meta: { title: 'Tra cứu khách hàng', requiresAuth: true },
    component: () => import('@/views/manager/CustomerSerach.vue'),
  },

  // Warehouse routes
  {
    path: '/warehouse/dashboard',
    name: 'warehouse-dashboard',
    meta: { title: 'Trang chính', requiresAuth: true },
    component: () => import('@/views/warehouse/WarehouseDashboard.vue'),
  },
  {
    path: '/warehouse/products',
    name: 'search-product',
    meta: { title: 'Tra cứu lô hàng/Hàng hóa', requiresAuth: true },
    component: () => import('@/views/warehouse/ProductSearch.vue'),
  },
  {
    path: '/warehouse/receipts',
    name: 'search-receipt',
    meta: { title: 'Tra cứu phiếu', requiresAuth: true },
    component: () => import('@/views/warehouse/ReceiptSearch.vue'),
  },
  {
    path: '/warehouse/batches',
    name: 'search-batches',
    meta: { title: 'Tra cứu lô hàng', requiresAuth: true },
    component: () => import('@/views/warehouse/BatchSearch.vue'),
  },
  {
    path: '/warehouse/receipt',
    name: 'create-receipt',
    meta: { title: 'Tạo phiếu nhập/xuất', requiresAuth: true },
    component: () => import('@/views/warehouse/CreateReceipt.vue'),
  },
  {
    path: '/warehouse/transfer',
    name: 'transfer',
    meta: { title: 'Chuyển hàng', requiresAuth: true },
    component: () => import('@/views/warehouse/TransferView.vue'),
  },
  {
    path: '/warehouse/warehouses',
    name: 'manage-warehouses',
    meta: { title: 'Quản lý kho', requiresAuth: true },
    component: () => import('@/views/warehouse/WarehouseManagement.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  },
})

// Auth guard
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('user')

  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ path: '/login' })
  } else {
    next()
  }
})

export default router
