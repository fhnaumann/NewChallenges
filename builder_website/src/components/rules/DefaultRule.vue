<template>
  <div>
    <div class="flex flex-col space-y-5">
      <p class="text-2xl">{{ t("rules.description_header") }}</p>
      <p class="text-wrap ml-4">{{ t(`rules.types.${props.rule.id}.tooltip`) }}</p>
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import type { RuleView } from '../view/rules'

const props = defineProps({
  rule: {
    type: Object as () => RuleView,
    required: true,
  },
})

const { t } = useI18n()

</script>
