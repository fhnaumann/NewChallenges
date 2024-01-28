<schema xmlns="http://purl.oclc.org/dsdl/schematron" queryBinding="xpath">
  <pattern>
    <!--rule context="root/rules/enabledRules/noDeath">
      <assert test="not(exists(punishments/deathPunishment))">
        NoDeath rule cannnot contain the Death punishment.
      </assert>
    </rule-->
    <!--rule context="root/rules">
      <assert test="not(exists(enabledRules/noDeath) and exists(enabledGlobalPunishments/deathPunishment))">
        Global Death punishment and NoDeath rule cannot be both active.
      </assert>
    </rule-->
  </pattern>
</schema>