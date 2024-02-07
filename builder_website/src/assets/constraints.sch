<schema xmlns="http://purl.oclc.org/dsdl/schematron" queryBinding="xpath">
  <pattern>
    <rule context="root/rules/enabledRules/noDeath">
      <assert test="not(punishments/deathPunishment)">
        NoDeath rule cannnot contain the Death punishment.
      </assert>
    </rule>
    <rule context="root/rules">
      <assert test="not(enabledRules/noDeath and enabledGlobalPunishments/deathPunishment)">
        Global Death punishment and NoDeath rule cannot be both active.
      </assert>
    </rule>


    <rule context="root/rules/enabledRules/*/punishments/healthPunishment">
      <report test="randomizeHeartsLost='true' and heartsLost">
        Value of 'heartsLost' will be ignored because 'randomizeHeartsLost' is set to true.
      </report>
    </rule>

    <rule context="root/goals/itemGoal">
      <assert test="items/* and (allItems='true' or allBlocks='true')">
        If 'allItems' or 'allBlocks' is selected, then no specific items may be selected.
      </assert>
    </rule>
  </pattern>
</schema>
