<template>
  <BaseCriteriaModification criteria-key="mlgSetting" relative-u-r-l-to-wiki="settings/mlgsetting"
                            criteria-type="settings">
    <template #configuration>
      <div class="flex flex-col space-y-4">
        <div class="flex items-center">
          <MinMaxRange :model-access="minMaxRangeAccess" :absolute-min-min-value="config.minTimeSeconds.minimum"
                       :default-min-value="config.minTimeSeconds.default"
                       :default-max-value="config.maxTimeSeconds.default"
                       :absolute-max-max-value="config.maxTimeSeconds.maximum" />
        </div>
        <div class="flex items-center space-x-2">
          <label for="mlgSetting.height">{{ t('settings.types.mlgSetting.settings.height.name') }}</label>
          <InputNumber input-id="mlgSetting.height" show-buttons :min="config.height.minimum"
                       :max="config.height.maximum" mode="decimal" :model-value="heightOrDefault()"
                       @update:model-value="updateHeight" />
        </div>
      </div>
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">

  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import MinMaxRange from '@/components/MinMaxRange.vue'
  import InputNumber from 'primevue/inputnumber'
  import type { ModelAccess } from '@/main'
  import MLGSetting from '@/components/settings/MLGSetting.vue'
  import type { MinMaxRangeConfig } from '@fhnaumann/criteria-interfaces'

  const { set, model } = useModelStore()
  const { t } = useI18n()

  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.MLGSettingConfig.properties

  const baseSetString = 'settings.mlgSetting'

  const minMaxRangeAccess: ModelAccess<MinMaxRangeConfig> = {
    get: model1 => model1.settings?.mlgSetting,
    where: baseSetString,
    testSchematron: false,
  }


  function heightOrDefault(): number {
    return model.settings?.mlgSetting?.height !== undefined ? model.settings.mlgSetting.height! : config.height.default
  }

  function updateHeight(value: number) {
    set(`${baseSetString}.height`, value, false)
  }

</script>