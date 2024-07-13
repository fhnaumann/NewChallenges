<template>
  <BaseCriteriaModification criteria-key="deathGoal" relative-u-r-l-to-wiki="goals/deathgoal" criteria-type="goals">
    <template #configuration>
      <div class="flex flex-col space-y-2">
        <div class="flex space-x-2 items-center">
          <label for="deathAmount">{{ t('goals.types.deathGoal.settings.deathAmount.name') }}</label>
          <InputNumber :model-value="deathAmountOrDefault()" @update:model-value="updateDeathAmount"
                       :min="1" :max="100" input-id="deathAmount"
                       :disabled="individualDeathMessages" />

        </div>
        <div>
          <Checkbox :model-value="countTotemOrDefault()" @update:model-value="updateCountTotem" binary input-id="countTotem" />
          <label class="ml-2" for="countTotem">{{ t('goals.types.deathGoal.settings.countTotem.name') }}</label>
        </div>
        <div>
          <Checkbox v-model="individualDeathMessages" binary input-id="individualDeathMessages" />
          <label class="ml-2"
                 for="individualDeathMessages">{{ t('goals.types.deathGoal.settings.individualDeathMessages.name')
            }}</label>
        </div>
        <div v-if="individualDeathMessages" class="w-[80rem]">
          <CollectableDropdownConfiguration :all-possible-data="ALL_DEATH_MESSAGES_DATA"
                                            :dropdown-placeholder-text="t('goals.types.deathGoal.settings.dropdown.deathPlaceholder')"
                                            :render-selection="!allDeathMessagesOnce" :model-access="{
                                              get: model => model.goals?.deathGoal?.deathMessages,
                                              where: 'goals.deathGoal.deathMessages',
                                              testSchematron: false
                                            }"
                                            :raw-text="dataRow => translate(dataRow.code)"
                                            :collectable-text-prefix="t('goals.types.deathGoal.settings.dropdown.deathPrefix')"
                                            :show-image="false"
                                            :disabled="allDeathMessagesOnce"
                                            dropdown-class="w-[80rem]"
                                            :collectable-amount-prefix="t('goals.types.deathGoal.settings.dropdown.amountPrefix')" />
          <Checkbox v-model="allDeathMessagesOnce" @update:model-value="updateAllDeathMessagesOnce"
                    input-id="allDeathMessagesOnce" binary />
          <label for="allDeathMessagesOnce"
                 class="ml-2">{{ t('goals.types.deathGoal.settings.allDeathMessagesOnce.name') }}</label>
          <FixedOrderConfiguration :model-access="baseModelAccess" />
          <TimerConfiguration :model-access="baseModelAccess" />
        </div>
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
  import Checkbox from 'primevue/checkbox'
  import { ref, watch } from 'vue'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import {
    ALL_DEATH_MESSAGES_DATA,
    type DeathMessageDataRow,
    fromDataRowArray2CollectableEntryArray,
  } from '@/models/data_row'
  import { storeToRefs } from 'pinia'
  import { useVarHelperStore } from '@/stores/var_helper'
  import type { ModelAccess } from '@/main'
  import DeathGoal from '@/components/goals/types/DeathGoal.vue'
  import type { DeathGoalConfig } from '@/models/death'
  import FixedOrderConfiguration from '@/components/goals/FixedOrderConfiguration.vue'
  import TimerConfiguration from '@/components/goals/TimerConfiguration.vue'
  import type { BlockBreakGoalConfig } from '@/models/blockbreak'
  import { useTranslation } from '@/language'

  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.DeathGoalConfig.properties
  const { t } = useI18n()

  const individualDeathMessages = ref<boolean>(false)

  const { allDeathMessagesOnce } = storeToRefs(useVarHelperStore())

  const baseModelAccess: ModelAccess<DeathGoalConfig> = {
    get: model1 => model1.goals?.deathGoal,
    where: 'goals.deathGoal',
    testSchematron: false,
  }

  const { translate } = useTranslation()

  function updateDeathAmount(value: number) {
    set('goals.deathGoal.deathAmount.amountNeeded', value, false)
  }

  function deathAmountOrDefault(): number {
    return model.goals?.deathGoal?.deathAmount?.amountNeeded !== undefined ? model.goals.deathGoal.deathAmount.amountNeeded! : 1
  }

  function countTotemOrDefault(): boolean {
    return model.goals?.deathGoal?.countTotem !== undefined ? model.goals.deathGoal.countTotem : config.countTotem.default
  }

  function updateCountTotem(value: boolean) {
    set('goals.deathGoal.countTotem', value, false)
  }

  watch(individualDeathMessages, (value) => {
    if (value) {
      updateDeathAmount(1)
    }
    else {
      set('goals.deathGoal.deathMessages', undefined, false)
      allDeathMessagesOnce.value = false
    }
  })

  function updateAllDeathMessagesOnce(allDeathMessagesOnce: boolean) {
    set('goals.deathGoal.deathMessages', allDeathMessagesOnce ? fromDataRowArray2CollectableEntryArray(ALL_DEATH_MESSAGES_DATA) : undefined, true)
  }
</script>