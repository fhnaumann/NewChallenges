<template>
    <div class="flex flex-row items-center space-x-4 h-12">
        <p>{{ collectablePrefix }}</p>
            <Dropdown v-model="newSelectedMob" :options="possibleData" option-label="label"
                display="chip" :virtual-scroller-options="{ itemSize: 44 }" filter class="w-full md:w-80">
                <template #value="slotProps">
                    <div v-if="slotProps.value" class="flex justify-start items-center space-x-2">
                        <img class="w-6" :src="'/rendered_items/' + img + '.png'" :alt="slotProps.value" @error="$event.target.src = 'unknown.png'">
                        <div>{{ label }}</div>
                    </div>
                </template>
                <template #option="slotProps">
                    <div class="flex justify-start items-center space-x-2">
                        <img class="w-6" :src="'/rendered_items/' + img + '.png'" :alt="slotProps.option" @error="$event.target.src = 'unknown.png'">
                        <div>{{ slotProps.option.label }}</div>
                    </div>
                </template>
            </Dropdown>
            <p>{{ amountPrefix }}</p>
            <InputNumber v-model="kills" showButtons :min="1" :max="100" :step="1" inputStyle="width:32px" />
            <Button v-if="selectedData" label="Delete" @click="$emit('deleteEntry', selectedData)" />
    </div>
</template>

<script setup lang="ts">
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import { ref, defineComponent, toRef, watch, computed } from 'vue'
import { useConfigStore } from '@/main';


const props = defineProps({
    selectedData: String,
    label: String,
    img: String,
    possibleData: [],
    collectablePrefix: String,
    amountPrefix: String
})
const newSelectedMob = computed({
    get: () => props.selectedData,
    set: (newValue) =>  emit('update:selectedData', newValue)
})
const store = useConfigStore().model

const kills = ref(1)

watch(kills, (newKills) => {
    emit('update:valueForAmount', newKills)
    //store.goals.MobGoal.settings.mobs[props.selectedData.code].amountNeeded = newKills
})

const emit = defineEmits(["deleteEntry", "update:selectedData", "update:valueForAmount"])

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