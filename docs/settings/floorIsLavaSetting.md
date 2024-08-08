# Floor is Lava Setting

Move quickly to avoid the inevitable lava! The ground the player stands on first becomes a magma block and then lava.
Optionally it transforms back to its original block.

## Configuration

[!badge Time until block change]
:    The time (in ticks, where 20 ticks = 1 second) it takes to perform one block change. For example, if it is 20, then
it takes 1 second after a player moved on a block to it being transformed to magma. Another second later it is transformed
to lava. The default value is 30 ticks (1.5 seconds).

[!badge Lava remains permanently]
:    Controls if the lava remains permanently once it has been transformed. If not, then the lava will be transformed
back to its original block.

## Example Configuration

Below are some examples you can directly copy to your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeathRule)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon (MobGoal)

**Settings:** Floor is Lava after 2 seconds (FloorIsLavaSetting)
:::

[!file Example 1](../static/examples/no_death_end_challenge_mob_goal_1_ender_dragon_floorislava_setting_1s_no_permanent_lava.json)