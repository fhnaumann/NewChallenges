# 🚧 Under construction 🚧

### Here are the links to the hosted websites/plugin that are in this repository:

⛏️ [Plugin](https://hangar.papermc.io/wand555/Challenges) ([Legacy Plugin](https://www.spigotmc.org/resources/challenges-plugin.76945/))

🪄 [Builder](https://www.mc-challenges.com)

📖 [Documentation](https://www.wiki.mc-challenges.com/)


## Testing

### Unit Test: Challenges Plugin

Verify correct behaviour inside the Challenges Plugin. JUnit is the test suite and MockBukkit is used to provide a mock Minecraft Server. External dependencies like sending data to AWS is mocked.

### "Unit" Test: Builder Frontend

Verify correct behaviour inside the Builder Frontend. Cypress is used.

### "Unit" Test: Live Frontend

Verify correct behaviour inside the Live Frontend. Cypress is used and all the data is mocked. No request to AWS is made.

### Unit Test: AWS Lambdas

Verify correct behaviour of the Lambda Handlers. Moto is used to mock the AWS resources.

### Integration Test: Challenges Plugin -> AWS

Verify the test data on AWS (S3, DynamoDB) that was added by the 
plugin during a test run. More precisely this Integration Test verifies correct behaviour between 
the challenge plugin, the AWS gateway, the AWS Lambdas, and the data stored on AWS.
The test data that was stored is explicitly not cleared after, because it is used in following Integration Tests.

### Integration Test: AWS -> Live Frontend

Reuse the test data from the previous Integration Test. This test verifies that the data from AWS is correctly displayed.
The test data is now cleared.

### E2E Test

The two Integration Tests form E2E tests.