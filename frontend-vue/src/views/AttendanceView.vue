<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

// 1. 데이터 타입 정의
interface Attendance {
  id: number
  workoutDate: string // "2026-02-10"
  imageUrl: string
}

const userId = ref(localStorage.getItem('loginUser') || '')
const file = ref<File | null>(null)
const attendanceList = ref<Attendance[]>([])

// 📅 달력 관련 변수 (2026년 2월 기준)
const currentYear = ref(2026)
const currentMonth = ref(2) // 2월

// 2. 파일 선택 함수
const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files[0]) {
    file.value = target.files[0]
  }
}
const isLoading = ref(false) // ✨ 로딩 상태 변수

// 3. 이미지 업로드 (출석 체크)
const uploadImage = async () => {
  if (!file.value) return alert('사진을 선택해주세요!')

  isLoading.value = true // ✨ 로딩 시작! (버튼 비활성화)

  const formData = new FormData()
  formData.append('image', file.value)
  formData.append('userId', userId.value)

  try {
    const res = await fetch('http://localhost:8080/api/attendance', {
      method: 'POST',
      body: formData
    })
    const msg = await res.text()
    alert(msg)
    fetchAttendance() // 목록 갱신
  } catch (err) {
    alert('업로드 실패 ㅠㅠ')
  } finally {
    isLoading.value = false // ✨ 로딩 끝! (무조건 실행됨)
  }
}

// 4. 출석 기록 가져오기
const fetchAttendance = async () => {
  const res = await fetch(`http://localhost:8080/api/attendance/${userId.value}`)
  const data = await res.json()
  attendanceList.value = data
}

// 5. ✨ 달력 날짜 생성 로직 (핵심!)
const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value - 1 // 자바스크립트는 0월부터 시작

  // 이번 달 1일이 무슨 요일인지? (0:일, 1:월 ... 6:토)
  const firstDayOfWeek = new Date(year, month, 1).getDay()

  // 이번 달 마지막 날짜가 며칠인지? (28일? 31일?)
  const lastDate = new Date(year, month + 1, 0).getDate()

  const days = []

  // 1. 빈 칸 채우기 (1일 전까지)
  for (let i = 0; i < firstDayOfWeek; i++) {
    days.push(null)
  }

  // 2. 날짜 채우기 (1일 ~ 마지막 날)
  for (let i = 1; i <= lastDate; i++) {
    days.push(i)
  }

  return days
})

// 6. 특정 날짜에 운동했는지 확인하는 함수
const isAttended = (day: number) => {
  if (!day) return false

  // "2026-02-05" 형식 만들기
  const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(day).padStart(2, '0')}`

  // 리스트에 이 날짜가 있는지 찾기
  return attendanceList.value.some(item => item.workoutDate === dateStr)
}

// 🔥 초기화 함수
const resetAll = async () => {
  if (!confirm('정말 모든 데이터를 삭제하시겠습니까? (되돌릴 수 없어요!)')) return

  try {
    const res = await fetch('http://localhost:8080/api/attendance/reset', {
      method: 'DELETE'
    })
    const msg = await res.text()
    alert(msg)

    // 화면도 갱신 (빈 목록으로)
    fetchAttendance()
    // 달력 갱신을 위해 페이지 새로고침이 깔끔할 수 있음
    window.location.reload()
  } catch (err) {
    alert('초기화 실패 ㅠㅠ')
  }
}

onMounted(() => {
  fetchAttendance()
})
</script>

<template>
  <div class="container">
    <h1 class="title">📅 2026년 2월 오운완</h1>

    <div class="upload-section">
      <input type="file" @change="handleFileChange" accept="image/*" class="file-input"/>
      <button @click="uploadImage" class="upload-btn" :disabled="isLoading">
        {{ isLoading ? 'AI 분석 중... 🤖' : '오늘 인증하기 📸' }}
      </button>
    </div>

    <p v-if="isLoading" style="color: #42b883; font-weight: bold; margin-top: 10px;">
      구글 AI가 사진 속 날짜를 읽고 있어요... 잠시만 기다려주세요!
    </p>

    <div class="calendar-wrapper">
      <div class="weekdays">
        <div class="weekday sun">일</div>
        <div class="weekday">월</div>
        <div class="weekday">화</div>
        <div class="weekday">수</div>
        <div class="weekday">목</div>
        <div class="weekday">금</div>
        <div class="weekday">토</div>
      </div>

      <div class="days-grid">
        <div
            v-for="(day, index) in calendarDays"
            :key="index"
            class="day-cell"
            :class="{ 'empty': !day, 'attended': day && isAttended(day) }"
        >
          <span v-if="day" class="day-number">{{ day }}</span>

          <div v-if="day && isAttended(day)" class="stamp">
            💪
          </div>
        </div>
      </div>
    </div>
    <p>
      <div style="text-align: right; margin-bottom: 10px;">
        <button @click="resetAll" class="reset-btn">🗑️ 데이터 전체 초기화</button>
      </div>
    </p>
  </div>
</template>

<style scoped>
.container { max-width: 500px; margin: 40px auto; font-family: 'Suit', sans-serif; }
.title { text-align: center; color: #333; margin-bottom: 20px; }

/* 업로드 버튼 스타일 */
.upload-section { display: flex; justify-content: center; gap: 10px; margin-bottom: 30px; }
.file-input { border: 1px solid #ddd; padding: 5px; border-radius: 5px; }
.upload-btn { background: #42b883; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-weight: bold; }
.upload-btn:hover { background: #3aa876; }

/* 달력 스타일 */
.calendar-wrapper { border: 1px solid #eee; border-radius: 15px; overflow: hidden; box-shadow: 0 10px 20px rgba(0,0,0,0.05); }

.weekdays { display: grid; grid-template-columns: repeat(7, 1fr); background: #f8f9fa; border-bottom: 1px solid #eee; }
.weekday { padding: 10px; text-align: center; font-weight: bold; color: #666; font-size: 0.9rem; }
.sun { color: #ff4d4f; }

.days-grid { display: grid; grid-template-columns: repeat(7, 1fr); background: white; }
.day-cell { height: 80px; border-right: 1px solid #f0f0f0; border-bottom: 1px solid #f0f0f0; padding: 5px; position: relative; }
.day-cell:nth-child(7n) { border-right: none; } /* 오른쪽 끝 선 제거 */

.day-number { font-size: 0.9rem; font-weight: bold; color: #333; }
.empty { background: #fafafa; }

/* 출석했을 때 스타일 (도장) */
.attended { background-color: #f0fdf4; }
.stamp { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 2rem; animation: pop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }

@keyframes pop {
  0% { transform: translate(-50%, -50%) scale(0); }
  100% { transform: translate(-50%, -50%) scale(1); }
}

/* 빨간색 경고 버튼 스타일 */
.reset-btn {
  background: #ff4d4f;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.8rem;
}
.reset-btn:hover { background: #d9363e; }
</style>