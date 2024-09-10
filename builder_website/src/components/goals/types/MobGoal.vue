<template>
  <BaseCriteriaModification criteria-key="mobGoal" relative-u-r-l-to-wiki="goals/mobgoal" criteria-type="goals">
    <template #configuration>
      <CollectableDropdownConfiguration
        :all-possible-data="ALL_ENTITY_TYPE_DATA"
        :dropdown-placeholder-text="t('goals.types.mobGoal.settings.dropdown.mobPlaceholder')"
        :render-selection="!killAllMobsOnce"
        :model-access="{
                                        get: model => model.goals?.mobGoal?.mobs,
                                        where: 'goals.mobGoal.mobs',
                                        testSchematron: false
                                        }"
        :collectable-text-prefix="t('goals.types.mobGoal.settings.dropdown.mobPrefix')"
        :show-image="true" :disabled="killAllMobsOnce"
        :collectable-amount-prefix="t('goals.types.mobGoal.settings.dropdown.amountPrefix')" />
      <Checkbox v-model="killAllMobsOnce" @update:model-value="updateKillAllMobsOnce" input-id="killAllMobsOnce"
                binary />
      <label for="killAllMobsOnce" class="ml-2">{{ t('goals.types.mobGoal.settings.killAllMobs.name') }}</label>
      <FixedOrderConfiguration :model-access="baseModelAccess" />
      <TimerConfiguration :model-access="baseModelAccess" />
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import { useI18n } from 'vue-i18n'
  import { useModelStore } from '@/stores/model'
  import { ALL_ENTITY_TYPE_DATA, fromDataRowArray2CollectableEntryArray } from '@/models/data_row_loaded'
  import { ref } from 'vue'
  import TimerConfiguration from '@/components/goals/TimerConfiguration.vue'
  import Checkbox from 'primevue/checkbox'
  import type { ModelAccess } from '@/main'
  import type { MobGoalConfig } from 'criteria-interfaces'
  import FixedOrderConfiguration from '@/components/goals/FixedOrderConfiguration.vue'
  import { useVarHelperStore } from '@/stores/var_helper'
  import { storeToRefs } from 'pinia'
  import type { CollectableEntryConfig } from 'criteria-interfaces'

  const { t } = useI18n()
  const { model, set } = useModelStore()
  const { killAllMobsOnce } = storeToRefs(useVarHelperStore())

  const baseModelAccess: ModelAccess<MobGoalConfig> = {
    get: model => model.goals?.mobGoal,
    where: 'goals.mobGoal',
    testSchematron: false,
  }

  set('goals.mobGoal.mobs', [{
    collectableName: 'ender_dragon',
    collectableData: {
      amountNeeded: 1,
    },
  }] as CollectableEntryConfig[], false)

  function updateKillAllMobsOnce(killAllMobsOnce: boolean) {
    set('goals.mobGoal.mobs', killAllMobsOnce ? fromDataRowArray2CollectableEntryArray(ALL_ENTITY_TYPE_DATA) : undefined, true)
  }

</script>