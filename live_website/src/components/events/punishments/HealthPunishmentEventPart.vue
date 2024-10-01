<template>
  <i18n-t :keypath="`events.healthPunishment.${data.affects}`" tag="span" class="flex flex-wrap items-center text-2xl">
    <template #player>
      <div class="mr-2">
        <PlayerHead :size="32" v-bind="causer" />
      </div>
    </template>
    <template #hearts>
      <span class="mx-1">{{ data.actualHeartsLost }}</span>
      <div class="h-full">
        <img class="w-4 inline-block align-middle -translate-y-0.5" src="/minecraft_heart.png" alt="heart" />
      </div>
    </template>
  </i18n-t>
</template>

<script setup lang="ts">

import type { HealthPunishmentDataConfig } from '@criteria-interfaces/punishments'
import PlayerHead from '@/components/PlayerHead.vue'
import type { PlayerConfig } from '@criteria-interfaces/live'

defineProps<{
  causer: PlayerConfig
  data: HealthPunishmentDataConfig
}>()

</script>