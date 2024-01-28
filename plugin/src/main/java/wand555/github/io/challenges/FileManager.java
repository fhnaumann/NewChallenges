package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import wand555.github.io.challenges.generated.*;

import java.io.*;

public class FileManager {

    public static void writeToFile(ChallengeManager challengeManager, Writer writer) {
        EnabledRules enabledRulesConfig = new EnabledRules();
        challengeManager.getRules().forEach(rule -> rule.addToGeneratedConfig(enabledRulesConfig));
        RulesConfig rulesConfig = new RulesConfig(new PunishmentsConfig(), enabledRulesConfig);

        GoalsConfig goalsConfig = new GoalsConfig();
        challengeManager.getGoals().forEach(goal -> goal.addToGeneratedConfig(goalsConfig));


        TestOutputSchema testOutputSchema = new TestOutputSchema(goalsConfig, rulesConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(writer, testOutputSchema);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TestOutputSchema readFromFile(Reader reader) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(reader, TestOutputSchema.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
