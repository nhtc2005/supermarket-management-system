<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import AdminNavTabs from '@/components/AdminNavTabs.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import {
  mdiPlus,
  mdiMagnify,
  mdiPencil,
  mdiDelete,
  mdiEmail,
  mdiPhone,
  mdiEye,
} from '@mdi/js'

const searchQuery = ref('')
const isModalActive = ref(false)
const isDeleteModalActive = ref(false)
const selectedEmployee = ref(null)
const form = ref({
  id: null,
  code: '',
  name: '',
  email: '',
  phone: '',
  role: '',
  startDate: '',
})

const employees = ref([
  {
    id: 1,
    code: 'E001',
    name: 'Nguyễn Văn A',
    email: 'nguyenvana@supermarket.com',
    phone: '0123456789',
    role: 'admin',
    startDate: '2023-01-15',
    status: 'working',
  },
  {
    id: 2,
    code: 'E002',
    name: 'Trần Thị B',
    email: 'tranthib@supermarket.com',
    phone: '0987654321',
    role: 'sales',
    startDate: '2023-02-20',
    status: 'working',
  },
  {
    id: 3,
    code: 'E003',
    name: 'Lê Văn C',
    email: 'levanc@supermarket.com',
    phone: '0912345678',
    role: 'warehouse',
    startDate: '2023-03-10',
    status: 'working',
  },
  {
    id: 4,
    code: 'E004',
    name: 'Phạm Thị D',
    email: 'phamthid@supermarket.com',
    phone: '0923456789',
    role: 'manager',
    startDate: '2023-04-05',
    status: 'working',
  },
  {
    id: 5,
    code: 'E005',
    name: 'Hoàng Văn E',
    email: 'hoangvane@supermarket.com',
    phone: '0934567890',
    role: 'admin',
    startDate: '2023-05-12',
    status: 'working',
  },
])

const roles = [
  { value: 'admin', label: 'Quản trị viên' },
  { value: 'sales', label: 'Nhân viên bán hàng' },
  { value: 'warehouse', label: 'Nhân viên kho' },
  { value: 'manager', label: 'Ban quản lý' },
]

const filteredEmployees = computed(() => {
  if (!searchQuery.value) return employees.value
  const query = searchQuery.value.toLowerCase()
  return employees.value.filter(
    (e) =>
      e.name.toLowerCase().includes(query) ||
      e.email.toLowerCase().includes(query) ||
      e.code.toLowerCase().includes(query),
  )
})

const getRoleLabel = (role) => {
  const roleObj = roles.find((r) => r.value === role)
  return roleObj ? roleObj.label : role
}

const getRoleColor = (role) => {
  const colors = {
    admin: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200',
    sales: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
    warehouse: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
    manager: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
  }
  return colors[role] || 'bg-gray-100 text-gray-800'
}

const openAddModal = () => {
  form.value = {
    id: null,
    code: '',
    name: '',
    email: '',
    phone: '',
    role: '',
    startDate: '',
  }
  selectedEmployee.value = null
  isModalActive.value = true
}

const openEditModal = (employee) => {
  form.value = { ...employee }
  selectedEmployee.value = employee
  isModalActive.value = true
}

const saveEmployee = () => {
  if (form.value.id) {
    const index = employees.value.findIndex((e) => e.id === form.value.id)
    if (index !== -1) {
      employees.value[index] = { ...form.value, status: 'working' }
    }
  } else {
    const newEmployee = {
      ...form.value,
      id: employees.value.length + 1,
      status: 'working',
    }
    employees.value.push(newEmployee)
  }
  isModalActive.value = false
}

const openDeleteModal = (employee) => {
  selectedEmployee.value = employee
  isDeleteModalActive.value = true
}

const deleteEmployee = () => {
  if (selectedEmployee.value) {
    employees.value = employees.value.filter((e) => e.id !== selectedEmployee.value.id)
    isDeleteModalActive.value = false
    selectedEmployee.value = null
  }
}

onMounted(() => {
  // Fetch employees
})
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
        <div class="flex justify-between items-center mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">Quản Lý Nhân Viên</h2>
            <p class="text-gray-600 dark:text-gray-400">Thêm, sửa, xóa và phân quyền nhân viên</p>
          </div>
          <BaseButton :icon="mdiPlus" label="Thêm Nhân Viên" color="info" @click="openAddModal" />
        </div>

        <div class="flex space-x-4 mb-6">
          <FormField class="flex-1">
            <FormControl
              v-model="searchQuery"
              :icon="mdiMagnify"
              placeholder="Tìm kiếm theo tên, email, mã nhân viên..."
            />
          </FormField>
        </div>

        <CardBox has-table>
          <table>
            <thead>
              <tr>
                <th>Mã NV</th>
                <th>Nhân viên</th>
                <th>Liên hệ</th>
                <th>Vai trò</th>
                <th>Ngày vào làm</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="employee in filteredEmployees" :key="employee.id">
                <td data-label="Mã NV">{{ employee.code }}</td>
                <td data-label="Nhân viên">
                  <div class="flex items-center space-x-3">
                    <UserAvatar :username="employee.name" class="h-10 w-10" />
                    <span class="font-medium">{{ employee.name }}</span>
                  </div>
                </td>
                <td data-label="Liên hệ">
                  <div class="space-y-1">
                    <div class="flex items-center space-x-1 text-sm">
                      <BaseIcon :path="mdiEmail" size="16" class="text-gray-500" />
                      <span>{{ employee.email }}</span>
                    </div>
                    <div class="flex items-center space-x-1 text-sm">
                      <BaseIcon :path="mdiPhone" size="16" class="text-gray-500" />
                      <span>{{ employee.phone }}</span>
                    </div>
                  </div>
                </td>
                <td data-label="Vai trò">
                  <span class="px-2 py-1 text-xs rounded-full" :class="getRoleColor(employee.role)">
                    {{ getRoleLabel(employee.role) }}
                  </span>
                </td>
                <td data-label="Ngày vào làm">{{ employee.startDate }}</td>
                <td data-label="Trạng thái">
                  <span
                    class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200"
                  >
                    Đang làm việc
                  </span>
                </td>
                <td class="before:hidden lg:w-1 whitespace-nowrap">
                  <BaseButtons type="justify-start lg:justify-end" no-wrap>
                    <BaseButton
                      :icon="mdiPencil"
                      color="info"
                      small
                      @click="openEditModal(employee)"
                    />
                    <BaseButton
                      :icon="mdiEye"
                      color="info"
                      small
                    />
                    <BaseButton
                      :icon="mdiDelete"
                      color="danger"
                      small
                      @click="openDeleteModal(employee)"
                    />
                  </BaseButtons>
                </td>
              </tr>
            </tbody>
          </table>
        </CardBox>
      </CardBox>

      <!-- Modal thêm/sửa -->
      <CardBoxModal
        v-model="isModalActive"
        :title="form.id ? 'Sửa nhân viên' : 'Thêm nhân viên'"
      >
        <FormField label="Mã nhân viên">
          <FormControl v-model="form.code" placeholder="Nhập mã nhân viên" />
        </FormField>
        <FormField label="Họ tên">
          <FormControl v-model="form.name" placeholder="Nhập họ tên" />
        </FormField>
        <FormField label="Email">
          <FormControl v-model="form.email" type="email" placeholder="Nhập email" />
        </FormField>
        <FormField label="Số điện thoại">
          <FormControl v-model="form.phone" placeholder="Nhập số điện thoại" />
        </FormField>
        <FormField label="Vai trò">
          <FormControl v-model="form.role" :options="roles" />
        </FormField>
        <FormField label="Ngày vào làm">
          <FormControl v-model="form.startDate" type="date" />
        </FormField>
        <template #footer>
          <BaseButton label="Hủy" color="lightDark" @click="isModalActive = false" />
          <BaseButton label="Lưu" color="info" @click="saveEmployee" />
        </template>
      </CardBoxModal>

      <!-- Modal xóa -->
      <CardBoxModal
        v-model="isDeleteModalActive"
        title="Xác nhận xóa"
        button="danger"
        has-cancel
        @confirm="deleteEmployee"
      >
        <p>Bạn có chắc chắn muốn xóa nhân viên <strong>{{ selectedEmployee?.name }}</strong>?</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>

