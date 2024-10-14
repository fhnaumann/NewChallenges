<template>
  <EventBox :data="data" type="blockBreakGoal"  :event-index="0">
    <template #eventTrigger>
      <p class="flex items-center text-xl">UNKNOWN EVENT BOX</p>
      <p>Did you forget to add its type to the EventContainer?</p>
    </template>
  </EventBox>
</template>

<script setup lang="ts">
import EventBox from '@/components/events/EventBox.vue'
import type { DataConfig } from '@fhnaumann/criteria-interfaces'

const props = defineProps<{
  data: DataConfig
}>()

</script>
