<script setup lang="ts">
import { ref } from 'vue'
import { BASE_URL } from '../config';
import { useRouter } from 'vue-router'

const router = useRouter()

// 입력창 상태
const userId = ref('')
const password = ref('')
const name = ref('')
const isLoginMode = ref(true) // true면 로그인 화면, false면 회원가입 화면

// 🚀 로그인 실행
const doLogin = async () => {
  if (!userId.value || !password.value) return alert('아이디와 비밀번호를 입력해주세요!')

  const res = await fetch(`${BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId: userId.value, password: password.value })
  })

  const text = await res.text()
  if (!text) {
    // 서버가 null을 보냈다는 뜻 (로그인 실패)
    alert('아이디 또는 비밀번호가 틀렸습니다. ㅠㅠ')
  } else {
    // 로그인 성공! (서버가 회원 정보를 JSON으로 보냄)
    const user = JSON.parse(text)

    // ✨ 핵심: 브라우저 임시 저장소(localStorage)에 내 아이디 저장!
    localStorage.setItem('loginUser', user.userId)
    localStorage.setItem('userName', user.name)

    alert(`환영합니다, ${user.name}님! 💪`)
    router.push('/') // 메인(HomeView) 화면으로 이동!

    // 메뉴바 업데이트를 위해 새로고침 한 번 해줌
    setTimeout(() => window.location.reload(), 100)
  }
}

// 📝 회원가입 실행
const doSignup = async () => {
  if (!userId.value || !password.value || !name.value) return alert('모든 칸을 입력해주세요!')

  const res = await fetch(`${BASE_URL}/api/auth/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId: userId.value, password: password.value, name: name.value })
  })

  const msg = await res.text()
  if (msg.startsWith('fail')) {
    alert(msg.split(':')[1]) // "이미 존재하는 아이디입니다."
  } else {
    alert('회원가입 성공! 이제 로그인 해주세요. 🎉')
    isLoginMode.value = true // 로그인 화면으로 전환
  }
}
</script>

<template>
  <main class="login-container">
    <div class="login-box">
      <h1 class="title">{{ isLoginMode ? '🔐 로그인' : '📝 회원가입' }}</h1>
      <p class="subtitle">나만의 득근 일지를 시작하세요!</p>

      <div class="input-group">
        <input v-model="userId" type="text" placeholder="아이디" />
        <input v-model="password" type="password" placeholder="비밀번호" />
        <input v-if="!isLoginMode" v-model="name" type="text" placeholder="이름 (예: 영섭)" />
      </div>

      <button v-if="isLoginMode" @click="doLogin" class="main-btn">로그인</button>
      <button v-else @click="doSignup" class="main-btn signup-btn">회원가입 완료</button>

      <p class="toggle-text" @click="isLoginMode = !isLoginMode">
        {{ isLoginMode ? '아직 회원이 아니신가요? (회원가입)' : '이미 계정이 있으신가요? (로그인)' }}
      </p>
    </div>
  </main>
</template>

<style scoped>
.login-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; font-family: 'Suit', sans-serif; }
.login-box { background: white; padding: 40px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); width: 100%; max-width: 400px; text-align: center; border: 1px solid #eee; }
.title { color: #333; margin-bottom: 5px; }
.subtitle { color: #888; margin-bottom: 30px; font-size: 0.95rem; }

.input-group { display: flex; flex-direction: column; gap: 12px; margin-bottom: 25px; }
input { padding: 15px; border: 1px solid #ddd; border-radius: 10px; font-size: 16px; background: #f8f9fa; transition: 0.2s; }
input:focus { outline: none; border-color: #42b883; background: white; }

.main-btn { width: 100%; padding: 15px; background: #42b883; color: white; border: none; border-radius: 10px; font-size: 18px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.main-btn:hover { background: #3aa876; transform: translateY(-2px); }
.signup-btn { background: #333; }
.signup-btn:hover { background: #555; }

.toggle-text { margin-top: 20px; color: #666; font-size: 0.9rem; cursor: pointer; text-decoration: underline; }
.toggle-text:hover { color: #42b883; }
</style>