<template>
  <BaseCriteriaModification criteria-key="floorIsLavaSetting" relative-u-r-l-to-wiki="settings/floorislavasetting"
                            criteria-type="settings">
    <template #configuration>
      <div class="flex flex-col space-y-2">
        <div class="flex items-center">
          <label class="mr-2"
                 for="timeToNextBlockChangeInTicks">{{ t(`${baseLocaleString}.timeToNextBlockChangeInTicks.name`)
            }}</label>
          <InputNumber class="h-8" input-id="timeToNextBlockChangeInTicks" show-buttons
                       :min="config.timeToNextBlockChangeInTicks.minimum"
                       :max="config.timeToNextBlockChangeInTicks.maximum" mode="decimal"
                       :model-value="timeToNextBlockChangeInTicksOrDefault()"
                       @update:model-value="updateTimeToNextBlockChangeInTicks" />
        </div>
        <div class="flex items-center">
          <Checkbox :model-value="lavaRemainsPermanentlyOrDefault()" @update:model-value="updateLavaRemainsPermanently"
                    binary input-id="lavaRemainsPermanently" />
          <label class="ml-2" for="lavaRemainsPermanently">{{ t(`${baseLocaleString}.lavaRemainsPermanently.name`)
            }}</label>
        </div>
      </div>
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import { useModelStore } from '@/stores/model'
  import Checkbox from 'primevue/checkbox'
  import InputNumber from 'primevue/inputnumber'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'

  const { model, set } = useModelStore()
  const baseLocaleString = 'settings.types.floorIsLavaSetting.settings'
  const baseSetString = 'settings.floorIsLavaSetting'
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.FloorIsLavaSettingConfig.properties

  const { t } = useI18n()

  function timeToNextBlockChangeInTicksOrDefault() {
    return model.settings?.floorIsLavaSetting?.timeToNextBlockChangeInTicks !== undefined ? model.settings.floorIsLavaSetting.timeToNextBlockChangeInTicks! : config.timeToNextBlockChangeInTicks.default
  }

  function updateTimeToNextBlockChangeInTicks(value: number) {
    set(`${baseSetString}.timeToNextBlockChangeInTicks`, value, false)
  }

  function lavaRemainsPermanentlyOrDefault() {
    return model.settings?.floorIsLavaSetting?.lavaRemainsPermanently !== undefined ? model.settings.floorIsLavaSetting.lavaRemainsPermanently! : config.lavaRemainsPermanently.default
  }

  function updateLavaRemainsPermanently(value: boolean) {
    set(`${baseSetString}.lavaRemainsPermanently`, value, false)
  }

</script>