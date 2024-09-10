<template>
  <div class="flex flex-col min-w-64 max-w-80 px-4">
    <div class="bg-primary-900 rounded-t-lg">
      <p class="flex justify-center items-center">{{ formatTime(time) }}</p>
    </div>
    <div class="bg-primary-500 px-4 py-2 rounded-b-lg">
      <slot name="eventTrigger">

      </slot>
    </div>
  </div>

</template>

<script setup lang="ts">

import type { CriteriaKey, CriteriaType } from '@criteria-interfaces/model'
import { useI18n } from 'vue-i18n'
import PlayerHead from '@/components/PlayerHead.vue'
import { useTimeable } from '@/composables/timable'

const props = defineProps<{
  time: number
}>()

const { formatTime } = useTimeable()

</script>