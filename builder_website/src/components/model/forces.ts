import type { BlockBreakForceConfig } from "./blockbreak";

export interface ForcesConfig {
    blockBreakForce?: BlockBreakForceConfig
}

export interface BaseForceConfig {
    /**
     * The lower bound that is used to determine the time for the next forcing. In seconds.
     * 
     * @minimum 1
     * @maximum 3600
     * @TJS-type integer
     */
    minTime?: number

    /**
     * The upper bound that is used to determine the time for the next forcing. In seconds.
     * 
     * @minimum 1
     * @maximum 3600
     * @TJS-type integer
     */
    maxTime?: number
}