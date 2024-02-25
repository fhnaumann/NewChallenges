<template>
    <div>
      <div class="flex flex-col space-y-5">
        <p class="text-2xl">Description:</p>
        <p class="text-wrap ml-4">{{ props.goal.description }}</p>
        <slot></slot>
        <div>
          <Button class="cursor-pointer w-full h-10 bg-gray-600 py-1 rounded text-xl text-white " label="Transform into Force"/>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import type { GoalView } from '../view/goals';
  import Button from 'primevue/button';

  const props = defineProps({
    goal: {
      type: Object as () => GoalView,
      required: true,
    },
  })
  </script>
  