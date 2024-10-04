<template>
  <div
    class="max-h-[50rem] rounded-lg justify-items-stretch bg-background-color border-2 border-content-border text-color"
  >
    <div>
      <div class="flex items-center space-x-12 col-span-full row-start-1 max-h-12 mx-2 mt-2">
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
          <label class="ml-2" for="show_completed">{{
              t('goals.collectables.show_completed')
            }}</label>
        </div>
      </div>
      <div class="flex justify-center mt-2"><p>{{ t('goals.collectables.completionStatusTip') }}</p></div>
    </div>
    <div v-if="goalName" class="row-start-2 overflow-y-auto max-h-[42rem]">
      <!--div class="grid grid-cols-[repeat(auto-fit,_minmax(150px,_1fr))] gap-4 p-4">
        <div
          v-for="collectable in shownCollectablesBasedOffCheckbox"
          :key="collectable.collectableName"
          class="w-40 max-w-40 h-40 max-h-40 min-w-40 min-h-40 flex-none"
        >
          <EntryCompletion
            :collectable="collectable"
            :data-source="assumeDataSourceFrom(goalName!)"
            :events="filterEventsFor(collectable.collectableName, codeAccess!, events!)"
          />
        </div>
      </div-->
      <Paginator :pt="{root: '!bg-background-color flex items-center justify-center flex-wrap px-4 py-2 border-0 rounded-md text-color'}" template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink" :total-records="computedPartialMatches.length" :rows="27" v-model:first="first"/>
      <DataView :pt="{content: '!bg-background-color'}" :value="computedPartialMatches.slice(first, first+27)" layout="grid">
        <template #grid="slotProps">
          <div class="grid grid-cols-[repeat(auto-fit,_minmax(150px,_1fr))] gap-4 p-4">
            <div
              v-for="collectable in slotProps.items"
              :key="collectable.collectableName"
              class="w-40 max-w-40 h-40 max-h-40 min-w-40 min-h-40 flex-none"
            >
              <EntryCompletion
                :collectable="collectable"
                :data-source="assumeDataSourceFrom(goalName!)"
                :events="filterEventsFor(collectable.collectableName, codeAccess!, events!)"
              />
            </div>
          </div>
        </template>
        <template #empty>
          <div class="flex items-center justify-center"><p class="text-2xl text-color">{{ t('goals.collectables.no_match') }}</p></div>
        </template>
      </DataView>
    </div>
  </div>
</template>

<script setup lang="ts">
import Paginator from 'primevue/paginator'
import DataView from 'primevue/dataview'
import InputText from 'primevue/inputtext'
import Checkbox from 'primevue/checkbox'
import { type CollectableEntryConfig, type GoalName } from '@criteria-interfaces/goals'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'
import EntryCompletion from '@/components/goals/EntryCompletion.vue'
import type { DataSource } from '@/components/MaterialItem.vue'
import { computed, inject, onMounted, ref, toRaw, toRef, watch } from 'vue'
import { useCompletable } from '@/composables/completable'
import { useI18n } from 'vue-i18n'
import CompletionDetail from '@/components/goals/CompletionDetail.vue'
import { useSearchable } from '@/composables/searchable'
import { useTranslation } from '@/composables/language'
import { fromCode2DataRow } from '@/composables/data_row_loaded'

export interface MapGoalProps {
  goalName: GoalName
  collectables: CollectableEntryConfig[]
  events: MCEvent<any>[]
  codeAccess: (mcEvent: MCEvent<any>) => string
}

const dialogRef = inject('dialogRef')

const searchableAccessor = (value: CollectableEntryConfig) => translateDataRow(fromCode2DataRow(value.collectableName))
let searchable = ref(null)

const showCompleted = ref(false)
watch(showCompleted,(newValue) => {
  if(newValue) {
    searchable.value = useSearchable(collectables.value!, searchableAccessor)
    // Ugly but necessary: I need to reassign the actual ref variable (not the variable within the ref) to keep reactivity
    // between the ref in the composable (searchable) and this ref here.
    // eslint-disable-next-line
    searchFieldValue = searchable.value.searchFieldValue
    getPartialMatches.value = searchable.value.getPartialMatches
    console.log(collectables.value!)
    console.log(searchable)
  }
  else {
    searchable.value = useSearchable(keepNotYetComplete(collectables.value!, codeAccess.value!, events.value!), searchableAccessor)
    // Ugly but necessary: I need to reassign the actual ref variable (not the variable within the ref) to keep reactivity
    // between the ref in the composable (searchable) and this ref here.
    // eslint-disable-next-line
    searchFieldValue = searchable.value.searchFieldValue
    getPartialMatches.value = searchable.value.getPartialMatches
  }
  console.log(shownCollectablesBasedOffCheckbox.value)
})

const goalName = ref<GoalName | null>(null)
const collectables = ref<CollectableEntryConfig[] | null>(null)
let events = ref<MCEvent<DataConfig>[] | null>(null)
const codeAccess = ref<((mcEvent: MCEvent<DataConfig>) => string) | null>(null)

let searchFieldValue = ref()
const getPartialMatches = ref()

const first = ref(0)

const computedPartialMatches = computed(() => {
  return ((getPartialMatches.value !== undefined ? getPartialMatches.value() : []))
})
const shownCollectablesBasedOffCheckbox = ref<CollectableEntryConfig[]>([])

const { t } = useI18n()
const { keepNotYetComplete, filterEventsFor } = useCompletable()

const { translateDataRow } = useTranslation()

onMounted(() => {
  const props = (dialogRef as any).value.data as MapGoalProps
  goalName.value = props.goalName
  collectables.value = props.collectables
  // eslint-disable-next-line
  events.value = props.events
  codeAccess.value = props.codeAccess

  if (collectables.value === null || codeAccess.value === null) {
    throw Error(`Did you forget to add mappings for ${goalName.value} in MapGoal.vue?`)
  }



  searchable.value = useSearchable(
    props.collectables, searchableAccessor
  )

  // Ugly but necessary: I need to reassign the actual ref variable (not the variable within the ref) to keep reactivity
  // between the ref in the composable (searchable) and this ref here.
  // eslint-disable-next-line
  searchFieldValue = searchable.value.searchFieldValue
  getPartialMatches.value = searchable.value.getPartialMatches
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
