<template>
    <DefaultPunishment v-bind="props" v-slot="slotProps" @clear-on-disabled="clearOnDisabled">
        <div v-if="slotProps.punishable.active.value">
            <div class="flex flex-col space-y-4">
                <div class="flex items-center space-x-4">
                    <p>Items removed at once:</p>
                    <InputNumber input-style="width:32px" v-model="itemsRemovedAtOnce" show-buttons mode="decimal" :min="minItemsRemovedAtOnce" :max="maxItemsRemovedAtOnce" :disabled="randomizeItemsRemovedAtOnce || entireInventoryRemoved"/>
                    <Checkbox v-model="randomizeItemsRemovedAtOnce" :binary="true" input-id="randomizeItemsRemovedAtOnce" :disabled="entireInventoryRemoved"/>
                    <label for="randomizeItemsRemovedAtOnce" class="ml-2">Randomize every time</label>
                </div>
                <div class="flex items-center space-x-4">
                    <Checkbox v-model="entireInventoryRemoved" :binary="true" input-id="entireInventoryRemoved"/>
                    <label for="entireInventoryRemoved" class="ml-2">Entire inventory cleared</label>
                </div>
            </div>

        </div>
    </DefaultPunishment>
</template>

<script setup lang="ts">
import { useConfigStore, useJSONSchemaConfigStore } from '@/main';
import DefaultPunishment, { type PunishmentProps } from './DefaultPunishment.vue';
import { usePunishableCommons } from './Punishable';
import { ref, watch } from 'vue';
import InputNumber from 'primevue/inputnumber';
import Checkbox from 'primevue/checkbox';


const props = defineProps<PunishmentProps>()

const model = useConfigStore().model
const jsonSchema = useJSONSchemaConfigStore()

const { getPunishmentBasePath } = usePunishableCommons()

const minItemsRemovedAtOnce = jsonSchema.RandomItemPunishmentConfig.properties.itemsRemovedAtOnce.minimum
const maxItemsRemovedAtOnce = jsonSchema.RandomItemPunishmentConfig.properties.itemsRemovedAtOnce.maximum
const defaultItemsRemovedAtOnce = jsonSchema.RandomItemPunishmentConfig.properties.itemsRemovedAtOnce.default

const itemsRemovedAtOnce = ref<number>(defaultItemsRemovedAtOnce)
watch(itemsRemovedAtOnce, (newItemsRemovedAtOnce) => {
    getPunishmentBasePath(model, props).randomItemPunishment!.itemsRemovedAtOnce = newItemsRemovedAtOnce
})

const defaultRandomizeItemsRemovedAtOnce = jsonSchema.RandomItemPunishmentConfig.properties.randomizeItemsRemovedAtOnce.default

const randomizeItemsRemovedAtOnce = ref<boolean>(defaultRandomizeItemsRemovedAtOnce)
watch(randomizeItemsRemovedAtOnce, (isRandomizeItemsRemovedAtOnce) => {
    if(isRandomizeItemsRemovedAtOnce) {
        itemsRemovedAtOnce.value = defaultItemsRemovedAtOnce
    }
    getPunishmentBasePath(model, props).randomItemPunishment!.randomizeItemsRemovedAtOnce = isRandomizeItemsRemovedAtOnce
})

const defaultEntireInventoryRemoved = jsonSchema.RandomItemPunishmentConfig.properties.entireInventoryRemoved.default

const entireInventoryRemoved = ref<boolean>(defaultEntireInventoryRemoved)
watch(entireInventoryRemoved, (isEntireInventoryRemoved) => {
    if(isEntireInventoryRemoved) {
        itemsRemovedAtOnce.value = defaultItemsRemovedAtOnce
        randomizeItemsRemovedAtOnce.value = defaultRandomizeItemsRemovedAtOnce
    }
    getPunishmentBasePath(model, props).randomItemPunishment!.entireInventoryRemoved = isEntireInventoryRemoved
})

function clearOnDisabled() {
    itemsRemovedAtOnce.value = defaultItemsRemovedAtOnce
    randomizeItemsRemovedAtOnce.value = defaultRandomizeItemsRemovedAtOnce
    entireInventoryRemoved.value = defaultEntireInventoryRemoved
}

</script>