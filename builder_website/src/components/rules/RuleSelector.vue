<template>
    <div class="w-[32rem] h-[40rem] mt-2">
        <div>
            <InputText class="w-full" type="text" placeholder="Search" v-model="searchable.searchFieldValue.value" />
        </div>
    <div class="card">
        <DataView :value="searchable.getPartialMatches()" paginator :rows="3">
            <template #list="slotProps">
                <div class="grid grid-nogutter">
                    <div class="flex flex-column xl:flex-row xl:align-items-start p-4 gap-4 items-center w-[32rem]" v-for="(item, index) in slotProps.items" :key="index">
                        <RuleSelectionEntry class="w-full" :rule="item"/>
                    </div>
                </div>
            </template>
        </DataView>
    </div>
    </div>

</template>

<script setup lang="ts">
import DataView from 'primevue/dataview';
import InputText from 'primevue/inputtext';
import DataViewLayoutOptions from 'primevue/dataviewlayoutoptions';
import { ref, inject, defineComponent } from 'vue';
import RuleSelectionEntry from './RuleSelectionEntry.vue';
import type { Searchable } from '../searchable'
import { useSearchable } from '../searchable'
import type { RuleView } from '../view/rules';
import { useRulesViewStore } from '@/main'
import { useConfigStore } from '@/main'

const dialogRef = inject('dialogRef') as Object

const rulesView = useRulesViewStore()

const searchable = useSearchable(Object.values(rulesView.allrules))

</script>