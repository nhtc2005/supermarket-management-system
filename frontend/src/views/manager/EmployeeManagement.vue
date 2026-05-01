<script setup>
// imports
import { ref, computed, onMounted } from 'vue'
import { mdiAccount, mdiMagnify, mdiEye, mdiDelete } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import FormControl from '@/components/FormControl.vue'
import FormField from '@/components/FormField.vue'
import { api } from '@/plugins/axios'
import { useMainStore } from '@/stores/main'

const user = JSON.parse(localStorage.getItem('user'))
const userId = user?.id || ''
// role map
const roleMap = {
  admin: 'Quản trị viên',
  manager: 'Quản lý',
  sales: 'Nhân viên bán hàng',
  warehouse: 'Nhân viên kho',
}

// search
const searchQuery = ref('')
const employees = ref([])
const loading = ref(false)

// filtered list
const filteredEmployees = computed(() => {
  const q = searchQuery.value.toLowerCase()
  return employees.value.filter(
    (e) =>
      e.id.toLowerCase().includes(q) ||
      e.username.toLowerCase().includes(q) ||
      e.name.toLowerCase().includes(q),
  )
})

// detail modal
const modalActive = ref(false)
const selectedEmployee = ref(null)
const loadingDetail = ref(false)

// add modal
const addModalActive = ref(false)
const newEmployee = ref({ username: '', firstName: '', lastName: '', role: 'sales' })

// notify modal
const notifyActive = ref(false)
const notifyTitle = ref('')
const notifyMessage = ref('')

// confirm modal
const confirmActive = ref(false)
const confirmTitle = ref('')
const confirmMessage = ref('')
const confirmCallback = ref(null)

// show notify
const showNotify = (title, msg) => {
  notifyTitle.value = title
  notifyMessage.value = msg
  notifyActive.value = true
}

// show confirm
const showConfirm = (title, msg, cb) => {
  confirmTitle.value = title
  confirmMessage.value = msg
  confirmCallback.value = cb
  confirmActive.value = true
}

// confirm action
const confirmAction = () => {
  if (typeof confirmCallback.value === 'function') confirmCallback.value()
  confirmActive.value = false
}

// token
const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

// fetch employees
const fetchEmployees = async () => {
  loading.value = true
  try {
    const res = await api.get('/employees', {
      headers: getTokenHeader(),
      params: { page: 0, size: 1000 },
    })
    employees.value = res.data.data.content.map((e) => ({
      id: `${e.id}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type.toLowerCase(),
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    }))
  } catch (err) {
    showNotify('Lỗi', 'Không thể tải danh sách nhân viên')
  }
  loading.value = false
}

// fetch detail
const fetchEmployeeDetail = async (id) => {
  loadingDetail.value = true
  selectedEmployee.value = null
  try {
    const res = await api.get(`/employees/${id}`, { headers: getTokenHeader() })
    const e = res.data.data
    selectedEmployee.value = {
      id: `${e.id}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type,
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    }
  } catch (err) {
    selectedEmployee.value = employees.value.find((e) => e.id === id)
  }
  loadingDetail.value = false
}

// open detail
const viewEmployee = (emp) => {
  modalActive.value = true
  fetchEmployeeDetail(emp.id)
}

// open add
const openAddModal = () => {
  newEmployee.value = { username: '', firstName: '', lastName: '', role: 'sales' }
  addModalActive.value = true
}

// add employee
const addEmployee = async () => {
  try {
    const body = {
      employee: {
        username: newEmployee.value.username,
        password: '123456',
        firstName: newEmployee.value.firstName,
        lastName: newEmployee.value.lastName,
        managerId: userId,
      },
    }

    let res
    if (newEmployee.value.role.id === 'warehouse') {
      body.warehouseId = 1
      res = await api.post('/employees/warehouse', body, { headers: getTokenHeader() })
    } else {
      body.storeId = 0
      res = await api.post('/employees/sales', body, { headers: getTokenHeader() })
    }

    const e = res.data.data
    employees.value.push({
      id: `${e.id}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type.toLowerCase(),
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    })

    addModalActive.value = false
    showNotify('Thành công', 'Đã thêm nhân viên mới')
  } catch (err) {
    showNotify('Lỗi', err)
  }
}

// delete employee
const deleteEmployee = (id) => {
  showConfirm('Xác nhận xóa', 'Bạn có chắc muốn xóa nhân viên này không?', async () => {
    try {
      const numericId = parseInt(id.replace('EMP', ''))
      await api.delete(`/employees/${numericId}`, { headers: getTokenHeader() })
      employees.value = employees.value.filter((e) => e.id !== id)
      showNotify('Thành công', 'Đã xóa nhân viên')
    } catch (err) {
      showNotify('Lỗi', 'Không thể xóa nhân viên')
    }
  })
}

onMounted(fetchEmployees)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine :icon="mdiAccount" title="Quản lý nhân viên" main>
        <button
          @click="openAddModal"
          class="rounded-lg bg-blue-600 px-4 py-2 text-white shadow hover:bg-blue-700"
        >
          + Thêm nhân viên
        </button>
      </SectionTitleLine>

      <CardBox class="mb-6 rounded-xl border bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm nhân viên</h3>
            <p class="text-sm text-gray-600">Tìm theo mã số, tên hoặc tên đăng nhập</p>
          </div>
          <div class="relative w-full md:w-96">
            <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiMagnify" />
              </svg>
            </div>
            <FormControl
              v-model="searchQuery"
              placeholder="Nhập ID, tên hoặc username..."
              class="pl-10"
            />
          </div>
        </div>
      </CardBox>

      <CardBox class="overflow-hidden rounded-xl border bg-white shadow-sm" has-table>
        <div class="border-b bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách nhân viên</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredEmployees.length }} nhân viên
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã số
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Họ và tên
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Tên đăng nhập
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Vai trò
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody class="divide-y bg-white">
              <!-- Loading -->
              <tr v-if="loading">
                <td colspan="5" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <div
                      class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
                    ></div>
                    <p class="mt-4 text-sm text-gray-600">Đang tải dữ liệu...</p>
                  </div>
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="!loading && filteredEmployees.length === 0">
                <td colspan="5" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không có dữ liệu</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery">Không tìm thấy kết quả cho "{{ searchQuery }}"</span>
                      <span v-else>Chưa có dữ liệu trong hệ thống</span>
                    </p>
                  </div>
                </td>
              </tr>

              <!-- Table rows -->
              <tr
                v-for="emp in filteredEmployees"
                :key="emp.id"
                class="transition-colors hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">{{ emp.id }}</td>
                <td class="px-6 py-4">{{ emp.name }}</td>
                <td class="px-6 py-4">{{ emp.username }}</td>
                <td class="px-6 py-4">{{ roleMap[emp.role] || emp.role }}</td>
                <td class="px-6 py-4">
                  <div class="flex items-center justify-center gap-2">
                    <button
                      @click="viewEmployee(emp)"
                      class="rounded-lg bg-blue-100 p-2 text-blue-600 hover:bg-blue-200"
                      title="Xem chi tiết"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiEye" />
                      </svg>
                    </button>

                    <button
                      @click="deleteEmployee(emp.id)"
                      class="rounded-lg bg-red-100 p-2 text-red-600 hover:bg-red-200"
                      title="Xóa nhân viên"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiDelete" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Detail Modal -->
      <CardBoxModal v-model="modalActive" title="Chi tiết nhân viên" button-label="Đóng">
        <div v-if="selectedEmployee" class="space-y-6">
          <div class="flex justify-between border-b pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ selectedEmployee.name }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                Mã số: {{ selectedEmployee.id }} • Ngày tuyển dụng: {{ selectedEmployee.hireDate }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiAccount" />
              </svg>
            </div>
          </div>

          <div class="grid gap-6 sm:grid-cols-2">
            <div class="space-y-4">
              <h4 class="text-sm font-semibold text-gray-500 uppercase">Thông tin cơ bản</h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-sm font-medium">Username</span
                  ><span>{{ selectedEmployee.username }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium">Họ & tên</span
                  ><span>{{ selectedEmployee.name }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium">Vai trò</span
                  ><span>{{ roleMap[selectedEmployee.role] }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium">Ngày tuyển dụng</span
                  ><span>{{ selectedEmployee.hireDate }}</span>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <h4 class="text-sm font-semibold text-gray-500 uppercase">Thông tin khác</h4>
            </div>
          </div>
        </div>

        <!-- Loading detail -->
        <div v-else class="flex items-center justify-center py-12">
          <div
            class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
        </div>
      </CardBoxModal>

      <!-- Add Modal -->
      <CardBoxModal
        v-model="addModalActive"
        title="Thêm nhân viên mới"
        button-label="Thêm"
        has-cancel
        @confirm="addEmployee"
      >
        <div class="space-y-4">
          <FormField label="Username"><FormControl v-model="newEmployee.username" /></FormField>
          <FormField label="Họ"><FormControl v-model="newEmployee.firstName" /></FormField>
          <FormField label="Tên"><FormControl v-model="newEmployee.lastName" /></FormField>
          <FormField label="Vai trò">
            <FormControl
              v-model="newEmployee.role"
              :options="[
                { id: 'sales', label: 'Nhân viên bán hàng' },
                { id: 'warehouse', label: 'Nhân viên kho' },
              ]"
            />
          </FormField>
        </div>
      </CardBoxModal>

      <!-- Notify Modal -->
      <CardBoxModal v-model="notifyActive" :title="notifyTitle" button-label="Đóng">
        <p>{{ notifyMessage }}</p>
      </CardBoxModal>

      <!-- Confirm Modal -->
      <CardBoxModal
        v-model="confirmActive"
        :title="confirmTitle"
        button-label="Xác nhận"
        has-cancel
        @confirm="confirmAction"
      >
        <p>{{ confirmMessage }}</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
