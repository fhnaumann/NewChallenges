<template>
  <div class="flex flex-col min-w-64 max-w-80 px-4">
    <div class="bg-primary-900 rounded-t-lg">
      <p class="flex justify-center items-center">{{ formatTime(time) }}</p>
    </div>
    <div class="bg-primary-500 px-4 py-2">
      <slot name="eventTrigger">

      </slot>
    </div>
  </div>

</template>

<script setup lang="ts">

import type { CriteriaKey, CriteriaType } from '@criteria-interfaces/model'
import { useI18n } from 'vue-i18n'
import PlayerHead from '@/components/PlayerHead.vue'

const props = defineProps<{
  criteriaType: CriteriaType
  criteriaKey: CriteriaKey
  time: number
}>()

const { t } = useI18n()

const translationKey = `event.${props.criteriaKey}`

function formatTime(time: number): string {
  const one_second = 1
  const minute_in_sec = 60 * one_second
  const hour_in_sec = 60 * minute_in_sec
  const day_in_sec = 24 * hour_in_sec

  const timeParts: string[] = []
  let remainingTime = time
  if(remainingTime >= day_in_sec) {
    timeParts.push(`${remainingTime / day_in_sec}d`)
    remainingTime %= day_in_sec
  }
  if(remainingTime >= hour_in_sec) {
    timeParts.push(`${remainingTime / hour_in_sec}h`)
    remainingTime %= hour_in_sec
  }
  if(remainingTime >= minute_in_sec) {
    timeParts.push(`${remainingTime / minute_in_sec}m`)
    remainingTime %= minute_in_sec
  }
  timeParts.push(`${remainingTime}s`)

  return timeParts.join(" ")

}

</script>