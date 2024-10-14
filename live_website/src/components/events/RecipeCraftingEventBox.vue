<template>
  <EventBox v-bind="props" :class="`${type === 'noBlockBreak' ? 'customized-rule' : 'customized-goal'}`">
    <template #eventTrigger>
      <i18n-t :keypath="computedRecipeType" tag="div" class="flex items-center text-2xl" >
        <template #player>
          <div class="mr-2">
            <PlayerHead :size="32" v-bind="data.player" />
          </div>
        </template>
        <template #item>
          <RecipeView :craftin-type-data-row="fromCode2DataRow(data.recipeCrafted) as CraftingTypeDataRow" />
        </template>
      </i18n-t>
    </template>
  </EventBox>
</template>

<script setup lang="ts">

import type { ItemDataConfig } from '@fhnaumann/criteria-interfaces'
import EventBox from '@/components/events/EventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import MaterialItem from '@/components/MaterialItem.vue'
import type { CraftingDataConfig } from '@fhnaumann/criteria-interfaces'
import { useTranslation } from '@/composables/language'
import RecipeView from '@/components/events/RecipeView.vue'
import { fromCode2DataRow } from '@/composables/data_row_loaded'
import type { CraftingTypeDataRow } from '@fhnaumann/criteria-interfaces'
import { computed } from 'vue'
import type { CriteriaKey } from '@fhnaumann/criteria-interfaces'

const props = defineProps<{
  data: CraftingDataConfig,
  eventIndex: number
  type: CriteriaKey
}>()

const { craftingType2DataRow } = useTranslation()

const computedRecipeType = computed(() => {
  if(isRecipeFromCrafting()) {
    return "events.recipeCrafting.crafting_table"
  }
  else if(isRecipeFromSmeltingOrCooking()) {
    return "events.recipeCrafting.smelted"
  }
  else if(isRecipeFromSmithingUpgrade()) {
    return "events.recipeCrafting.smithing"
  }
  else {
    throw new Error("Unknown recipe type encountered.")
  }
})

function isRecipeFromCrafting() {
  return props.data.recipeCrafted in ["crafting", "stonecutting"]
}

function isRecipeFromSmeltingOrCooking() {
  return props.data.recipeCrafted in ["furnace", "blasting", "campfire", "smoking"]
}

function isRecipeFromSmithingUpgrade() {
  return props.data.recipeCrafted in ["smithing"]
}

</script>