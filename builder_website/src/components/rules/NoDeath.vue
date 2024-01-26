<template>
    <DefaultPunishableRuleVue :rule=noDeathRuleView>

    </DefaultPunishableRuleVue>
    <!--div>
        <div class="flex flex-col space-y-5">
            <PunishmentSettingsInRule current-rule="noDeath" />
        </div>        
    </div-->
</template>

<script setup lang="ts">
import { useRulesViewStore } from '@/main';
import type { RuleName } from '../model/rules';
import PunishmentSettingsInRule from '../punishments/PunishmentSettingsInRule.vue';
import type { RuleView } from '../view/rules';
import DefaultPunishableRuleVue from './DefaultPunishableRule.vue';

const noDeathRuleView = useRulesViewStore().allrules.noDeath


</script>