<template>
  <EventBox v-bind="props" :class="`${type === 'noBlockBreak' ? 'customized-rule' : 'customized-goal'}`">
    <template #eventTrigger>
      <i18n-t keypath="events.mobKill" tag="div" class="flex items-center text-2xl">
        <template #player>
          <div class="mr-2">
            <PlayerHead :size="32" v-bind="data.player" />
          </div>
        </template>
        <template #mob>
          <MaterialItem class="mx-2" :code="data.mob" :data-source="'entity_type'" img-class="w-8" />
        </template>
      </i18n-t>
    </template>
  </EventBox>
</template>

<script setup lang="ts">

import EventBox from '@/components/events/EventBox.vue'
import type { CriteriaKey, MobDataConfig } from '@fhnaumann/criteria-interfaces'
import PlayerHead from '@/components/PlayerHead.vue'
import MaterialItem from '@/components/MaterialItem.vue'

const props = defineProps<{
  data: MobDataConfig,
  eventIndex: number
  type: CriteriaKey
}>()

</script>