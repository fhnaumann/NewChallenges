<template>
  <div>
    <BaseRuleModification criteria-key="noBlockPlace" relative-u-r-l-to-wiki="rules/noblockplacerule" criteria-type="rules" >
      <template #configuration>
        <ExemptionSelection class="ml-4" :possible-exemptions="ALL_IS_BLOCK_MATERIAL_DATA" :show-images="true" :model-access="{
          get: model => model.rules?.enabledRules?.noBlockPlace?.exemptions!,
          where: 'rules.enabledRules.noBlockPlace.exemptions',
          testSchematron: true
        }" />
      </template>
    </BaseRuleModification>
  </div>
</template>

<script setup lang="ts">

import BaseRuleModification from '@/components/rules/BaseRuleModification.vue'
import ExemptionSelection from '@/components/rules/ExemptionSelection.vue'
import { ALL_IS_BLOCK_MATERIAL_DATA } from '@/models/data_row_loaded'
import { useModelStore } from '@/stores/model'

const { set, model } = useModelStore()

// set defaults if nothing is set so far
if(model.rules?.enabledRules?.noBlockPlace?.exemptions === undefined) {
  set('rules.enabledRules.noBlockPlace.exemptions', [ "water", "lava" ], true)
}

</script>