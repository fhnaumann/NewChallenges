# Random Effect Punishment

Players receive a configurable amount of status effects. This punishment considers all (including "positive") effects.
A random effect has a random duration between 10 seconds and 10 minutes and a random amplifier between 0 and 5 (all bounds are inclusive).

## Configuration

[!badge Hearts]
:   Select the number of random effects that players receive.

[!badge Randomize]
:   Choose a random number of random effects every time the punishment is triggered. Chooses a value between 1 (inclusive)
and 10 (inclusive) random effects.

[!badge Affected]
:   Select who is affected by this punishment.

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** NoMobKill

**Punishments:** Every player receives 2 random effects (RandomEffectPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

[!file Example 1](../static/examples/no_mob_kill_except_ender_dragon_2_random_effect_all_mob_goal_1_ender_dragon.json)
