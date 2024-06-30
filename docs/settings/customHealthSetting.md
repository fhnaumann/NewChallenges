# Custom Health Setting

Set the maximum number of hearts the players have in a challenge.

!!!info
Minecraft uses half-hearts as hearts. Therefore, one heart in this setting refers to half of a visual heart above the hotbar.
!!!

## Configuration

[!badge Hearts]
:    Select the amount of hearts the players play with. A regular player has 20 hearts. (Min: 1, Max: 100)

## Example Configuration

Below are some examples you can directly copy to your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)

**Settings:** 10 hearts (CustomHealthSetting)
:::

[!file Example 1](../static/examples/no_death_end_challenge_mob_goal_1_ender_dragon_custom_health_setting_10.json)