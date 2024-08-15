import { createRouter, createWebHistory } from 'vue-router'
import LiveChallenge from '@/components/LiveChallenge.vue'
import HomeView from '@/components/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/challenges/:challenge_id',
      component: LiveChallenge
    }
  ]
})

export default router
