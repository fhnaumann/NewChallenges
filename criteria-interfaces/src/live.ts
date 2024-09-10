import {CriteriaKey} from "./model";

export interface MCEvent<T> {

    /**
     * The unique ID to identify the challenge this event originated from.
     */
    challengeID: string,

    /**
     * The unique ID to identify the event in this challenge.
     */
    eventID: string,

    /**
     * @TSJ-type integer
     */
    timestamp: number,

    /**
     * The event type. Depending on the type, additional data may be transmitted.
     */
    eventType: CriteriaKey | "start" | "pause" | "resume" | "end"

    /**
     * Potential additional data involved.
     */
    data?: T
}

export interface DataConfig {

    /**
     * The player involved in the event (the "causer").
     */
    player: PlayerConfig

    /**
     * The number of "events" this data represents. For example, some goals allow to skip parts of it. In this case the
     * number will be different from 1
     *
     * @default 1
     * @TSJ-type integer
     */
    amount: number

    timestamp: number
}
export interface PlayerConfig {
    playerUUID: string
    playerName: string
    skinTextureURL: string
}

export interface TeamDataConfig {
    /**
     * The team the acted with.
     */
    teamName: string
    /**
     * The player who interacted with the team.
     */
    player: PlayerConfig

    /**
     * The action on the team (join or leave).
     */
    action: "join" | "leave"
}