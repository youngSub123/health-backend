<script setup lang="ts">
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()

// ✨ 로그아웃 처리 함수 추가! (스크립트 안에서는 localStorage를 바로 쓸 수 있습니다)
const handleLogout = () => {
  localStorage.clear() // 내 정보 싹 지우기
  window.location.href = '/login' // 로그인 화면으로 강제 이동
}
</script>

<template>
  <header v-if="route.path !== '/login'">
    <nav>
      <RouterLink to="/">🏠 홈</RouterLink>
      <RouterLink to="/dashboard">⚙️ 대시보드</RouterLink>
      <RouterLink to="/attendance">📅 오운완 인증</RouterLink>

      <a href="#" @click.prevent="handleLogout" class="logout-link">
        로그아웃 🚪
      </a>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
/* 전체 레이아웃 스타일 */
header {
  background-color: #333;
  padding: 1rem;
  text-align: center;
}

/* 기존 스타일 유지하고 로그아웃 버튼 스타일만 추가 */
.logout-link {
  color: #ff4d4f;
  margin-left: auto; /* 메뉴바 오른쪽 끝으로 밀어내기 */
  font-weight: bold;
}
.logout-link:hover { color: #d9363e; }

nav {
  display: flex;
  justify-content: center;
  gap: 20px;
}

nav a {
  color: white;
  text-decoration: none;
  font-weight: bold;
  font-size: 1.2rem;
}

nav a:hover {
  color: #42b883; /* Vue의 상징색 (초록) */
}

/* 현재 보고 있는 메뉴 강조 */
.router-link-active {
  color: #42b883;
  border-bottom: 2px solid #42b883;
}
</style>