<template>
  <div :class="{ 'bg-primary-800 rounded-lg': isCompletedBasedOffEvents(events, collectable.collectableData?.amountNeeded), 'bg-primary-500 rounded-lg': !isCompletedBasedOffEvents(events, collectable.collectableData?.amountNeeded) }">
    <div ref="reference" class="flex flex-col items-center justify-center rounded-lg border-2 border-primary-800 w-full h-full"
         @mouseenter="hover = true" @mouseleave="hover = false">
      <p class="font-semibold line-clamp-1">{{ translateDataRow(fromCode2DataRow(props.collectable.collectableName))
        }}</p>
      <MaterialItem :code="collectable.collectableName" :data-source="dataSource" img-class="w-12" />
      <p>{{ `${currentAmountOrDefault()}/${amountNeededOrDefault()}` }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">

import type { CollectableEntryConfig } from '@criteria-interfaces/goals'
import type { DataConfig, MCEvent, PlayerConfig } from '@criteria-interfaces/live'
import MaterialItem from '@/components/MaterialItem.vue'
import type { DataSource } from '@/components/MaterialItem.vue'
import { useTranslation } from '@/language'
import { fromCode2DataRow } from '@/composables/data_row_loaded'
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import PlayerHead from '@/components/PlayerHead.vue'
import { useTimeable } from '@/composables/timable'
import { useCompletable } from '@/composables/completable'
import { useJSONSchemaConfig } from '@/stores/default_model'
import { useFloating } from '@floating-ui/vue'

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

const hover = ref(false)

const isCompletedAfterCollectingOnce = props.collectable.collectableData.amountNeeded === 1


function currentAmountOrDefault(): number {
  return sumOfEvents(props.events)
}

function amountNeededOrDefault(): number {
  return props.collectable.collectableData?.amountNeeded ?? defaultConfig.properties.amountNeeded.default
}

</script>