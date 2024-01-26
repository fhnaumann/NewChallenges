<template>
    <div class="w-[32rem] h-[40rem] mt-2">
        <div>
            <InputText class="w-full" type="text" placeholder="Search" v-model="searchable.searchFieldValue.value" />
        </div>
        <div class="card">
            <DataView :value="searchable.getPartialMatches()" paginator :rows="5">
                <template #list="slotProps">
                    <div class="grid grid-nogutter">
                        <div class="flex flex-column xl:flex-row xl:align-items-start p-4 gap-4 items-center w-[32rem]"
                            v-for="(item, index) in slotProps.items" :key="index">
                            <GoalSelectionEntryVue class="w-full" :goal="item" />
                        </div>
                    </div>
                </template>
                <template #empty>
                    <div>
                        <p>No goals match your search.</p>
                    </div>
                    
                </template>
            </DataView>
        </div>
    </div>
</template>

<script setup lang="ts">
import InputText from 'primevue/inputtext';
import DataView from 'primevue/dataview';
import GoalSelectionEntryVue from "./GoalSelectionEntry.vue"
import DataViewLayoutOptions from 'primevue/dataviewlayoutoptions';
import { ref, inject, defineComponent } from 'vue';
import type { Searchable } from '../searchable'
import { useSearchable } from '../searchable'
import type { RuleView } from '../view/rules';
import { useGoalsViewStore, useRulesViewStore } from '@/main'
import { useConfigStore } from '@/main'

const dialogRef = inject('dialogRef') as Object

const goalsView = useGoalsViewStore()

const searchable = useSearchable(Object.values(goalsView.allgoals))

</script>