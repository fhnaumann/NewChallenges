# MLG Setting

Players have to complete MLGs that continuously appear randomly within a time frame. If a MLG is failed (by any player),
the challenge is lost. The time to the next MLG (at the start or after the last MLG) is determined randomly every time.

!!!warning Leaving while in an MLG
If a player leaves the server while performing an MLG, they will be teleported back to the main challenge world, and the MLG
will be aborted.
!!!

!!!info Receiving punishments while in an MLG
Players who are in an ongoing MLG are unable to receive punishments. In such a circumstance, the player will receive
their punishment immediately after completing the MLG.
!!!

## Configuration

[!badge Minimum Time]
:    The lower bound (in minutes) when determining the time to do the next MLG.

[!badge Maximum Time]
:    The upper bound (in minutes) when determining the time to do the next MLG.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeathRule)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)

**Settings:** MLGs randomly between 1 and 5 minutes (MLGSetting)
:::

[!file Example 1](../static/examples/no_death_end_challenge_mob_goal_1_ender_dragon_mlg_setting_30_height_60s_min_5m_max.json)