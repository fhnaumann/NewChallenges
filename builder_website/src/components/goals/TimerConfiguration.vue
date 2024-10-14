<template>
  <div class="flex flex-col space-y-2">
    <div class="flex flex-row items-center">
      <Checkbox v-model="enabled" :binary="true" :input-id="modelAccess.where" />
      <label :for="modelAccess.where" class="ml-2">{{ t('goals.timer.enable') }}</label>
    </div>
    <div v-if="enabled" class="flex flex-col space-y-2">
      <div class="flex flex-row items-center space-x-2">
        <p>{{ t("goals.timer.orderPrefix") }}</p>
        <InputNumber :model-value="orderOrDefault()" @update:model-value="updateOrder" :min="1" :max="100" />
      </div>
      <div class="flex flex-row items-center space-x-2">
        <p>{{ t('goals.timer.minTimerPrefix') }}</p>
        <InputNumber :model-value="sec2Minutes(minTimeSecOrDefault())" @update:model-value="updateMin"
                     :min="sec2Minutes(config.minTimeSeconds.minimum)" :max="sec2Minutes(maxTimeSecOrDefault())" />
        <p>{{ t('goals.timer.maxTimerPrefix') }}</p>
        <InputNumber :model-value="sec2Minutes(maxTimeSecOrDefault())" @update:model-value="updateMax"
                     :min="sec2Minutes(minTimeSecOrDefault())" :max="sec2Minutes(config.maxTimeSeconds.maximum)" />
      </div>
      <!--Slider class="w-96" :model-value="[sec2Minutes(minTimeSecOrDefault()), sec2Minutes(maxTimeSecOrDefault())]"
              @update:model-value="updateModel" range
              :min="sec2Minutes(config.minTimeSeconds.minimum)" :max="sec2Minutes(config.maxTimeSeconds.maximum)" /-->
    </div>
  </div>
</template>

<script setup lang="ts">


  import { useJSONSchemaConfig } from '@/stores/default_model'
  import type { ModelAccess } from '@/main'
  import type { Timeable } from '@fhnaumann/criteria-interfaces'
  import { useModelStore } from '@/stores/model'
  import Slider from 'primevue/slider'
  import InputNumber from 'primevue/inputnumber'
  import { ref, watch } from 'vue'
  import { useI18n } from 'vue-i18n'
  import Checkbox from 'primevue/checkbox'

  const props = defineProps<{
    modelAccess: ModelAccess<Timeable>
  }>()

  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const { t } = useI18n()

  const enabled = ref<boolean>(props.modelAccess.get(model)?.goalTimer != undefined)

  watch(() => enabled.value, value => {
    if (value) {
      set(`${props.modelAccess.where}.goalTimer.time`, -1, false)
      set(`${props.modelAccess.where}.goalTimer.startingTime`, -1, false)
      set(`${props.modelAccess.where}.goalTimer.order`, 1, false)
    }
    else {
      set(`${props.modelAccess.where}.goalTimer.time`, undefined, false)
      set(`${props.modelAccess.where}.goalTimer.startingTime`, undefined, false)
      set(`${props.modelAccess.where}.goalTimer.order`, undefined, false)
      set(`${props.modelAccess.where}.goalTimer.minTimeSeconds`, undefined, false)
      set(`${props.modelAccess.where}.goalTimer.maxTimeSeconds`, undefined, false)
    }
  })

  function updateOrder(value: number) {
    set(`${props.modelAccess.where}.goalTimer.order`, value, false)
  }

  function updateMin(value: number) {
    value = minutes2Sec(value)
    if (value <= maxTimeSecOrDefault()) {
      set(`${props.modelAccess.where}.goalTimer.minTimeSeconds`, value, false)
    }
  }

  function updateMax(value: number) {
    value = minutes2Sec(value)
    if (value >= minTimeSecOrDefault()) {
      set(`${props.modelAccess.where}.goalTimer.maxTimeSeconds`, value, false)
    }
  }

  function updateModel(value: number[]) {
    updateMin(value[0])
    updateMax(value[1])
  }

  // take default values from any goal config instead of the specific one (for now)
  const config = jsonSchemaConfig.GoalTimer.properties

  function minTimeSecOrDefault(): number {
    return props.modelAccess.get(model)?.goalTimer?.minTimeSeconds !== undefined
      ?
      props.modelAccess.get(model)?.goalTimer?.minTimeSeconds!
      : config.minTimeSeconds.default
  }

  function maxTimeSecOrDefault(): number {
    return props.modelAccess.get(model)?.goalTimer?.maxTimeSeconds !== undefined
      ?
      props.modelAccess.get(model)?.goalTimer?.maxTimeSeconds!
      : config.maxTimeSeconds.default
  }

  function orderOrDefault(): number {
    return props.modelAccess.get(model)?.goalTimer?.order !== undefined
      ?
      props.modelAccess.get(model)?.goalTimer?.order!
      : 1
  }

  function sec2Minutes(seconds: number): number {
    return seconds / 60
  }

  function minutes2Sec(minutes: number): number {
    return minutes * 60
  }
</script>
