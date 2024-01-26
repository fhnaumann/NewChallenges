<template>
    <!--AccordionTab :pt="{ headerAction: { class: { 'bg-green-600': isRuleActive() } }, header: { class: 'width: 32rem' } }"
        v-if="partialMatchesSearch(ruleName)">
        <template #header>
            <span class="flex justify-between items-center gap-2 w-full">
                <span class="font-bold whitespace-nowrap">{{ ruleName }}</span>
                <Button v-if="!isRuleActive()" @click="$event.stopPropagation(); activateRule()"
                    class="cursor-pointer bg-gray-600 py-1 px-1 rounded text-white">
                    Activate
                </Button>
                <Button v-else @click="$event.stopPropagation(); deactivateRule()"
                    class="cursor-pointer bg-gray-600 py-1 px-1 rounded text-white">
                    Deactivate
                </Button>
            </span>
        </template>
    </AccordionTab-->
    <span class="flex justify-between items-center gap-2 w-full">
        <span class="font-bold whitespace-nowrap">{{ ruleName }}</span>
        <Button v-if="!activatable.isRuleActive()" @click="$event.stopPropagation(); activatable.activateRule()"
            class="cursor-pointer bg-gray-600 py-1 px-1 rounded text-white">
            Activate
        </Button>
        <Button v-else @click="$event.stopPropagation(); activatable.deactivateRule()"
            class="cursor-pointer bg-gray-600 py-1 px-1 rounded text-white">
            Deactivate
        </Button>
    </span>
</template>
  
<script setup lang="ts">
import { ref, defineProps, defineExpose } from 'vue';
import { useConfigStore } from '@/main';
import NoBlockBreaking from './NoBlockBreaking.vue';
import AccordionTab from 'primevue/accordiontab';
import { useActivatableRule } from '../activatableRule.js';
const store = useConfigStore().model
console.log("ruleEntry")
const props = defineProps({
    ruleName: String,
    index: String,
})

const activatable = useActivatableRule(props.ruleName)

</script>