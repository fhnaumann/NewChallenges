<template>
  <button
    class="border-solid border-2 rounded-md bg-blue-500 hover:brightness-125"
    @click="addDefaultSectionToConfig"
  >
    <div class="flex items-center max-w-96">
      <img
        class="w-10"
        :src="'/img/' + props.rule?.id + '.png'"
        alt=""
        @error="$event.target.src = 'unknown.png'"
      />
      <div>
        <p class="text-3xl font-bold">{{ rule?.label }}</p>
        <p class="text-wrap ml-4">{{ rule?.description }}</p>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { useConfigStore, useDefaultConfigStore } from '@/main';
import type { NoBlockBreakRuleConfig, RuleName, RulesConfig, BaseRuleConfig } from '../model/rules';
import type { RuleView } from '../view/rules';

const props = defineProps({
  rule: {
    type: Object as () => RuleView,
    required: true
  },
})

const store = useConfigStore().model
const defaultConfig = useDefaultConfigStore()
function addDefaultSectionToConfig() {
    console.log("added default section to", props.rule.id)
    store.rules.enabledRules[props.rule.id] = defaultConfig.rules.enabledRules[props.rule.id as keyof BaseRuleConfig]
    //store.rules[props.rule.id] = props.rule.defaultSection

    
}
</script>