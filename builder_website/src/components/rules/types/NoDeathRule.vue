<template>
  <div>
    <BaseRuleModification criteria-key="noDeath" relative-u-r-l-to-wiki="rules/nodeathrule" criteria-type="rules">
      <template #configuration>
        <div class="mb-4">
          <Checkbox :model-value="ignoreTotemsOrDefault()" @update:model-value="updateIgnoreTotems" binary input-id="ignoreTotems" />
          <label class="ml-2" for="ignoreTotems">{{ t('rules.types.noDeath.settings.ignoreTotems.name') }}</label>
        </div>
      </template>
    </BaseRuleModification>
  </div>

</template>

<script setup lang="ts">
import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
import Checkbox from 'primevue/checkbox'
import BaseRuleModification from '@/components/rules/BaseRuleModification.vue'
import { useModelStore } from '@/stores/model'
import { useJSONSchemaConfig } from '@/stores/default_model'
import { useI18n } from 'vue-i18n'

const { set, model } = useModelStore()
const jsonSchemaConfig = useJSONSchemaConfig()
const config = jsonSchemaConfig.NoDeathRuleConfig.properties
const { t } = useI18n()

function ignoreTotemsOrDefault(): boolean {
  return model.rules?.enabledRules?.noDeath?.ignoreTotem !== undefined ? model.rules.enabledRules.noDeath.ignoreTotem! : config.ignoreTotem.default
}

function updateIgnoreTotems(value: boolean) {
  set('rules.enabledRules.noDeath.ignoreTotem', value, false)
}

</script>