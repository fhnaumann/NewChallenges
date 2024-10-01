<template>
  <div v-if="hasAppliedPunishments(data)">
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
import type { RuleDataConfig } from '@criteria-interfaces/rules'
import type {
  BlockBreakDataConfig,
  NoBlockBreakRuleDataConfig
} from '@criteria-interfaces/blockbreak'
import { useUtil } from '@/composables/util'
import type { Component } from 'vue'
import type {
  BasePunishmentDataConfig,
  HealthPunishmentDataConfig
} from '@criteria-interfaces/punishments'
import HealthPunishmentEventPart from '@/components/events/punishments/HealthPunishmentEventPart.vue'
import UnknownEventBox from '@/components/events/UnknownEventBox.vue'

const props = defineProps<{
  data: RuleDataConfig
}>()

const { hasAppliedPunishments } = useUtil()

function getMatchingPunishmentComponentFrom(data: BasePunishmentDataConfig): Component {
  if (data.punishmentName === 'healthPunishment') {
    return HealthPunishmentEventPart
  }

  return UnknownEventBox
}
</script>
