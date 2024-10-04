<template>
  <!--div class="flex flex-col items-center">
    <div>
      <p>{{ t('sidebar.stats.title') }}</p>
    </div>
  </div-->

  <div class="flex h-96">
    <div
      :class="[
        'fixed inset-y-0 left-0 z-50 text-color p-4 transition-all duration-300 transform sm:static space-y-4',
        sidebarOpen ? 'translate-x-0 w-96' : 'translate-x-0 w-12'
      ]"
    >
      <div class="flex items-center justify-start">
        <!-- Toggle button always at top left -->
        <!--Button icon-class="text-color" icon="pi pi-bars" text @click="toggleSidebar" /-->
        <div class="flex items-center justify-center w-full">
          <p
            :class="[
              'delay-100 animate-fadein text-color text-3xl font-semi-bold',
              sidebarOpen ? 'visible' : 'invisible'
            ]"
          >
            {{ t('sidebar.stats.title') }}
          </p>
        </div>
      </div>
      <div v-if="sidebarOpen" class="delay-100 bg-card text-color flex flex-col items-center">
        <div v-if="computedGoalEntries.length === 0">
          <p>No goals to show</p>
        </div>
        <div v-for="(goalEntry, index) in computedGoalEntries" :key="index" class="space-y-2">
          <div
            class="flex flex-col cursor-pointer ease-in-out duration-300 hover:bg-content-hover-background w-full py-2 px-8 text-xl rounded-xl border border-content-border hover:border-goal-accent"
            @click="() => showGoal(goalEntry.key as GoalName)"
          >
            <p>
              {{
                t('sidebar.stats.goal.title', {
                  goal: t(`goals.types.${goalEntry.key}.name`),
                  percentage: filterEventsFor(goalEntry.key)
                })
              }}
            </p>
            <div v-if="hasGoalTimer(goalEntry.key as GoalName)">
              <div>
                <ProgressBar :value="asPercentage(goalEntry.key as GoalName)">{{}}</ProgressBar>
              </div>
              <p class="flex justify-center text-sm">{{ formatTime(calculateRemainingTime(goalEntry.key as GoalName)) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import ProgressBar from 'primevue/progressbar'
import type { Model } from '@criteria-interfaces/model'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'
import { useRouter } from 'vue-router'
import type { CollectableEntryConfig, GoalName, GoalsConfig } from '@criteria-interfaces/goals'
import { useDialog } from 'primevue/usedialog'
import MapGoal from './goals/MapGoal.vue'
import type { MapGoalProps } from '@/components/goals/MapGoal.vue'
import type { CraftingDataConfig } from '@criteria-interfaces/crafting'
import type { DeathDataConfig } from '@criteria-interfaces/death'
import type { ItemDataConfig } from '@criteria-interfaces/item'
import type { BlockBreakDataConfig, MobDataConfig } from '../../../criteria-interfaces'
import { useTimeable } from '@/composables/timable'

const props = defineProps<{
  challenge: Model
  events: MCEvent<DataConfig>[]
  currentTime: number
}>()

const router = useRouter()
const { t } = useI18n()
const dialog = useDialog()
const { formatTime } = useTimeable()

const sidebarOpen = ref(true)

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}

function hasGoalTimer(goalName: GoalName): boolean {
  return props.challenge?.goals[goalName]?.goalTimer !== undefined
}

function calculateRemainingTime(goalName: GoalName): number {
  /*
  In theory the remaining time is stored inside the goalTimer, but the JSON file is not sent every second. It is only sent
  at specific "sync-spots" like challenge start/pause events. Therefore, the live frontend will calculate the remaining
  time on its own based on its own timer (that is synced when an event occurred). It calculates with
  remainingTime = startingTime - totalTimePassed
  The calculation above should only occur if the currentOrder value of the given goal matches the active currentOrder value.

   */
  const goalTimer = props.challenge!.goals![goalName]!.goalTimer!
  if (goalTimer.order !== props.challenge.currentOrder) {
    console.log(1)
    return goalTimer.startingTime // shouldn't really happen in practise
  }
  if (goalTimer.time === -1) {
    console.log(2)
    return goalTimer.startingTime // shouldn't really happen in practise
  }
  return Math.max(goalTimer.startingTime - props.currentTime, 0)
}

function asPercentage(goalName: GoalName): number {
  const goalTimer = props.challenge!.goals![goalName]!.goalTimer!
  const remainingTime = calculateRemainingTime(goalName)
  return (remainingTime / goalTimer.startingTime) * 100
}

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
      blockScroll: true,
      position: 'top',
      pt: {
        root: {
          class: 'border-0 pt-5 px-12 w-[120rem]'
        },
        content: {
          class: 'rounded-none'
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
  switch (goalName) {
    case 'craftingGoal':
      return goals.craftingGoal!.crafted!
    case 'deathGoal':
      return goals.deathGoal!.deathMessages!
    case 'itemGoal':
      return goals.itemGoal!.items!
    case 'blockBreakGoal':
      return goals.blockBreakGoal!.broken!
    case 'mobGoal':
      return goals.mobGoal!.mobs!
  }
}

function assumeCodeAccessFromMCEventFrom(
  goalName: GoalName
): (mcEvent: MCEvent<DataConfig>) => string {
  switch (goalName) {
    case 'craftingGoal':
      return (mcEvent) => (mcEvent.data as CraftingDataConfig).recipeCrafted
    case 'deathGoal':
      return (mcEvent) => (mcEvent.data as DeathDataConfig).deathMessageKey
    case 'itemGoal':
      return (mcEvent) => (mcEvent.data as ItemDataConfig).item
    case 'blockBreakGoal':
      return (mcEvent) => (mcEvent.data as BlockBreakDataConfig).broken
    case 'mobGoal':
      return (mcEvent) => (mcEvent.data as MobDataConfig).mob
  }
}

function filterEventsFor(goalName: GoalName): MCEvent<DataConfig>[] {
  return props.events.filter((value) => value.eventType === goalName)
}
</script>