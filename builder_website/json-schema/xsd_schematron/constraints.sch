<schema xmlns="http://purl.oclc.org/dsdl/schematron" queryBinding="xpath">
  <pattern>
    <rule context="rules/NoDeath">
      <assert test="not(exists(punishments/Death))">
        NoDeath rule should not contain the Death punishment.
      </assert>
    </rule>
    
    <rule context="rules/RandomDrops/materialRandomizations">
      <assert test="count(randomize/@from) = count(distinct-values(randomize/@from))">
      Two entries cannot have the same key.
      </assert>
    </rule>
    <!--rule context="rules/RandomDrops/materialRandomizations" >
      <let name="uniqueFromValues" value="distinct-values(randomize/@from)"/>
      <assert test="count(randomize/@from) = count($uniqueFromValues)">
        Duplicate entries found for 'from' key: <value-of select="string-join($uniqueFromValues[. = current()/@from], ', ')".

      </assert>
    </rule-->
  </pattern>
</schema>
