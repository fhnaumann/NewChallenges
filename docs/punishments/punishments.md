# Punishments

Punishments are events that are triggered when a rule is violated. They can be set either locally on a rule-per-rule basis, or globally and then be triggered when any rule is violated. However, local and global rules cannot be mixed. If a global punishment is selected, then no local punishments can be selected. Some punishments have additional configuration options. The next pages will go over each punishment and its configuration options.

## Common Configuration

Every punishment can decide who is affected by it.

[!badge Affected: All]
:   Every player is affected.

[!badge Affected: Causer]
:   Only the player who triggered the punishment (= who violated a rule) is affected.