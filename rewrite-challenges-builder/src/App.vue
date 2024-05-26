<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
import { useModelStore } from '@/stores/model'
import type { NoBlockBreakRuleConfig } from '@/models/blockbreak'
import { useI18n } from 'vue-i18n'
import DynamicDialog from 'primevue/dynamicdialog'

const modelStore = useModelStore()

console.log("early", modelStore.model)
modelStore.set("rules.enabledRules.noBlockBreak", {}, true)
modelStore.set("settings.customHealthSetting", {}, true)
modelStore.set('goals.blockBreakGoal', {}, true)
modelStore.set('goals.mobGoal', {}, true)
modelStore.set('rules.enabledRules.noMobKill', {}, true)
modelStore.set('rules.enabledRules.noMobKill2', {}, true)
modelStore.set('rules.enabledRules.noMobKill3', {}, true)
modelStore.set('rules.enabledRules.noMobKill4', {}, true)
modelStore.set('rules.enabledRules.noMobKill5', {}, true)
modelStore.set('rules.enabledRules.noMobKill6', {}, true)
modelStore.set('rules.enabledRules.noMobKill7', {}, true)
modelStore.set('rules.enabledRules.noMobKill8', {}, true)
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
