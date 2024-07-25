# Death Goal

A configurable amount of deaths has to be accumulated. Alternatively, individual death messages can be configured.

## Configuration

[!badge Deaths]
:    Select the amount of deaths. The value is only considered when no individual death messages are selected. Otherwise, it is ignored.

[!badge Count Deaths prevented by Totems]
:    If enabled, using a totem will not prevent the death from counting.

[!badge Enable individual death message selection]
:    Enable individual death messages.

[!badge Individual Dropdown Selection]
:   Select death messages and their necessary amounts individually with the dropdown option.

[!badge All Death Messages Once]
:   Every death message has to be accumulated once. This does not include every combination with every mob. For example,
'\[player\] was shot by \[mob\]' is reached when any mob killed the player. It does not have to be once with every mob.

[!badge Fixed random order]
:   If selected, every death message has to be accumulated in a specific order that is determined when the settings file is generated. Otherwise, the deaths can be accumulated in any order.

## Example Configuration

:::example_configuration
**Example 1**

**Rules:** NoBlockPlace, NoBlockBreak

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Reach 100 deaths (DeathGoal)
:::
[!file Example 1](../static/examples/no_block_place_no_block_break_end_challenge_death_goal_death_amount_100.json)

:::example_configuration
**Example 2**

**Rules:** NoBlockPlace, NoBlockBreak

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Accumulate every death message once in a fixed order (DeathGoal)
:::
[!file Example 2](../static/examples/no_block_place_no_block_break_end_challenge_death_goal_all_death_types_once_fixed_order.json)