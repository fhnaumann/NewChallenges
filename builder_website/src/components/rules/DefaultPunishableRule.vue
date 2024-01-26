<template>
    <DefaultRule :rule="props.rule">
        <slot></slot>
        <PunishmentSettingsInRule :current-rule="rule.id" />
    </DefaultRule>
</template>

<script setup lang="ts">
import PunishmentSettingsInRule from '../punishments/PunishmentSettingsInRule.vue';
import DefaultRule from './DefaultRule.vue';
import type { RuleView } from '../view/rules';

const props = defineProps({
    rule: {
        type: Object as () => RuleView,
        required: true
    }
})

</script>