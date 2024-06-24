# Block Place Goal

When Block Place Goal is selected, a configurable collection of blocks have to be placed in order to complete the goal. The configuration includes blocks as well as amounts.

## Configuration

[!badge Individual Dropdown Selection]
:   Select blocks and their necessary amounts individually wit the dropdown option.

[!badge Place all blocks]
:   Every obtainable (mineable) block has to be placed once.

[!badge Fixed random order]
:   If selected, every block has to be placed in a specific order that is determined when the settings file is generated. Otherwise, the blocks can be broken in any order.

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Place 1 Enderdragon Egg (BlockPlaceGoal)
:::

[!file Example 1](../static/examples/no_death_end_challenge_block_place_goal_dragon_egg_fixed_order.json)

:::example_configuration
**Example 2**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Place every obtainable block once in a fixed order (BlockPlaceGoal)
:::

[!file Example 2](../static/examples/no_death_end_challenge_block_place_goal_every_block_once_fixed_order.json)