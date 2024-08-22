<template>
  <BaseRuleModification criteria-key="noMobKill" relative-u-r-l-to-wiki="rules/nomobkillrule" criteria-type="rules">
    <template #configuration>
      <ExemptionSelection class="pl-4"
                          :possible-exemptions="ALL_ENTITY_TYPE_DATA"
                          :show-images="false"
                          :model-access="{
            get: model => model.rules?.enabledRules?.noMobKill?.exemptions!,
            where: 'rules.enabledRules.noMobKill.exemptions',
            testSchematron: true
          }"
      />
    </template>
  </BaseRuleModification>
</template>

<script setup lang="ts">

  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import { ALL_ENTITY_TYPE_DATA } from '@/models/data_row_loaded'
  import ExemptionSelection from '@/components/rules/ExemptionSelection.vue'
  import { useModelStore } from '@/stores/model'
  import BaseRuleModification from '@/components/rules/BaseRuleModification.vue'

  const { set } = useModelStore()

  set('rules.enabledRules.noMobKill.exemptions', ["ender_dragon"], false)
</script>