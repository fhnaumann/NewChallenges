<template>
  <EventBox :data="data">
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
import type { MobDataConfig } from '@criteria-interfaces/mob'
import PlayerHead from '@/components/PlayerHead.vue'
import MaterialItem from '@/components/MaterialItem.vue'

defineProps<{
  data: MobDataConfig
}>()

</script>