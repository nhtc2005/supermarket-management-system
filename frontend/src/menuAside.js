import {
  mdiMonitor,
  mdiClipboardPlus,
  mdiClipboardCheck,
  mdiDatabaseSearch,
  mdiPackageVariant,
  mdiArrowRight,
  mdiWarehouse,
  mdiStore,
  mdiAccountGroup,
  mdiAccount,
  mdiSale,
  mdiChartBar,
  mdiCashRegister,
  mdiCog,
  mdiCart,
  mdiGift,
} from '@mdi/js'

export default {
  admin: [
    { to: '/admin-dashboard', icon: mdiMonitor, label: 'Tổng quan' },
    { to: '/admin/employees', icon: mdiAccountGroup, label: 'Nhân viên' },
    { to: '/admin/settings', icon: mdiCog, label: 'Cài đặt' },
    { to: '/admin/logs', icon: mdiChartBar, label: 'Nhật ký' },
  ],

  manager: [
    { to: '/manager/dashboard', icon: mdiMonitor, label: 'Tổng quan' },
    {
      label: 'Quản lý',
      icon: mdiClipboardCheck,
      menu: [
        { to: '/manager/employees', icon: mdiAccountGroup, label: 'Nhân viên' },
        { to: '/manager/products', icon: mdiPackageVariant, label: 'Hàng hóa' },
        { to: '/manager/stores', icon: mdiStore, label: 'Cửa hàng' },
        { to: '/manager/discounts', icon: mdiSale, label: 'Ưu đãi giảm giá' },
      ],
    },
    { to: '/manager/customers', icon: mdiAccount, label: 'Tra cứu khách hàng' },
  ],

  warehouse: [
    { to: '/warehouse/dashboard', icon: mdiMonitor, label: 'Tổng quan' },
    {
      label: 'Tra cứu',
      icon: mdiDatabaseSearch,
      menu: [
        { to: '/warehouse/products', icon: mdiPackageVariant, label: 'Hàng hóa' },
        { to: '/warehouse/batches', icon: mdiPackageVariant, label: 'Lô hàng' },
        { to: '/warehouse/receipts', icon: mdiClipboardCheck, label: 'Phiếu nhập/xuất' },
      ],
    },
    { to: '/warehouse/receipt', icon: mdiClipboardPlus, label: 'Nhập/xuất kho' },
    { to: '/warehouse/transfer', icon: mdiArrowRight, label: 'Chuyển hàng vào cửa hàng' },
    { to: '/warehouse/warehouses', icon: mdiWarehouse, label: 'Quản lí kho' },
  ],

  sales: [
    { to: '/sales/pos', icon: mdiCart, label: 'Bán Hàng' },
    { to: '/sales/search', icon: mdiDatabaseSearch, label: 'Tra Cứu' },
    { to: '/sales/history', icon: mdiCashRegister, label: 'Lịch Sử' },
  ],

  customer: [
    { to: '/customer/orders', icon: mdiCashRegister, label: 'Lịch Sử Mua Hàng' },
    { to: '/customer/promotions', icon: mdiGift, label: 'Khuyến Mãi & Tích Điểm' },
  ],
}
