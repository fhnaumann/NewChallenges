<template>
  <component :class="getCriteriaColorFrom(mcEvent)" :is="getMatchingComponentFrom(mcEvent)" :data="mcEvent.data" />
</template>

<script setup lang="ts">

import type { MCEvent } from '@criteria-interfaces/live'
import type { Component } from 'vue'
import type { BlockBreakDataConfig } from '@criteria-interfaces/blockbreak'
import BlockBreakEventBox from '@/components/events/BlockBreakEventBox.vue'
import UnknownEventBox from '@/components/events/UnknownEventBox.vue'
import type { BlockPlaceDataConfig } from '@criteria-interfaces/blockplace'
import BlockPlaceEventBox from '@/components/events/BlockPlaceEventBox.vue'
import { useUtil } from '@/composables/util'
import type { MobDataConfig } from '@criteria-interfaces/mob'
import MobKillEventBox from '@/components/events/MobKillEventBox.vue'

const props = defineProps<{
  mcEvent: MCEvent<any>
}>()

const { getCriteriaColorFrom } = useUtil()

function getMatchingComponentFrom(mcEvent: MCEvent<MCEvent<any>>): Component {
  const data = mcEvent.data
  if(isBlockBreak(data)) {
    return BlockBreakEventBox
  }
  if(isBlockPlace(data)) {
    return BlockPlaceEventBox
  }
  if(isMobKill(data)) {
    return MobKillEventBox
  }
  else {
    return UnknownEventBox
  }
}

function isBlockBreak(data: any): data is BlockBreakDataConfig {
  return data && typeof data.broken === 'string'
}

function isBlockPlace(data: any): data is BlockPlaceDataConfig {
  return data && typeof data.placed === 'string'
}

function isMobKill(data: any): data is MobDataConfig {
  return data && typeof data.mob === 'string'
}

</script>