<template>
  <button
    class="border-solid border-2 rounded-md bg-blue-500 hover:brightness-125"
    @click="addDefaultSectionToConfig"
  >
    <div class="flex flex-col items-start max-w-96">
      <p class="text-3xl font-bold ml-2">{{ rule.label }}</p>
      <p class="text-wrap ml-4">{{ rule.description }}</p>
    </div>
  </button>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { useConfigStore, useDefaultConfigStore } from '@/main';
import type { RuleName, RulesConfig, BaseRuleConfig } from '../model/rules';
import type { RuleView } from '../view/rules';
import { useValidator } from '../validator';
import type { Model } from '../model/model';

const props = defineProps({
  rule: {
    type: Object as () => RuleView,
    required: true
  },
})

const store = useConfigStore().model
const defaultConfig = useDefaultConfigStore()
const validator = useValidator()

function addDefaultSectionToConfig() {

    const { valid, messages } = validator.isValid(store, (copy) => {
      addSection(copy)
    })
    if(valid) {
      addSection(store)
    }
    console.log("added default section to", props.rule.id)
    
    //store.rules[props.rule.id] = props.rule.defaultSection

    
}

function addSection(model: Model) {
  model.rules.enabledRules[props.rule.id] = defaultConfig.rules.enabledRules[props.rule.id as keyof BaseRuleConfig]
}
</script>