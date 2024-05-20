<template>
  <BaseCriteriaModification criteria-name-i18-n-path="block_break_goal">
    <template #configuration>
      <CollectableDropdownConfiguration
        selected-data=""
        dropdown-placeholder-text=""
        collectable-text-prefix=""
        show-image=""
        disabled=""
        possible-data=""
        collectable-amount-prefix="" />
      <Checkbox
        v-model="breakAllBlocksOnce"
        input-id="breakAllBlocksOnce"
        binary />
      <label
        for="breakAllBlocksOnce"
        class="ml-2"
        >{{ t('goals.block_block_goal.break_all_blocks_once.text') }}</label
      >
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">
  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import CollectableDropdownConfiguration from '@/components/goals/CollectableDropdownConfiguration.vue'
  import { useRoute } from 'vue-router'
  import { ref, watch } from 'vue'
  import { useFetchable } from '@/fetchable'
  import type { BlockBreakGoalConfig } from '@/models/blockbreak'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const route = useRoute()
  const { fetch } = useFetchable()

  watch(
    () => route.params.id,
    value => fetchCriteriaData(value as string),
    { immediate: true },
  )

  const breakAllBlocksOnce = ref<boolean>(false)

  function fetchCriteriaData(id: string) {
    const fetched: BlockBreakGoalConfig = fetch<BlockBreakGoalConfig>('goals', 'blockBreakGoal', id)

    breakAllBlocksOnce.value = fetched.breakAllBlocksOnce !== undefined ? fetched.breakAllBlocksOnce : false
  }
</script>
