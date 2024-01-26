<template>
  <div>
    <div class="flex flex-col space-y-5">
      <p class="text-2xl">Description:</p>
      <p class="text-wrap ml-4">{{ props.rule.description }}</p>
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { RuleView } from '../view/rules'

const props = defineProps({
  rule: {
    type: Object as () => RuleView,
    required: true,
  },
})
</script>
