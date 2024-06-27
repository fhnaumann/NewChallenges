# Health Punishment

Players lose a configurable amount of hearts.

## Configuration

[!badge Hearts]
:   Select the number of hearts (half-hearts) that are lost. A regular player has 20 hearts.

[!badge Randomize]
:   Choose a random number of hearts every time the punishment is triggered. Chooses a value between 1 (inclusive)
and 10 (inclusive) hearts.

[!badge Affected]
:   Select who is affected by this punishment.

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** NoItem

**Punishments:** Every player loses 1 heart (HealthPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)
:::

[!file Example 1](../static/examples/no_item_1_heart_lost_all_mob_goal_1_ender_dragon.json)
