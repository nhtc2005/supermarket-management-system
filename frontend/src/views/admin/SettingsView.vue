<script setup>
import { ref } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import AdminNavTabs from '@/components/AdminNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import FormCheckRadio from '@/components/FormCheckRadio.vue'
import {
  mdiHome,
  mdiBell,
  mdiLock,
  mdiPrinter,
} from '@mdi/js'

const activeTab = ref('general')
const activeSubTab = ref('general')

const generalSettings = ref({
  supermarketName: 'Siêu Thị ABC',
  phone: '1900-1234',
  address: '123 Đường ABC, Quận 1, TP.HCM',
  taxCode: '0123456789',
})

const notificationSettings = ref({
  lowStockWarning: true,
  expirationWarning: true,
  dailyReportEmail: false,
})

const securitySettings = ref({
  twoFactorAuth: false,
  logActionHistory: true,
  sessionTimeout: 30,
  minPasswordLength: 8,
})

const deviceSettings = ref({
  invoicePrinter: true,
  barcodeScanner: true,
  printerPort: 'COM1',
  scannerPort: 'USB',
})

const saveSettings = () => {
  alert('Đã lưu cài đặt thành công!')
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">Quản Trị Hệ Thống</h1>
        <p class="text-gray-600 dark:text-gray-400">Chào mừng, Nguyễn Văn Admin</p>
      </div>

      <!-- Navigation Tabs -->
      <AdminNavTabs />

      <CardBox>
        <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-6">Cài Đặt Hệ Thống</h2>

        <!-- Sub Navigation Tabs -->
        <div class="flex space-x-2 mb-6 border-b border-gray-200 dark:border-gray-700">
          <button
            @click="activeSubTab = 'general'"
            class="px-4 py-2 rounded-t-lg flex items-center space-x-2"
            :class="
              activeSubTab === 'general'
                ? 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-white font-medium'
                : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800'
            "
          >
            <BaseIcon :path="mdiHome" size="20" />
            <span>Chung</span>
          </button>
          <button
            @click="activeSubTab = 'notifications'"
            class="px-4 py-2 rounded-t-lg flex items-center space-x-2"
            :class="
              activeSubTab === 'notifications'
                ? 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-white font-medium'
                : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800'
            "
          >
            <BaseIcon :path="mdiBell" size="20" />
            <span>Thông báo</span>
          </button>
          <button
            @click="activeSubTab = 'security'"
            class="px-4 py-2 rounded-t-lg flex items-center space-x-2"
            :class="
              activeSubTab === 'security'
                ? 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-white font-medium'
                : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800'
            "
          >
            <BaseIcon :path="mdiLock" size="20" />
            <span>Bảo mật</span>
          </button>
          <button
            @click="activeSubTab = 'devices'"
            class="px-4 py-2 rounded-t-lg flex items-center space-x-2"
            :class="
              activeSubTab === 'devices'
                ? 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-white font-medium'
                : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800'
            "
          >
            <BaseIcon :path="mdiPrinter" size="20" />
            <span>Thiết bị</span>
          </button>
        </div>

        <!-- General Tab -->
        <div v-if="activeSubTab === 'general'" class="space-y-4">
          <FormField label="Tên siêu thị">
            <FormControl v-model="generalSettings.supermarketName" />
          </FormField>
          <FormField label="Số điện thoại">
            <FormControl v-model="generalSettings.phone" />
          </FormField>
          <FormField label="Địa chỉ">
            <FormControl v-model="generalSettings.address" />
          </FormField>
          <FormField label="Mã số thuế">
            <FormControl v-model="generalSettings.taxCode" />
          </FormField>
        </div>

        <!-- Notifications Tab -->
        <div v-if="activeSubTab === 'notifications'" class="space-y-4">
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Cảnh báo tồn kho thấp</span>
            <FormCheckRadio
              v-model="notificationSettings.lowStockWarning"
              type="switch"
              name="lowStockWarning"
              :input-value="true"
            />
          </div>
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Cảnh báo hết hạn</span>
            <FormCheckRadio
              v-model="notificationSettings.expirationWarning"
              type="switch"
              name="expirationWarning"
              :input-value="true"
            />
          </div>
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Email báo cáo hàng ngày</span>
            <FormCheckRadio
              v-model="notificationSettings.dailyReportEmail"
              type="switch"
              name="dailyReportEmail"
              :input-value="true"
            />
          </div>
        </div>

        <!-- Security Tab -->
        <div v-if="activeSubTab === 'security'" class="space-y-4">
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Xác thực 2 yếu tố</span>
            <FormCheckRadio
              v-model="securitySettings.twoFactorAuth"
              type="switch"
              name="twoFactorAuth"
              :input-value="true"
            />
          </div>
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Ghi lại lịch sử thao tác</span>
            <FormCheckRadio
              v-model="securitySettings.logActionHistory"
              type="switch"
              name="logActionHistory"
              :input-value="true"
            />
          </div>
          <FormField label="Timeout phiên (phút)">
            <FormControl v-model="securitySettings.sessionTimeout" type="number" />
          </FormField>
          <FormField label="Độ dài mật khẩu tối thiểu">
            <FormControl v-model="securitySettings.minPasswordLength" type="number" />
          </FormField>
        </div>

        <!-- Devices Tab -->
        <div v-if="activeSubTab === 'devices'" class="space-y-4">
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Máy in hóa đơn</span>
            <FormCheckRadio
              v-model="deviceSettings.invoicePrinter"
              type="switch"
              name="invoicePrinter"
              :input-value="true"
            />
          </div>
          <div class="flex justify-between items-center p-4 rounded-lg bg-gray-50 dark:bg-slate-800">
            <span class="text-gray-700 dark:text-gray-300">Máy quét mã vạch</span>
            <FormCheckRadio
              v-model="deviceSettings.barcodeScanner"
              type="switch"
              name="barcodeScanner"
              :input-value="true"
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <FormField label="Cổng máy in">
              <FormControl v-model="deviceSettings.printerPort" />
            </FormField>
            <FormField label="Cổng máy quét">
              <FormControl v-model="deviceSettings.scannerPort" />
            </FormField>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-end space-x-4 mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
          <BaseButton label="Hủy" color="lightDark" />
          <BaseButton label="Lưu" color="info" @click="saveSettings" />
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>

