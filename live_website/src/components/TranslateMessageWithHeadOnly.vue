<template>
  <i18n-t :keypath="translationKey" tag="div" v-bind="placeholders" class="flex items-center border-blue-500 border-2" >
    <template #player>
      <div class="border-red-500 border-2 mr-2">
        <PlayerHead :skin-texture-u-r-l="skinTextureURL" :player-name="playerName" :size="32" />
      </div>
    </template>
    <slot name="additionalPlaceholders"/>
  </i18n-t>
</template>

<script setup lang="ts">

  import PlayerHead from '@/components/PlayerHead.vue'

  const props = defineProps<{
    translationKey: string,
    skinTextureURL: string,
    playerName: string,
    placeholders?: Record<string, string | number | (() => string | number)>
  }>()
</script>