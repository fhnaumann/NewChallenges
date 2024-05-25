<template>
  <Checkbox
    :model-value="model"
    @update:model-value="(newModelValue: boolean) => emit('updateModel', newModelValue)"
    v-bind="$attrs" :pt="preset" :pt-options="{ mergeSections: false, mergeProps: false }"
    :input-id="translatedLabel"
    binary />
  <label
    :for="translatedLabel"
    class="ml-2"
  >{{ translatedLabel }}</label>
</template>

<script setup lang="ts">
  import Checkbox from 'primevue/checkbox'
  import { useI18n } from 'vue-i18n'
  import type { CriteriaType } from '@/models/model'
  import { ref } from 'vue'

  defineOptions({
    inheritAttrs: false,
  })

  const compProps = defineProps<{
    criteriaType: CriteriaType,
    model: boolean
    translatedLabel: string
  }>()

  const emit = defineEmits<{
    updateModel: [newModelValue: boolean]
  }>()

  const preset = {
    root: {
      class: ['relative', 'inline-flex', 'align-bottom', 'w-5', 'h-5', 'cursor-pointer', 'select-none']
    },
    box: ({ props, context }) => ({
      class: [
        // Alignment
        'flex',
        'items-center',
        'justify-center',
        // Size
        'w-5',
        'h-5',
        // Shape
        'rounded',
        'border',
        // Colors
        {
          'border-surface-300 dark:border-surface-700': !context.checked && !props.invalid,
          'bg-surface-0  dark:bg-surface-950': !context.checked && !props.invalid && !props.disabled,
          'border-rule-500 bg-rule-500': context.checked && compProps.criteriaType === 'rules',
          'border-goal-500 bg-goal-500': context.checked && compProps.criteriaType === 'goals',
          'border-setting-500 bg-setting-500': context.checked && compProps.criteriaType === 'settings'
        },
        // Invalid State
        'invalid:focus:ring-red-200',
        'invalid:hover:border-red-500',
        { 'border-red-500 dark:border-red-400': props.invalid },
        // States
        {
          'peer-hover:border-surface-400 dark:peer-hover:border-surface-600': !props.disabled && !context.checked && !props.invalid,
          'peer-hover:bg-rule-600 peer-hover:border-rule-600': !props.disabled && context.checked && compProps.criteriaType === 'rules',
          'peer-hover:bg-goal-600 peer-hover:border-goal-600': !props.disabled && context.checked && compProps.criteriaType === 'goals',
          'peer-hover:bg-setting-600 peer-hover:border-setting-600': !props.disabled && context.checked && compProps.criteriaType === 'settings',
          'peer-focus-visible:z-10 peer-focus-visible:outline-none peer-focus-visible:outline-offset-0 peer-focus-visible:ring-1 peer-focus-visible:ring-primary-500 dark:peer-focus-visible:ring-primary-400': !props.disabled,
          'bg-surface-200 dark:bg-surface-700 select-none pointer-events-none cursor-default': props.disabled
        },
        // Transitions
        'transition-colors',
        'duration-200'
      ]
    }),
    input: {
      class: ['peer', 'w-full ', 'h-full', 'absolute', 'top-0 left-0', 'z-10', 'p-0', 'm-0', 'opacity-0', 'rounded', 'outline-none', 'border border-surface-300 dark:border-surface-700', 'appearance-none', 'cursor-pointer']
    },
    icon: {
      class: ['w-[0.875rem]', 'h-[0.875rem]', 'text-white dark:text-surface-950', 'transition-all', 'duration-200']
    }
  }
</script>