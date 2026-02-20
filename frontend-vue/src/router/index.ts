import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('../views/HomeView.vue')
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/LoginView.vue')
        },
        {
            path: '/dashboard',
            name: 'dashboard',
            component: () => import('../views/DashboardView.vue')
        },
        {
            path: '/attendance',
            name: 'attendance',
            component: () => import('../views/AttendanceView.vue')
        }
    ]
})

// ✨ [핵심] 전역 라우트 가드 (문지기 역할)
router.beforeEach((to, from, next) => {
    // 1. 로그인 여부 확인 (브라우저에 아이디가 저장되어 있는지)
    const isAuthenticated = localStorage.getItem('loginUser')

    // 2. 가려는 곳이 로그인 페이지가 '아닌데', 로그인이 '안 되어' 있다면?
    if (to.name !== 'login' && !isAuthenticated) {
        next({ name: 'login' }) // 🛑 강제로 로그인 페이지로 쫓아냄!
    }
    // 3. 이미 로그인했는데 로그인 페이지로 가려고 한다면?
    else if (to.name === 'login' && isAuthenticated) {
        next({ name: 'home' }) // 🏠 메인 화면으로 돌려보냄!
    }
    // 4. 그 외의 정상적인 접근은 통과!
    else {
        next()
    }
})

export default router