<template>
  <div class="grid grid-cols-1 grid-rows-2">
    <div class="flex">
      <InputText type="text" :placeholder="t('goals.collectables.search')" v-model="searchFieldValue" />
    </div>
    <Checkbox v-model="showCompleted" binary />
    <div v-if="goalName">
      <DataView :value="collectables" layout="grid">
        <template #grid="slotProps">
          <div class="grid grid-cols-12 gap-4 w-full">
            <div class="col-span-1" v-for="(collectable, index) in slotProps.items" :key="index">
              <EntryCompletion class="w-full h-full" :collectable="collectable" :data-source="assumeDataSourceFrom(goalName!)"
                               :events="filterEventsFor(collectable.collectableName)" />
            </div>
          </div>
        </template>
      </DataView>
      <!--div v-for="collectable in collectables" :key="collectable.collectableName">
        <EntryCompletion v-if="showCompleted || !isCompleted(collectable.collectableData)" :collectable="collectable"
                          :data-source="assumeDataSourceFrom(goalName!)"
                          :events="filterEventsFor(collectable.collectableName)" />
      </div-->

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
import { inject, onMounted, ref } from 'vue'
import { useCompletable } from '@/composables/completable'
import { useI18n } from 'vue-i18n'
import Temp from '@/components/goals/Temp.vue'

export interface MapGoalProps {
  goalName: GoalName
  collectables: CollectableEntryConfig[]
  events: MCEvent<DataConfig>[],
  codeAccess: (mcEvent: MCEvent<DataConfig>) => string
}

const dialogRef = inject('dialogRef')

const showCompleted = ref(false)

const goalName = ref<GoalName | null>(null)
const collectables = ref<CollectableEntryConfig[] | null>(null)
const events = ref<MCEvent<DataConfig>[] | null>(null)
const codeAccess = ref<((mcEvent: MCEvent<DataConfig>) => string) | null>(null)

const searchFieldValue = ref()

const { t } = useI18n()
const { isCompleted } = useCompletable()

console.log('begin')

onMounted(() => {
  const props = (dialogRef as any).value.data as MapGoalProps
  goalName.value = props.goalName
  collectables.value = props.collectables
  events.value = props.events
  codeAccess.value = props.codeAccess
  console.log('mount')
})

function filterEventsFor(code: string): MCEvent<DataConfig>[] {
  return events.value!.filter(mcEvent => codeAccess.value!(mcEvent) === code)
}

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