<template>
  <EventBox v-bind="props" :class="`${type === 'noBlockBreak' ? 'customized-rule' : 'customized-goal'}`">
    <template #eventTrigger>
      <i18n-t keypath="events.blockBreak" tag="div" class="flex items-center text-2xl">
        <template #player>
          <div class="mr-2">
            <PlayerHead v-bind="data.player" :size="32"/>
          </div>
        </template>
        <template #block>
          <MaterialItem class="mx-2" img-class="w-8" :code="data.broken" data-source="material" />
        </template>
      </i18n-t>
    </template>
  </EventBox>
</template>

<script setup lang="ts">

import EventBox from '@/components/events/EventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import type { BlockBreakDataConfig, NoBlockBreakRuleDataConfig } from '@fhnaumann/criteria-interfaces'
import MaterialItem from '@/components/MaterialItem.vue'
import { useUtil } from '@/composables/util'
import type { RuleDataConfig } from '@fhnaumann/criteria-interfaces'
import PunishmentEventContainer from '@/components/events/punishments/PunishmentEventContainer.vue'
import type { CriteriaKey } from '@fhnaumann/criteria-interfaces'

const props = defineProps<{
  data: BlockBreakDataConfig | NoBlockBreakRuleDataConfig
  eventIndex: number
  type: CriteriaKey
}>()

const { hasAppliedPunishments } = useUtil()

</script>