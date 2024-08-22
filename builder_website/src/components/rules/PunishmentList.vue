<template>
  <div :class="globalVar ? 'customized-rule' : ''">
    <div class="flex flex-col space-y-4 mx-2 w-[32rem]">
      <CancelPunishment :model-access="createModelAccess('cancelPunishment')" />
      <SuppressPunishment v-if="suppressableRules.includes((criteriaKey as any)!)" :model-access="createModelAccess('suppressPunishment')" />
      <EndPunishment :model-access="createModelAccess('endPunishment')" />
      <HealthPunishment class="shrink" :model-access="createModelAccess('healthPunishment')" />
      <RandomEffectPunishment :model-access="createModelAccess('randomEffectPunishment')" />
      <MLGPunishment :model-access="createModelAccess('mlgPunishment')" />
    </div>
  </div>
</template>

<script setup lang="ts">
  import Checkbox from 'primevue/checkbox'
  import HealthPunishment from '@/components/rules/punishments/HealthPunishment.vue'
  import type { CriteriaKey, CriteriaType, RuleName } from 'criteria-interfaces'
  import type { ModelAccess } from '@/main'
  import type { PunishableRuleConfig } from 'criteria-interfaces'
  import type { BasePunishmentConfig, PunishmentName } from 'criteria-interfaces'
  import RandomEffectPunishment from '@/components/rules/punishments/RandomEffectPunishment.vue'
  import EndPunishment from '@/components/rules/punishments/EndPunishment.vue'
  import { inject, onMounted, ref } from 'vue'
  import MLGPunishment from '@/components/rules/punishments/MLGPunishment.vue'
  import CancelPunishment from '@/components/rules/punishments/CancelPunishment.vue'
  import SuppressPunishment from '@/components/rules/punishments/SuppressPunishment.vue'

  /*
  PunishmentList can be instantiated from two places:
  1st as an object with props as a local punishment selection.
  2nd as an object without props as a global punishment selection.
  In the latter, the information is given by the dialogRef during the mounting
   */

  const props = defineProps<{
    criteriaKey: CriteriaKey | undefined,
    global: boolean
  }>()

  const dialogRef = inject('dialogRef') as any;
  onMounted(() => {

    globalVar.value = dialogRef !== undefined ? dialogRef.value.data.global : false
    console.log("set globalVar to", globalVar.value)
  })
  const globalVar = ref<boolean>(props.global) // assume local punishment (if global, it will be overridden in onMounted)

  const suppressableRules: RuleName[] = [
    'noBlockBreak',
    'noMobKill'
  ]

  function createModelAccess(punishmentType: PunishmentName): ModelAccess<BasePunishmentConfig> {
    if (globalVar.value) {
      return {
        get: model => model.rules?.enabledGlobalPunishments?.[punishmentType],
        where: `rules.enabledGlobalPunishments.${punishmentType}`,
        testSchematron: true,
      }
    } else {
      return {
        get: model => model.rules?.enabledRules?.[props.criteriaKey!]?.punishments?.[punishmentType],
        where: `rules.enabledRules.${props.criteriaKey}.punishments.${punishmentType}`,
        testSchematron: true,
      }
    }
  }
</script>