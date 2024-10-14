<template>
  <EventBox v-bind="props" :class="`${type === 'noBlockBreak' ? 'customized-rule' : 'customized-goal'}`">
    <template #eventTrigger>
      <i18n-t keypath="events.death" tag="div" class="flex items-center text-2xl" >
        <template #player>
          <div class="mr-2">
            <PlayerHead :size="32" v-bind="data.player" />
          </div>
        </template>
      </i18n-t>
    </template>
  </EventBox>
</template>

<script setup lang="ts">
import EventBox from '@/components/events/EventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import MaterialItem from '@/components/MaterialItem.vue'
import type { DeathDataConfig } from '@fhnaumann/criteria-interfaces'
import type { CriteriaKey } from '@fhnaumann/criteria-interfaces'

const props = defineProps<{
  data: DeathDataConfig
  eventIndex: number
  type: CriteriaKey
}>()

</script>