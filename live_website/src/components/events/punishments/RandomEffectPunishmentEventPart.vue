<template>
  <i18n-t :keypath="`events.randomEffectPunishment.${data.affects}`" tag="span" class="flex flex-wrap items-center text-2xl">
    <template #player>
      <div class="mr-2">
        <PlayerHead :size="32" v-bind="causer" />
      </div>
    </template>
    <template #effect>
      <span class="mx-1">{{ data.appliedPotionEffects }}</span>
    </template>
  </i18n-t>
</template>

<script setup lang="ts">

import PlayerHead from '@/components/PlayerHead.vue'
import type { PlayerConfig, RandomEffectPunishmentDataConfig } from '@fhnaumann/criteria-interfaces'

defineProps<{
  causer: PlayerConfig
  data: RandomEffectPunishmentDataConfig
}>()

</script>