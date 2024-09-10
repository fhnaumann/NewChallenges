<template>
  <div>
    <p>TEST2</p>
    <!-- TranslateMessage :render-props="renderProps" translation-key="test" /-->
    <i18n-t keypath="test" tag="div" class="flex items-center border-blue-500 border-2">
      <template #player>
        <div class="border-red-500 border-2 mx-2">
          <PlayerHead skin-texture-u-r-l="http://textures.minecraft.net/texture/7fd9ba42a7c81eeea22f1524271ae85a8e045ce0af5a6ae16c6406ae917e68b5"
                      player-name="wand555" :size="64" />
        </div>
      </template>
      <template #item>
        <div class="border-red-500 border-2 mx-2">
          <MaterialItem />
        </div>
      </template>
    </i18n-t>
  </div>
</template>

<script setup lang="ts">

import { useI18n } from 'vue-i18n'
import PlayerHead from './PlayerHead.vue'
import TranslateMessageWithHeadOnly from '@/components/TranslateMessageWithHeadOnly.vue'
import TranslateMessage, { type RenderProps } from '@/components/TranslateMessage.vue'
import MaterialItem from '@/components/MaterialItem.vue'
import type { PlayerConfig } from '@criteria-interfaces/live'
import { type NoBlockBreakRuleDataConfig } from '@criteria-interfaces/blockbreak'

const { t } = useI18n()
export interface EventProps {
  player: PlayerConfig
}

</script>