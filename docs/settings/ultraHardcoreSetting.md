# Ultra Hardcore Setting

Change the way players are able to regenerate health or *cheat* death.

!!!info Violating a Setting
Unlike rules, violating a setting (e.g., consuming a golden apple) does not trigger a punishment. Instead, it is just
silently ignored.
!!!

## Configuration

[!badge Natural Regeneration]
:    Controls if natural regeneration is enabled. If disabled, players are not able to regenerate naturally by
consuming food.

[!badge Regenerate with Golden Apples]
:    Controls if regenerating with ![golden apples](../static/mc-textures/minecraft_golden_apple.png){width=25 height=25} is enabled.

[!badge Regenerate with Enchanted Golden Apples]
:    Controls if regenerating with ![enchanted golden apples](../static/mc-textures/minecraft_enchanted_golden_apple.png){width=25 height=25} is enabled.

[!badge Regenerate with Suspicious Stew]
:    Controls if regenerating with suspicious stew is enabled. It is possible to craft a suspicious stew that provides
regeneration with an ![oxeye daisy](../static/mc-textures/minecraft_oxeye_daisy.png){width=25 height=25}.

[!badge Allow Absorption Hearts]
:    Controls if absorption hearts are kept or removed when gained. 

[!badge Allow Totems]
:    Controls if ![totems of undying](../static/mc-textures/minecraft_totem_of_undying.png){width=25 height=25} may be used.

!!!info Absorption Hearts gained from ![golden apples](../static/mc-textures/minecraft_golden_apple.png){width=25 height=25}, ![enchanted golden apples](../static/mc-textures/minecraft_enchanted_golden_apple.png){width=25 height=25} and ![totems of undying](../static/mc-textures/minecraft_totem_of_undying.png){width=25 height=25}
Absorption hearts may be gained from a variety of sources, including
consuming ![golden apples](../static/mc-textures/minecraft_golden_apple.png){width=25 height=25}, ![enchanted golden apples](../static/mc-textures/minecraft_enchanted_golden_apple.png){width=25 height=25} and using a ![totems of undying](../static/mc-textures/minecraft_totem_of_undying.png){width=25 height=25}. If one of the *sources* 
is disabled by a setting from above, then the player still receives their absorption heart based on this setting. \
For example, disabling regeneration with golden apples and enabling absorption hearts lets the player keep the gained 
two absorption hearts from consuming a golden apple. However, the player won't receive any regular (red) hearts.
!!!

## Example Configuration

Below are some examples you can directly copy to your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeathRule)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)

**Settings:** No Natural Regeneration (UltraHardcoreSetting)
:::

[!file Example 1](../static/examples/no_death_end_challenge_mob_goal_1_ender_dragon_no_natural_reg.json)

:::example_configuration
**Example 2**

**Rules:** [NoDeath](../rules/noDeathRule)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)

**Settings:** No Regeneration at all (UltraHardcoreSetting)
:::

[!file Example 2](../static/examples/no_death_end_challenge_mob_goal_1_ender_dragon_no_reg_at_all.json)

