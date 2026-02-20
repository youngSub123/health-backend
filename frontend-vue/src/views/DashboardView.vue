<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

const userId = ref(localStorage.getItem('loginUser') || '')

// 입력 폼 데이터 (익숙한 수치로 렌더링되도록 기본값 세팅)
const height = ref(184)
const weight = ref(96)

// 🧮 자동 계산 로직 (Vue의 computed 활용)
const bmi = computed(() => {
  if (height.value === 0) return 0
  const heightInMeter = height.value / 100
  return (weight.value / (heightInMeter * heightInMeter)).toFixed(1)
})

const recommendedProtein = computed(() => {
  // 체중 1kg당 1.6g 곱하기 (근비대 타겟)
  return Math.round(weight.value * 1.6)
})

// 서버에서 내 프로필 가져오기
const fetchProfile = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/profile/${userId.value}`)
    const data = await res.json()
    height.value = data.height
    weight.value = data.weight
  } catch (err) {
    console.error('프로필 로딩 실패:', err)
  }
}

// 서버에 내 프로필 저장하기
const saveProfile = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/profile', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: userId.value,
        height: height.value,
        weight: weight.value,
        targetProtein: recommendedProtein.value
      })
    })
    const msg = await res.text()
    alert(msg)
  } catch (err) {
    alert('저장 실패 ㅠㅠ')
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<template>
  <main class="container">
    <h1 class="title">⚙️ 나의 신체 대시보드</h1>

    <div class="dashboard-card">
      <h2 class="subtitle">신체 정보 입력</h2>

      <div class="input-group">
        <label>키 (cm)</label>
        <input v-model="height" type="number" class="dash-input" />
      </div>

      <div class="input-group">
        <label>몸무게 (kg)</label>
        <input v-model="weight" type="number" class="dash-input" />
      </div>

      <button @click="saveProfile" class="save-btn">내 정보 업데이트 💾</button>
    </div>

    <div class="result-card">
      <h2 class="subtitle">📊 분석 결과</h2>

      <div class="stat-row">
        <span>현재 BMI (체질량지수)</span>
        <span class="stat-value">{{ bmi }}</span>
      </div>

      <div class="stat-row highlight">
        <span>일일 권장 단백질 섭취량</span>
        <span class="stat-value">{{ recommendedProtein }}g</span>
      </div>
      <p class="desc-text">* 근비대 및 체지방 커팅을 위해 체중 1kg당 1.6g으로 계산되었습니다.</p>
    </div>
  </main>
</template>

<style scoped>
.container { max-width: 500px; margin: 40px auto; font-family: 'Suit', sans-serif; padding: 0 20px; }
.title { text-align: center; color: #333; margin-bottom: 30px; }
.subtitle { margin-top: 0; color: #2c3e50; font-size: 1.2rem; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }

.dashboard-card, .result-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 20px; border: 1px solid #eee; }

.input-group { margin-bottom: 15px; display: flex; flex-direction: column; gap: 8px; }
.input-group label { font-weight: bold; color: #666; }
.dash-input { padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 16px; }

.save-btn { width: 100%; padding: 15px; background: #333; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: bold; cursor: pointer; margin-top: 10px; transition: 0.2s; }
.save-btn:hover { background: #555; }

.stat-row { display: flex; justify-content: space-between; align-items: center; padding: 15px 0; border-bottom: 1px dashed #eee; font-size: 1.1rem; color: #555; }
.stat-row:last-of-type { border-bottom: none; }
.stat-value { font-weight: bold; font-size: 1.3rem; color: #2c3e50; }

.highlight { background: #f0fdf4; padding: 15px; border-radius: 8px; margin-top: 10px; border: 1px solid #b7eb8f; }
.highlight .stat-value { color: #42b883; font-size: 1.5rem; }
.desc-text { font-size: 0.85rem; color: #888; margin-top: 10px; text-align: center; }
</style>