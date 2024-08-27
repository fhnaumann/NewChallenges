<template>
  <div v-if="computedGoalEntries.length === 0">
    <p>No goals to show</p>
  </div>
  <div v-for="(goalEntry, index) in computedGoalEntries" :key="index">
    <Button :label="t(`goals.types.${goalEntry.key}.name`)" @click="() => showGoal(goalEntry.key as GoalName)"/>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import type { Model } from '@criteria-interfaces/model'

const props = defineProps<{
  challenge: Model
  events: MCEvent<DataConfig>[]
}>()

import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'
import { useRoute, useRouter } from 'vue-router'
import type { CollectableEntryConfig, GoalName, GoalsConfig } from '@criteria-interfaces/goals'
import { useDialog } from 'primevue/usedialog'
import MapGoal from './goals/MapGoal.vue';
import type { MapGoalProps } from '@/components/goals/MapGoal.vue'
import type { CraftingDataConfig } from '@criteria-interfaces/crafting'
import type { DeathDataConfig } from '@criteria-interfaces/death'
import type { ItemDataConfig } from '@criteria-interfaces/item'
import type { BlockBreakDataConfig } from '../../../criteria-interfaces'

const router = useRouter()
const { t } = useI18n()
const dialog = useDialog()

const computedGoalEntries = computed(() => {
  return Object.entries(props.challenge.goals!)
    .filter(([_, value]) => value !== undefined)
    .map(([key, value]) => ({ key, value }))
})

const showGoal = (goalName: GoalName) => {
  dialog.open(MapGoal, {
    props: {
      modal: true,
      showHeader: false,
      draggable: false,
      pt: {
        root: {
          class: 'border-0 pt-5 px-12 w-[120rem]'
        },
        content: {
          class: 'rounded-none'
        },
        mask: {
          class: 'backdrop-blur-[2px]'
        }
      }
    },
    data: {
      goalName: goalName,
      collectables: assumeCollectableEntryConfigAccessFrom(goalName),
      events: filterEventsFor(goalName),
      codeAccess: assumeCodeAccessFromMCEventFrom(goalName)
    } as MapGoalProps
  })
}

function assumeCollectableEntryConfigAccessFrom(goalName: GoalName): CollectableEntryConfig[] {
  const goals: GoalsConfig = props.challenge.goals!
  switch(goalName) {
    case 'craftingGoal': return goals.craftingGoal!.crafted!
    case 'deathGoal': return goals.deathGoal!.deathMessages!
    case 'itemGoal': return goals.itemGoal!.items!
    case 'blockBreakGoal': return goals.blockBreakGoal!.broken!
  }
}

function assumeCodeAccessFromMCEventFrom(goalName: GoalName): (mcEvent: MCEvent<DataConfig>) => string {
  switch(goalName) {
    case 'craftingGoal': return mcEvent => (mcEvent.data as CraftingDataConfig).recipeCrafted
    case 'deathGoal': return mcEvent => (mcEvent.data as DeathDataConfig).deathMessageKey
    case 'itemGoal': return mcEvent => (mcEvent.data as ItemDataConfig).item
    case 'blockBreakGoal': return mcEvent => (mcEvent.data as BlockBreakDataConfig).broken
  }
}

function filterEventsFor(goalName: GoalName): MCEvent<DataConfig>[] {
  return props.events.filter(value => value.eventType === goalName)
}


</script>