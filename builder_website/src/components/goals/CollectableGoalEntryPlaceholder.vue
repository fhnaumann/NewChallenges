<template>
    <div class="flex flex-row items-center space-x-4 h-12">
        <p>{{ collectablePrefix }}</p>
            <Dropdown v-model="selectedData" :options="possibleData" option-label="label" :placeholder="placeHolderText" :disabled="disabled"
                display="chip" :virtual-scroller-options="{ itemSize: 44 }" filter class="w-full md:w-80"
                @update:modelValue="$emit('transferDataFromPlaceHolderToNewInstance', selectedData!); selectedData=undefined">
                <template #value="slotProps">
                    <div v-if="slotProps.value" class="flex justify-start items-center space-x-2">
                        <img class="w-6" :src="'/rendered_items/' + slotProps.value.img_name" :alt="slotProps.value" @error="$event.target!.src = 'unknown.png'">
                        <div>{{ translation.translate(slotProps.value.translation_key) }}</div>
                    </div>
                </template>
                <template #option="slotProps">
                    <div class="flex justify-start items-center space-x-2">
                        <img class="w-6" :src="'/rendered_items/' + slotProps.option.img_name" :alt="slotProps.option" @error="$event.target!.src = 'unknown.png'">
                        <div>{{ translation.translate(slotProps.option.translation_key) }}</div>
                    </div>
                </template>
            </Dropdown>
            <p>{{ amountPrefix }}</p>
            <InputNumber v-model="kills" showButtons :min="1" :max="100" :step="1" :disabled="!selectedData" inputStyle="width:32px" />
    </div>
</template>

<script setup lang="ts">
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import { ref, defineComponent, toRef, watch } from 'vue'
import { useConfigStore } from '@/main';
import type { DataRow } from '../loadableDataRow';
import { useTranslation } from '../language';

//const props = defineProps(['possibleMobs', 'collectablePrefix', 'amountPrefix', 'placeHolderText'])
const props = defineProps<{
    possibleData: DataRow[]
    collectablePrefix: string
    amountPrefix: string
    placeHolderText: string
    disabled: boolean
}>()

const translation = useTranslation()

const selectedData = ref<DataRow>()
const kills = 0
// defineEmits(["transferDataFromPlaceHolderToNewInstance"])
const emits = defineEmits<{
    transferDataFromPlaceHolderToNewInstance: [newSelectedData: DataRow]
}>()

</script>