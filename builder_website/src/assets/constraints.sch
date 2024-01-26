<schema xmlns="http://purl.oclc.org/dsdl/schematron" queryBinding="xpath">
  <pattern>


    <rule context="root/rules/enabledGlobalPunishments">
      <assert test="not(exists(endPunishment))">
        End Punishment exists, but it may not (DUMMY RULE)
      </assert>
    </rule>
    <rule context="root/goals/mobGoal/mobs">
      <assert test="not(exists(WITHER))">
        Wither may not be selected in MobGoal (DUMMY RULE)
      </assert>
    </rule>
    <!--rule context="root">
      <assert test="false">
      test
      </assert>
    </rule-->
    <!--rule context="root/rules">
      <assert test="exists(NoDamage)">
        NoDamage does not exist! (This is just a test rule, not an actual rule)
      </assert>
    </rule>


    <rule context="root/rules/NoDeath">
      <assert test="not(exists(punishments/Death))">
        NoDeath rule should not contain the Death punishment.
      </assert>
    </rule>
    
    <rule context="rules/RandomDrops/materialRandomizations">
      <assert test="count(randomize/@from) = count(distinct-values(randomize/@from))">
      Two entries cannot have the same key.
      </assert>
    </rule-->
    <!--rule context="rules/RandomDrops/materialRandomizations" >
      <let name="uniqueFromValues" value="distinct-values(randomize/@from)"/>
      <assert test="count(randomize/@from) = count($uniqueFromValues)">
        Duplicate entries found for 'from' key: <value-of select="string-join($uniqueFromValues[. = current()/@from], ', ')".

      </assert>
    </rule-->
  </pattern>
</schema>
