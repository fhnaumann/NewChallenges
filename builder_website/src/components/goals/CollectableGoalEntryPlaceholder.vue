<template>
    <div class="flex flex-row items-center space-x-4 h-12">
        <p>{{ collectablePrefix }}</p>
            <Dropdown v-model="selectedMob" :options="possibleMobs" option-label="label" :placeholder="placeHolderText"
                display="chip" :virtual-scroller-options="{ itemSize: 44 }" filter class="w-full md:w-80"
                @update:modelValue="$emit('transferDataFromPlaceHolderToNewInstance', selectedMob); selectedMob=null">
                <template #value="slotProps">
                    <div v-if="slotProps.value" class="flex justify-start items-center space-x-2">
                        <img class="w-6" :src="'/rendered_items/' + slotProps.value.image + '.png'" :alt="slotProps.value" @error="$event.target.src = 'unknown.png'">
                        <div>{{ slotProps.value.label }}</div>
                    </div>
                </template>
                <template #option="slotProps">
                    <div class="flex justify-start items-center space-x-2">
                        <img class="w-6" :src="'/rendered_items/' + slotProps.option.image + '.png'" :alt="slotProps.option" @error="$event.target.src = 'unknown.png'">
                        <div>{{ slotProps.option.label }}</div>
                    </div>
                </template>
            </Dropdown>
            <p>{{ amountPrefix }}</p>
            <InputNumber v-model="kills" showButtons :min="1" :max="100" :step="1" :disabled="!selectedMob" inputStyle="width:32px" />
    </div>
</template>

<script setup lang="ts">
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import { ref, defineComponent, toRef, watch } from 'vue'
import { useConfigStore } from '@/main';

const props = defineProps(['possibleMobs', 'collectablePrefix', 'amountPrefix', 'placeHolderText'])

const selectedMob = ref()
const kills = 0
defineEmits(["transferDataFromPlaceHolderToNewInstance"])

</script>