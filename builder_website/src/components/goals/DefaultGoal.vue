<template>
    <div>
      <div class="flex flex-col space-y-5">
        <p class="text-2xl">Description:</p>
        <p class="text-wrap ml-4">{{ props.goal.description }}</p>
        <slot></slot>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import type { GoalView } from '../view/goals';
  
  const props = defineProps({
    goal: {
      type: Object as () => GoalView,
      required: true,
    },
  })
  </script>
  