<template>
    <div class="flex flex-row items-center space-x-4 h-12">
        <p>{{ collectablePrefix }}</p>
            <Dropdown v-model="newSelectedMob" :options="possibleData" :option-label=translateDataRow :disabled="disabled"
                display="chip" :virtual-scroller-options="{ itemSize: 44 }" filter class="w-full md:w-80">
                <template #value="slotProps">
                    <div v-if="slotProps.value" class="flex justify-start items-center space-x-2">
                        <img v-if="showImage" class="w-6" :src="BASE_IMG_URL + '/rendered_images/' + slotProps.value.img_name" :alt="slotProps.value" @error="$event.target.src = 'unknown.png'">
                        <div>{{ translate(slotProps.value.translation_key) }}</div>
                    </div>
                </template>
                <template #option="slotProps">
                    <div class="flex justify-start items-center space-x-2">
                        <img v-if="showImage" class="w-6" :src="BASE_IMG_URL + '/rendered_images/' + slotProps.option.img_name" :alt="slotProps.option" @error="$event.target.src = 'unknown.png'">
                        <div>{{ translate(slotProps.option.translation_key) }}</div>
                    </div>
                </template>
            </Dropdown>
            <p>{{ amountPrefix }}</p>
            <InputNumber v-model="kills" showButtons :min="1" :max="100" :step="1" :disabled="disabled" inputStyle="width:32px" />
            <Button v-if="selectedData" label="Delete" @click="$emit('deleteEntry', selectedData)" :disabled="disabled" />
    </div>
</template>

<script setup lang="ts">
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import { ref, defineComponent, toRef, watch, computed } from 'vue'
import { BASE_IMG_URL, useConfigStore } from '@/main';
import { type DataRow } from '../loadableDataRow';
import { useTranslation } from '../language';

const props = defineProps<{
    selectedData: DataRow,
    possibleData: DataRow[],
    collectablePrefix: string,
    amountPrefix: string,
    disabled: boolean,
    showImage: boolean
}>()

console.log(props.selectedData)
const newSelectedMob = computed({
    get: () => props.selectedData,
    set: (newValue) =>  emit('updateSelectedData', newValue)
})
const store = useConfigStore().model

const {translate, translateDataRow} = useTranslation()

const kills = ref(1)

watch(kills, (newKills) => {
    emit('updateValueForAmount', newKills)
    //store.goals.MobGoal.settings.mobs[props.selectedData.code].amountNeeded = newKills
})

// const emit = defineEmits(["deleteEntry", "update:selectedData", "update:valueForAmount"])
const emit = defineEmits<{
    updateSelectedData: [newSelectedData: DataRow]
    updateValueForAmount: [newAmount: number]
    deleteEntry: [dataRowToDelete: DataRow]
}>()
defineComponent({
    Dropdown,
    InputNumber,
    Button
})

// call once so the store object is updated correctly
/*const existingAmountNeeded = store.goals.MobGoal.settings.mobs[props.selectedData.code]?.amountNeeded
    store.goals.MobGoal.settings.mobs[props.selectedData.code] = {
            amountNeeded: existingAmountNeeded != undefined ? existingAmountNeeded : 1,
            currentAmount: 0,
    }*/
</script>