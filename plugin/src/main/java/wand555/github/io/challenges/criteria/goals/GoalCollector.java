package wand555.github.io.challenges.criteria.goals;

import org.bukkit.Keyed;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.mapping.ModelMapper;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GoalCollector<K extends Keyed> implements Storable<List<CollectableEntryConfig>> {

    private static final Logger logger = ChallengesDebugLogger.getLogger(GoalCollector.class);

    private final Context context;
    private final Map<K, Collect> toCollect;
    private Map.Entry<K, Collect> currentlyToCollect;
    private final Iterator<Map.Entry<K, Collect>> iterator;
    private final boolean fixedOrder;

    public GoalCollector(Context context, List<CollectableEntryConfig> collectables, Class<K> enumType, boolean fixedOrder, boolean shuffled) {
        this.context = context;
        if(collectables.size() == 1) {
            logger.info("Detected that goal using %s as keys has only one collectable. Ignoring actual 'fixedOrder' value and setting it to true.".formatted(enumType.getSimpleName()));
            this.fixedOrder = true;
        }
        else {
            this.fixedOrder = fixedOrder;
        }
        if(fixedOrder && !shuffled) {
            logger.fine("Shuffling collectables.");
            Collections.shuffle(collectables);
        }
        this.toCollect = ModelMapper.str2Collectable(collectables, context.dataSourceContext(), enumType);
        this.iterator =  this.fixedOrder ? this.toCollect.entrySet().iterator() : null;
        this.currentlyToCollect =  this.fixedOrder ? (iterator.hasNext() ? iterator.next() : null) : null;

    }

    public boolean hasNext() {
        return fixedOrder && iterator.hasNext();
    }

    public Map.Entry<K, Collect> next() {
        currentlyToCollect = iterator.next();
        return currentlyToCollect;
    }

    @Nullable
    public Map.Entry<K, Collect> getCurrentlyToCollect() {
        return currentlyToCollect;
    }

    /**
     * Iterates all the collectables and
     *
     * @return the current status (e.g. "3 out of 5 completed")
     */
    @NotNull
    public GoalCollector.CollectionStatus getTotalCollectionStatus() {
        int stillToComplete = (int) getToCollect().values().stream().filter(Predicate.not(Collect::isComplete)).count();
        return new CollectionStatus(getToCollect().size() - stillToComplete, getToCollect().size());
    }

    public Map<K, Collect> getToCollect() {
        return toCollect;
    }

    public boolean isComplete() {
        return getToCollect().values().stream().allMatch(Collect::isComplete);
    }

    @Override
    public List<CollectableEntryConfig> toGeneratedJSONClass() {
        return toCollect.entrySet().stream().map(collectEntryFromMap -> {
            Collect collect = collectEntryFromMap.getValue();
            return new CollectableEntryConfig(
                    collect.toGeneratedJSONClass(),
                    DataSourceJSON.toCode(collectEntryFromMap.getKey())
            );
        }).toList();
    }

    public boolean isFixedOrder() {
        return fixedOrder;
    }

    public record CollectionStatus(int collectablesStillToComplete, int allCollectables) {}
}
