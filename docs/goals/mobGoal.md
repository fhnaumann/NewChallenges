# Mob Goal

When Mob Goal is selected, then a configurable collection of mobs have to be killed in order to complete the goal. The configuration includes different mob types as well as amounts. 

## Configuration

[!badge Individual Dropdown Selection]
:   Select mobs and their necessary amounts individually with the dropdown option.

[!badge Collect all mobs]
:   Every (living) mob has to be killed once.

[!badge Fixed random order]
:   If selected, every mob has to be killed in a specific order that is determined when the settings file is generated. Otherwise the mobs can be killed in any order.

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 1](../static/examples/no_death_end_challenge_mob_goal_1_ender_dragon.json)


:::example_configuration
**Example 2**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill every mob once in a fixed order (MobGoal)
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 2](../static/examples/no_death_end_challenge_mob_goal_every_mob_once_fixed_order.json)