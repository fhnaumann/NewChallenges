<template>
  <BaseCriteriaModification criteria-key="blockPlaceGoal" relative-u-r-l-to-wiki="goals/blockplacegoal"
                            criteria-type="goals">
    <template #configuration>
      <div>
        <CollectableDropdownConfiguration :all-possible-data="ALL_IS_BLOCK_MATERIAL_DATA"
                                          :dropdown-placeholder-text="t('goals.types.blockPlaceGoal.settings.dropdown.blockPlaceholder')"
                                          :collectable-amount-prefix="t('goals.types.blockPlaceGoal.settings.dropdown.amountPrefix')"
                                          :render-selection="!placeAllBlocksOnce"
                                          :model-access="{
                                            get: model => model.goals?.blockPlaceGoal?.placed,
                                            where: 'goals.blockPlaceGoal.placed',
                                            testSchematron: false
                                          }"
                                          :collectable-text-prefix="t('goals.types.blockPlaceGoal.settings.dropdown.blockPrefix')"
                                          :show-image="true" :disabled="placeAllBlocksOnce" />
        <Checkbox v-model="placeAllBlocksOnce" @update:model-value="updatePlaceAllBlocksOnce"
                  input-id="placeAllBlocksOnce" binary />
        <label for="placeAllBlocksOnce" class="ml-2">{{ t('goals.types.blockPlaceGoal.settings.placeAllBlocks.name') }}</label>
        <FixedOrderConfiguration :model-access="baseModelAccess" />
        <TimerConfiguration :model-access="baseModelAccess" />
      </div>
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import { useI18n } from 'vue-i18n'
  import { useModelStore } from '@/stores/model'
  import { storeToRefs } from 'pinia'
  import { useVarHelperStore } from '@/stores/var_helper'
  import type { ModelAccess } from '@/main'
  import type { BlockPlaceGoalConfig } from '@fhnaumann/criteria-interfaces'
  import { ALL_IS_BLOCK_MATERIAL_DATA, fromDataRowArray2CollectableEntryArray } from '@/models/data_row_loaded'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import FixedOrderConfiguration from '@/components/goals/FixedOrderConfiguration.vue'
  import TimerConfiguration from '@/components/goals/TimerConfiguration.vue'
  import Checkbox from 'primevue/checkbox'
  import type { CollectableEntryConfig } from '@fhnaumann/criteria-interfaces'

  const { t } = useI18n()
  const { model, set } = useModelStore()
  const { placeAllBlocksOnce } = storeToRefs(useVarHelperStore())

  const baseModelAccess: ModelAccess<BlockPlaceGoalConfig> = {
    get: model => model.goals?.blockPlaceGoal,
    where: 'goals.blockPlaceGoal',
    testSchematron: false,
  }

  // set defaults if nothing is set so far
  if(model.goals?.blockPlaceGoal?.placed === undefined) {
    set('goals.blockPlaceGoal.placed', [{
      collectableName: 'dragon_egg',
      collectableData: {
        amountNeeded: 1
      }
    } as CollectableEntryConfig], false)
  }

  function updatePlaceAllBlocksOnce(placeAllBlocksOnce: boolean) {
    set('goals.blockPlaceGoal.placed', placeAllBlocksOnce ? fromDataRowArray2CollectableEntryArray(ALL_IS_BLOCK_MATERIAL_DATA) : undefined, true)
  }

</script>