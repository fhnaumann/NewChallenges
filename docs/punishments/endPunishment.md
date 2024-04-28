# End Punishment

When End Punishment is selected, the challenge is immediately lost.

!!!info 2nd Chance
If this punishment is triggered, every player will be put in spectator mode, and the timer will halt. You can purposely ignore this, set your gamemode back to survival and, continue the timer with `/timer resume`. You can play on as if nothing has happened.
!!!

## Example Configuration

Below are some examples you can directly copy into your server without using the website.

:::example_configuration
**Example 1**

**Rules:** [NoDeath](../rules/noDeath.md)

**Punishments:** The challenge is over for everyone (EndPunishment)

**Goals:** Kill 1 Enderdragon ([MobGoal](../goals/mobGoal.md))
:::

!!!danger Rename file after download
Rename the file to `data.json` and place it in the `settings` folder! Otherwise the plugin won't recognize the file!
!!!

[!file Example 1](../static/examples/no_death_end_challenge_mob_goal.json)