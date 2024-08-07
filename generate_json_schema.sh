#!/bin/bash
schema_file_name="challenges_schema.json"

echo "Typescript -> JSON Schema..."
cd builder_website
echo "$PWD"
typescript-json-schema src/models/model.ts Model > src/assets/$schema_file_name --noExtraProps --strictNullChecks --required --defaultNumberType=integer --propOrder
cd ..
echo "Typescript -> JSON Schema DONE!"
echo "Copying into maven resource folder..."
cp builder_website/src/assets/$schema_file_name plugin/src/main/resources
echo "Copying into maven resource folder DONE!"
echo "JSON Schema -> Java classes..."
cd plugin
mvn clean jsonschema2pojo:generate -f pom.xml
echo "JSON Schema -> Java classes DONE!"