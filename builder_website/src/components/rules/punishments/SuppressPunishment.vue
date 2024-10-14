<template>
  <BasePunishment punishment-type="suppressPunishment" :model-access="modelAccess" hide-affected />
</template>

<script setup lang="ts">
  import type { ModelAccess } from '@/main'
  import type { SuppressPunishmentConfig } from '@fhnaumann/criteria-interfaces'
  import BasePunishment from '@/components/rules/punishments/BasePunishment.vue'

  const props = defineProps<{
    modelAccess: ModelAccess<SuppressPunishmentConfig>
  }>()
</script>