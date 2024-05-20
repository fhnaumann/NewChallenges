<template>
  <SearchComponent />
  <div class="flex justify-center space-x-10">
    <CriteriaTypeOverview criteria-type="rules" />
    <CriteriaTypeOverview criteria-type="goals" />
    <CriteriaTypeOverview criteria-type="settings" />
  </div>
</template>

<script setup lang="ts">
  import CriteriaTypeOverview from '@/components/CriteriaTypeOverview.vue'
  import NoBlockBreakRule from '@/components/rules/types/NoBlockBreakRule.vue'
  import BlockBreakGoal from '@/components/goals/types/BlockBreakGoal.vue'
  import { useModelStore } from '@/stores/model'
  import type { NoBlockBreakRuleConfig } from '@/models/blockbreak'
  import SearchComponent from '@/components/search/SearchComponent.vue'
</script>
