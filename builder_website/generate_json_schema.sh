#!/bin/bash
echo Typescript -> JSON Schema...
typescript-json-schema src/components/model/model.ts Model > "test-output-schema2.json" --noExtraProps --strictNullChecks --required --defaultProps
echo Typescript -> JSON Schema DONE!
echo Copying into maven resource folder...
cp test-output-schema2.json ../Challenges/src/main/resources
echo Copying into maven resource folder DONE!
echo JSON Schema -> Java classes...
cd ../Challenges
mvn jsonschema2pojo:generate -f pom.xml
echo JSON Schema -> Java classes DONE!