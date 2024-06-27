# No Item Rule

Players are not allowed to collect items in their inventory.

## Punishment Trigger

Any player collects any item, except for items which are in the exemption list.

## Configuration

[!badge Exemptions]
:    Select items which are excluded and do not trigger the punishments.

[!badge text="Local Punishments"](../punishments/punishments.md)
:    Select punishments that trigger when this rule is violated.

## Example Configuration

Below are some examples you can directly copy to your server without using the website.

:::example_configuration
**Example 1**

**Rules:** NoItem

**Punishments:** Every player loses 1 heart (HealthPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

[!file Example 1](../static/examples/no_item_1_heart_lost_all_mob_goal_1_ender_dragon.json)