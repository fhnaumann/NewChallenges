<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useModelStore } from '@/stores/model'
import { useI18n } from 'vue-i18n'
import DynamicDialog from 'primevue/dynamicdialog'

const modelStore = useModelStore()

console.log("early", modelStore.model)
modelStore.set("rules.enabledRules.noBlockBreak", {}, true)
modelStore.set("settings.customHealthSetting", {}, true)
modelStore.set('goals.blockBreakGoal', {}, true)
modelStore.set('goals.mobGoal', {}, true)

console.log("reached")

const i18n = useI18n()
console.log("1", i18n.locale.value)
console.log("2", i18n.messages.value[i18n.locale.value])

</script>

<template>
  <div>
  </div>
  <router-view></router-view>
  <DynamicDialog />
</template>
