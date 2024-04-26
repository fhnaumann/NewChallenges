# Mob Goal

When Mob Goal is selected, then a configurable collection of mobs have to be killed in order to complete the goal. The configuration includes different mob types as well as amounts. 

## Configuration

You can select mobs and their necessary amounts individually with the dropdown option. Alternatively, you can check the [!badge Collect all mobs] checkbox which adds every (living) mob exactly once to the goal. If the [!badge Fixed random order] checkbox is selected, then every mob has to be killed in a specific order that is determined when the settings file is generated on the website. Otherwise, the mobs can be killed in any order.

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndChallenge)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 1](../static/goals/no_death_end_challenge_mob_goal_1_ender_dragon.json)


:::example_configuration
**Example 2**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndChallenge)

**Goals:** Kill every mob once in a fixed order (MobGoal)
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 2](../static/goals/no_death_end_challenge_mob_goal_every_mob_once_fixed_order.json)