<template>
  <BaseCriteriaModification criteria-type="goals" criteria-key="blockBreakGoal" relative-u-r-l-to-wiki="goals/blockbreakgoal">
    <template #configuration>
      <div>
        <CollectableDropdownConfiguration
          :model-access="{
        get: modelInFunc => modelInFunc.goals?.blockBreakGoal?.broken,
        where: 'goals.blockBreakGoal.broken',
        testSchematron: false
        }"
          :dropdown-placeholder-text="t('goals.types.blockBreakGoal.settings.dropdown.blockPlaceholder')"
          :collectable-text-prefix="t('goals.types.blockBreakGoal.settings.dropdown.blockPrefix')"
          :show-image="true"
          :disabled="breakAllBlocksOnce"
          :all-possible-data="ALL_IS_BLOCK_MATERIAL_DATA"
          :collectable-amount-prefix="t('goals.types.blockBreakGoal.settings.dropdown.amountPrefix')"
          :render-selection="!breakAllBlocksOnce"
        />

        <!--CheckboxCriteria :translated-label="t('goals.types.blockBreakGoal.settings.breakAllBlocks.name')"
                          :model="breakAllBlocksOnce" criteria-type="goals"
                          @updateModel="(newModelValue: boolean) => breakAllBlocksOnce=newModelValue" /-->
        <Checkbox
          v-model="breakAllBlocksOnce"
          @update:model-value="updateBreakAllBlocksOnce"
          input-id="breakAllBlocksOnce"
          binary />
        <label
          for="breakAllBlocksOnce"
          class="ml-2"
          >{{ t('goals.types.blockBreakGoal.settings.breakAllBlocks.name') }}</label
        >
        <FixedOrderConfiguration :model-access="baseModelAcces" />
        <TimerConfiguration :model-access="baseModelAcces" />
      </div>
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import { useRoute } from 'vue-router'
  import { ref, watch } from 'vue'
  import { useFetchable } from '@/fetchable'
  import type { BlockBreakGoalConfig } from '@/models/blockbreak'
  import { useI18n } from 'vue-i18n'
  import { useModelStore } from '@/stores/model'
  import { ALL_IS_BLOCK_MATERIAL_DATA, fromDataRowArray2CollectableEntryArray } from '@/models/data_row'
  import Checkbox from 'primevue/checkbox'
  import TimerConfiguration from '@/components/goals/TimerConfiguration.vue'
  import CheckboxCriteria from '@/components/wrappers/CheckboxCriteria.vue'
  import FixedOrderConfiguration from '@/components/goals/FixedOrderConfiguration.vue'
  import type { ModelAccess } from '@/main'

  const { t } = useI18n()
  const route = useRoute()
  const { model, set } = useModelStore()

  const baseModelAcces: ModelAccess<BlockBreakGoalConfig> = {
    get: model => model.goals?.blockBreakGoal,
    where: 'goals.blockBreakGoal',
    testSchematron: false
  }

  const breakAllBlocksOnce = ref<boolean>(false)

  function updateBreakAllBlocksOnce(breakAllBlocksOnce: boolean) {
    set('goals.blockBreakGoal.broken', breakAllBlocksOnce ? fromDataRowArray2CollectableEntryArray(ALL_IS_BLOCK_MATERIAL_DATA) : undefined, true)
  }

</script>
