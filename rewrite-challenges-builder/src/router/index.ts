import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NoBlockBreakRule from '@/components/rules/types/NoBlockBreakRule.vue'
import AllCriteriaOverview from '@/components/AllCriteriaOverview.vue'
import CustomHealthSetting from '@/components/settings/CustomHealthSetting.vue'
import BlockBreakGoal from '@/components/goals/types/BlockBreakGoal.vue'
import MobGoal from '@/components/goals/types/MobGoal.vue'
import NoMobKillRule from '@/components/rules/types/NoMobKillRule.vue'
import NoItemRule from '@/components/rules/types/NoItemRule.vue'
import ItemGoal from '@/components/goals/types/ItemGoal.vue'
import DeathGoal from '@/components/goals/types/DeathGoal.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: AllCriteriaOverview
    },
    /*
    RULES
     */
    {
      path: '/rules/noBlockBreak',
      name: 'NoBlockBreak',
      component: NoBlockBreakRule
    },
    {
      path: '/rules/noMobKill',
      name: 'NoMobKillRule',
      component: NoMobKillRule
    },
    {
      path: '/rules/noItem',
      name: 'NoItemRule',
      component: NoItemRule
    },
    /*
    GOALS
     */
    {
      path: '/goals/mobGoal',
      name: 'MobGoal',
      component: MobGoal
    },
    {
      path: '/goals/blockBreakGoal',
      name: 'BlockBreakGoal',
      component: BlockBreakGoal
    },
    {
      path: '/goals/itemGoal',
      name: 'ItemGoal',
      component: ItemGoal
    },
    {
      path: '/goals/deathGoal',
      name: 'DeathGoal',
      component: DeathGoal
    },
    /*
    SETTINGS
     */
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
