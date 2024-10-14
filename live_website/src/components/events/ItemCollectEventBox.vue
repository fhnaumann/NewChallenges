<template>
  <EventBox v-bind="props" :class="`${type === 'noBlockBreak' ? 'customized-rule' : 'customized-goal'}`">
    <template #eventTrigger>
      <i18n-t keypath="events.itemCollect" tag="div" class="flex items-center text-2xl" >
        <template #player>
          <div class="mr-2">
            <PlayerHead :size="32" v-bind="data.player" />
          </div>
        </template>
        <template #item>
          <MaterialItem class="mx-2" :code="data.item" data-source="material" img-class="w-8" />
        </template>
      </i18n-t>
    </template>
  </EventBox>
</template>

<script setup lang="ts">

import type { CriteriaKey, ItemDataConfig } from '@fhnaumann/criteria-interfaces'
import EventBox from '@/components/events/EventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import MaterialItem from '@/components/MaterialItem.vue'

const props = defineProps<{
  data: ItemDataConfig
  eventIndex: number
  type: CriteriaKey
}>()

</script>