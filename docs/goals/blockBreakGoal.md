# Block Break Goal

When Block Break Goal is selected, a configurable collection of blocks have to be broken in order to complete the goal. The configuration includes different blocks as well as amounts.

!!!info Difference to Item Goal
The [Item Goal](/goals/itemGoal.md) has the option to only collect blocks. This is different to this goal, because the in the Item Goal players just need to hold the actual Item in their inventory. Here they need to actually break the block.
!!!

## Configuration

[!badge Individual Dropdown Selection]
:   Select blocks and their necessary amoutns individually wit the dropdown option.

[!badge Break all blocks]
:   Every obtainable (mineable) block has to be broken once.

[!badge Fixed random order]
:   If selected, every block has to be broken in a specific order that is determined when the settings file is generated. Otherwise the blocks can be broken in any order.

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndChallenge)

**Goals:** Break 1 beacon (Block Break Goal)
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 1](../static/examples/no_death_end_challenge_block_break_goal_1_beacon.json)

:::example_configuration
**Example 2**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndChallenge)

**Goals:** Break every obtainable block once in a fixed order (Block Break Goal)
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 2](../static/examples/no_death_end_challenge_block_break_goal_every_block_once_fixed_order.json)