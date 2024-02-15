import { ref } from "vue"
import type { BaseGoalConfig, GoalsConfig } from "./model/goals"
import type { GoalsView } from "./view/goals"
import { useConfigStore } from "@/main"

export interface Searchable {
    label: string,
}

export function useSearchable(options: Searchable[]) {
    const model = useConfigStore().model

    const searchFieldValue = ref<string>('')

    function getPartialMatches(): Searchable[] {

        if(!searchFieldValue.value.trim()) {
            return options
        }
        else {
            return options.filter((searchable) => 
                searchable.label.toLowerCase().includes(searchFieldValue.value.toLowerCase()) 
                    )
        }         
    }

    function allActiveGoalsAsLabels(goalsView: GoalsView): string[] {
        const allGoalsView = Object.values(goalsView.allgoals)
        const activeGoals = Object.keys(model.goals!)

        return allGoalsView.filter(goalView => activeGoals.includes(goalView.id)).map(goalView => goalView.label)
    }

    return { searchFieldValue, getPartialMatches, allActiveGoalsAsLabels }
}