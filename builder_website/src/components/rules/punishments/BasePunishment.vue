<template>
  <div class="flex flex-col">
    <div class="flex justify-between">
      <div class="flex items-center space-x-2">
        <Checkbox :model-value="enabled"
                  @update:model-value="setPunishmentEnabled" binary :input-id="punishmentType" />
        <label :for="punishmentType">{{ t(`punishments.types.${punishmentType}.name`) }}</label>
      </div>
      <div v-if="!hideAffected" class="flex items-center space-x-2">
        <p>{{ t(`punishments.affects.affected.name`) }}</p>
        <Dropdown :model-value="affects"
                  @update:model-value="updateAffects" :options="['all', 'causer']" :disabled="!enabled" :id="`${punishmentType}.affects`" >
          <template #value="slotProps">
            <div>{{ t(`punishments.affects.affected.types.${slotProps.value}.name`) }}</div>
          </template>
          <template #option="slotProps">
            <div>{{ t(`punishments.affects.affected.types.${slotProps.option}.name`) }}</div>
          </template>
        </Dropdown>
      </div>
    </div>
    <div class="ml-4" v-if="enabled">
      <slot name="configuration" :base="basePunishmentInConfig()">

      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">

  import type { ModelAccess } from '@/main'
  import type { Affects, BasePunishmentConfig, PunishmentName } from '@/models/punishments'
  import type { PunishableRuleConfig } from '@/models/rules'
  import Checkbox from 'primevue/checkbox'
  import { computed, ref } from 'vue'
  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'
  import Dropdown from 'primevue/dropdown'

  const props = defineProps<{
    modelAccess: ModelAccess<BasePunishmentConfig>
    punishmentType: PunishmentName
    hideAffected?: boolean
  }>()

  const { t } = useI18n()
  const { model, set } = useModelStore()


  const enabled = computed(() => {
    return props.modelAccess.get(model) !== undefined
  })

  const affects = computed(() => {
    return props.modelAccess.get(model)?.affects !== undefined ? props.modelAccess.get(model)?.affects : 'all'
  })

  function setPunishmentEnabled(enabled: boolean) {
    set(basePunishmentInConfig(), enabled ? {} : undefined, true)
  }

  function updateAffects(value: Affects) {
    set(`${basePunishmentInConfig()}.affects`, value, false)
  }

  function basePunishmentInConfig(): string {
    return `${props.modelAccess.where}`
  }
</script>