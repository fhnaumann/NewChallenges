<template>
  <DefaultPunishableRule :rule="props.rule">
    <p class="text-xl">Configuration:</p>
    <div class="flex items-center space-x-4 h-12">
      <p class="">Exemptions:</p>
      <MultiSelect
        :model-value="selectedData"
        @update:model-value="updateIfValid"
        :options="fullData.filter((row: DataRow) => row.isBlock)"
        option-label="label"
        placeholder="Select exemptions"
        display="chip"
        :virtual-scroller-options="{ itemSize: 44, delay: 100 }"
        
        filter
        class="w-full md:w-80"
      >
        <template #option="slotProps">
          <div class="flex justify-center items-center space-x-2">
            <img
              class="w-6"
              :alt="slotProps.option"
              :src="'/rendered_items/' + slotProps.option.image + '.png'"
              @error="$event.target.src = 'unknown.png'"
            />
            <div>{{ slotProps.option.label }}</div>
          </div>
        </template>
        <template #footer>
          <div class="py-2 px-3">
            <b>{{ selectedData ? Object.keys(selectedData).length : 0 }}</b> selected
          </div>
        </template>
      </MultiSelect>
      <Button
        class="bg-gray-700 rounded-lg h-full w-52"
        :disabled="Object.keys(selectedData).length == 0"
        @click="visible = true"
        >View selected exemptions</Button
      >
    </div>
    <Sidebar v-model:visible="visible" header="Exemptions" position="right">
      <div
        class="flex items-center space-x-2"
        v-for="item in selectedData"
        :key="item.code"
      >
        <img
          class="w-6"
          :alt="item.code"
          :src="'/rendered_items/' + item.image + '.png'"
          @error="$event.target.src = 'unknown.png'"
        />
        <p>{{ item.label }}</p>
      </div>
    </Sidebar>
  </DefaultPunishableRule>
</template>

<script setup lang="ts">
import DefaultPunishableRule from './DefaultPunishableRule.vue'
import MultiSelect from 'primevue/multiselect'
import Sidebar from 'primevue/sidebar'
import { ref, defineComponent, toRef, toRaw, computed } from 'vue'
import matList from '../../assets/items.csv?raw'
import PunishmentSettingsInRule from '../punishments/PunishmentSettingsInRule.vue'
import {
  useConfigStore,
  useDefaultConfigStore,
  useRulesViewStore,
} from '@/main'
import type { RuleView } from '../view/rules'
import { useLoadableDataRow, type DataRow } from '../loadableDataRow'
import { watch } from 'vue'
import { useValidator } from '../validator'
import type { Model } from '../model/model'
import Row from 'primevue/row'

const props = defineProps({
  rule: {
    type: Object as () => RuleView,
    required: true,
  },
})
const store = useConfigStore().model
const defaultConfig = useDefaultConfigStore()
const rulesViewStore = useRulesViewStore()
const validator = useValidator()
// IMPORTANT TO CLEAR THE CONFIG
// For some reason deleting the entire goal does not delete "something", which
// prevents the config from being wiped, therefore manually clearing it here
if(!Object.hasOwn(store.rules.enabledRules, 'noBlockBreak')) {
  store.rules.enabledRules.noBlockBreak = structuredClone(toRaw(defaultConfig.rules.enabledRules.noBlockBreak))
}


const { fullData, selectedData } = useLoadableDataRow(matList)

function updateIfValid(newSelectedData: DataRow[]) {
  const { valid, messages } = validator.isValid(store, (copy) => {
    updateStore(copy, newSelectedData)
  })
  if(valid) {
    selectedData.value = newSelectedData
    //updateStore(store, newSelectedData)
  }
}
watch(selectedData, (newSelectedData) => {
  console.log("watching selecteddata", newSelectedData)
  updateStore(store, newSelectedData)
})
watch(store.rules.enabledRules.noBlockBreak!.exemptions, (newExemptions) => {
  console.log("store exemptions modification detected, new:", newExemptions)
  selectedData.value = []
  newExemptions.forEach(exemption => selectedData.value.push(fullData.find((row: DataRow) => row.code === exemption)!))
}, {deep: true})
function updateStore(model: Model = store, newSelectedData: DataRow[]) {
  console.log("UPDATING EXEMPTIONS", newSelectedData)
  model.rules.enabledRules.noBlockBreak!.exemptions = newSelectedData.map((row) => row.code)
}

const visible = ref(false)


defineComponent({
  MultiSelect,
  Sidebar,
  PunishmentSettingsInRule,
})
</script>
