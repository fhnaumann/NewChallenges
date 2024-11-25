<template>
  <div v-if="hasAppliedPunishments(data)">
    <p>123</p>
    <component
      class="customized-rule"
      v-for="punishment in data.appliedPunishments"
      :key="punishment.punishmentName"
      :is="getMatchingPunishmentComponentFrom(punishment)"
      :causer="data.player"
      :data="punishment"
    />
  </div>
</template>

<script setup lang="ts">
import type { RuleDataConfig } from '@fhnaumann/criteria-interfaces'
import { useUtil } from '@/composables/util'
import type { Component } from 'vue'
import type {
  BasePunishmentDataConfig,
  HealthPunishmentDataConfig
} from '@fhnaumann/criteria-interfaces'
import HealthPunishmentEventPart from '@/components/events/punishments/HealthPunishmentEventPart.vue'
import UnknownEventBox from '@/components/events/UnknownEventBox.vue'
import RandomEffectPunishmentEventPart from './RandomEffectPunishmentEventPart.vue'

const props = defineProps<{
  data: RuleDataConfig
}>()

const { hasAppliedPunishments } = useUtil()

function getMatchingPunishmentComponentFrom(data: BasePunishmentDataConfig): Component {
  if (data.punishmentName === 'healthPunishment') {
    return HealthPunishmentEventPart
  }
  if (data.punishmentName === 'randomEffectPunishment') {
    return RandomEffectPunishmentEventPart
  }
  return UnknownEventBox
}
</script>
