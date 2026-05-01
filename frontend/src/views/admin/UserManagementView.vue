<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLine.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import { mdiPlus, mdiMagnify, mdiPencil, mdiDelete, mdiAccountGroup } from '@mdi/js'

const users = ref([])
const searchQuery = ref('')
const isModalActive = ref(false)
const isDeleteModalActive = ref(false)
const selectedUser = ref(null)
const form = ref({
  id: null,
  username: '',
  email: '',
  fullName: '',
  role: 'customer',
  phone: '',
})

const roles = [
  { value: 'admin', label: 'Quản trị viên' },
  { value: 'manager', label: 'Quản lý' },
  { value: 'sales', label: 'Nhân viên bán hàng' },
  { value: 'warehouse', label: 'Nhân viên kho' },
  { value: 'customer', label: 'Khách hàng' },
]

const fetchUsers = async () => {
  try {
    // Giả lập dữ liệu
    users.value = [
      {
        id: 1,
        username: 'admin',
        email: 'admin@example.com',
        fullName: 'Nguyễn Văn Admin',
        role: 'admin',
        phone: '0123456789',
        createdAt: '2024-01-15',
      },
      {
        id: 2,
        username: 'manager1',
        email: 'manager@example.com',
        fullName: 'Trần Thị Quản Lý',
        role: 'manager',
        phone: '0987654321',
        createdAt: '2024-01-20',
      },
      {
        id: 3,
        username: 'sales1',
        email: 'sales@example.com',
        fullName: 'Lê Văn Bán Hàng',
        role: 'sales',
        phone: '0912345678',
        createdAt: '2024-02-01',
      },
    ]
  } catch (error) {
    console.error('Lỗi tải danh sách người dùng:', error)
  }
}

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(
    (user) =>
      user.username.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query) ||
      user.fullName.toLowerCase().includes(query),
  )
})

const openAddModal = () => {
  form.value = {
    id: null,
    username: '',
    email: '',
    fullName: '',
    role: 'customer',
    phone: '',
  }
  selectedUser.value = null
  isModalActive.value = true
}

const openEditModal = (user) => {
  form.value = { ...user }
  selectedUser.value = user
  isModalActive.value = true
}

const saveUser = () => {
  if (form.value.id) {
    // Cập nhật
    const index = users.value.findIndex((u) => u.id === form.value.id)
    if (index !== -1) {
      users.value[index] = { ...form.value }
    }
  } else {
    // Thêm mới
    const newUser = {
      ...form.value,
      id: users.value.length + 1,
      createdAt: new Date().toISOString().split('T')[0],
    }
    users.value.push(newUser)
  }
  isModalActive.value = false
}

const openDeleteModal = (user) => {
  selectedUser.value = user
  isDeleteModalActive.value = true
}

const deleteUser = () => {
  if (selectedUser.value) {
    users.value = users.value.filter((u) => u.id !== selectedUser.value.id)
    isDeleteModalActive.value = false
    selectedUser.value = null
  }
}

const getRoleLabel = (role) => {
  const roleObj = roles.find((r) => r.value === role)
  return roleObj ? roleObj.label : role
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiAccountGroup" title="Quản lý người dùng" main>
        <BaseButton :icon="mdiPlus" label="Thêm người dùng" color="info" @click="openAddModal" />
      </SectionTitleLineWithButton>

      <CardBox>
        <FormField>
          <FormControl
            v-model="searchQuery"
            :icon="mdiMagnify"
            placeholder="Tìm kiếm theo tên, email, username..."
          />
        </FormField>
      </CardBox>

      <CardBox has-table>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên đăng nhập</th>
              <th>Email</th>
              <th>Họ tên</th>
              <th>Vai trò</th>
              <th>Số điện thoại</th>
              <th>Ngày tạo</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td data-label="ID">{{ user.id }}</td>
              <td data-label="Tên đăng nhập">{{ user.username }}</td>
              <td data-label="Email">{{ user.email }}</td>
              <td data-label="Họ tên">{{ user.fullName }}</td>
              <td data-label="Vai trò">
                <span
                  class="rounded-full px-2 py-1 text-xs"
                  :class="{
                    'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200':
                      user.role === 'admin',
                    'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200':
                      user.role === 'manager',
                    'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200':
                      user.role === 'sales',
                    'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200':
                      user.role === 'warehouse',
                    'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200':
                      user.role === 'customer',
                  }"
                >
                  {{ getRoleLabel(user.role) }}
                </span>
              </td>
              <td data-label="Số điện thoại">{{ user.phone }}</td>
              <td data-label="Ngày tạo">{{ user.createdAt }}</td>
              <td class="whitespace-nowrap before:hidden lg:w-1">
                <BaseButtons type="justify-start lg:justify-end" no-wrap>
                  <BaseButton :icon="mdiPencil" color="info" small @click="openEditModal(user)" />
                  <BaseButton
                    :icon="mdiDelete"
                    color="danger"
                    small
                    @click="openDeleteModal(user)"
                  />
                </BaseButtons>
              </td>
            </tr>
          </tbody>
        </table>
      </CardBox>

      <!-- Modal thêm/sửa -->
      <CardBoxModal v-model="isModalActive" :title="form.id ? 'Sửa người dùng' : 'Thêm người dùng'">
        <FormField label="Tên đăng nhập">
          <FormControl v-model="form.username" placeholder="Nhập tên đăng nhập" />
        </FormField>
        <FormField label="Email">
          <FormControl v-model="form.email" type="email" placeholder="Nhập email" />
        </FormField>
        <FormField label="Họ tên">
          <FormControl v-model="form.fullName" placeholder="Nhập họ tên" />
        </FormField>
        <FormField label="Vai trò">
          <FormControl v-model="form.role" :options="roles" />
        </FormField>
        <FormField label="Số điện thoại">
          <FormControl v-model="form.phone" placeholder="Nhập số điện thoại" />
        </FormField>
        <template #footer>
          <BaseButton label="Hủy" color="lightDark" @click="isModalActive = false" />
          <BaseButton label="Lưu" color="info" @click="saveUser" />
        </template>
      </CardBoxModal>

      <!-- Modal xóa -->
      <CardBoxModal
        v-model="isDeleteModalActive"
        title="Xác nhận xóa"
        button="danger"
        has-cancel
        @confirm="deleteUser"
      >
        <p>
          Bạn có chắc chắn muốn xóa người dùng <strong>{{ selectedUser?.fullName }}</strong
          >?
        </p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
