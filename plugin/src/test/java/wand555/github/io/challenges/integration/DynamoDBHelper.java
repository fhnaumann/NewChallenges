package wand555.github.io.challenges.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import wand555.github.io.challenges.generated.MCEventAlias;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DynamoDBHelper {

    private static final String TABLE_NAME = "challenge-events-testing";

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                                                                       .credentialsProvider(DefaultCredentialsProvider.create())
                                                                       .endpointOverride(URI.create(
                                                                               "https://dynamodb.eu-central-1.amazonaws.com"))
                                                                       .region(Region.EU_CENTRAL_1)
                                                                       .build();

    public static <T> T queryItems(String challengeID, String eventID, int timestamp, Class<T> dataConfigClass) throws JsonProcessingException {

        String timestampEventID = STR."\{timestamp}#\{eventID}";
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("challenge_ID", AttributeValue.builder().s(challengeID).build());
        key.put("timestamp#eventID", AttributeValue.builder().s(timestampEventID).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                                                      .tableName(TABLE_NAME)
                                                      .key(key)
                                                      // .consistentRead(true) <- costs twice as much
                                                      .build();

        GetItemResponse response = dynamoDbClient.getItem(getItemRequest);
        if(!response.hasItem()) {
            fail(STR."No item in DynamoDB with challengeID=\{challengeID}, eventID=\{eventID}, timestamp=\{timestamp}");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String asJSON = EnhancedDocument.fromAttributeValueMap(response.item()).toJson();
        System.out.println(asJSON);
        JsonNode dataNode = objectMapper.readTree(asJSON).get("data");
        return objectMapper.treeToValue(dataNode, dataConfigClass);
    }

    public static void setUpEmptyDynamoDBTestingTable() {
        try {
            CreateTableRequest createTableRequest = CreateTableRequest
                    .builder()
                    .tableName(TABLE_NAME)
                    .keySchema(
                            KeySchemaElement.builder()
                                            .attributeName("challenge_ID")
                                            .keyType(KeyType.HASH)
                                            .build(),
                            KeySchemaElement.builder()
                                            .attributeName("timestamp#eventID")
                                            .keyType(KeyType.RANGE)
                                            .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                               .attributeName("challenge_ID")
                                               .attributeType(ScalarAttributeType.S)
                                               .build(),
                            AttributeDefinition.builder()
                                               .attributeName("timestamp#eventID")
                                               .attributeType(ScalarAttributeType.S)
                                               .build()
                    ).billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
            dynamoDbClient.createTable(createTableRequest);
            dynamoDbClient.waiter().waitUntilTableExists(builder -> builder.tableName(TABLE_NAME));
            System.out.println("Created testing table.");
        } catch(Exception e) {
            fail("Error creating DynamoDB table", e);
        }
    }

    public static void clearDynamoDBTestingTable() {
        try {
            DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                                                                      .tableName(TABLE_NAME)
                                                                      .build();
            dynamoDbClient.deleteTable(deleteTableRequest);
            dynamoDbClient.waiter().waitUntilTableNotExists(builder -> builder.tableName(TABLE_NAME));
            System.out.println("Deleted testing table.");
        } catch(Exception e) {
            if(e instanceof ResourceNotFoundException) {
                System.out.println("Table not found. Assuming it was manually deleted.");
                return;
            }
            fail("Error deleting DynamoDB table.", e);
        }
    }
}
