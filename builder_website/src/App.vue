<script setup lang="ts">
  import { RouterLink, RouterView } from 'vue-router'
  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'
  import DynamicDialog from 'primevue/dynamicdialog'
  import type { MobGoalConfig } from 'criteria-interfaces'
  import type { NoDeathRuleConfig } from 'criteria-interfaces'

  const { model, set } = useModelStore()

  set('rules.enabledRules.noDeath', { punishments: { endPunishment: { affects: 'all' } } } as NoDeathRuleConfig, false)
  set('goals.mobGoal', { mobs: [{ collectableName: 'ender_dragon', collectableData: {} }], fixedOrder: true } as MobGoalConfig, false)

  const i18n = useI18n()

</script>

<template>
  <div>
  </div>
  <router-view></router-view>
  <DynamicDialog />
  <Toast />
</template>
