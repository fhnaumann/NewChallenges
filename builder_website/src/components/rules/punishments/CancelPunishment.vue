<template>
  <BasePunishment punishment-type="cancelPunishment" :model-access="modelAccess" hide-affected />
</template>

<script setup lang="ts">
  import BasePunishment from '@/components/rules/punishments/BasePunishment.vue'
  import type { ModelAccess } from '@/main'
  import type { CancelPunishmentConfig, EndPunishmentConfig, HealthPunishmentConfig } from 'criteria-interfaces'

  const props = defineProps<{
    modelAccess: ModelAccess<CancelPunishmentConfig>
  }>()

</script>