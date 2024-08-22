<template>
  <BaseCriteriaModification criteria-key="craftingGoal" relative-u-r-l-to-wiki="goals/craftinggoal" criteria-type="goals">
    <template #configuration>
      <CollectableDropdownConfiguration :all-possible-data="ALL_RECIPES"
                                        :dropdown-placeholder-text="t('goals.types.craftingGoal.settings.dropdown.recipePlaceholder')"
                                        :render-selection="!(allRecipesOnce || allCraftingRecipesOnce || allSmeltingRecipesOnce || allSmithingUpgradesOnce)"
                                        :model-access="{
                                          get: model => model.goals?.craftingGoal?.crafted,
                                          where: 'goals.craftingGoal.crafted',
                                          testSchematron: false
                                        }"
                                        :collectable-text-prefix="t('goals.types.craftingGoal.settings.dropdown.recipePrefix')"
                                        :show-image="false"
                                        :disabled="allRecipesOnce || allCraftingRecipesOnce || allSmeltingRecipesOnce || allSmithingUpgradesOnce"
                                        :collectable-amount-prefix="t('goals.types.craftingGoal.settings.dropdown.amountPrefix')" >
        <template #row="rowProps">
          <div class="flex justify-center space-x-2">
            <img class="w-6"
                 :src="BASE_IMG_URL + '/rendered_images/' + fromCode2DataRow((rowProps.dataRow as CraftingTypeDataRow).result).img_name"
                 @error="($event.target as HTMLInputElement).src = '/unknown.png'"
                 :alt="fromCode2DataRow((rowProps.dataRow as CraftingTypeDataRow).result).code" />
            <div class="flex justify-center space-x-2" v-if="(rowProps.dataRow as CraftingTypeDataRow).source">
              <p>from</p>
              <img class="w-6"
                   :src="BASE_IMG_URL + '/rendered_images/' + fromCode2DataRow((rowProps.dataRow as CraftingTypeDataRow).source!).img_name"
                   @error="($event.target as HTMLInputElement).src = '/unknown.png'"
                   :alt="craftingType2DataRow((rowProps.dataRow as CraftingTypeDataRow).recipeType).code" />
            </div>
            <p>in</p>
            <img class="w-6"
                 :src="BASE_IMG_URL + '/rendered_images/' + craftingType2DataRow((rowProps.dataRow as CraftingTypeDataRow).recipeType).img_name"
                 @error="($event.target as HTMLInputElement).src = '/unknown.png'"
                 :alt="craftingType2DataRow((rowProps.dataRow as CraftingTypeDataRow).recipeType).code" />
          </div>
        </template>

      </CollectableDropdownConfiguration>

      <div>
        <Checkbox v-model="allRecipesOnce" @update:model-value="updateAllRecipesOnce" input-id="allRecipesOnce" binary/>
        <label for="allRecipesOnce" class="ml-2">{{ t('goals.types.craftingGoal.settings.allRecipesOnce.name') }}</label>
      </div>
      <div>
        <Checkbox v-model="allCraftingRecipesOnce" @update:model-value="updateAllCraftingRecipesOnce" input-id="allCraftingRecipesOnce" binary :disabled="allRecipesOnce" />
        <label for="allCraftingRecipesOnce" class="ml-2">{{ t('goals.types.craftingGoal.settings.allCraftingRecipesOnce.name') }}</label>
      </div>
      <div>
        <Checkbox v-model="allSmeltingRecipesOnce" @update:model-value="updateAllSmeltingRecipesOnce" input-id="allSmeltingRecipesOnce" binary :disabled="allRecipesOnce" />
        <label for="allSmeltingRecipesOnce" class="ml-2">{{ t('goals.types.craftingGoal.settings.allSmeltingRecipesOnce.name') }}</label>
      </div>
      <div>
        <Checkbox v-model="allSmithingUpgradesOnce" @update:model-value="updateAllSmithingRecipesOnce" input-id="allSmithingUpgradesOnce" binary :disabled="allRecipesOnce" />
        <label for="allSmithingUpgradesOnce" class="ml-2">{{ t('goals.types.craftingGoal.settings.allSmithingUpgradesOnce.name') }}</label>
      </div>
      <FixedOrderConfiguration :model-access="baseModelAccess" />
      <TimerConfiguration :model-access="baseModelAccess" />
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import Checkbox from 'primevue/checkbox'
  import { useModelStore } from '@/stores/model'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'
  import type { ModelAccess } from '@/main'
  import type { CraftingGoalConfig } from 'criteria-interfaces'
  import { useTranslation } from '@/language'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import type { DataRow, CraftingTypeDataRow } from 'criteria-interfaces'
  import {
    ALL_RECIPES,
    fromCode2DataRow,
    fromDataRowArray2CollectableEntryArray,
  } from '@/models/data_row_loaded'
  import { storeToRefs } from 'pinia'
  import { useVarHelperStore } from '@/stores/var_helper'
  import type { CollectableEntryConfig } from 'criteria-interfaces'
  import { watch } from 'vue'
  import { BASE_IMG_URL } from '@/constants'
  import FixedOrderConfiguration from '@/components/goals/FixedOrderConfiguration.vue'
  import TimerConfiguration from '@/components/goals/TimerConfiguration.vue'

  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.CraftingGoalConfig.properties
  const { t } = useI18n()

  const baseModelAccess: ModelAccess<CraftingGoalConfig> = {
    get: model1 => model1.goals?.craftingGoal,
    where: 'goals.craftingGoal',
    testSchematron: false,
  }

  const { craftingType2DataRow } = useTranslation()

  const { allRecipesOnce, allCraftingRecipesOnce, allSmeltingRecipesOnce, allSmithingUpgradesOnce } = storeToRefs(useVarHelperStore())

  watch(allRecipesOnce, newAllRecipesOnce => {
    if(newAllRecipesOnce) {
      allCraftingRecipesOnce.value = false
      allSmeltingRecipesOnce.value = false
      allSmithingUpgradesOnce.value = false
    }
  })
  watch(allCraftingRecipesOnce, newAllCraftingRecipesOnce => {
    if(newAllCraftingRecipesOnce && allSmeltingRecipesOnce.value && allSmithingUpgradesOnce.value) {
      allRecipesOnce.value = true
    }
  })
  watch(allSmeltingRecipesOnce, newAllSmeltingRecipesOnce => {
    if(newAllSmeltingRecipesOnce && allCraftingRecipesOnce.value && allSmithingUpgradesOnce.value) {
      allRecipesOnce.value = true
    }
  })
  watch(allSmithingUpgradesOnce, newAllSmithingRecipesOnce => {
    if(newAllSmithingRecipesOnce && allCraftingRecipesOnce.value && allSmeltingRecipesOnce.value) {
      allRecipesOnce.value = true
    }
  })

  // set default values (if there are no existing ones
  if(model.goals?.craftingGoal?.crafted === undefined) {
    allCraftingRecipesOnce.value = true
    updateAllCraftingRecipesOnce(allCraftingRecipesOnce.value)
  }

  function updateAllRecipesOnce(allCRecipesOnce: boolean) {
    updateItems(ALL_RECIPES, allCRecipesOnce)
  }

  function updateAllCraftingRecipesOnce(allCraftingRecipesOnce: boolean) {
    updateItems(ALL_RECIPES.filter(value => ['crafting', 'stonecutting'].includes(value.recipeType)), allCraftingRecipesOnce)
  }

  function updateAllSmeltingRecipesOnce(allSmeltingRecipesOnce: boolean) {
    updateItems(ALL_RECIPES.filter(value => ['furnace', 'blasting', 'smoking', 'campfire'].includes(value.recipeType)), allSmeltingRecipesOnce)
  }

  function updateAllSmithingRecipesOnce(allSmithingRecipesOnce: boolean) {
    updateItems(ALL_RECIPES.filter(value => value.recipeType === 'smithing'), allSmithingRecipesOnce)
  }

  function updateItems(data: DataRow[], add: boolean) {
    set('goals.craftingGoal.crafted', add ? fromDataRowArray2CollectableEntryArray(data) : undefined, true)
  }
</script>