<template>
  <BaseCriteriaModification criteria-key="itemGoal" relative-u-r-l-to-wiki="goals/itemGoal" criteria-type="goals">
    <template #configuration>
      <CollectableDropdownConfiguration
        :all-possible-data="ALL_IS_ITEM_MATERIAL_DATA"
        :dropdown-placeholder-text="t('goals.types.itemGoal.settings.dropdown.itemPlaceholder')"
        :render-selection="!(collectEveryItemOnce || collectAllItemsOnce || collectAllBlockItemsOnce)"
        :model-access="{
                                        get: model => model.goals?.itemGoal?.items,
                                        where: 'goals.itemGoal.items',
                                        testSchematron: false
                                        }"
        :collectable-text-prefix="t('goals.types.itemGoal.settings.dropdown.itemPrefix')"
        :show-image="true" :disabled="collectEveryItemOnce || collectAllItemsOnce || collectAllBlockItemsOnce"
        :collectable-amount-prefix="t('goals.types.itemGoal.settings.dropdown.amountPrefix')" />
      <div>
        <Checkbox v-model="collectEveryItemOnce" @update:model-value="updateCollectEverythingOnce"
                  input-id="collectEveryItemOnce" binary />
        <label for="collectEveryItemOnce" class="ml-2">{{ t('goals.types.itemGoal.settings.collectEverything.name') }}</label>
      </div>
      <div>
        <Checkbox v-model="collectAllItemsOnce" @update:model-value="updateCollectAllItemsOnce"
                  input-id="collectAllItemsOnce" binary :disabled="collectEveryItemOnce" />
        <label for="collectAllItemsOnce" class="ml-2">{{ t('goals.types.itemGoal.settings.collectAllItems.name') }}</label>
      </div>
      <div>
        <Checkbox v-model="collectAllBlockItemsOnce" @update:model-value="updateCollectAllBlockItemsOnce"
                  input-id="collectAllBlockItemsOnce" binary :disabled="collectEveryItemOnce" />
        <label for="collectAllBlockItemsOnce" class="ml-2">{{ t('goals.types.itemGoal.settings.collectAllBlocks.name') }}</label>
      </div>
      <FixedOrderConfiguration :model-access="baseModelAccess" />
      <TimerConfiguration :model-access="baseModelAccess" />
    </template>
  </BaseCriteriaModification>

</template>

<script setup lang="ts">

  import {
    ALL_ENTITY_TYPE_DATA, ALL_IS_BLOCK_MATERIAL_DATA,
    ALL_IS_ITEM_MATERIAL_DATA, type DataRow,
    fromDataRowArray2CollectableEntryArray,
  } from '@/models/data_row'
  import FixedOrderConfiguration from '@/components/goals/FixedOrderConfiguration.vue'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import Checkbox from 'primevue/checkbox'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import TimerConfiguration from '@/components/goals/TimerConfiguration.vue'
  import { useModelStore } from '@/stores/model'
  import { storeToRefs } from 'pinia'
  import { useVarHelperStore } from '@/stores/var_helper'
  import type { ModelAccess } from '@/main'
  import type { ItemGoalConfig } from '@/models/item'
  import type { CollectableEntryConfig } from '@/models/goals'
  import { useI18n } from 'vue-i18n'
  import { watch } from 'vue'

  const { t } = useI18n()
  const { set } = useModelStore()
  const { collectEveryItemOnce, collectAllItemsOnce, collectAllBlockItemsOnce } = storeToRefs(useVarHelperStore())

  const baseModelAccess: ModelAccess<ItemGoalConfig> = {
    get: model => model.goals?.itemGoal,
    where: 'goals.itemGoal',
    testSchematron: true
  }

  set('goals.itemGoal.items', [{
    collectableName: 'dragon_egg',
    collectableData: {
      amountNeeded: 1
    }
  } as CollectableEntryConfig], false)

  watch(collectEveryItemOnce, newCollectEveryItemOnce => {
    if(newCollectEveryItemOnce) {
      collectAllItemsOnce.value = false
      collectAllBlockItemsOnce.value = false
    }
  })
  watch(collectEveryItemOnce, newCollectEveryItemOnce => {
    if(newCollectEveryItemOnce && collectAllBlockItemsOnce.value) {
      collectEveryItemOnce.value = true
    }
  })
  watch(collectAllBlockItemsOnce, newCollectAllBlockItemsOnce => {
    if(newCollectAllBlockItemsOnce && collectAllItemsOnce.value) {
      collectEveryItemOnce.value = true
    }
  })

  function updateCollectEverythingOnce(collectEveryItemOnce: boolean) {
    updateItems(ALL_IS_ITEM_MATERIAL_DATA, collectEveryItemOnce)
  }

  function updateCollectAllItemsOnce(collectAllItemsOnce: boolean) {
    updateItems(ALL_IS_ITEM_MATERIAL_DATA.filter(value => !value.is_block), collectAllItemsOnce)
  }

  function updateCollectAllBlockItemsOnce(collectAllBlockItemsOnce: boolean) {
    updateItems(ALL_IS_ITEM_MATERIAL_DATA.filter(value => value.is_block), collectAllBlockItemsOnce)
  }

  function updateItems(data: DataRow[], add: boolean) {
    set('goals.itemGoal.items', add ? fromDataRowArray2CollectableEntryArray(data) : undefined, true)
  }

</script>