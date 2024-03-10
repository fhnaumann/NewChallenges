<template>
  <DefaultPunishableRule :rule="props.rule">
    <p class="text-xl">Configuration:</p>
    <div class="flex items-center space-x-4 h-12">
      <p class="">Exemptions:</p>
      <MultiSelect
        :model-value="selectedData"
        @update:model-value="updateIfValid"
        :options="allBlockMaterialData"
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
              :src="BASE_IMG_URL + '/rendered_images/' + slotProps.option.img_name"
              @error="$event.target.src = 'unknown.png'"
            />
            <div>{{ translation.translate(slotProps.option.translation_key) }}</div>
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
          :src="'/rendered_items/' + item.img_name"
          @error="$event.target.src = 'unknown.png'"
        />
        <p>{{ translation.translate(item.translation_key) }}</p>
      </div>
    </Sidebar>
  </DefaultPunishableRule>
</template>

<script setup lang="ts">
import { BASE_IMG_URL } from '@/main'
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
import { type DataRow } from '../loadableDataRow'
import { watch } from 'vue'
import { useValidator } from '../validator'
import type { Model } from '../model/model'
import Row from 'primevue/row'
import { allBlockMaterialData } from '../loadableDataRow'
import { useTranslation } from '../language'

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

const translation = useTranslation()

// IMPORTANT TO CLEAR THE CONFIG
// For some reason deleting the entire goal does not delete "something", which
// prevents the config from being wiped, therefore manually clearing it here
if(!Object.hasOwn(store.rules.enabledRules, 'noBlockBreak')) {
  store.rules.enabledRules.noBlockBreak = structuredClone(toRaw(defaultConfig.rules.enabledRules.noBlockBreak))
}

const selectedData = ref<DataRow[]>([])
watch(selectedData, (newSelectedData) => {
  console.log("watching selecteddata", newSelectedData)
  updateExemptionsInModel(store, newSelectedData)
})

function updateIfValid(newSelectedData: DataRow[]) {
  const { valid, messages } = validator.isValid(store, (copy) => {
    updateExemptionsInModel(copy, newSelectedData)
  })
  if(valid) {
    selectedData.value = newSelectedData
  }
}

function updateExemptionsInModel(model: Model = store, newSelectedData: DataRow[]) {
  console.log("UPDATING EXEMPTIONS", newSelectedData)
  model.rules!.enabledRules!.noBlockBreak!.exemptions = newSelectedData.map((dataRow: DataRow) => dataRow.code)
}

const visible = ref(false)


defineComponent({
  MultiSelect,
  Sidebar,
  PunishmentSettingsInRule,
})
</script>
