# No Mob Kill Rule

Players are not allowed to kill mobs.

!!!info Default exemptions
Killing the Enderdragon is permitted by default. You can change that by manually removing them from the exemption list.
!!!

## Punishment Trigger

Any player kills any mob, except for mobs which are in the exemption list. Indirectly killing mobs (e.g. with a lava bucket)
does not cause the punishment trigger.

## Configuration

[!badge Exemptions]
:    Select mobs which are excluded and do not trigger the punishments.

[!badge text="Local Punishments"](../punishments/punishments.md)
:    Select punishments that trigger when this rule is violated.

## Example Configuration

Below are some examples you can directly copy to your server without using the website.

:::example_configuration
**Example 1**

**Rules:** NoMobKill

**Punishments:** Every player receives 2 random effects (RandomEffectPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

[!file Example 1](../static/examples/no_mob_kill_except_ender_dragon_2_random_effect_all_mob_goal_1_ender_dragon.json)