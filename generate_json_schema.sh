#!/bin/bash
schema_file_name="challenges_schema.json"

echo "Typescript -> JSON Schema..."
cd criteria-interfaces
# typescript-json-schema "tsconfig.json" "*" > src/assets/$schema_file_name --noExtraProps --strictNullChecks --required --defaultNumberType=integer --propOrder
typescript-json-schema src/model.ts Model > src/assets/$schema_file_name --noExtraProps --strictNullChecks --required --defaultNumberType=integer --propOrder --include "src/**/*.ts"
echo "Typescript -> JSON Schema DONE!"
echo "Building criteria-interfaces NPM package"
npm run build
cd ..
echo "Copying into builder_website..."
cp criteria-interfaces/src/assets/$schema_file_name builder_website/src/assets
echo "Copying into live_website..."
cp criteria-interfaces/src/assets/$schema_file_name live_website/src/assets
echo "Copying into maven resource folder..."
cp criteria-interfaces/src/assets/$schema_file_name plugin/src/main/resources
echo "JSON Schema -> Java classes..."
cd plugin
mvn clean jsonschema2pojo:generate -f pom.xml
echo "JSON Schema -> Java classes DONE!"