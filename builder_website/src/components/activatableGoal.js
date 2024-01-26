import { useConfigStore } from "@/main";

export function useActivatableGoal(goalName) {

    const store = useConfigStore().model

    function activateGoal() {
        store.goals[goalName] = {}
    }

    function deactivateGoal() {
        delete store.goals[goalName]
    }

    function isGoalActive() {
        return Object.hasOwn(store.goals, goalName)
    }

    return {activateGoal, deactivateGoal, isGoalActive}

}