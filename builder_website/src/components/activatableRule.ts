import { useConfigStore } from "@/main";

export function useActivatableRule(ruleName: string) {

    const store:any = useConfigStore().model

    function activateRule() {
        store.rules[ruleName] = {}
    }

    function deactivateRule() {
        delete store.rules[ruleName]
    }

    function isRuleActive() {
        return Object.hasOwn(store.rules, ruleName)
    }

    return {activateRule, deactivateRule, isRuleActive}

}