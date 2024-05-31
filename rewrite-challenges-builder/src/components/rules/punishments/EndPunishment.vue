<template>
  <BasePunishment punishment-type="endPunishment" :model-access="modelAccess" />
</template>

<script setup lang="ts">
import BasePunishment from '@/components/rules/punishments/BasePunishment.vue'
import type { ModelAccess } from '@/main'
import type { EndPunishmentConfig, HealthPunishmentConfig } from '@/models/punishments'

const props = defineProps<{
  modelAccess: ModelAccess<EndPunishmentConfig>
}>()

</script>