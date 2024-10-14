<template>
  <component :class="getCriteriaColorFrom(mcEvent)" :is="getMatchingComponentFrom(mcEvent)" :data="mcEvent.data" :type="mcEvent.eventType" :eventIndex="eventIndex"/>
</template>

<script setup lang="ts">

import type { MCEvent } from '@fhnaumann/criteria-interfaces'
import type { Component } from 'vue'
import type { BlockBreakDataConfig } from '@fhnaumann/criteria-interfaces'
import BlockBreakEventBox from '@/components/events/BlockBreakEventBox.vue'
import UnknownEventBox from '@/components/events/UnknownEventBox.vue'
import type { BlockPlaceDataConfig } from '@fhnaumann/criteria-interfaces'
import BlockPlaceEventBox from '@/components/events/BlockPlaceEventBox.vue'
import { useUtil } from '@/composables/util'
import type { MobDataConfig } from '@fhnaumann/criteria-interfaces'
import MobKillEventBox from '@/components/events/MobKillEventBox.vue'
import type { ItemDataConfig } from '@fhnaumann/criteria-interfaces'
import type { DeathDataConfig } from '@fhnaumann/criteria-interfaces'
import type { CraftingDataConfig } from '@fhnaumann/criteria-interfaces'
import ItemCollectEventBox from '@/components/events/ItemCollectEventBox.vue'
import DeathEventBox from '@/components/events/DeathEventBox.vue'
import RecipeCraftingEventBox from '@/components/events/RecipeCraftingEventBox.vue'

const props = defineProps<{
  mcEvent: MCEvent<any>,
  eventIndex: number
}>()

const { getCriteriaColorFrom } = useUtil()

function getMatchingComponentFrom(mcEvent: MCEvent<any>): Component {
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
  if(isItemCollect(data)) {
    return ItemCollectEventBox
  }
  if(isDeath(data)) {
    return DeathEventBox
  }
  if(isRecipeCrafting(data)) {
    return RecipeCraftingEventBox
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

function isItemCollect(data: any): data is ItemDataConfig {
  return data && typeof data.item === 'string'
}

function isDeath(data: any): data is DeathDataConfig {
  return data && typeof data.deathMessageKey === 'string'
}

function isRecipeCrafting(data: any): data is CraftingDataConfig {
  return data && typeof data.recipeCrafted === 'string'
}


</script>