<template>
  <Button
    class="border-solid border-2 rounded-md bg-blue-500 hover:brightness-125"
    :disabled="isActive()"
    @click="addDefaultSectionToConfig"
  >
    <div class="flex items-center max-w-96">
      <img
        class="w-10"
        :src="'/img/' + props.goal?.id + '.png'"
        alt=""
        @error="$event.target.src = 'unknown.png'"
      />
      <div>
        <p class="text-3xl font-bold">{{ goal.label }}</p>
        <p class="text-wrap ml-4">{{ goal.description }}</p>
      </div>
    </div>
  </button>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { useConfigStore, useDefaultConfigStore } from '@/main'
import type { GoalView } from '../view/goals'
import type { BaseRuleConfig } from '../model/rules';
import type Button from 'primevue/button';
const props = defineProps({
  goal: {
    type: Object as () => GoalView,
    required: true,
  },
})

function isActive() {
  try {
    return store.goals![props.goal.id]
  } catch (error) {
    return false
  }
}

const store = useConfigStore().model
const defaultConfig = useDefaultConfigStore()

function addDefaultSectionToConfig() {
  console.log('added default section to', props.goal.id)
  store.goals[props.goal.id as keyof BaseRuleConfig] = defaultConfig.goals[props.goal.id as keyof BaseRuleConfig]
  isActive()
}
</script>
