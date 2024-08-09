import { createRouter, createWebHistory, type RouteParams } from 'vue-router'
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
import NoDeathRule from '@/components/rules/types/NoDeathRule.vue'
import NoBlockPlaceRule from '@/components/rules/types/NoBlockPlaceRule.vue'
import BlockPlaceGoal from '@/components/goals/types/BlockPlaceGoal.vue'
import UltraHardcoreSetting from '@/components/settings/UltraHardcoreSetting.vue'
import MLGSetting from '@/components/settings/MLGSetting.vue'
import FloorIsLavaSetting from '@/components/settings/FloorIsLavaSetting.vue'
import NoCraftingRule from '@/components/rules/types/NoCraftingRule.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: AllCriteriaOverview
    },
    /*
    blockplace
     */
    {
      path: '/rules/noBlockPlace',
      name: 'NoBlockPlace',
      component: NoBlockPlaceRule
    },
    {
      path: '/goals/blockPlaceGoal',
      name: 'BlockPlaceGoal',
      component: BlockPlaceGoal
    },
    /*
    blockbreak
     */
    {
      path: '/rules/noBlockBreak',
      name: 'NoBlockBreak',
      component: NoBlockBreakRule
    },
    {
      path: '/goals/blockBreakGoal',
      name: 'BlockBreakGoal',
      component: BlockBreakGoal,
    },
    /*
    mob
     */
    {
      path: '/rules/noMobKill',
      name: 'NoMobKillRule',
      component: NoMobKillRule
    },
    {
      path: '/goals/mobGoal',
      name: 'MobGoal',
      component: MobGoal
    },
    /*
    death
     */
    {
      path: '/rules/noDeath',
      name: 'NoDeath',
      component: NoDeathRule
    },
    {
      path: '/goals/deathGoal',
      name: 'DeathGoal',
      component: DeathGoal
    },
    /*
     * item
     */
    {
      path: '/rules/noItem',
      name: 'NoItemRule',
      component: NoItemRule
    },
    {
      path: '/goals/itemGoal',
      name: 'ItemGoal',
      component: ItemGoal
    },
    /*
    crafting
     */
    {
      path: '/rules/noCrafting',
      name: 'NoCraftingRule',
      component: NoCraftingRule
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
      path: '/settings/ultraHardcoreSetting',
      name: 'Ultra Hardcore',
      component: UltraHardcoreSetting
    },
    {
      path: '/settings/mlgSetting',
      name: 'MLG',
      component: MLGSetting
    },
    {
      path: '/settings/floorIsLavaSetting',
      name: 'Floor Is Lava',
      component: FloorIsLavaSetting
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
