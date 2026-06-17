<script setup>
import { ref, computed } from 'vue'

const doctor = ref({
  fullName: 'PGS.TS Nguyễn Văn An',
  specialty: 'Tim mạch'
})

const patients = ref([
  {
    id: 1,
    name: 'Nguyễn Phú Long',
    age: 45,
    gender: 'Nam',
    phone: '0901234567',
    appointmentTime: '08:00',
    symptom: 'Đau ngực',
    status: 'Chờ khám'
  },
  {
    id: 2,
    name: 'Nguyễn Trần Hồng Nhung',
    age: 35,
    gender: 'Nữ',
    phone: '0912345678',
    appointmentTime: '09:00',
    symptom: 'Khó thở',
    status: 'Đang khám'
  },
  {
    id: 3,
    name: 'Trần Văn Bình',
    age: 52,
    gender: 'Nam',
    phone: '0988888888',
    appointmentTime: '10:00',
    symptom: 'Tăng huyết áp',
    status: 'Đã khám'
  }
])

const selectedPatient = ref(null)

const waitingCount = computed(
  () => patients.value.filter(p => p.status === 'Chờ khám').length
)

const completedCount = computed(
  () => patients.value.filter(p => p.status === 'Đã khám').length
)
</script>

<template>
  <div class="min-h-screen bg-slate-100 p-6">

    <div class="max-w-7xl mx-auto">

      <!-- Header -->
      <div class="bg-white rounded-3xl shadow p-6 mb-6">
        <h1 class="text-3xl font-bold">
          Bảng điều khiển bác sĩ
        </h1>

        <p class="text-gray-500 mt-2">
          {{ doctor.fullName }} - {{ doctor.specialty }}
        </p>
      </div>

      <!-- Statistics -->
      <div class="grid md:grid-cols-4 gap-5 mb-6">

        <div class="bg-white p-5 rounded-2xl shadow">
          <p class="text-gray-500">Tổng bệnh nhân</p>
          <h2 class="text-4xl font-bold mt-2">
            {{ patients.length }}
          </h2>
        </div>

        <div class="bg-white p-5 rounded-2xl shadow">
          <p class="text-gray-500">Chờ khám</p>
          <h2 class="text-4xl font-bold text-orange-500 mt-2">
            {{ waitingCount }}
          </h2>
        </div>

        <div class="bg-white p-5 rounded-2xl shadow">
          <p class="text-gray-500">Đã khám</p>
          <h2 class="text-4xl font-bold text-green-500 mt-2">
            {{ completedCount }}
          </h2>
        </div>

        <div class="bg-white p-5 rounded-2xl shadow">
          <p class="text-gray-500">Lịch hôm nay</p>
          <h2 class="text-4xl font-bold text-blue-500 mt-2">
            {{ patients.length }}
          </h2>
        </div>

      </div>

      <div class="grid lg:grid-cols-3 gap-6">

        <!-- Patient List -->
        <div class="lg:col-span-2 bg-white rounded-3xl shadow p-6">

          <h2 class="text-xl font-bold mb-4">
            Danh sách bệnh nhân cần khám
          </h2>

          <table class="w-full">
            <thead>
              <tr class="border-b">
                <th class="text-left py-3">Giờ</th>
                <th class="text-left">Họ tên</th>
                <th class="text-left">Triệu chứng</th>
                <th class="text-left">Trạng thái</th>
                <th></th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="patient in patients"
                :key="patient.id"
                class="border-b"
              >
                <td class="py-3">
                  {{ patient.appointmentTime }}
                </td>

                <td>
                  {{ patient.name }}
                </td>

                <td>
                  {{ patient.symptom }}
                </td>

                <td>
                  {{ patient.status }}
                </td>

                <td>
                  <button
                    @click="selectedPatient = patient"
                    class="px-3 py-1 bg-blue-500 text-white rounded"
                  >
                    Chi tiết
                  </button>
                </td>
              </tr>
            </tbody>

          </table>
        </div>

        <!-- Patient Detail -->
        <div class="bg-white rounded-3xl shadow p-6">

          <h2 class="text-xl font-bold mb-4">
            Thông tin bệnh nhân
          </h2>

          <div v-if="selectedPatient">

            <p><strong>Họ tên:</strong> {{ selectedPatient.name }}</p>

            <p class="mt-2">
              <strong>Tuổi:</strong>
              {{ selectedPatient.age }}
            </p>

            <p class="mt-2">
              <strong>Giới tính:</strong>
              {{ selectedPatient.gender }}
            </p>

            <p class="mt-2">
              <strong>Điện thoại:</strong>
              {{ selectedPatient.phone }}
            </p>

            <p class="mt-2">
              <strong>Triệu chứng:</strong>
              {{ selectedPatient.symptom }}
            </p>

            <p class="mt-2">
              <strong>Trạng thái:</strong>
              {{ selectedPatient.status }}
            </p>

          </div>

          <div
            v-else
            class="text-gray-500"
          >
            Chọn bệnh nhân để xem chi tiết
          </div>

        </div>

      </div>

    </div>

  </div>
</template>