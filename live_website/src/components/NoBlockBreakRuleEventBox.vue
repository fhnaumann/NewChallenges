<template>
  <EventBox class="customized-rule" criteria-key="noBlockBreak" criteria-type="rules" :time="data.timestamp">
    <template #eventTrigger>
      <i18n-t keypath="events.blockBreak" tag="div" class="flex items-center text-2xl">
        <template #player>
          <div class="mr-2">
            <PlayerHead player-u-u-i-d="c41c9dcf-20cb-406d-aacd-bde2320283c6" player-name="wand555"
                        skin-texture-u-r-l="http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970" />

          </div>

          <!--div class="border-red-500 border-2 mx-2">
            <PlayerHead v-bind="props.data.player" />
          </div-->
        </template>
        <template #block>
          <MaterialItem class="mx-2" img-class="w-8" :code="props.data.broken" data-source="material" />
        </template>
      </i18n-t>
    </template>
  </EventBox>
</template>

<script setup lang="ts">

import EventBox from '@/components/EventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import type { NoBlockBreakRuleDataConfig } from '@criteria-interfaces/blockbreak'
import MaterialItem from '@/components/MaterialItem.vue'

const props = defineProps<{
  data: NoBlockBreakRuleDataConfig
}>()

</script>