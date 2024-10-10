<template>
  <div class="cursor-pointer w-full h-full" @click="toggle" :data-cy="collectable.collectableName">
    <div
      :class="[
        'bg-card rounded-lg',
        isCompletedBasedOffEvents(events, collectable.collectableData?.amountNeeded) ? '' : ''
      ]">
      <div ref="reference"
           :class="{
             'flex flex-col items-center justify-between rounded-lg border border-content-border w-40 h-40': !clicked,
             'flex flex-col items-center justify-between rounded-lg border border-content-border w-40 h-40 bg-primary-400 text-color-emphasis': clicked
           }">
        <p class="font-semibold line-clamp-1 text-color">{{ translateDataRow(fromCode2DataRow(props.collectable.collectableName))
          }}</p>
        <MaterialItem :code="collectable.collectableName" :data-source="dataSource" img-class="w-12" />
        <p class="text-color" :data-cy="`${collectable.collectableName}-completionStatus`">{{ `${currentAmountOrDefault()}/${amountNeededOrDefault()}` }}</p>
      </div>
    </div>
  </div>
  <Popover ref="op" class="-translate-x-[3rem]" @hide="clicked = false">
    <CompletionDetail class="bg-card p-4 rounded-xl w-64 min-w-64 max-w-64" :collectable="collectable" :events="events" />
  </Popover>
</template>

<script setup lang="ts">
import InputText from 'primevue/inputtext'
import InputGroup from 'primevue/inputgroup'
import InputGroupAddon from 'primevue/inputgroupaddon'
import Popover from 'primevue/popover'
import type { CollectableEntryConfig } from '@criteria-interfaces/goals'
import type { DataConfig, MCEvent, PlayerConfig } from '@criteria-interfaces/live'
import MaterialItem from '@/components/MaterialItem.vue'
import type { DataSource } from '@/components/MaterialItem.vue'
import { useTranslation } from '@/composables/language'
import { fromCode2DataRow } from '@/composables/data_row_loaded'
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import PlayerHead from '@/components/PlayerHead.vue'
import { useTimeable } from '@/composables/timable'
import { useCompletable } from '@/composables/completable'
import { useJSONSchemaConfig } from '@/stores/default_model'
import { useFloating } from '@floating-ui/vue'
import CompletionDetail from '@/components/goals/CompletionDetail.vue'

const props = defineProps<{
  collectable: CollectableEntryConfig,
  events: MCEvent<DataConfig>[],
  dataSource: DataSource
}>()

const defaultConfig = useJSONSchemaConfig().CollectableDataConfig
const { translateDataRow } = useTranslation()
const { t } = useI18n()
const { formatTime } = useTimeable()
const { sumOfEvents, isCompletedBasedOffEvents } = useCompletable()

const reference = ref(null)
const floating = ref(null)
const {floatingStyles} = useFloating(reference, floating);


const op = ref(null)
const clicked = ref(false)

const toggle = (event) => {
  op.value!.toggle(event);
  clicked.value = !clicked.value
}


const isCompletedAfterCollectingOnce = props.collectable.collectableData.amountNeeded === 1


function currentAmountOrDefault(): number {
  return sumOfEvents(props.events)
}

function amountNeededOrDefault(): number {
  return props.collectable.collectableData?.amountNeeded ?? defaultConfig.properties.amountNeeded.default
}

</script>