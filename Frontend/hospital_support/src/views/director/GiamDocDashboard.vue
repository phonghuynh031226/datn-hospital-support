<script setup>
import { ref, computed, onMounted } from 'vue'
import TrinhSoanThao from '../../components/TrinhSoanThao.vue'

const API_GIAM_DOC = 'http://localhost:8080/api/v1/giam-doc'
const activeMenu = ref('analytics') // analytics, staff, clinics, accounts, settings, news, doctor-bio
const staff = ref([])
const clinics = ref([])
const accounts = ref([])
const newsList = ref([])

const showAddStaffModal = ref(false)
const showAddClinicModal = ref(false)
const showAddNewsModal = ref(false)
const showScheduleModal = ref(false)
const showEditBioModal = ref(false)

const toast = ref({
  show: false,
  message: '',
  type: 'success'
})

let toastTimer = null

function showToast(message, type = 'success') {
  toast.value = {
    show: true,
    message,
    type
  }

  if (toastTimer) {
    clearTimeout(toastTimer)
  }

  toastTimer = setTimeout(() => {
    toast.value.show = false
  }, 3000)
}


// Bio Edit Form
const selectedDoctorForBio = ref(null)
const bioForm = ref({
  id: null,
  name: '',
  bio: ''
})

// Schedule Assignment Form
const selectedStaffMember = ref(null)
const scheduleForm = ref({
  days: [],
  shift: 'Ca sáng (08:00 - 12:00)',
  room: 'Phòng 101 (Tim mạch)'
})

// News Form
const isEditingNews = ref(false)
const newsForm = ref({
  id: null,
  title: '',
  summary: '',
  content: '',
  category: 'Sự kiện',
  image: '',
  author: 'Phòng Ban Giám Đốc',
  date: ''
})

// Period filter tabs for reports
const activePeriod = ref('month') // week, month, quarter, year
const analyticsData = {
  week: {
    revenue: 32400000,
    patients: 310,
    efficiency: [
      { name: 'Khoa Tim mạch', patients: 78, rate: 82, color: 'bg-indigo-600' },
      { name: 'Khoa Nội tổng quát', patients: 105, rate: 88, color: 'bg-emerald-600' },
      { name: 'Khoa Ngoại khoa', patients: 45, rate: 60, color: 'bg-amber-600' },
      { name: 'Khoa Sản phụ khoa', patients: 38, rate: 70, color: 'bg-rose-600' },
      { name: 'Khoa Mắt', patients: 44, rate: 75, color: 'bg-purple-600' }
    ]
  },
  month: {
    revenue: 154200000,
    patients: 1420,
    efficiency: [
      { name: 'Khoa Tim mạch', patients: 350, rate: 85, color: 'bg-indigo-600' },
      { name: 'Khoa Nội tổng quát', patients: 480, rate: 92, color: 'bg-emerald-600' },
      { name: 'Khoa Ngoại khoa', patients: 210, rate: 65, color: 'bg-amber-600' },
      { name: 'Khoa Sản phụ khoa', patients: 180, rate: 78, color: 'bg-rose-600' },
      { name: 'Khoa Mắt', patients: 200, rate: 80, color: 'bg-purple-600' }
    ]
  },
  quarter: {
    revenue: 462600000,
    patients: 4260,
    efficiency: [
      { name: 'Khoa Tim mạch', patients: 1050, rate: 87, color: 'bg-indigo-600' },
      { name: 'Khoa Nội tổng quát', patients: 1440, rate: 93, color: 'bg-emerald-600' },
      { name: 'Khoa Ngoại khoa', patients: 630, rate: 68, color: 'bg-amber-600' },
      { name: 'Khoa Sản phụ khoa', patients: 540, rate: 79, color: 'bg-rose-600' },
      { name: 'Khoa Mắt', patients: 600, rate: 82, color: 'bg-purple-600' }
    ]
  },
  year: {
    revenue: 1895000000,
    patients: 16800,
    efficiency: [
      { name: 'Khoa Tim mạch', patients: 4200, rate: 89, color: 'bg-indigo-600' },
      { name: 'Khoa Nội tổng quát', patients: 5600, rate: 94, color: 'bg-emerald-600' },
      { name: 'Khoa Ngoại khoa', patients: 2500, rate: 72, color: 'bg-amber-600' },
      { name: 'Khoa Sản phụ khoa', patients: 2100, rate: 81, color: 'bg-rose-600' },
      { name: 'Khoa Mắt', patients: 2400, rate: 84, color: 'bg-purple-600' }
    ]
  }
}

const currentRevenue = computed(() => analyticsData[activePeriod.value].revenue)
const currentPatients = computed(() => analyticsData[activePeriod.value].patients)
const currentEfficiency = computed(() => analyticsData[activePeriod.value].efficiency)

// System settings mock data
const systemConfig = ref({
  id: null,
tenBenhVien: '',
duongDanLogo: '',
duongDanBanner: '',
mauChuDao: '#2563EB',
ngayCapNhat: null,

  banner1: 'https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&fit=crop&w=1200&q=80',
  banner2: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80',
  banner3: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=1200&q=80',
  introText: `<h2 style="text-align: center; color: #1e3a8a;"><strong>HỆ THỐNG Y TẾ BỆNH VIỆN ĐA KHOA QUỐC TẾ</strong></h2>` +
    `<p style="text-align: center; font-style: italic; color: #4b5563;">Hệ thống y tế hiện đại, uy tín hàng đầu — Nơi sức khỏe của bạn là ưu tiên số một</p>` +
    `<hr />` +
    `<p>Bệnh viện Đa khoa Quốc tế tự hào là một trong những cơ sở y tế hàng đầu Việt Nam cung cấp dịch vụ khám chữa bệnh chất lượng cao theo tiêu chuẩn quốc tế. Với triết lý <strong>"Tận tâm vì sức khỏe người bệnh"</strong>, chúng tôi không ngừng cải tiến quy trình, đầu tư trang thiết bị công nghệ hiện đại và quy tụ đội ngũ chuyên gia, y bác sĩ đầu ngành nhằm đem lại trải nghiệm y tế hoàn hảo nhất.</p>` +
    `<p style="text-align: center;">` +
    `  <img src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&amp;fit=crop&amp;w=800&amp;q=80" alt="Bệnh viện đa khoa quốc tế" style="border-radius: 8px; max-width: 100%; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);" />` +
    `</p>` +
    `<h3 style="color: #0f766e;"><strong><u>Tầm Nhìn &amp; Sứ Mệnh Phát Triển</u></strong></h3>` +
    `<p>Chúng tôi hướng tới mục tiêu trở thành một tổ chức y tế chuẩn quốc tế hàng đầu, nơi người bệnh gửi trọn niềm tin bằng dịch vụ chuyên nghiệp và chi phí hợp lý nhất. Sứ mệnh của chúng tôi là mang lại giải pháp chăm sóc sức khỏe toàn diện, an toàn và hiệu quả cao.</p>` +
    `<h3 style="color: #0f766e;"><strong><u>Những Lý Do Lựa Chọn Dịch Vụ Của Chúng Tôi</u></strong></h3>` +
    `<ul>` +
    `  <li><strong>Đội ngũ chuyên gia hàng đầu:</strong> Các Giáo sư, Tiến sĩ, Bác sĩ chuyên khoa giàu kinh nghiệm, tận tụy phục vụ người bệnh.</li>` +
    `  <li><strong>Trang thiết bị tiên tiến nhất:</strong> Hệ thống máy chụp MRI, CT Scanner 128 dãy, hệ thống nội soi thế hệ mới giúp phát hiện sớm mọi dấu hiệu bất thường.</li>` +
    `  <li><strong>Không gian khám chữa bệnh cao cấp:</strong> Môi trường bệnh viện xanh, sạch, an toàn, thiết kế theo mô hình khách sạn bệnh viện tiện nghi.</li>` +
    `  <li><strong>Dịch vụ hỗ trợ người bệnh 24/7:</strong> Hỗ trợ tận tâm từ lúc đón tiếp, khám bệnh cho đến khi xuất viện và chăm sóc tại nhà.</li>` +
    `</ul>` +
    `<hr />` +
    `<h3 style="color: #0f766e;"><strong><u>Thông Tin Liên Hệ &amp; Địa Chỉ Bản Đồ</u></strong></h3>` +
    `<p>Quý khách hàng có nhu cầu tư vấn hoặc đặt lịch khám vui lòng liên hệ với chúng tôi qua các kênh thông tin chính thức dưới đây:</p>` +
    `<ul>` +
    `  <li><strong>Địa chỉ chính thức:</strong> <a href="https://maps.google.com/?q=Bệnh+viện+Đa+khoa+Quốc+tế" target="_blank" rel="noopener noreferrer"><u>Bản đồ hướng dẫn đường đi đến Bệnh viện</u></a></li>` +
    `  <li><strong>Hotline Đặt lịch:</strong> <a href="tel:19001234" style="color: #2563eb; font-weight: bold;"><u>1900 1234</u></a> (Hỗ trợ 24/7)</li>` +
    `  <li><strong>Email phản hồi &amp; Hỗ trợ:</strong> <a href="mailto:contact@hospital.com" style="color: #2563eb; font-weight: bold;"><u>contact@hospital.com</u></a></li>` +
    `</ul>`,
  contactPhone: '1900 1234',
  contactEmail: 'contact@hospital.com',
  notifyEmail: true,
  notifySMS: false,
  whyChooseUsTitle: 'Vì Sao Chọn Bệnh Viện Đa Khoa?',
  whyChooseUsSubtitle: 'Chúng tôi cam kết mang đến dịch vụ y tế chất lượng cao nhất, vì sức khỏe của bạn và gia đình',
  reasons: [
    {
      icon: 'bi-people-fill',
      title: 'Đội Ngũ Chuyên Gia',
      desc: 'Hơn 50 bác sĩ đầu ngành, giàu kinh nghiệm, được đào tạo tại các trường y khoa hàng đầu trong và ngoài nước.',
      color: 'from-blue-500 to-blue-700'
    },
    {
      icon: 'bi-gpu-card',
      title: 'Thiết Bị Hiện Đại',
      desc: 'Hệ thống trang thiết bị y tế tiên tiến, nhập khẩu từ Đức, Nhật Bản, đảm bảo chẩn đoán chính xác nhất.',
      color: 'from-emerald-500 to-emerald-700'
    },
    {
      icon: 'bi-cash-coin',
      title: 'Chi Phí Hợp Lý',
      desc: 'Cam kết chi phí minh bạch, hợp lý. Hỗ trợ bảo hiểm y tế và các chương trình ưu đãi cho bệnh nhân.',
      color: 'from-amber-500 to-amber-700'
    },
    {
      icon: 'bi-heart-pulse-fill',
      title: 'Phục Vụ Tận Tâm',
      desc: 'Dịch vụ chăm sóc chu đáo, tận tâm từ khâu tiếp đón đến điều trị. Luôn lấy bệnh nhân làm trung tâm.',
      color: 'from-rose-500 to-rose-700'
    }
  ]
})

// Form states for staff/clinic
const staffForm = ref({ name: '', role: 'doctor', specialty: 'Tim mạch', phone: '', schedule: 'Thứ 2 - Thứ 6 Ca sáng (08:00 - 12:00)', bio: '' })
const clinicForm = ref({ name: '', price: 150000, headDoctor: 'PGS.TS Nguyễn Văn An' })

onMounted(() => {
  loadStaff()
  loadClinics()
  loadAccounts()
  loadNews()
  loadSystemConfig()
})

function loadStaff() {
  const mockStaff = [
    { id: 1, name: 'PGS.TS Nguyễn Văn An', role: 'Bác sĩ chuyên khoa', specialty: 'Tim mạch', phone: '0909998888', schedule: 'Thứ 2, Thứ 4, Thứ 6 Ca sáng (08:00 - 12:00)', room: 'Phòng 101 (Tim mạch)', status: 'Đang trực', bio: 'Nguyên Phó Giám đốc Bệnh viện Tim, Giáo sư thỉnh giảng Đại học Y Hà Nội. Chuyên sâu về can thiệp tim mạch, điều trị suy tim và tăng huyết áp.' },
    { id: 2, name: 'TS.BS Trần Thị Mai', role: 'Bác sĩ chuyên khoa', specialty: 'Sản phụ khoa', phone: '0908887777', schedule: 'Thứ 3, Thứ 5 Ca sáng (08:00 - 12:00)', room: 'Phòng 102 (Sản phụ khoa)', status: 'Đang trực', bio: 'Tốt nghiệp Tiến sĩ tại Pháp. Hơn 20 năm kinh nghiệm trong chẩn đoán trước sinh và phẫu thuật phụ khoa nội soi phức tạp.' },
    { id: 3, name: 'GS.TS Lê Hoàng Minh', role: 'Bác sĩ chuyên khoa', specialty: 'Ngoại khoa', phone: '0907773333', schedule: 'Thứ 2, Thứ 5 Ca sáng (08:00 - 12:00)', room: 'Phòng 202 (Ngoại khoa)', status: 'Đang trực', bio: 'Giáo sư đầu ngành ngoại khoa Việt Nam. Đã trực tiếp phẫu thuật thành công hàng ngàn ca ngoại tiêu hóa và chấn thương nặng.' },
    { id: 4, name: 'ThS.BS Phạm Thùy Linh', role: 'Bác sĩ chuyên khoa', specialty: 'Nhi khoa', phone: '0906662222', schedule: 'Thứ 2, Thứ 3, Thứ 4, Thứ 5, Thứ 6, Thứ 7 Ca sáng (08:00 - 12:00)', room: 'Phòng 203 (Nhi khoa)', status: 'Đang trực', bio: 'Thạc sĩ Nhi khoa Đại học Y Hà Nội. Yêu trẻ em, có kinh nghiệm chuyên sâu về dinh dưỡng, hô hấp và tiêm chủng trẻ em.' },
    { id: 5, name: 'TS.BS Lê Thị Lan', role: 'Bác sĩ chuyên khoa', specialty: 'Mắt', phone: '0905551111', schedule: 'Thứ 3, Thứ 6 Ca chiều (13:00 - 17:00)', room: 'Phòng 204 (Mắt)', status: 'Đang trực', bio: 'Chuyên gia hàng đầu về phẫu thuật Phaco điều trị đục thủy tinh thể (cườm khô) ở người cao tuổi. Thành viên Hội Nhãn khoa Việt Nam.' },
    { id: 6, name: 'ThS.BS Trần Văn Bình', role: 'Bác sĩ chuyên khoa', specialty: 'Nội tổng quát', phone: '0904440000', schedule: 'Thứ 2, Thứ 3, Thứ 4, Thứ 5, Thứ 6, Thứ 7 Ca sáng (08:00 - 12:00)', room: 'Phòng 201 (Nội tổng quát)', status: 'Đang trực', bio: 'Chuyên gia khám điều trị các bệnh lão khoa, xương khớp, tiểu đường và các bệnh chuyển hóa mãn tính ở người lớn tuổi.' },
    { id: 7, name: 'Điều dưỡng Nguyễn Thị Mai', role: 'Điều dưỡng hành chính', specialty: 'Tiếp đón', phone: '0907654321', schedule: 'Thứ 2 - Thứ 7 Ca sáng (08:00 - 12:00)', room: 'Quầy tiếp đón', status: 'Đang trực', bio: 'Hơn 5 năm làm việc tại bộ phận tiếp nhận bệnh nhân. Nhiệt tình, chu đáo.' },
    { id: 8, name: 'Dược sĩ Trần Thanh Thủy', role: 'Dược sĩ cấp phát', specialty: 'Nhà thuốc', phone: '0908886666', schedule: 'Thứ 2 - Thứ 7 Ca chiều (13:00 - 17:00)', room: 'Quầy dược phẩm', status: 'Đang trực', bio: 'Dược sĩ đại học, có chứng chỉ hành nghề dược lâm sàng.' },
    { id: 9, name: 'Thủ kho Phạm Minh Tuấn', role: 'Thủ kho dược phẩm', specialty: 'Kho thuốc', phone: '0907775555', schedule: 'Thứ 2 - Thứ 6 Ca sáng (08:00 - 12:00)', room: 'Kho dược phẩm', status: 'Ngoại tuyến', bio: 'Quản lý kho dược phẩm bệnh viện, chịu trách nhiệm nhập xuất và kiểm kê hàng hóa.' }
  ]
  const data = localStorage.getItem('hospitalStaff')
  if (data && JSON.parse(data).length >= 6) {
    staff.value = JSON.parse(data)
  } else {
    staff.value = mockStaff
    localStorage.setItem('hospitalStaff', JSON.stringify(mockStaff))
  }
}

function loadClinics() {
  const mockClinics = [
    { name: 'Tim mạch', price: 150000, headDoctor: 'PGS.TS Nguyễn Văn An' },
    { name: 'Sản phụ khoa', price: 180000, headDoctor: 'TS.BS Trần Thị Mai' },
    { name: 'Ngoại khoa', price: 200000, headDoctor: 'GS.TS Lê Hoàng Minh' },
    { name: 'Nhi khoa', price: 120000, headDoctor: 'ThS.BS Phạm Thùy Linh' },
    { name: 'Mắt', price: 100000, headDoctor: 'TS.BS Lê Thị Lan' }
  ]
  const data = localStorage.getItem('hospitalClinics')
  if (data) {
    clinics.value = JSON.parse(data)
  } else {
    clinics.value = mockClinics
    localStorage.setItem('hospitalClinics', JSON.stringify(mockClinics))
  }
}

function loadAccounts() {
  const mockAccounts = [
    { id: 1, username: 'patient', role: 'Bệnh nhân', name: 'Nguyễn Văn A', status: 'Hoạt động' },
    { id: 2, username: 'nurse', role: 'Điều dưỡng', name: 'Nguyễn Thị Mai', status: 'Hoạt động' },
    { id: 3, username: 'doctor', role: 'Bác sĩ', name: 'Nguyễn Văn An', status: 'Hoạt động' },
    { id: 4, username: 'pharmacist', role: 'Dược sĩ', name: 'Trần Thanh Thủy', status: 'Hoạt động' },
    { id: 5, username: 'warehouse', role: 'Thủ kho', name: 'Phạm Minh Tuấn', status: 'Hoạt động' }
  ]
  const data = localStorage.getItem('hospitalAccounts')
  if (data) {
    accounts.value = JSON.parse(data)
  } else {
    accounts.value = mockAccounts
    localStorage.setItem('hospitalAccounts', JSON.stringify(mockAccounts))
  }
}

function loadNews() {
  const defaultNews = [
    {
      id: 1,
      image: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80',
      title: 'Bệnh viện triển khai chương trình khám sức khỏe miễn phí cho người cao tuổi',
      summary: 'Nhằm hưởng ứng tháng hành động vì người cao tuổi, bệnh viện tổ chức chương trình khám miễn phí cho bệnh nhân trên 60 tuổi, bao gồm đo đường huyết, điện tim và tư vấn dinh dưỡng xương khớp.',
      content: '<h3>Chương trình ý nghĩa vì cộng đồng</h3><p>Nhằm hưởng ứng tháng hành động vì người cao tuổi Việt Nam và mong muốn đồng hành chăm sóc sức khỏe cộng đồng, Bệnh viện Đa Khoa thông báo triển khai chiến dịch chăm sóc sức khỏe cộng đồng đặc biệt với các nội dung thiết thực:</p><ul><li>Đo đường huyết mao mạch phòng ngừa đái tháo đường.</li><li>Đo điện tâm đồ (ECG) tầm soát bệnh lý tim mạch, huyết áp.</li><li>Khám chuyên khoa Cơ Xương Khớp miễn phí.</li></ul>',
      date: '10/06/2026',
      category: 'Sự kiện',
      author: 'Phòng Truyền Thông'
    },
    {
      id: 2,
      image: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=600&q=80',
      title: 'Ứng dụng kỹ thuật phẫu thuật nội soi tiên tiến trong điều trị bệnh tim',
      summary: 'Lần đầu tiên tại Việt Nam, bệnh viện áp dụng thành công kỹ thuật phẫu thuật nội soi robot trong lĩnh vực tim mạch cho bệnh nhân cao tuổi bị hẹp van động mạch chủ.',
      content: '<h3>Kỷ nguyên mới trong phẫu thuật tim mạch</h3><p>Đội ngũ chuyên khoa Ngoại tim mạch của Bệnh viện đã thực hiện thành công ca phẫu thuật thay van động mạch chủ bằng kỹ thuật phẫu thuật nội soi robot ít xâm lấn. Phương pháp này mang lại nhiều ưu điểm vượt trội như vết mổ nhỏ, hồi phục nhanh và hạn chế biến chứng...</p>',
      date: '05/06/2026',
      category: 'Y khoa',
      author: 'TS.BS Nguyễn Văn An'
    },
    {
      id: 3,
      image: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=600&q=80',
      title: 'Hướng dẫn đăng ký khám bệnh trực tuyến — Nhanh chóng & Tiện lợi',
      summary: 'Hệ thống đặt lịch khám trực tuyến giúp bệnh nhân tiết kiệm thời gian chờ đợi, chủ động lựa chọn bác sĩ và khung giờ phù hợp ngay tại nhà thông qua máy tính hoặc điện thoại.',
      content: '<h3>Các bước đặt lịch khám trực tuyến dễ dàng</h3><p>Bệnh nhân chỉ cần truy cập website, chọn Khoa khám, chọn Khung giờ mong muốn và mô tả triệu chứng để được xếp lịch và nhận số thứ tự ưu tiên khám bệnh mà không cần xếp hàng tại quầy lễ tân...</p>',
      date: '01/06/2026',
      category: 'Hướng dẫn',
      author: 'Tổ Chăm Sóc Khách Hàng'
    },
    {
      id: 4,
      image: 'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=600&q=80',
      title: 'Chế độ dinh dưỡng khoa học dành cho người bệnh đái tháo đường',
      summary: 'Chế độ ăn uống hợp lý đóng vai trò quyết định giúp kiểm soát tốt chỉ số đường huyết, duy trì năng lượng và hạn chế tối đa các biến chứng nguy hiểm của bệnh tiểu đường.',
      content: '<h3>Dinh dưỡng kiểm soát đường huyết</h3><p>Người bệnh đái tháo đường cần xây dựng thực đơn ưu tiên các thực phẩm có chỉ số đường huyết (GI) thấp như rau xanh, các loại hạt ngũ cốc nguyên cám. Hạn chế tối đa tinh bột tinh chế, đồ ngọt có ga. Hãy chia nhỏ bữa ăn thành 5-6 bữa/ngày để duy trì đường huyết ổn định...</p>',
      date: '28/05/2026',
      category: 'Dinh dưỡng',
      author: 'Dược sĩ Trần Thanh Thủy'
    },
    {
      id: 5,
      image: 'https://images.unsplash.com/photo-1579684389782-64d84b5e901a?auto=format&fit=crop&w=600&q=80',
      title: 'Tầm soát ung thư sớm: Chìa khóa vàng bảo vệ sức khỏe gia đình',
      summary: 'Việc thực hiện kiểm tra sức khỏe và tầm soát ung thư định kỳ giúp phát hiện mầm mống tế bào ác tính ở giai đoạn đầu, nâng cao đáng kể tỷ lệ điều trị thành công lên tới 90%.',
      content: '<h3>Bảo vệ sức khỏe chủ động từ sớm</h3><p>Nhiều bệnh lý ung thư diễn biến âm thầm không có triệu chứng rõ rệt ở giai đoạn đầu. Việc tầm soát định kỳ (như nội soi tiêu hóa, chụp X-quang tuyến vú, xét nghiệm chỉ dấu khối u) là biện pháp hiệu quả nhất để phát hiện bệnh sớm, từ đó có phác đồ can thiệp kịp thời và tiết kiệm chi phí...</p>',
      date: '25/05/2026',
      category: 'Y khoa',
      author: 'GS.TS Lê Hoàng Minh'
    },
    {
      id: 6,
      image: 'https://images.unsplash.com/photo-1512864084360-7c0c4d0a0845?auto=format&fit=crop&w=600&q=80',
      title: 'Bí quyết bảo vệ đôi mắt khỏe mạnh trong kỷ nguyên số',
      summary: 'Tiếp xúc liên tục với thiết bị điện tử khiến mắt dễ bị khô, mỏi và giảm thị lực. Bác sĩ chuyên khoa khuyên bạn nên áp dụng quy tắc 20-20-20 để bảo vệ mắt.',
      content: '<h3>Bảo vệ mắt trong kỷ nguyên số</h3><p>Hội Nhãn khoa khuyến cáo người thường xuyên làm việc trước màn hình máy tính thực hiện quy tắc 20-20-20: Cứ sau 20 phút làm việc, hãy nhìn xa 20 feet (khoảng 6m) trong vòng 20 giây. Bổ sung thực phẩm giàu Vitamin A, Omega-3 và khám mắt định kỳ...</p>',
      date: '20/05/2026',
      category: 'Y khoa',
      author: 'TS.BS Lê Thị Lan'
    }
  ]
  const data = localStorage.getItem('hospitalNews')
  if (data) {
    const list = JSON.parse(data)
    // If the saved news list is less than 6, merge it with the defaults so that the user immediately has 6 articles
    if (list.length < 6) {
      const merged = [...list]
      defaultNews.forEach(d => {
        if (!merged.some(m => m.title === d.title)) {
          merged.push(d)
        }
      })
      newsList.value = merged
      localStorage.setItem('hospitalNews', JSON.stringify(merged))
    } else {
      newsList.value = list
    }
  } else {
    newsList.value = defaultNews
    localStorage.setItem('hospitalNews', JSON.stringify(defaultNews))
  }
}

async function loadSystemConfig() {
      try {
    const res = await fetch(`${API_GIAM_DOC}/cai-dat`, {
      method: 'GET'
    })

    if (!res.ok) {
      const errorText = await res.text()
      throw new Error(`Không thể tải cài đặt hệ thống. Status ${res.status}: ${errorText}`)
    }

    const data = await res.json()

    systemConfig.value.id = data.id
    systemConfig.value.tenBenhVien = data.tenBenhVien || ''
    systemConfig.value.duongDanLogo = data.duongDanLogo || ''
    systemConfig.value.duongDanBanner = data.duongDanBanner || ''
    systemConfig.value.mauChuDao = data.mauChuDao || '#2563EB'
    systemConfig.value.ngayCapNhat = data.ngayCapNhat || null

    if (data.duongDanBanner) {
      systemConfig.value.banner1 = data.duongDanBanner
      systemConfig.value.banner2 = data.duongDanBanner
      systemConfig.value.banner3 = data.duongDanBanner
    }
  } catch (error) {
    console.error('Lỗi loadSystemConfig:', error)
    showToast('Không thể tải cài đặt hệ thống từ backend!', 'error')
  }
}

function toggleAccountStatus(id) {
  const list = [...accounts.value]
  const idx = list.findIndex(acc => acc.id === id)
  if (idx !== -1) {
    list[idx].status = list[idx].status === 'Hoạt động' ? 'Bị Khóa' : 'Hoạt động'
    accounts.value = list
    localStorage.setItem('hospitalAccounts', JSON.stringify(list))
    showToast(`Đã cập nhật trạng thái tài khoản: ${list[idx].username} thành [${list[idx].status}]`, 'success')
  }
}

function addStaff() {
  const f = staffForm.value
  if (!f.name || !f.phone) {
    showToast('Vui lòng nhập đầy đủ thông tin nhân sự!', 'error')
    return
  }
  const newMember = {
    id: Date.now(),
    name: f.name,
    role: f.role === 'doctor' ? 'Bác sĩ chuyên khoa' : f.role === 'nurse' ? 'Điều dưỡng hành chính' : 'Dược sĩ cấp phát',
    specialty: f.specialty,
    phone: f.phone,
    schedule: f.schedule,
    room: f.role === 'doctor' ? 'Phòng 101 (Tim mạch)' : f.role === 'nurse' ? 'Quầy tiếp đón' : 'Quầy dược phẩm',
    status: 'Ngoại tuyến',
    bio: f.bio || 'Chưa cập nhật tiểu sử.'
  }
  staff.value.push(newMember)
  localStorage.setItem('hospitalStaff', JSON.stringify(staff.value))
  
  staffForm.value = { name: '', role: 'doctor', specialty: 'Tim mạch', phone: '', schedule: 'Thứ 2 - Thứ 6 Ca sáng (08:00 - 12:00)', bio: '' }
  showAddStaffModal.value = false
  showToast('Đã bổ sung nhân sự mới thành công!', 'success')
}

function removeStaff(id) {
  if (confirm('Xóa nhân sự này khỏi hệ thống?')) {
    staff.value = staff.value.filter(s => s.id !== id)
    localStorage.setItem('hospitalStaff', JSON.stringify(staff.value))
  }
}

// Open Work Schedule Assignment Modal
function openScheduleModal(member) {
  selectedStaffMember.value = member
  scheduleForm.value.days = []
  scheduleForm.value.room = member.room || 'Phòng 101 (Tim mạch)'
  
  const daysOfWeek = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật']
  daysOfWeek.forEach(d => {
    if (member.schedule.includes(d)) {
      scheduleForm.value.days.push(d)
    }
  })
  
  if (member.schedule.includes('chiều') || member.schedule.includes('Chiêu')) {
    scheduleForm.value.shift = 'Ca chiều (13:00 - 17:00)'
  } else if (member.schedule.includes('tối') || member.schedule.includes('Tối')) {
    scheduleForm.value.shift = 'Ca tối (17:00 - 21:00)'
  } else {
    scheduleForm.value.shift = 'Ca sáng (08:00 - 12:00)'
  }
  
  showScheduleModal.value = true
}

function saveSchedule() {
  if (scheduleForm.value.days.length === 0) {
    showToast('Vui lòng chọn ít nhất một ngày làm việc!', 'error')
    return
  }
  
  const list = [...staff.value]
  const idx = list.findIndex(s => s.id === selectedStaffMember.value.id)
  if (idx !== -1) {
    const daysStr = scheduleForm.value.days.join(', ')
    const shiftStr = scheduleForm.value.shift
    list[idx].schedule = `${daysStr} ${shiftStr}`
    list[idx].room = scheduleForm.value.room
    staff.value = list
    localStorage.setItem('hospitalStaff', JSON.stringify(list))
    showToast(`Đã phân công lịch trực cho ${selectedStaffMember.value.name}: ${list[idx].schedule} tại ${list[idx].room}`, 'success')
  }
  
  showScheduleModal.value = false
  selectedStaffMember.value = null
}

function addClinic() {
  const f = clinicForm.value
  if (!f.name || !f.price) {
    showToast('Vui lòng nhập đầy đủ thông tin!', 'error')
    return
  }
  clinics.value.push({
    name: f.name,
    price: Number(f.price),
    headDoctor: f.headDoctor
  })
  localStorage.setItem('hospitalClinics', JSON.stringify(clinics.value))
  
  clinicForm.value = { name: '', price: 150000, headDoctor: 'PGS.TS Nguyễn Văn An' }
  showAddClinicModal.value = false
  showToast('Đã mở thêm khoa khám bệnh thành công!', 'success')
}

function removeClinic(name) {
  if (confirm('Đóng khoa chuyên môn này?')) {
    clinics.value = clinics.value.filter(c => c.name !== name)
    localStorage.setItem('hospitalClinics', JSON.stringify(clinics.value))
  }
}

function handleBannerFile(event, key) {
  const file = event.target.files[0]

  if (file) {
    const reader = new FileReader()

    reader.onload = (e) => {
      systemConfig.value[key] = e.target.result

      if (key === 'banner1' || key === 'banner2' || key === 'banner3') {
        systemConfig.value.duongDanBanner = e.target.result
      }

      if (key === 'duongDanLogo') {
        systemConfig.value.duongDanLogo = e.target.result
      }
    }

    reader.readAsDataURL(file)
  }
}

async function saveSystemConfig() {
  try {
    const mauChuDaoHopLe =
      systemConfig.value.mauChuDao && systemConfig.value.mauChuDao.startsWith('#')
        ? systemConfig.value.mauChuDao
        : '#2563EB'

    const logoHopLe =
      systemConfig.value.duongDanLogo && systemConfig.value.duongDanLogo.startsWith('http')
        ? systemConfig.value.duongDanLogo
        : ''

    const bannerHopLe =
      systemConfig.value.duongDanBanner && systemConfig.value.duongDanBanner.startsWith('http')
        ? systemConfig.value.duongDanBanner
        : ''

    const payload = {
      tenBenhVien: systemConfig.value.tenBenhVien || 'Bệnh Viện Đa Khoa',
      duongDanLogo: logoHopLe,
      duongDanBanner: bannerHopLe,
      mauChuDao: mauChuDaoHopLe
    }

    console.log('Payload gửi lên BE:', payload)

    const res = await fetch(`${API_GIAM_DOC}/cai-dat`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })

    const text = await res.text()
    console.log('BE trả về:', text)

    if (!res.ok) {
      throw new Error(text)
    }

    const data = JSON.parse(text)

    systemConfig.value.id = data.id
    systemConfig.value.tenBenhVien = data.tenBenhVien || ''
    systemConfig.value.duongDanLogo = data.duongDanLogo || ''
    systemConfig.value.duongDanBanner = data.duongDanBanner || ''
    systemConfig.value.mauChuDao = data.mauChuDao || '#2563EB'
    systemConfig.value.ngayCapNhat = data.ngayCapNhat || null

    showToast('Đã lưu cấu hình hệ thống thành công!', 'success')
  } catch (error) {
    console.error('Lỗi saveSystemConfig:', error)
    showToast('Không thể lưu cấu hình hệ thống!', 'error')
  }
}

function deleteNews(id) {
  if (confirm('Bác có chắc chắn muốn xóa bài viết tin tức này không?')) {
    newsList.value = newsList.value.filter(item => item.id !== id)
    localStorage.setItem('hospitalNews', JSON.stringify(newsList.value))
    showToast('Đã xóa bài viết thành công.', 'success')
  }
}

function openEditBioModal(doctor) {
  selectedDoctorForBio.value = doctor
  bioForm.value = {
    id: doctor.id,
    name: doctor.name,
    bio: doctor.bio || ''
  }
  showEditBioModal.value = true
}

function saveDoctorBio() {
  const idx = staff.value.findIndex(s => s.id === bioForm.value.id)
  if (idx !== -1) {
    staff.value[idx].bio = bioForm.value.bio
    localStorage.setItem('hospitalStaff', JSON.stringify(staff.value))
    showToast(`Đã cập nhật tiểu sử cho bác sĩ ${bioForm.value.name}!`, 'success')
  }
  showEditBioModal.value = false
  selectedDoctorForBio.value = null
}

const doctorsList = computed(() => {
  return staff.value.filter(s => s.role === 'Bác sĩ chuyên khoa')
})
</script>

<template>
  <div class="min-h-[calc(100vh-112px)] bg-gray-50 flex">
    <div
      v-if="toast.show"
      class="fixed top-5 right-5 z-[9999] flex items-center gap-3 px-5 py-4 rounded-2xl shadow-2xl text-white font-semibold transition-all duration-300"
      :class="toast.type === 'success' ? 'bg-emerald-600' : 'bg-red-600'"
    >
      <span class="text-xl">{{ toast.type === 'success' ? '✅' : '⚠️' }}</span>
      <span>{{ toast.message }}</span>
    </div>

    <!-- Left Navigation Sidebar -->
    <aside class="w-64 bg-white border-r border-gray-150 flex flex-col justify-between p-4 flex-shrink-0">
      <div class="space-y-6">
        <!-- Brand/User Header -->
        <div class="flex items-center gap-3 p-2 pb-4 border-b border-gray-100">
          <img 
            src="https://i.ibb.co/4gXZ9qs4/z7939748517288-6fea6e7a88629fd0c0ef6ce9326fd25f.png" 
            alt="Logo" 
            class="w-10 h-10 object-contain rounded-lg bg-gray-50 p-0.5 shadow-sm"
          />
          <div>
            <h4 class="text-sm font-bold text-gray-800">Ban Điều Hành</h4>
            <span class="text-xs text-purple-600 font-semibold">Giám đốc Lê Hoàng Minh</span>
          </div>
        </div>

        <!-- Menu links -->
        <nav class="space-y-1">
          <button 
            @click="activeMenu = 'analytics'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'analytics' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-pie-chart text-lg"></i>
            Báo cáo thống kê
          </button>
          
          <button 
            @click="activeMenu = 'staff'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'staff' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-people-fill text-lg"></i>
            Quản lý nhân viên
          </button>

          <button 
            @click="activeMenu = 'clinics'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'clinics' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-hospital text-lg"></i>
            Quản lý chuyên khoa
          </button>

          <button 
            @click="activeMenu = 'accounts'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'accounts' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-shield-lock text-lg"></i>
            Danh sách tài khoản
          </button>

          <button 
            @click="activeMenu = 'news'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'news' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-newspaper text-lg"></i>
            Quản lý tin tức
          </button>

          <button 
            @click="activeMenu = 'settings'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'settings' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-gear text-lg"></i>
            Cấu hình hệ thống
          </button>

          <button 
            @click="activeMenu = 'doctor-bio'"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all"
            :class="activeMenu === 'doctor-bio' ? 'bg-purple-50 text-purple-800 shadow-sm' : 'text-gray-500 hover:text-gray-800 hover:bg-gray-50'"
          >
            <i class="bi bi-journal-text text-lg"></i>
            Quản lý bio bác sĩ
          </button>
        </nav>
      </div>

      <div class="p-2 border-t text-xs text-gray-400 text-center">
        Hospital Admin Panel v2.0
      </div>
    </aside>

    <!-- Main Content Area -->
    <main class="flex-1 p-6 md:p-8 overflow-y-auto">
      
      <!-- ==================== MENU 1: ANALYTICS & CHARTS ==================== -->
      <div v-if="activeMenu === 'analytics'" class="space-y-8 animate-fade-in">
        <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-4">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Tổng quan báo cáo & Doanh thu</h2>
            <p class="text-sm text-gray-400">Xem trực quan biểu đồ doanh số đóng viện phí, lưu lượng bệnh nhân và hiệu suất buồng bệnh.</p>
          </div>
          <!-- Period Filter Tabs -->
          <div class="flex gap-1 bg-gray-100 p-1 rounded-2xl">
            <button @click="activePeriod = 'week'" class="px-4 py-2 rounded-xl text-xs font-bold transition-all" :class="activePeriod === 'week' ? 'bg-purple-700 text-white shadow-sm' : 'text-gray-500 hover:text-gray-800'">Tuần này</button>
            <button @click="activePeriod = 'month'" class="px-4 py-2 rounded-xl text-xs font-bold transition-all" :class="activePeriod === 'month' ? 'bg-purple-700 text-white shadow-sm' : 'text-gray-500 hover:text-gray-800'">Tháng này</button>
            <button @click="activePeriod = 'quarter'" class="px-4 py-2 rounded-xl text-xs font-bold transition-all" :class="activePeriod === 'quarter' ? 'bg-purple-700 text-white shadow-sm' : 'text-gray-500 hover:text-gray-800'">Quý này</button>
            <button @click="activePeriod = 'year'" class="px-4 py-2 rounded-xl text-xs font-bold transition-all" :class="activePeriod === 'year' ? 'bg-purple-700 text-white shadow-sm' : 'text-gray-500 hover:text-gray-800'">Năm nay</button>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex items-center justify-between">
            <div>
              <span class="text-xs text-gray-400 font-bold uppercase">Tổng Doanh Thu Viện Phí</span>
              <h4 class="text-3xl font-black text-purple-700 mt-1">{{ currentRevenue.toLocaleString('vi-VN') }}đ</h4>
              <span class="text-xs text-emerald-600 font-semibold mt-1 block"><i class="bi bi-graph-up mr-1"></i> Tăng 12% so với kỳ trước</span>
            </div>
            <span class="text-3xl bg-purple-50 p-3 rounded-2xl">💰</span>
          </div>

          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex items-center justify-between">
            <div>
              <span class="text-xs text-gray-400 font-bold uppercase">Lưu Lượng Bệnh Nhân</span>
              <h4 class="text-3xl font-black text-gray-800 mt-1">{{ currentPatients }} lượt</h4>
              <span class="text-xs text-emerald-600 font-semibold mt-1 block"><i class="bi bi-people-fill mr-1"></i> Số lượt khám kỳ hiện tại</span>
            </div>
            <span class="text-3xl bg-emerald-50 p-3 rounded-2xl">🤒</span>
          </div>

          <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm flex items-center justify-between">
            <div>
              <span class="text-xs text-gray-400 font-bold uppercase">Tổng Số Nhân Sự</span>
              <h4 class="text-3xl font-black text-purple-700 mt-1">{{ staff.length }} nhân sự</h4>
              <span class="text-xs text-purple-650 font-semibold mt-1 block"><i class="bi bi-shield-check mr-1"></i> Hoạt động trực xoay ca</span>
            </div>
            <span class="text-3xl bg-purple-50 p-3 rounded-2xl">🧑‍⚕️</span>
          </div>
        </div>

        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm">
          <h3 class="text-lg font-bold text-gray-850 mb-6 flex items-center gap-2">
            <i class="bi bi-activity text-purple-700"></i>
            Tỷ Lệ Đăng Ký Khám & Công Suất Hoạt Động Các Khoa
          </h3>

          <div class="space-y-5">
            <div v-for="item in currentEfficiency" :key="item.name" class="space-y-1">
              <div class="flex justify-between text-sm">
                <span class="font-bold text-gray-750">{{ item.name }}</span>
                <span class="text-gray-400 font-semibold">{{ item.patients }} ca (Hiệu suất: {{ item.rate }}%)</span>
              </div>
              <div class="w-full bg-gray-100 h-3 rounded-full overflow-hidden">
                <div :class="[item.color, 'h-full rounded-full transition-all duration-1000']" :style="{ width: `${item.rate}%` }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 2: STAFF & WORK SCHEDULE ==================== -->
      <div v-else-if="activeMenu === 'staff'" class="space-y-6 animate-fade-in">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Quản lý nhân viên & Lịch làm việc</h2>
            <p class="text-sm text-gray-400">Danh sách nhân sự y khoa toàn viện kèm lịch trực chi tiết.</p>
          </div>
          <button 
            @click="showAddStaffModal = true"
            class="py-2.5 px-4 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-xs transition-colors flex items-center gap-1"
          >
            <i class="bi bi-person-plus-fill"></i> Thêm nhân viên
          </button>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Họ và tên</th>
                  <th class="py-3.5 px-6">Vai trò</th>
                  <th class="py-3.5 px-6">Khoa phụ trách</th>
                  <th class="py-3.5 px-6">Phòng làm việc</th>
                  <th class="py-3.5 px-6">Lịch làm việc trực</th>
                  <th class="py-3.5 px-6">Số điện thoại</th>
                  <th class="py-3.5 px-6 text-center">Trạng thái trực</th>
                  <th class="py-3.5 px-6 text-center">Hành động</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="s in staff" :key="s.id" class="hover:bg-gray-50/20">
                  <td class="py-4 px-6 font-bold text-gray-850">{{ s.name }}</td>
                  <td class="py-4 px-6 text-gray-600 font-medium">{{ s.role }}</td>
                  <td class="py-4 px-6 text-purple-700 font-semibold">{{ s.specialty }}</td>
                  <td class="py-4 px-6 text-indigo-600 font-semibold text-xs">{{ s.room || '—' }}</td>
                  <td class="py-4 px-6 text-gray-550 font-medium">{{ s.schedule }}</td>
                  <td class="py-4 px-6 font-mono text-xs">{{ s.phone }}</td>
                  <td class="py-4 px-6 text-center">
                    <span class="text-[10px] font-bold px-2 py-0.5 rounded-full"
                          :class="s.status === 'Đang trực' ? 'bg-emerald-100 text-emerald-800' : 'bg-gray-100 text-gray-500'">
                      {{ s.status }}
                    </span>
                  </td>
                  <td class="py-4 px-6 text-center">
                    <button @click="openScheduleModal(s)" class="text-indigo-600 hover:bg-indigo-50 p-1.5 rounded-lg mr-1.5 transition-colors" title="Phân công lịch làm việc">
                      <i class="bi bi-calendar-event"></i>
                    </button>
                    <button @click="removeStaff(s.id)" class="text-rose-500 hover:bg-rose-50 p-1.5 rounded-lg transition-colors" title="Xóa nhân sự">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 3: CLINICS AND PRICING ==================== -->
      <div v-else-if="activeMenu === 'clinics'" class="space-y-6 animate-fade-in">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Quản lý chuyên khoa & Giá khám</h2>
            <p class="text-sm text-gray-400">Danh sách chuyên khoa lâm sàng, trưởng khoa và định giá khám chữa bệnh.</p>
          </div>
          <button 
            @click="showAddClinicModal = true"
            class="py-2.5 px-4 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-xs transition-colors flex items-center gap-1"
          >
            <i class="bi bi-plus-circle"></i> Mở thêm chuyên khoa
          </button>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Tên chuyên khoa</th>
                  <th class="py-3.5 px-6">Bác sĩ Trưởng khoa phụ trách</th>
                  <th class="py-3.5 px-6 text-right">Giá khám dịch vụ</th>
                  <th class="py-3.5 px-6 text-center">Xóa bỏ</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="c in clinics" :key="c.name" class="hover:bg-gray-50/20">
                  <td class="py-4 px-6 font-bold text-gray-850">Khoa {{ c.name }}</td>
                  <td class="py-4 px-6 text-gray-600 font-medium">{{ c.headDoctor }}</td>
                  <td class="py-4 px-6 text-right font-bold text-purple-700">{{ c.price.toLocaleString('vi-VN') }}đ</td>
                  <td class="py-4 px-6 text-center">
                    <button @click="removeClinic(c.name)" class="text-rose-500 hover:bg-rose-50 p-1.5 rounded-lg transition-colors">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 4: ACCOUNT MANAGEMENT ==================== -->
      <div v-else-if="activeMenu === 'accounts'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Quản lý bảo mật tài khoản</h2>
          <p class="text-sm text-gray-400">Khóa hoặc kích hoạt lại tài khoản bệnh nhân và các vai trò nhân viên y tế để bảo mật thông tin.</p>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Tên đăng nhập</th>
                  <th class="py-3.5 px-6">Vai trò người dùng</th>
                  <th class="py-3.5 px-6">Tên hiển thị</th>
                  <th class="py-3.5 px-6 text-center">Trạng thái bảo mật</th>
                  <th class="py-3.5 px-6 text-center">Hành động khóa</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="acc in accounts" :key="acc.id" class="hover:bg-gray-50/20">
                  <td class="py-4 px-6 font-mono font-bold text-gray-800">{{ acc.username }}</td>
                  <td class="py-4 px-6 text-gray-500 font-medium">{{ acc.role }}</td>
                  <td class="py-4 px-6 font-semibold text-gray-750">{{ acc.name }}</td>
                  <td class="py-4 px-6 text-center">
                    <span class="text-xs font-bold px-2.5 py-1 rounded-full"
                          :class="acc.status === 'Hoạt động' ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'">
                      {{ acc.status }}
                    </span>
                  </td>
                  <td class="py-4 px-6 text-center">
                    <button 
                      @click="toggleAccountStatus(acc.id)"
                      class="px-3 py-1.5 rounded-lg text-xs font-bold border transition-colors"
                      :class="acc.status === 'Hoạt động' 
                        ? 'border-red-200 text-red-600 hover:bg-red-50' 
                        : 'border-emerald-200 text-emerald-600 hover:bg-emerald-50'"
                    >
                      <i :class="acc.status === 'Hoạt động' ? 'bi bi-lock-fill' : 'bi bi-unlock-fill'"></i>
                      {{ acc.status === 'Hoạt động' ? 'Khóa TK' : 'Mở Khóa' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 5: SYSTEM CONFIG ==================== -->
      <div v-else-if="activeMenu === 'settings'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Cài đặt hệ thống cổng bệnh viện</h2>
          <p class="text-sm text-gray-400">Chỉnh sửa tin tức, banner, thông tin giới thiệu, liên hệ và cấu hình gửi thông báo SMS/Email.</p>
        </div>

        <div class="bg-white p-6 md:p-8 rounded-3xl border border-gray-100 shadow-sm space-y-6">
          <form @submit.prevent="saveSystemConfig" class="space-y-5 text-sm">
            
            <h3 class="text-base font-bold text-purple-800 border-b pb-2">🖼️ Cấu hình slide banner trang chủ (Gắn link ảnh hoặc upload file)</h3>
            
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div class="space-y-2">
                <label for="banner1-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Banner 1 (Đường dẫn hoặc tải lên)</label>
                <input id="banner1-in" type="text" v-model="systemConfig.banner1" class="w-full px-3 py-2 rounded-xl border border-gray-250 mb-1 animate-all text-xs" placeholder="Nhập liên kết ảnh..." />
                <input type="file" @change="handleBannerFile($event, 'banner1')" class="block w-full text-xs text-gray-500 file:mr-2 file:py-1 file:px-2 file:rounded file:border-0 file:text-xs file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100 cursor-pointer" accept="image/*" />
                <div v-if="systemConfig.banner1" class="mt-2 h-20 w-full overflow-hidden border rounded-lg bg-gray-50">
                  <img :src="systemConfig.banner1" class="w-full h-full object-cover" />
                </div>
              </div>
              <div class="space-y-2">
                <label for="banner2-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Banner 2 (Đường dẫn hoặc tải lên)</label>
                <input id="banner2-in" type="text" v-model="systemConfig.banner2" class="w-full px-3 py-2 rounded-xl border border-gray-250 mb-1 animate-all text-xs" placeholder="Nhập liên kết ảnh..." />
                <input type="file" @change="handleBannerFile($event, 'banner2')" class="block w-full text-xs text-gray-500 file:mr-2 file:py-1 file:px-2 file:rounded file:border-0 file:text-xs file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100 cursor-pointer" accept="image/*" />
                <div v-if="systemConfig.banner2" class="mt-2 h-20 w-full overflow-hidden border rounded-lg bg-gray-50">
                  <img :src="systemConfig.banner2" class="w-full h-full object-cover" />
                </div>
              </div>
              <div class="space-y-2">
                <label for="banner3-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Banner 3 (Đường dẫn hoặc tải lên)</label>
                <input id="banner3-in" type="text" v-model="systemConfig.banner3" class="w-full px-3 py-2 rounded-xl border border-gray-250 mb-1 animate-all text-xs" placeholder="Nhập liên kết ảnh..." />
                <input type="file" @change="handleBannerFile($event, 'banner3')" class="block w-full text-xs text-gray-500 file:mr-2 file:py-1 file:px-2 file:rounded file:border-0 file:text-xs file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100 cursor-pointer" accept="image/*" />
                <div v-if="systemConfig.banner3" class="mt-2 h-20 w-full overflow-hidden border rounded-lg bg-gray-50">
                  <img :src="systemConfig.banner3" class="w-full h-full object-cover" />
                </div>
              </div>
            </div>

            <div>
              <label class="block text-xs text-gray-450 font-bold uppercase mb-2">Bài viết giới thiệu bệnh viện (Biên tập bằng Rich Text Editor)</label>
              <TrinhSoanThao v-model="systemConfig.introText" />
            </div>

            <h3 class="text-base font-bold text-purple-800 border-b pb-2 pt-4">⭐ Cấu hình phần "Vì sao chọn chúng tôi" (Trang chủ)</h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="whyChooseUsTitle" class="block text-xs text-gray-450 font-bold uppercase mb-1">Tiêu đề chính</label>
                <input id="whyChooseUsTitle" type="text" v-model="systemConfig.whyChooseUsTitle" class="w-full px-3 py-2 rounded-xl border border-gray-250 text-sm" placeholder="Vì Sao Chọn Bệnh Viện Đa Khoa?" />
              </div>
              <div>
                <label for="whyChooseUsSubtitle" class="block text-xs text-gray-450 font-bold uppercase mb-1">Tiêu đề phụ / Mô tả ngắn</label>
                <input id="whyChooseUsSubtitle" type="text" v-model="systemConfig.whyChooseUsSubtitle" class="w-full px-3 py-2 rounded-xl border border-gray-250 text-sm" placeholder="Chúng tôi cam kết mang đến dịch vụ y tế chất lượng cao nhất..." />
              </div>
            </div>

            <!-- List of 4 reasons -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 pt-2">
              <div v-for="(reason, index) in systemConfig.reasons" :key="index" class="p-5 border border-purple-100 rounded-2xl bg-purple-50/10 space-y-3">
                <div class="flex justify-between items-center border-b border-purple-50 pb-2">
                  <span class="font-bold text-purple-800 text-xs uppercase">Lý do thứ {{ index + 1 }}</span>
                  <div class="flex items-center gap-1.5">
                    <span class="text-xs text-gray-500 font-semibold">Xem trước màu:</span>
                    <div class="w-5 h-5 rounded bg-gradient-to-br" :class="reason.color"></div>
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-3">
                  <div>
                    <label :for="'reason-title-' + index" class="block text-xs text-gray-500 font-semibold mb-1">Tiêu đề lý do</label>
                    <input :id="'reason-title-' + index" type="text" v-model="reason.title" class="w-full px-3 py-1.5 rounded-lg border border-gray-200 text-xs" />
                  </div>
                  <div>
                    <label :for="'reason-color-' + index" class="block text-xs text-gray-500 font-semibold mb-1">Màu sắc hiển thị</label>
                    <select :id="'reason-color-' + index" v-model="reason.color" class="w-full px-3 py-1.5 rounded-lg border border-gray-200 text-xs">
                      <option value="from-blue-500 to-blue-700">Xanh dương (Blue)</option>
                      <option value="from-emerald-500 to-emerald-700">Lục (Emerald)</option>
                      <option value="from-amber-500 to-amber-700">Vàng (Amber)</option>
                      <option value="from-rose-500 to-rose-700">Đỏ (Rose)</option>
                      <option value="from-purple-500 to-purple-700">Tím (Purple)</option>
                      <option value="from-teal-500 to-teal-700">Xanh mòng két (Teal)</option>
                    </select>
                  </div>
                </div>

                <div class="grid grid-cols-12 gap-3">
                  <div class="col-span-4">
                    <label :for="'reason-icon-' + index" class="block text-xs text-gray-500 font-semibold mb-1">Icon đại diện</label>
                    <select :id="'reason-icon-' + index" v-model="reason.icon" class="w-full px-2 py-1.5 rounded-lg border border-gray-200 text-xs">
                      <option value="bi-people-fill">Đội ngũ (People)</option>
                      <option value="bi-gpu-card">Thiết bị (GPU/Card)</option>
                      <option value="bi-cash-coin">Chi phí (Cash)</option>
                      <option value="bi-heart-pulse-fill">Tận tâm (Heart Pulse)</option>
                      <option value="bi-star-fill">Chất lượng (Star)</option>
                      <option value="bi-award-fill">Giải thưởng (Award)</option>
                      <option value="bi-shield-fill-check">An toàn (Shield Check)</option>
                      <option value="bi-clock-fill">Nhanh chóng (Clock)</option>
                    </select>
                  </div>
                  <div class="col-span-8">
                    <label :for="'reason-desc-' + index" class="block text-xs text-gray-500 font-semibold mb-1">Mô tả lý do</label>
                    <textarea :id="'reason-desc-' + index" v-model="reason.desc" rows="2" class="w-full px-3 py-1.5 rounded-lg border border-gray-200 text-xs resize-none"></textarea>
                  </div>
                </div>
              </div>
            </div>

            <h3 class="text-base font-bold text-purple-800 border-b pb-2 pt-4">📞 Thông tin liên hệ bệnh viện</h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label for="phone-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Số hotline trực ban</label>
                <input id="phone-in" type="text" v-model="systemConfig.contactPhone" class="w-full px-3 py-2 rounded-xl border border-gray-250" />
              </div>
              <div>
                <label for="email-in" class="block text-xs text-gray-450 font-bold uppercase mb-1">Email liên hệ hành chính</label>
                <input id="email-in" type="email" v-model="systemConfig.contactEmail" class="w-full px-3 py-2 rounded-xl border border-gray-250" />
              </div>
            </div>

            <h3 class="text-base font-bold text-purple-800 border-b pb-2 pt-4">🔔 Cấu hình gửi thông báo tự động cho bệnh nhân</h3>
            
            <div class="flex flex-col sm:flex-row gap-6">
              <label class="flex items-center gap-2 font-semibold text-gray-650 cursor-pointer">
                <input type="checkbox" v-model="systemConfig.notifyEmail" class="rounded text-purple-600 focus:ring-purple-500" />
                Gửi email tự động khi đặt lịch thành công
              </label>
              <label class="flex items-center gap-2 font-semibold text-gray-650 cursor-pointer">
                <input type="checkbox" v-model="systemConfig.notifySMS" class="rounded text-purple-600 focus:ring-purple-500" />
                Gửi SMS mã khám khi số thứ tự đến lượt
              </label>
            </div>

            <div class="pt-4 flex justify-end">
              <button type="submit" class="py-3 px-6 bg-purple-700 hover:bg-purple-850 text-white font-bold rounded-2xl text-base shadow">
                Lưu cấu hình hệ thống
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- ==================== MENU 6: NEWS MANAGEMENT ==================== -->
      <div v-else-if="activeMenu === 'news'" class="space-y-6 animate-fade-in">
        <div class="flex justify-between items-center">
          <div>
            <h2 class="text-2xl font-black text-gray-800">Quản lý tin tức & Hoạt động</h2>
            <p class="text-sm text-gray-400">Danh sách các bài viết truyền thông công khai trên trang chủ bệnh viện.</p>
          </div>
          <button 
            @click="openAddNewsModal"
            class="py-2.5 px-4 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-xs transition-colors flex items-center gap-1"
          >
            <i class="bi bi-plus-circle-fill"></i> Viết bài mới
          </button>
        </div>

        <!-- News articles table -->
        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Ảnh bài viết</th>
                  <th class="py-3.5 px-6">Tiêu đề tin tức</th>
                  <th class="py-3.5 px-6">Chuyên mục</th>
                  <th class="py-3.5 px-6">Ngày đăng</th>
                  <th class="py-3.5 px-6">Tác giả</th>
                  <th class="py-3.5 px-6 text-center">Hành động</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="item in newsList" :key="item.id" class="hover:bg-gray-50/20">
                  <td class="py-4 px-6">
                    <div class="w-16 h-12 rounded-lg overflow-hidden border bg-gray-50">
                      <img :src="item.image" class="w-full h-full object-cover" />
                    </div>
                  </td>
                  <td class="py-4 px-6 font-bold text-gray-800 max-w-xs truncate">{{ item.title }}</td>
                  <td class="py-4 px-6 text-purple-700 font-semibold">{{ item.category }}</td>
                  <td class="py-4 px-6 font-mono text-xs">{{ item.date }}</td>
                  <td class="py-4 px-6 text-gray-550 font-semibold">{{ item.author }}</td>
                  <td class="py-4 px-6 text-center">
                    <button @click="openEditNewsModal(item)" class="text-indigo-600 hover:bg-indigo-50 p-1.5 rounded-lg mr-1.5 transition-colors" title="Sửa bài viết">
                      <i class="bi bi-pencil-fill"></i>
                    </button>
                    <button @click="deleteNews(item.id)" class="text-rose-500 hover:bg-rose-50 p-1.5 rounded-lg transition-colors" title="Xóa bài viết">
                      <i class="bi bi-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ==================== MENU 7: DOCTOR BIO MANAGEMENT ==================== -->
      <div v-else-if="activeMenu === 'doctor-bio'" class="space-y-6 animate-fade-in">
        <div>
          <h2 class="text-2xl font-black text-gray-800">Quản lý tiểu sử (Bio) Bác sĩ</h2>
          <p class="text-sm text-gray-400">Xem và cập nhật thông tin giới thiệu, học hàm học vị, kinh nghiệm của đội ngũ bác sĩ chuyên khoa.</p>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse text-sm">
              <thead>
                <tr class="bg-gray-50 border-b border-gray-100 font-bold text-gray-500">
                  <th class="py-3.5 px-6">Họ và tên</th>
                  <th class="py-3.5 px-6">Chuyên khoa</th>
                  <th class="py-3.5 px-6">Tiểu sử / Bio bác sĩ</th>
                  <th class="py-3.5 px-6 text-center">Hành động</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 text-gray-700">
                <tr v-for="doc in doctorsList" :key="doc.id" class="hover:bg-gray-50/20">
                  <td class="py-4 px-6 font-bold text-gray-850">{{ doc.name }}</td>
                  <td class="py-4 px-6 text-purple-750 font-semibold">{{ doc.specialty }}</td>
                  <td class="py-4 px-6 text-gray-600 max-w-lg">
                    <p class="line-clamp-3 text-xs leading-relaxed">{{ doc.bio || 'Chưa cập nhật tiểu sử.' }}</p>
                  </td>
                  <td class="py-4 px-6 text-center">
                    <button 
                      @click="openEditBioModal(doc)" 
                      class="px-3 py-1.5 bg-purple-50 text-purple-750 font-bold rounded-xl text-xs hover:bg-purple-100 transition-colors flex items-center gap-1 mx-auto"
                    >
                      <i class="bi bi-pencil-square"></i> Cập nhật Bio
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

    </main>

    <!-- ==================== ADD STAFF MODAL ==================== -->
    <div v-if="showAddStaffModal" class="fixed inset-0 bg-black/55 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="text-center">
          <h3 class="text-2xl font-black text-gray-800">Thêm Nhân Sự Mới</h3>
          <p class="text-sm text-gray-500 mt-1">Điền chi tiết thông tin nhân viên y tế cần bổ sung</p>
        </div>

        <form @submit.prevent="addStaff" class="space-y-4 text-sm">
          <div>
            <label for="add-staff-name" class="block text-sm font-semibold text-gray-700 mb-1.5">Họ và tên nhân sự</label>
            <input 
              id="add-staff-name"
              v-model="staffForm.name"
              type="text" 
              required
              placeholder="Nhập họ và tên đầy đủ..."
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-300"
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label for="add-staff-role" class="block text-sm font-semibold text-gray-700 mb-1.5">Vai trò</label>
              <select 
                id="add-staff-role"
                v-model="staffForm.role"
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
              >
                <option value="doctor">Bác sĩ</option>
                <option value="nurse">Điều dưỡng</option>
                <option value="pharmacist">Dược sĩ</option>
              </select>
            </div>
            <div>
              <label for="add-staff-specialty" class="block text-sm font-semibold text-gray-700 mb-1.5">Chuyên môn</label>
              <input 
                id="add-staff-specialty"
                v-model="staffForm.specialty"
                type="text" 
                required
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label for="add-staff-sched" class="block text-sm font-semibold text-gray-700 mb-1.5">Lịch biểu trực</label>
            <input 
              id="add-staff-sched"
              v-model="staffForm.schedule"
              type="text" 
              required
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
            />
          </div>

          <div>
            <label for="add-staff-phone" class="block text-sm font-semibold text-gray-700 mb-1.5">Số điện thoại trực</label>
            <input 
              id="add-staff-phone"
              v-model="staffForm.phone"
              type="tel" 
              required
              placeholder="Số điện thoại di động..."
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-300"
            />
          </div>

          <div>
            <label for="add-staff-bio" class="block text-sm font-semibold text-gray-700 mb-1.5">Tiểu sử / Bio (Tuỳ chọn)</label>
            <textarea
              id="add-staff-bio"
              v-model="staffForm.bio"
              rows="3"
              placeholder="Nhập tiểu sử, học hàm học vị, chuyên môn nổi bật..."
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-300 resize-none"
            ></textarea>
          </div>

          <div class="flex gap-3 pt-3">
            <button 
              type="button"
              @click="showAddStaffModal = false"
              class="flex-1 py-3 border border-gray-200 rounded-xl font-bold text-gray-650 hover:bg-gray-50 text-sm"
            >
              Hủy
            </button>
            <button 
              type="submit"
              class="flex-1 py-3 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-sm shadow"
            >
              Xác nhận thêm nhân viên
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ==================== ADD CLINIC MODAL ==================== -->
    <div v-if="showAddClinicModal" class="fixed inset-0 bg-black/55 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="text-center">
          <h3 class="text-2xl font-black text-gray-800">Mở Khoa Lâm Sàng Mới</h3>
          <p class="text-sm text-gray-500 mt-1">Cài đặt tên khoa và đơn giá dịch vụ khám</p>
        </div>

        <form @submit.prevent="addClinic" class="space-y-4 text-sm">
          <div>
            <label for="add-clinic-name" class="block text-sm font-semibold text-gray-700 mb-1.5">Tên khoa</label>
            <input 
              id="add-clinic-name"
              v-model="clinicForm.name"
              type="text" 
              required
              placeholder="Tên khoa (Ví dụ: Thần kinh, Da liễu...)"
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
            />
          </div>

          <div>
            <label for="add-clinic-price" class="block text-sm font-semibold text-gray-700 mb-1.5">Đơn giá khám bệnh (đ)</label>
            <input 
              id="add-clinic-price"
              v-model="clinicForm.price"
              type="number" 
              required
              min="0"
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-300 text-right font-bold text-purple-750"
            />
          </div>

          <div>
            <label for="add-clinic-doc" class="block text-sm font-semibold text-gray-700 mb-1.5">Bác sĩ phụ trách trưởng khoa</label>
            <input 
              id="add-clinic-doc"
              v-model="clinicForm.headDoctor"
              type="text" 
              required
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
            />
          </div>

          <div class="flex gap-3 pt-3">
            <button 
              type="button"
              @click="showAddClinicModal = false"
              class="flex-1 py-3 border border-gray-200 rounded-xl font-bold text-gray-650 hover:bg-gray-50 text-sm"
            >
              Hủy
            </button>
            <button 
              type="submit"
              class="flex-1 py-3 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-sm shadow"
            >
              Xác nhận mở khoa
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ==================== WORK SCHEDULE ASSIGNMENT MODAL ==================== -->
    <div v-if="showScheduleModal" class="fixed inset-0 bg-black/55 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="text-center">
          <h3 class="text-2xl font-black text-gray-800">Phân Công Lịch Trực</h3>
          <p class="text-sm text-gray-500 mt-1">Phân công ngày và ca làm việc cho: <strong class="text-purple-700">{{ selectedStaffMember?.name }}</strong></p>
        </div>

        <form @submit.prevent="saveSchedule" class="space-y-5 text-sm">
          <!-- Days of Week Selection -->
          <div>
            <span class="block text-sm font-semibold text-gray-750 mb-2">Chọn ngày làm việc trực:</span>
            <div class="grid grid-cols-4 gap-2">
              <label 
                v-for="d in ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật']" 
                :key="d"
                class="flex flex-col items-center justify-center p-2 border rounded-xl cursor-pointer text-xs font-bold transition-all text-center"
                :class="scheduleForm.days.includes(d) ? 'bg-purple-50 border-purple-500 text-purple-700 shadow-sm' : 'border-gray-200 text-gray-500 hover:bg-gray-50'"
              >
                <input type="checkbox" :value="d" v-model="scheduleForm.days" class="sr-only" />
                {{ d }}
              </label>
            </div>
          </div>

          <!-- Shift Selection -->
          <div>
            <label for="schedule-shift" class="block text-sm font-semibold text-gray-750 mb-2">Chọn ca trực:</label>
            <select 
              id="schedule-shift"
              v-model="scheduleForm.shift"
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
            >
              <option value="Ca sáng (08:00 - 12:00)">Ca sáng (08:00 - 12:00)</option>
              <option value="Ca chiều (13:00 - 17:00)">Ca chiều (13:00 - 17:00)</option>
              <option value="Ca tối (17:00 - 21:00)">Ca tối (17:00 - 21:00)</option>
            </select>
          </div>

          <!-- Room Selection -->
          <div>
            <label for="schedule-room" class="block text-sm font-semibold text-gray-750 mb-2">Phòng trực / Phòng khám:</label>
            <select 
              id="schedule-room"
              v-model="scheduleForm.room"
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
            >
              <option value="Phòng 101 (Tim mạch)">Phòng 101 (Tim mạch)</option>
              <option value="Phòng 102 (Sản phụ khoa)">Phòng 102 (Sản phụ khoa)</option>
              <option value="Phòng 201 (Nội tổng quát)">Phòng 201 (Nội tổng quát)</option>
              <option value="Phòng 202 (Ngoại khoa)">Phòng 202 (Ngoại khoa)</option>
              <option value="Phòng 203 (Nhi khoa)">Phòng 203 (Nhi khoa)</option>
              <option value="Phòng 204 (Mắt)">Phòng 204 (Mắt)</option>
              <option value="Quầy tiếp đón">Quầy tiếp đón</option>
              <option value="Quầy dược phẩm">Quầy dược phẩm</option>
              <option value="Kho dược phẩm">Kho dược phẩm</option>
            </select>
          </div>

          <div class="flex gap-3 pt-3">
            <button 
              type="button" 
              @click="showScheduleModal = false; selectedStaffMember = null" 
              class="flex-1 py-3 border border-gray-200 rounded-xl font-bold text-gray-650 hover:bg-gray-50 text-sm"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              class="flex-1 py-3 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-sm shadow"
            >
              Xác nhận phân công
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ==================== WRITE/EDIT NEWS MODAL ==================== -->
    <div v-if="showAddNewsModal" class="fixed inset-0 bg-black/55 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="flex justify-between items-center border-b pb-4">
          <h3 class="text-2xl font-black text-gray-800">
            {{ isEditingNews ? 'Chỉnh Sửa Bài Viết Tin Tức' : 'Viết Bài Tin Tức Mới' }}
          </h3>
          <button @click="showAddNewsModal = false" class="text-gray-400 hover:text-gray-600 text-2xl">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <form @submit.prevent="saveNews" class="space-y-4 text-sm">
          <div class="grid grid-cols-1 md:grid-cols-12 gap-4">
            <div class="md:col-span-8">
              <label for="news-title" class="block text-sm font-semibold text-gray-700 mb-1.5">Tiêu đề bài viết</label>
              <input 
                id="news-title"
                v-model="newsForm.title" 
                type="text" 
                required 
                placeholder="Nhập tiêu đề tin tức..." 
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
              />
            </div>
            <div class="md:col-span-4">
              <label for="news-category" class="block text-sm font-semibold text-gray-700 mb-1.5">Chuyên mục</label>
              <select 
                id="news-category"
                v-model="newsForm.category" 
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none"
              >
                <option value="Sự kiện">Sự kiện</option>
                <option value="Y khoa">Y khoa</option>
                <option value="Hướng dẫn">Hướng dẫn</option>
                <option value="Dinh dưỡng">Dinh dưỡng</option>
              </select>
            </div>
          </div>

          <div>
            <label for="news-summary" class="block text-sm font-semibold text-gray-700 mb-1.5">Tóm tắt ngắn (Hiển thị ở danh sách tin)</label>
            <textarea 
              id="news-summary"
              v-model="newsForm.summary" 
              rows="2" 
              required 
              placeholder="Nhập tóm tắt ngắn của bài viết..." 
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none resize-none"
            ></textarea>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 items-end">
            <div>
              <label for="news-image" class="block text-sm font-semibold text-gray-700 mb-1.5">Ảnh bài viết (Nhập URL hoặc upload)</label>
              <input 
                id="news-image"
                v-model="newsForm.image" 
                type="text" 
                placeholder="https://..." 
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none mb-1 text-xs"
              />
              <input type="file" @change="handleNewsImageFile" class="block w-full text-xs text-gray-500 file:mr-2 file:py-1 file:px-2 file:rounded file:border-0 file:text-xs file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100 cursor-pointer" accept="image/*" />
            </div>
            <div v-if="newsForm.image" class="h-24 overflow-hidden border rounded-2xl bg-gray-50 flex items-center justify-center">
              <img :src="newsForm.image" class="h-full object-cover rounded-xl" />
            </div>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">Nội dung chi tiết bài viết (Biên tập bằng Rich Text Editor CKEditor 5)</label>
            <TrinhSoanThao v-model="newsForm.content" />
          </div>

          <div class="flex gap-3 pt-4 border-t">
            <button 
              type="button" 
              @click="showAddNewsModal = false" 
              class="flex-1 py-3 border border-gray-200 rounded-xl font-bold text-gray-650 hover:bg-gray-50 text-sm"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              class="flex-1 py-3 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-sm shadow"
            >
              Lưu bài viết công bố
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ==================== EDIT BIO MODAL ==================== -->
    <div v-if="showEditBioModal" class="fixed inset-0 bg-black/55 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
      <div class="bg-white rounded-3xl shadow-2xl max-w-lg w-full p-6 md:p-8 animate-fade-in-up space-y-6">
        <div class="text-center border-b pb-4">
          <h3 class="text-2xl font-black text-gray-800">Cập nhật Bio Bác sĩ</h3>
          <p class="text-sm text-gray-500 mt-1">Cập nhật học vị, kinh nghiệm cho bác sĩ <strong class="text-purple-700">{{ bioForm.name }}</strong></p>
        </div>

        <form @submit.prevent="saveDoctorBio" class="space-y-4 text-sm">
          <div>
            <label for="edit-doctor-bio" class="block text-sm font-semibold text-gray-700 mb-1.5">Nội dung tiểu sử / Bio</label>
            <textarea
              id="edit-doctor-bio"
              v-model="bioForm.bio"
              rows="8"
              required
              placeholder="Nhập chi tiết quá trình học tập, công tác, chuyên sâu lâm sàng..."
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-300 resize-none text-gray-700 leading-relaxed text-sm"
            ></textarea>
          </div>

          <div class="flex gap-3 pt-3">
            <button 
              type="button"
              @click="showEditBioModal = false; selectedDoctorForBio = null"
              class="flex-1 py-3 border border-gray-200 rounded-xl font-bold text-gray-650 hover:bg-gray-50 text-sm"
            >
              Hủy
            </button>
            <button 
              type="submit"
              class="flex-1 py-3 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl text-sm shadow"
            >
              Lưu thay đổi
            </button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>