import type { GoalsConfig } from "./goals"
import type { RulesConfig } from "./rules"

// running the command: 
// typescript-json-schema src/components/model/model.ts Model > "challenges_schema.json" --noExtraProps --strictNullChecks --required --defaultProps
// NOTE: Any class that exports an interface may not import any other file (except for other interfaces)!

export interface Model {
    rules?: RulesConfig,
    goals?: GoalsConfig,

    /**
     * The time (in seconds) that has passed since the challenge was started.
     * 
     * @minimum 0
     * @default 0 
     * @TJS-type integer
     */
    timer?: number
}

export interface Base {
}

