<template>
  <BaseCriteriaModification criteria-key="customHealthSetting" relative-u-r-l-to-wiki="settings/customhealthsetting"
                            criteria-type="settings"
  >
    <template #configuration>
      <div class="flex flex-row space-x-2 items-center h-12">
        <label for="customHealthSetting.hearts">{{ t('settings.types.customHealthSetting.settings.hearts.name') }}</label>
        <InputNumber class="h-8" input-id="customHealthSettingHearts" show-buttons :min="minHeartsLost" :max="maxHeartsLost" mode="decimal"
                     :model-value="modelStore.model.settings?.customHealthSetting?.hearts"
                     @update:model-value="(newHearts: number) => modelStore.set('settings.customHealthSetting.hearts', newHearts, true)" />
      </div>
    </template>
  </BaseCriteriaModification>

</template>
<script setup lang="ts">
  import InputNumber from 'primevue/inputnumber'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import modelSchema from '../../assets/challenges_schema.json'

  const modelStore = useModelStore()

  const { t } = useI18n()



  const customHealthDefaults = modelSchema.definitions.CustomHealthSettingConfig.properties
  const minHeartsLost = customHealthDefaults.hearts.minimum
  const maxHeartsLost = customHealthDefaults.hearts.maximum

  modelStore.set("settings.customHealthSetting.hearts", customHealthDefaults.hearts.default, false)

</script>