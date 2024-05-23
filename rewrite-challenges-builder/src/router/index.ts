import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NoBlockBreakRule from '@/components/rules/types/NoBlockBreakRule.vue'
import AllCriteriaOverview from '@/components/AllCriteriaOverview.vue'
import CustomHealthSetting from '@/components/settings/CustomHealthSetting.vue'
import BlockBreakGoal from '@/components/goals/types/BlockBreakGoal.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: AllCriteriaOverview
    },
    {
      path: '/rules/noBlockBreak',
      name: 'NoBlockBreak',
      component: NoBlockBreakRule
    },
    {
      path: '/goals/blockBreakGoal',
      name: 'BlockBreakGoal',
      component: BlockBreakGoal
    },
    {
      path: '/settings/customHealthSetting',
      name: 'Custom Health',
      component: CustomHealthSetting
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
  ]
})

export default router
