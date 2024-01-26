#!/bin/bash
schema_file_name="challenges_schema.json"

echo "Typescript -> JSON Schema..."
cd builder_website
typescript-json-schema src/components/model/model.ts Model > $schema_file_name --noExtraProps --strictNullChecks --required --defaultProps
cd ..
echo "Typescript -> JSON Schema DONE!"
echo "Copying into maven resource folder..."
cp builder_website/$schema_file_name plugin/src/main/resources
echo "Copying into maven resource folder DONE!"
echo "JSON Schema -> Java classes..."
cd plugin
mvn jsonschema2pojo:generate -f pom.xml
echo "JSON Schema -> Java classes DONE!"