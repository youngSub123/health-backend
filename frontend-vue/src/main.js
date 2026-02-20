import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 1. 라우터 설정 파일 가져오기

const app = createApp(App)

app.use(router) // 👈 2. [핵심] 앱에 라우터를 장착합니다! (이게 없으면 에러남)

app.mount('#app')