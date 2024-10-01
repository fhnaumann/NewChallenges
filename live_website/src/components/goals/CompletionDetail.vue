<template>
  <div class="flex flex-col items-center text-color">
    <div v-if="!isSingleCompletionType(collectable.collectableData)" class="space-y-4">
      <div v-if="isCompletedBasedOffEvents(events, collectable.collectableData.amountNeeded)">
        <p>{{ t('goals.collectables.entry.collected_multiple_complete', { time: formatTime(events.at(-1)) }) }}</p>
      </div>
      <div v-else>
        <p>{{ t('goals.collectables.entry.collected_incomplete') }}</p>
      </div>
      <div v-for="contributor in getUniquePlayersThatContributed()" :key="contributor.player.playerUUID">
        <i18n-t keypath="goals.collectables.entry.collected_multiple" tag="div" class="flex items-center">
          <template #player>
            <div class="mx-2">
              <PlayerHead v-bind="contributor.player" :size=32 />
            </div>
          </template>
          <template #amount>
            <p class="ml-2">{{ contributor.totalAmount }}</p>
          </template>
        </i18n-t>
      </div>
    </div>
    <div v-else class="space-y-4">
      <div v-if="isCompletedBasedOffEvents(events, collectable.collectableData.amountNeeded)">
        <i18n-t keypath="goals.collectables.entry.collected_once_complete" tag="div" class="flex items-center">
          <template #player>
            <div class="mx-2">
              <PlayerHead v-bind="events[0].data?.player" :size=32 />
            </div>
          </template>
          <template #time>
            <p class="ml-2">{{ formatTime(events[0].timestamp) }}</p>
          </template>
        </i18n-t>
      </div>
      <div v-else>
        <p>{{ t('goals.collectables.entry.collected_incomplete') }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CollectableEntryConfig } from '@criteria-interfaces/goals'
import { useCompletable } from '@/composables/completable'
import { useI18n } from 'vue-i18n'
import PlayerHead from '@/components/PlayerHead.vue'
import type { DataConfig, MCEvent, PlayerConfig } from '@criteria-interfaces/live'
import { useTimeable } from '@/composables/timable'

const props = defineProps<{
  collectable: CollectableEntryConfig,
  events: MCEvent<DataConfig>[]
}>()

const { t } = useI18n()

const { formatTime } = useTimeable()
const { isCompletedBasedOffEvents, isSingleCompletionType } = useCompletable()

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

</script>
