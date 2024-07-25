<template>
  <BaseRuleModification criteria-key="noItem" relative-u-r-l-to-wiki="rules/noitemrule" criteria-type="rules">
    <template #configuration>
      <ExemptionSelection class="pl-4"
                          :possible-exemptions="ALL_IS_ITEM_MATERIAL_DATA"
                          :show-images="true"
                          :model-access="{
            get: model => model.rules?.enabledRules?.noItem?.exemptions!,
            where: 'rules.enabledRules.noItem.exemptions',
            testSchematron: true
          }"
      />
    </template>
  </BaseRuleModification>
</template>

<script setup lang="ts">

  import { useRouter } from 'vue-router'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import { ALL_IS_BLOCK_MATERIAL_DATA, ALL_IS_ITEM_MATERIAL_DATA } from '@/models/data_row'
  import ExemptionSelection from '@/components/rules/ExemptionSelection.vue'
  import BaseRuleModification from '@/components/rules/BaseRuleModification.vue'
</script>