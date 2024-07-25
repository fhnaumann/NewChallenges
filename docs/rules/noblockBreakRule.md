# No Block Break Rule

Players are not allowed to break blocks.

## Punishment Trigger

Any player breaks any block, except for blocks which are in the exemption list.

## Configuration

[!badge Exemptions]
:    Select blocks which are excluded and do not trigger the punishments.

[!badge text="Local Punishments"](../punishments/punishments.md)
:    Select punishments that trigger when this rule is violated.

## Example Configuration

Below are some examples you can directly copy to your server without using the website.

:::example_configuration
**Example 1**

**Rules:** NoBlockBreak

**Punishments:** Every player loses 1 heart (HealthPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

[!file Example 1](../static/examples/no_block_break_1_heart_lost_all_mob_goal_1_ender_dragon.json)