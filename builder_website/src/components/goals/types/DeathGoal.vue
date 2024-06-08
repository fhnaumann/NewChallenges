<template>
  <BaseCriteriaModification criteria-key="deathGoal" relative-u-r-l-to-wiki="goals/deathgoal" criteria-type="goals">
    <template #configuration>
      <div class="flex space-x-2 items-center">
        <label for="deathAmount">{{ t('goals.types.deathGoal.settings.deathAmount.name') }}</label>
        <InputNumber :model-value="deathAmountOrDefault()" @update:model-value="updateDeathAmount"
                     :min="config.deathAmount.minimum" :max="config.deathAmount.maximum" input-id="deathAmount" />

      </div>
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import InputNumber from 'primevue/inputnumber'
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import { useModelStore } from '@/stores/model'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'


  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.DeathGoalConfig.properties
  const { t } = useI18n()

  function updateDeathAmount(value: number) {
    set('goals.deathGoal.deathAmount', value, false)
  }

  function deathAmountOrDefault(): number {
    return model.goals?.deathGoal?.deathAmount !== undefined ? model.goals.deathGoal.deathAmount! : config.deathAmount.default
  }
</script>