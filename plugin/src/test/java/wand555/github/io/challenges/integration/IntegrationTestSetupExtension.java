package wand555.github.io.challenges.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class IntegrationTestSetupExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("Warning: Tests in this suite access real AWS instances.");
        if(!System.getProperty("stage", "").equalsIgnoreCase("testing")) {
            Assertions.fail("Stage variable not set to 'testing' when running the test suite. Aborting!");
        }
        DynamoDBHelper.clearDynamoDBTestingTable();

        // TODO: clear s3

        DynamoDBHelper.setUpEmptyDynamoDBTestingTable();
    }
}
