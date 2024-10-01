<template>
  <div class="flex flex-col min-w-64 max-w-80 px-4 text-color-emphasis">
    <div class="group" @mouseenter="$emit('myMouseEnter')" @mouseleave="$emit('myMouseLeave')">
      <div
        class="bg-card rounded-t-lg border-t-2 border-x-2 border-content-border ease-in-out duration-300 group-hover:bg-accent group-hover:border-accent">
        <p class="flex justify-center items-center">{{ formatTime(data.timestamp) }}</p>
      </div>
      <div
        class="bg-card px-4 py-2 rounded-b-lg border-2 border-content-border ease-in-out duration-300 group-hover:border-accent">
        <slot name="eventTrigger">

        </slot>
        <div class="flex items-center text-2xl">
          <PunishmentEventContainer :data="data as RuleDataConfig"/>
        </div>
      </div>
    </div>

  </div>

</template>

<script setup lang="ts">

import type { CriteriaKey, CriteriaType } from '@criteria-interfaces/model'
import { useI18n } from 'vue-i18n'
import PlayerHead from '@/components/PlayerHead.vue'
import { useTimeable } from '@/composables/timable'
import PunishmentEventContainer from '@/components/events/punishments/PunishmentEventContainer.vue'
import type { DataConfig } from '@criteria-interfaces/live'
import type { RuleDataConfig } from '@criteria-interfaces/rules'

const props = defineProps<{
  data: DataConfig
  eventIndex: number
  type: CriteriaKey
}>()

const { formatTime } = useTimeable()

defineEmits<{
  myMouseEnter: []
  myMouseLeave: []
}>()
</script>