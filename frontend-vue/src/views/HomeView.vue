<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

// 차트 구성 요소 등록
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// --- 1. 타입 정의 ---
interface Workout {
  id: number
  exerciseName: string
  weight: number
  setNum: number
  reps: number
}

// --- 2. 상태 변수 (운동 기록용) ---
const workouts = ref<Workout[]>([])
const name = ref('')
const weight = ref(0)
const setNum = ref(0)
const reps = ref(0)
const editId = ref<number | null>(null)

// --- 3. 상태 변수 (단백질 트래커용) ---
const targetProtein = ref(154) // 대시보드에서 가져오기 전 기본값
const currentProtein = ref(0)
const inputProtein = ref(0)
//const userId = ref('user1') // 임시 유저 ID
const userId = ref(localStorage.getItem('loginUser') || '')
const userName = ref(localStorage.getItem('userName') || '나')

// --- 4. 자동 계산 로직 (Computed) ---
// 단백질 게이지 바 퍼센트 계산 (최대 100%)
const proteinProgress = computed(() => {
  if (targetProtein.value === 0) return 0
  return Math.min((currentProtein.value / targetProtein.value) * 100, 100)
})

// 운동 볼륨 차트 데이터 세팅
const chartData = computed(() => {
  return {
    labels: workouts.value.map(w => w.exerciseName),
    datasets: [
      {
        label: '총 볼륨 (kg)',
        backgroundColor: '#42b883', // Vue 초록색
        borderRadius: 5,
        data: workouts.value.map(w => w.weight * w.setNum * w.reps)
      }
    ]
  }
})
const chartOptions = { responsive: true, maintainAspectRatio: false }

// --- 5. 1RM 계산 함수 ---
const get1RM = (weight: number, reps: number) => {
  if (weight === 0 || reps === 0) return 0
  if (reps === 1) return weight
  return Math.round(weight * (1 + reps / 30)) // Epley 공식
}

// --- 6. API 연동 (단백질 및 프로필) ---
const fetchProfile = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/profile/${userId.value}`)
    const data = await res.json()
    if (data.targetProtein) {
      targetProtein.value = data.targetProtein // 대시보드 데이터 덮어쓰기
    }
  } catch (err) { console.error('프로필 로딩 실패:', err) }
}

const fetchProtein = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/protein/${userId.value}`)
    currentProtein.value = await res.json()
  } catch (err) { console.error(err) }
}

const addProtein = async () => {
  if (inputProtein.value <= 0) return alert('추가할 단백질량(g)을 입력하세요!')
  await fetch(`http://localhost:8080/api/protein/${userId.value}?amount=${inputProtein.value}`, { method: 'POST' })
  inputProtein.value = 0
  fetchProtein() // 추가 후 화면 갱신
}

const resetProtein = async () => {
  await fetch(`http://localhost:8080/api/protein/${userId.value}`, { method: 'DELETE' })
  fetchProtein()
}

// --- 7. API 연동 (운동 기록 CRUD) ---
const fetchWorkouts = async () => {
  try {
    // 주소를 아까 컨트롤러에서 만든 /api/workouts/list/내아이디 로 변경!
    const res = await fetch(`http://localhost:8080/api/workouts/list/${userId.value}`)
    workouts.value = await res.json()
  } catch (err) { console.error(err) }
}

const saveWorkout = async () => {
  if (!name.value) return alert('운동 이름을 입력해주세요!')

  const url = editId.value ? `http://localhost:8080/api/workouts/${editId.value}` : 'http://localhost:8080/api/workouts'
  const method = editId.value ? 'PUT' : 'POST'

  await fetch(url, {
    method: method,
    headers: { 'Content-Type': 'application/json' },
    // ✨ body에 userId: userId.value 추가!
    body: JSON.stringify({
      userId: userId.value,
      exerciseName: name.value, // (주의: 컨트롤러 엔티티 이름인 exerciseName으로 맞춰주세요)
      weight: weight.value,
      setNum: setNum.value,
      reps: reps.value
    })
  })

  resetForm()
  fetchWorkouts()
}

const startEdit = (workout: Workout) => {
  name.value = workout.exerciseName; weight.value = workout.weight; setNum.value = workout.setNum; reps.value = workout.reps; editId.value = workout.id
}

const resetForm = () => {
  name.value = ''; weight.value = 0; setNum.value = 0; reps.value = 0; editId.value = null
}

const deleteWorkout = async (id: number) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  await fetch(`http://localhost:8080/api/workouts/${id}`, { method: 'DELETE' })
  fetchWorkouts()
}

// --- 8. 화면 켜질 때 실행 (Lifecycle) ---
onMounted(() => {
  fetchWorkouts() // 운동 기록 가져오기
  fetchProtein()  // 오늘 먹은 단백질 가져오기
  fetchProfile()  // 내 목표 단백질량(대시보드) 가져오기
})
</script>

<template>
  <main class="container">
    <h1 class="title">🏋️‍♂️ {{ userName }}의 득근 일지</h1>

    <div class="tracker-box">
      <div class="tracker-header">
        <h3>🥩 매일 단백질 목표: <span class="highlight-text">{{ currentProtein }}g / {{ targetProtein }}g</span></h3>
        <button @click="resetProtein" class="reset-pro-btn">초기화</button>
      </div>

      <div class="progress-bar-bg">
        <div class="progress-bar-fill" :style="{ width: proteinProgress + '%' }">
          <span v-if="proteinProgress > 5">{{ Math.round(proteinProgress) }}%</span>
        </div>
      </div>

      <div class="protein-input-row">
        <input v-model="inputProtein" type="number" placeholder="먹은 단백질(g) 입력" class="pro-input" />
        <button @click="addProtein" class="add-pro-btn">추가하기 🍗</button>
      </div>
    </div>

    <div v-if="workouts.length > 0" class="chart-box">
      <div class="chart-container">
        <Bar :data="chartData" :options="chartOptions" />
      </div>
    </div>

    <div class="input-box" :class="{ 'edit-mode': editId }">
      <h2 v-if="editId" class="mode-title">🛠️ 기록 수정 중...</h2>
      <div class="input-row">
        <input v-model="name" type="text" placeholder="운동 이름 (예: 벤치프레스)" class="main-input" />
      </div>
      <div class="input-row three-cols">
        <input v-model="weight" type="number" placeholder="무게(kg)" />
        <input v-model="setNum" type="number" placeholder="세트" />
        <input v-model="reps" type="number" placeholder="회" />
      </div>
      <div class="btn-group">
        <button @click="saveWorkout" class="action-btn" :class="editId ? 'edit-btn' : 'add-btn'">
          {{ editId ? '수정 완료 ✨' : '기록 추가 🔥' }}
        </button>
        <button v-if="editId" @click="resetForm" class="cancel-btn">취소</button>
      </div>
    </div>

    <div class="list-box">
      <p v-if="workouts.length === 0" class="empty-msg">아직 기록이 없어요. 운동을 시작해보세요!</p>

      <div v-else v-for="workout in workouts" :key="workout.id" class="card" :class="{ 'highlight': editId === workout.id }">
        <div class="info">
          <h3>{{ workout.exerciseName }}</h3>
          <p class="basic-info">{{ workout.weight }}kg · {{ workout.setNum }}세트 · {{ workout.reps }}회</p>

          <div class="stats-row">
            <span class="badge volume-badge">총 볼륨: {{ workout.weight * workout.setNum * workout.reps }}kg</span>
            <span class="badge rm-badge">🔥 추정 1RM: {{ get1RM(workout.weight, workout.reps) }}kg</span>
          </div>
        </div>

        <div class="card-btns">
          <button @click="startEdit(workout)" class="edit-icon-btn">수정</button>
          <button @click="deleteWorkout(workout.id)" class="del-btn">삭제</button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
/* 🎨 공통 스타일 */
.container { max-width: 600px; margin: 40px auto; padding: 0 20px; font-family: 'Suit', sans-serif; }
.title { text-align: center; color: #333; margin-bottom: 30px; }

/* 🥩 단백질 트래커 스타일 */
.tracker-box { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 4px 12px rgba(255,100,100,0.1); margin-bottom: 30px; border: 2px solid #ffccc7; }
.tracker-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.tracker-header h3 { margin: 0; color: #cf1322; }
.highlight-text { color: #f5222d; font-size: 1.2rem; }
.progress-bar-bg { width: 100%; height: 25px; background: #f5f5f5; border-radius: 15px; overflow: hidden; margin-bottom: 15px; box-shadow: inset 0 1px 3px rgba(0,0,0,0.1); }
.progress-bar-fill { height: 100%; background: linear-gradient(90deg, #ff7875, #f5222d); border-radius: 15px; display: flex; align-items: center; justify-content: flex-end; padding-right: 10px; color: white; font-weight: bold; font-size: 0.85rem; transition: width 0.5s ease-in-out; }
.protein-input-row { display: flex; gap: 10px; }
.pro-input { flex: 2; padding: 10px; border: 1px solid #ffa39e; border-radius: 8px; font-size: 16px; }
.add-pro-btn { flex: 1; background: #ff4d4f; color: white; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.add-pro-btn:hover { background: #d9363e; }
.reset-pro-btn { background: transparent; border: 1px solid #d9d9d9; padding: 4px 8px; border-radius: 5px; cursor: pointer; font-size: 0.8rem; color: #666; }

/* 📊 차트 스타일 */
.chart-box { background: white; padding: 20px; border-radius: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 30px; border: 1px solid #eee; }
.chart-container { height: 250px; position: relative; }

/* 📝 입력 폼 스타일 */
.input-box { background: #f8f9fa; padding: 25px; border-radius: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 30px; border: 2px solid transparent; transition: 0.3s; }
.input-box.edit-mode { border-color: #42b883; background: #f0fdf4; }
.mode-title { margin: 0 0 15px 0; font-size: 1.1rem; color: #42b883; font-weight: bold; }
.input-row { margin-bottom: 12px; }
.three-cols { display: flex; gap: 10px; }
input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 16px; box-sizing: border-box; }
.main-input { font-weight: bold; }
.btn-group { display: flex; gap: 10px; }
.action-btn { flex: 1; padding: 15px; color: white; border: none; border-radius: 8px; font-size: 18px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.add-btn { background: #333; } .add-btn:hover { background: #555; }
.edit-btn { background: #42b883; } .edit-btn:hover { background: #3aa876; }
.cancel-btn { background: #ccc; color: white; border: none; padding: 0 20px; border-radius: 8px; cursor: pointer; font-weight: bold; }

/* 📋 리스트 & 카드 스타일 */
.card { background: white; padding: 20px; border-radius: 12px; border: 1px solid #eee; margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.03); transition: 0.2s; }
.card.highlight { border-color: #42b883; background-color: #f0fdf4; transform: scale(1.02); }
.info h3 { margin: 0 0 5px 0; color: #2c3e50; }
.basic-info { margin: 0; color: #666; font-size: 0.95rem; }

/* 🏅 배지 스타일 (볼륨, 1RM) */
.stats-row { display: flex; gap: 8px; margin-top: 10px; flex-wrap: wrap; }
.badge { padding: 4px 8px; border-radius: 6px; font-size: 0.85rem; font-weight: bold; }
.volume-badge { background: #e6f7ff; color: #007aff; border: 1px solid #bae0ff; }
.rm-badge { background: #fff0f6; color: #eb2f96; border: 1px solid #ffadd2; }

/* 🔘 수정/삭제 버튼 스타일 */
.card-btns { display: flex; gap: 8px; }
.edit-icon-btn { padding: 8px 12px; background: #f6ffed; color: #52c41a; border: 1px solid #b7eb8f; border-radius: 6px; cursor: pointer; }
.del-btn { padding: 8px 12px; background: #fff1f0; color: #ff4d4f; border: 1px solid #ffa39e; border-radius: 6px; cursor: pointer; }
.empty-msg { text-align: center; color: #aaa; padding: 40px; }
</style>