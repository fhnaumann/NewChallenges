<template>
  <BaseRuleModification criteria-key="noCrafting" relative-u-r-l-to-wiki="rules/mocraftingrule" criteria-type="rules">
    <template #configuration>
      <ExemptionSelection class="pl-4" :possible-exemptions="ALL_RECIPES" :show-images="false" :model-access="{
        get: model => baseModelAccess.get(model)?.exemptions,
        where: `${baseModelAccess.where}.exemptions`,
        testSchematron: false
      }">
        <template #row="rowProps">
          <div class="flex justify-center space-x-2">
            <img class="w-6"
                  :src="BASE_IMG_URL + '/rendered_images/' + fromCode2DataRow((rowProps.dataRow as CraftingTypeDataRow).result).img_name"
                  @error="($event.target as HTMLInputElement).src = '/unknown.png'"
                  :alt="fromCode2DataRow((rowProps.dataRow as CraftingTypeDataRow).result).code" />
            <p>from</p>
            <img class="w-6"
                 :src="BASE_IMG_URL + '/rendered_images/' + craftingType2DataRow((rowProps.dataRow as CraftingTypeDataRow).recipeType).img_name"
                 @error="($event.target as HTMLInputElement).src = '/unknown.png'"
                 :alt="craftingType2DataRow((rowProps.dataRow as CraftingTypeDataRow).recipeType).code" />
          </div>
        </template>
      </ExemptionSelection>
      <div>
        <div>
          <Checkbox :model-value="internalCraftingOrDefault()" @update:model-value="updateInternalCrafting" binary input-id="internalCrafting" />
          <label class="ml-2" for="internalCrafting">{{ t('rules.types.noCrafting.settings.internalCrafting.name') }}</label>
        </div>
        <div>
          <Checkbox :model-value="workbenchCraftingOrDefault()" @update:model-value="updateWorkbenchCrafting" binary input-id="workbenchCrafting" />
          <label class="ml-2" for="workbenchCrafting">{{ t('rules.types.noCrafting.settings.workbenchCrafting.name') }}</label>
        </div>
        <div>
          <Checkbox :model-value="furnaceSmeltingOrDefault()" @update:model-value="updateFurnaceSmelting" binary input-id="furnaceSmelting" />
          <label class="ml-2" for="furnaceSmelting">{{ t('rules.types.noCrafting.settings.furnaceSmelting.name') }}</label>
        </div>
        <div>
          <Checkbox :model-value="campfireCookingOrDefault()" @update:model-value="updateCampfireCooking" binary input-id="campfireCooking" />
          <label class="ml-2" for="campfireCooking">{{ t('rules.types.noCrafting.settings.campfireCooking.name') }}</label>
        </div>
        <div>
          <Checkbox :model-value="smithingOrDefault()" @update:model-value="updateSmithing" binary input-id="smithing" />
          <label class="ml-2" for="smithing">{{ t('rules.types.noCrafting.settings.smithing.name') }}</label>
        </div>
        <div>
          <Checkbox :model-value="stonecutterOrDefault()" @update:model-value="updateStonecutter" binary input-id="stonecutter" />
          <label class="ml-2" for="stonecutter">{{ t('rules.types.noCrafting.settings.stonecutter.name') }}</label>
        </div>
      </div>
    </template>
  </BaseRuleModification>
</template>

<script setup lang="ts">
  import BaseRuleModification from '@/components/rules/BaseRuleModification.vue'
  import ExemptionSelection from '@/components/rules/ExemptionSelection.vue'
  import type { CraftingTypeDataRow } from 'criteria-interfaces'
  import {
    ALL_MATERIAL_DATA,
    ALL_RECIPES,
    fromCode2DataRow,
  } from '@/models/data_row_loaded'
  import type { ModelAccess } from '@/main'
  import type { NoCraftingRuleConfig } from 'criteria-interfaces'
  import Checkbox from 'primevue/checkbox'
  import { useModelStore } from '@/stores/model'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'
  import { useTranslation } from '@/language'
  import { BASE_IMG_URL } from '@/constants'

  const baseModelAccess: ModelAccess<NoCraftingRuleConfig> = {
    get: model => model.rules?.enabledRules?.noCrafting,
    where: 'rules.enabledRules.noCrafting',
    testSchematron: false,
  }

  const { t } = useI18n()
  const { translate, translateDataRow, craftingType2DataRow } = useTranslation()

  const {set, model } = useModelStore()
  const defaultConfig = useJSONSchemaConfig().NoCraftingRuleConfig.properties

  function internalCraftingOrDefault(): boolean {
    return baseModelAccess.get(model)?.internalCrafting ?? defaultConfig.internalCrafting.default
  }

  function updateInternalCrafting(value: boolean) {
    set(`${baseModelAccess.where}.internalCrafting`, value, true)
  }

  function workbenchCraftingOrDefault(): boolean {
    return baseModelAccess.get(model)?.workbenchCrafting ?? defaultConfig.workbenchCrafting.default
  }

  function updateWorkbenchCrafting(value: boolean) {
    set(`${baseModelAccess.where}.workbenchCrafting`, value, true)
  }

  function furnaceSmeltingOrDefault(): boolean {
    return baseModelAccess.get(model)?.furnaceSmelting ?? defaultConfig.furnaceSmelting.default
  }

  function updateFurnaceSmelting(value: boolean) {
    set(`${baseModelAccess.where}.furnaceSmelting`, value, true)
  }

  function campfireCookingOrDefault(): boolean {
    return baseModelAccess.get(model)?.campfireCooking ?? defaultConfig.campfireCooking.default
  }

  function updateCampfireCooking(value: boolean) {
    set(`${baseModelAccess.where}.campfireCooking`, value, true)
  }

  function smithingOrDefault(): boolean {
    return baseModelAccess.get(model)?.smithing ?? defaultConfig.smithing.default
  }

  function updateSmithing(value: boolean) {
    set(`${baseModelAccess.where}.smithing`, value, true)
  }

  function stonecutterOrDefault(): boolean {
    return baseModelAccess.get(model)?.stonecutter ?? defaultConfig.stonecutter.default
  }

  function updateStonecutter(value: boolean) {
    set(`${baseModelAccess.where}.stonecutter`, value, true)
  }

</script>