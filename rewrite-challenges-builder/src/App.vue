<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useModelStore } from '@/stores/model'
import { useI18n } from 'vue-i18n'
import DynamicDialog from 'primevue/dynamicdialog'

const { model, set } = useModelStore()

set("rules.enabledRules.noBlockBreak", {}, true)
set("settings.customHealthSetting", {}, true)
set('goals.blockBreakGoal', {}, true)
set('goals.mobGoal', {}, true)

const i18n = useI18n()

</script>

<template>
  <div>
  </div>
  <router-view></router-view>
  <DynamicDialog />
  <Toast />
</template>
