import { writeFileSync } from 'fs';

function writeJSONToFile(data, filePath) {
  writeFileSync(filePath, JSON.stringify(data, null, 2));
}

function CustomReporter(runner) {
  runner.on('end', function () {
    const tests = runner.testResults.tests;

    // Filter out failed tests
    const failedTests = tests.filter(test => test.state === 'failed');

    // Extract relevant information from failed tests
    const jsonData = failedTests.map(test => {
      return {
        title: test.title,
        error: test.err.message
      };
    });

    // Write JSON file with failed test data
    const filePath = 'cypress/artifacts/failed-tests.json';
    writeJSONToFile(jsonData, filePath);
  });
}

export default CustomReporter;