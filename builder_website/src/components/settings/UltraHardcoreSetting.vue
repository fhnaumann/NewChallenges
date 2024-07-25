<template>
  <BaseCriteriaModification criteria-key="ultraHardcoreSetting" relative-u-r-l-to-wiki="settings/ultrahardcoresetting"
                            criteria-type="settings">
    <template #configuration>
      <div class="flex flex-col space-y-2">
        <div class="flex items-center">
          <Checkbox :model-value="naturalRegenerationOrDefault()" @update:model-value="updateNaturalRegeneration" binary
                    input-id="naturalRegeneration" />
          <label for="naturalRegeneration" class="ml-2">{{ t(`${baseLocaleString}.naturalRegeneration.name`) }}</label>
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="regWithGoldenApplesOrDefault()" @update:model-value="updateRegWithGoldenApples" binary
                    input-id="regWithGoldenApples" />
          <label for="regWithGoldenApples" class="ml-2">{{ t(`${baseLocaleString}.regWithGoldenApples.name`) }}</label>
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="regWithEnchantedGoldenApplesOrDefault()" @update:model-value="updateRegWithEnchantedGoldenApples" binary
                    input-id="regWithEnchantedGoldenApples" />
          <label for="regWithEnchantedGoldenApples" class="ml-2">{{ t(`${baseLocaleString}.regWithEnchantedGoldenApples.name`) }}</label>
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="regWithSuspiciousStewOrDefault()" @update:model-value="updateRegWithSuspiciousStew" binary
                    input-id="regWithSuspiciousStew" />
          <label for="regWithSuspiciousStew" class="ml-2">{{ t(`${baseLocaleString}.regWithSuspiciousStew.name`) }}</label>
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="regWithPotionsOrDefault()" @update:model-value="updateRegWithPotions" binary
                    input-id="regWithPotions" />
          <label for="regWithPotions" class="ml-2">{{ t(`${baseLocaleString}.regWithPotions.name`) }}</label>
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="allowAbsorptionHeartsOrDefault()" @update:model-value="updateAllowAbsorptionHearts" binary
                    input-id="allowAbsorptionHearts" />
          <label for="allowAbsorptionHearts" class="ml-2">{{ t(`${baseLocaleString}.allowAbsorptionHearts.name`) }}</label>
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="allowTotemsOrDefault()" @update:model-value="updateAllowTotems" binary
                    input-id="allowTotems" />
          <label for="allowTotems" class="ml-2">{{ t(`${baseLocaleString}.allowTotems.name`) }}</label>
        </div>
      </div>
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'
  import Checkbox from 'primevue/checkbox'
  import { useJSONSchemaConfig } from '@/stores/default_model'

  const { model, set } = useModelStore()
  const baseLocaleString = 'settings.types.ultraHardcoreSetting.settings'
  const baseSetString = 'settings.ultraHardcoreSetting'
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.UltraHardcoreSettingConfig.properties

  const { t } = useI18n()


  function naturalRegenerationOrDefault() {
    return model.settings?.ultraHardcoreSetting?.naturalRegeneration !== undefined ? model.settings?.ultraHardcoreSetting.naturalRegeneration! : config.naturalRegeneration.default
  }

  function updateNaturalRegeneration(value: boolean) {
    set(`${baseSetString}.naturalRegeneration`, value, false)
  }

  function regWithGoldenApplesOrDefault() {
    return model.settings?.ultraHardcoreSetting?.regWithGoldenApples !== undefined ? model.settings?.ultraHardcoreSetting.regWithGoldenApples! : config.regWithGoldenApples.default
  }

  function updateRegWithGoldenApples(value: boolean) {
    set(`${baseSetString}.regWithGoldenApples`, value, false)
  }

  function regWithEnchantedGoldenApplesOrDefault() {
    return model.settings?.ultraHardcoreSetting?.regWithEnchantedGoldenApples !== undefined ? model.settings?.ultraHardcoreSetting.regWithEnchantedGoldenApples! : config.regWithEnchantedGoldenApples.default
  }

  function updateRegWithEnchantedGoldenApples(value: boolean) {
    set(`${baseSetString}.regWithEnchantedGoldenApples`, value, false)
  }

  function regWithSuspiciousStewOrDefault() {
    return model.settings?.ultraHardcoreSetting?.regWithSuspiciousStew !== undefined ? model.settings?.ultraHardcoreSetting.regWithSuspiciousStew! : config.regWithSuspiciousStew.default
  }

  function updateRegWithSuspiciousStew(value: boolean) {
    set(`${baseSetString}.regWithSuspiciousStew`, value, false)
  }

  function regWithPotionsOrDefault() {
    return model.settings?.ultraHardcoreSetting?.regWithPotions !== undefined ? model.settings?.ultraHardcoreSetting.regWithPotions! : config.regWithPotions.default
  }

  function updateRegWithPotions(value: boolean) {
    set(`${baseSetString}.regWithPotions`, value, false)
  }

  function allowAbsorptionHeartsOrDefault() {
    return model.settings?.ultraHardcoreSetting?.allowAbsorptionHearts !== undefined ? model.settings?.ultraHardcoreSetting.allowAbsorptionHearts! : config.allowAbsorptionHearts.default
  }

  function updateAllowAbsorptionHearts(value: boolean) {
    set(`${baseSetString}.allowAbsorptionHearts`, value, false)
  }

  function allowTotemsOrDefault() {
    return model.settings?.ultraHardcoreSetting?.allowTotems !== undefined ? model.settings?.ultraHardcoreSetting.allowTotems! : config.allowTotems.default
  }

  function updateAllowTotems(value: boolean) {
    set(`${baseSetString}.allowTotems`, value, false)
  }

</script>