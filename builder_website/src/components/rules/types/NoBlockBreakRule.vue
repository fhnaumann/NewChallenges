<template>
  <div>
    <!--div class="... flex flex-row justify-between space-x-5 pt-4 h-svh  bg-red-100"-->
    <BaseRuleModification criteria-type="rules" criteria-key="noBlockBreak" relative-u-r-l-to-wiki="rules/noblockbreakrule">
      <template #configuration>
        <ExemptionSelection class="pl-4"
                            :possible-exemptions="ALL_IS_BLOCK_MATERIAL_DATA"
                            :show-images="true"
                            :model-access="{
            get: model => model.rules?.enabledRules?.noBlockBreak?.exemptions!,
            where: 'rules.enabledRules.noBlockBreak.exemptions',
            testSchematron: true
          }"
        />
      </template>
    </BaseRuleModification>


  </div>
</template>

<script setup lang="ts">
  import MultiSelect from 'primevue/multiselect'
  import Button from 'primevue/button'
  import { useModelStore } from '@/stores/model'
  import ExemptionSelection from '@/components/rules/ExemptionSelection.vue'
  import SaveOrRemoveChanges from '@/components/SaveOrRemoveChanges.vue'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import BaseRuleModification from '@/components/rules/BaseRuleModification.vue'
  import { ALL_IS_BLOCK_MATERIAL_DATA } from '@/models/data_row_loaded'

</script>
