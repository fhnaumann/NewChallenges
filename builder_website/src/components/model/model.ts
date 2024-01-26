import type { GoalsConfig } from "./goals"
import type { RulesConfig } from "./rules"

// running the command: 
// typescript-json-schema src/components/model/model.ts Model > "test-output-schema.json" --noExtraProps --strictNullChecks --required --defaultProps
// NOTE: Any class that exports an interface may not import any other file (except for other interfaces)!

export interface Model {
    rules: RulesConfig,
    goals: GoalsConfig
}

