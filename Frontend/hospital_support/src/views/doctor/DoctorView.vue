<script setup>
import { ref } from 'vue'
import { patients } from '@/data/patientData'

const patientList = ref(patients)
const selectedPatient = ref(null)

const startExamination = (patient) => {
  selectedPatient.value = patient
}

const finishExamination = () => {
  selectedPatient.value.status = 'COMPLETED'

  alert(
    `Đã hoàn thành khám cho ${selectedPatient.value.fullName}`
  )

  selectedPatient.value = null
}
</script>

<template>
  <div class="container">

    <h2>Danh sách bệnh nhân chờ khám</h2>

    <table border="1">
      <thead>
        <tr>
          <th>Họ tên</th>
          <th>Triệu chứng</th>
          <th>Giờ khám</th>
          <th>Trạng thái</th>
          <th></th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="patient in patientList"
          :key="patient.id"
          v-show="patient.status === 'WAITING'"
        >
          <td>{{ patient.fullName }}</td>
          <td>{{ patient.symptoms }}</td>
          <td>{{ patient.appointmentTime }}</td>
          <td>{{ patient.status }}</td>
          <td>
            <button
              @click="startExamination(patient)"
            >
              Bắt đầu khám
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="selectedPatient" class="exam-form">
      <h2>Phiếu khám bệnh</h2>

      <p><b>Họ tên:</b> {{ selectedPatient.fullName }}</p>
      <p><b>Giới tính:</b> {{ selectedPatient.gender }}</p>
      <p><b>Tuổi:</b> {{ selectedPatient.age }}</p>
      <p><b>SĐT:</b> {{ selectedPatient.phone }}</p>
      <p><b>Triệu chứng:</b> {{ selectedPatient.symptoms }}</p>

      <div>
        <label>Chẩn đoán</label>
        <textarea
          v-model="selectedPatient.diagnosis"
        ></textarea>
      </div>

      <div>
        <label>Đơn thuốc</label>
        <textarea
          v-model="selectedPatient.prescription"
        ></textarea>
      </div>

      <div>
        <label>Ghi chú</label>
        <textarea
          v-model="selectedPatient.note"
        ></textarea>
      </div>

      <button @click="finishExamination">
        Hoàn thành khám
      </button>
    </div>

  </div>
</template>