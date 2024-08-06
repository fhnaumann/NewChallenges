# Teams

By default, challenges are meant to be played together where all players try to contribute towards reaching the set goals.
If a player violates a rule, all players (if configured) are punished.

Enabling teams in a challenge completely changes this behaviour. Now it's your team vs. their team in a race to complete
all goals. The team who completes all goals first, wins. Now only you and your team are punished when a rule is violated.

### Enabling Teams

You can enable teams on the builder website by clicking on "Modify Teams" at the bottom of the website. 
Here you can add new teams which will exist in the challenge. Once you [exported the configuration file, provided it
on the server](./getting-started.md) and loaded the challenge (`/load <challengename>`), players may join the teams.

Players can join a team by clicking on the team in chat or by writing `/challengeteams join <teamname>`. Players can
leave a team by clicking on their current team in chat or by writing `/challengeteams leave <teamname>`.

Teams are meant to be fixed at the start of the challenge. However, there is no restriction on players joining, leaving,
or swapping teams as the challenge progresses.

### Troubleshooting

!!!warning
Under some circumstances (messing with the configuration file manually, or having another plugin that uses scoreboard teams),
there may be situations where a player is visually part of a team that no longer exists in the context of the loaded
challenge. In such a situation you should manually reset the team scoreboards with `/scoreboard objectives setdisplay list`.
!!!