<template>
  <div
    class="grid grid-cols-[3fr,1fr] grid-flow-row auto-rows-min max-h-[50rem] rounded-lg border-2 border-primary-800 justify-items-stretch customized-goal bg-primary-200"
  >
    <div
      class="flex items-center space-x-12 col-span-full row-start-1 max-h-12 mx-2 mt-2"
    >
      <div class="flex-1">
        <InputText
          class="w-full"
          type="text"
          :placeholder="t('goals.collectables.search')"
          v-model="searchFieldValue"
        />
      </div>
      <div class="flex items-center">
        <Checkbox v-model="showCompleted" binary input-id="show_completed" />
        <label class="ml-2" for="show_completed">{{ t('goals.collectables.show_completed') }}</label></div>
    </div>
    <div v-if="goalName" class="row-start-2 overflow-y-auto max-h-[42rem]">
      <div class="grid grid-cols-[repeat(auto-fit,_minmax(150px,_1fr))] gap-4 p-4">
        <div v-for="collectable in shownCollectablesBasedOffCheckbox" :key="collectable.collectableName">
          <EntryCompletion class="w-40 max-w-40 h-40 max-h-40"
            :collectable="collectable"
            :data-source="assumeDataSourceFrom(goalName!)"
            :events="filterEventsFor(collectable.collectableName, codeAccess!, events!)"
            @mouseover="beingHovered = collectable"
            @mouseleave="beingHovered = null"
          />
        </div>
      </div>
    </div>
    <div class="col-start-2 row-start-2 mt-4">
      <div class="flex flex-col items-center justify-center h-full">
        <p class="text-3xl font-bold">{{ t('goals.collectables.entry.completion') }}</p>
        <div v-if="beingHovered" class="flex-grow flex items-start justify-center">
          <CompletionDetail
            :collectable="beingHovered"
            :events="filterEventsFor(beingHovered.collectableName, codeAccess!, events!)"
          />
        </div>
        <div v-else class="flex-grow flex items-center justify-center">
          <p>
            {{ t('goals.collectables.entry.nothing_selected') }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import DataView from 'primevue/dataview'
import InputText from 'primevue/inputtext'
import Checkbox from 'primevue/checkbox'
import { type CollectableEntryConfig, type GoalName } from '@criteria-interfaces/goals'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'
import EntryCompletion from '@/components/goals/EntryCompletion.vue'
import type { DataSource } from '@/components/MaterialItem.vue'
import { computed, inject, onMounted, ref, toRaw, toRef } from 'vue'
import { useCompletable } from '@/composables/completable'
import { useI18n } from 'vue-i18n'
import CompletionDetail from '@/components/goals/CompletionDetail.vue'

export interface MapGoalProps {
  goalName: GoalName
  collectables: CollectableEntryConfig[]
  events: MCEvent<any>[]
  codeAccess: (mcEvent: MCEvent<any>) => string
}

const dialogRef = inject('dialogRef')

const showCompleted = ref(false)

const goalName = ref<GoalName | null>(null)
const collectables = ref<CollectableEntryConfig[] | null>(null)
let events = ref<MCEvent<DataConfig>[] | null>(null)
const codeAccess = ref<((mcEvent: MCEvent<DataConfig>) => string) | null>(null)

const searchFieldValue = ref()

const beingHovered = ref<CollectableEntryConfig | null>(null)

const shownCollectablesBasedOffCheckbox = computed(() => {
  if(showCompleted.value) {
    return collectables.value
  }
  else {
    return keepNotYetComplete(collectables.value!, codeAccess.value!, events.value!)
  }
})

const { t } = useI18n()
const { keepNotYetComplete, filterEventsFor } = useCompletable()

onMounted(() => {
  const props = (dialogRef as any).value.data as MapGoalProps
  goalName.value = props.goalName
  collectables.value = props.collectables
  // eslint-disable-next-line
  events.value = props.events
  codeAccess.value = props.codeAccess

  if(collectables.value === null || codeAccess.value === null) {
    throw Error(`Did you forget to add mappings for ${goalName.value} in MapGoal.vue?`)
  }
})

function assumeDataSourceFrom(goalName: GoalName): DataSource {
  switch (goalName) {
    case 'blockBreakGoal':
    case 'blockPlaceGoal':
    case 'itemGoal':
      return 'material'
    case 'mobGoal':
      return 'entity_type'
    case 'deathGoal':
      return 'death_message'
    case 'craftingGoal':
      return 'crafting_recipe'
  }
}
</script>
