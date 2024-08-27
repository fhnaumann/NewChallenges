<template>
  <div ref="reference" class="flex flex-col items-center justify-center bg-primary-500 rounded-lg border-b-2 border-primary-800"
       @mouseenter="hover = true" @mouseleave="hover = false">
    <p class="font-semibold line-clamp-1">{{ translateDataRow(fromCode2DataRow(props.collectable.collectableName)) }}</p>
    <MaterialItem :code="collectable.collectableName" :data-source="dataSource" img-class="w-12" />
    <p>{{ `${currentAmountOrDefault()}/${amountNeededOrDefault()}` }}</p>
  </div>
  <div ref="floating" :style="floatingStyles" class="flex flex-col items-center justify-center bg-primary-500 rounded-lg">
    <p>{{ t('goals.collectables.entry.completion') }}</p>
    <div v-if="isCompleted(props.collectable.collectableData)">
      <i18n-t v-if="isCompletedAfterCollectingOnce" keypath="goals.collectables.entry.collected_once_complete" tag="div"
              class="flex items-center">
        <template #player>
          <PlayerHead v-bind="events[0].data!.player!" />
        </template>
        <template #time>
          <p>{{ formatTime(events[0].timestamp) }}</p>
        </template>
      </i18n-t>
      <div v-else>
        <i18n-t v-for="contribution in getUniquePlayersThatContributed()" :key="contribution.player.playerUUID" keypath="goals.collectables.entry.multiple" tag="div" class="flex items-center">
          <template #player>
            <PlayerHead v-bind="contribution.player" />
          </template>
          <template #amount>
            <p>{{ contribution.totalAmount }}</p>
          </template>
        </i18n-t>
      </div>
    </div>
    <div v-else>
      <p>{{ t(`goals.collectables.entry.collected_incomplete`) }}</p>
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
const { isCompleted } = useCompletable()

const reference = ref(null)
const floating = ref(null)
const {floatingStyles} = useFloating(reference, floating);

const hover = ref(false)

console.log("entry begin")

onMounted(() => console.log("entry mounted"))

const isCompletedAfterCollectingOnce = props.collectable.collectableData.amountNeeded === 1

function getUniquePlayersThatContributed(): PlayerContributions[] {
  const playerContributions: PlayerContributions[] = []

  props.events.forEach(value => {
    const involvedPlayer = playerContributions.find(contribution => contribution.player.playerUUID === value.data?.player.playerUUID)
    if (involvedPlayer) {
      involvedPlayer.totalAmount += value.data?.amount ?? 0
    } else {
      playerContributions.push({ player: value.data!.player, totalAmount: value.data!.amount })
    }
  })

  playerContributions.sort((a, b) => b.totalAmount - a.totalAmount)
  return playerContributions
}

interface PlayerContributions {
  player: PlayerConfig,
  totalAmount: number
}

function currentAmountOrDefault(): number {
  return props.collectable.collectableData?.currentAmount ?? defaultConfig.properties.currentAmount.default
}

function amountNeededOrDefault(): number {
  return props.collectable.collectableData?.amountNeeded ?? defaultConfig.properties.amountNeeded.default
}

</script>