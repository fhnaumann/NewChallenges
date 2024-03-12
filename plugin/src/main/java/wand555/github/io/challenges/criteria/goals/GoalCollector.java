package wand555.github.io.challenges.criteria.goals;

import org.bukkit.Keyed;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.mapping.ModelMapper;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GoalCollector<K extends Keyed> implements Storable<List<CollectableEntryConfig>> {

    private final Context context;
    private final Map<K, Collect> toCollect;
    private Map.Entry<K, Collect> currentlyToCollect;
    private final Iterator<Map.Entry<K, Collect>> iterator;
    private final boolean fixedOrder;

    public GoalCollector(Context context, List<CollectableEntryConfig> collectables, Class<K> enumType, boolean fixedOrder, boolean shuffled) {
        this.context = context;
        if(fixedOrder && !shuffled) {
            Collections.shuffle(collectables);
        }
        this.toCollect = ModelMapper.str2Collectable(collectables, context.dataSourceContext(), enumType);
        this.iterator = fixedOrder ? this.toCollect.entrySet().iterator() : null;
        this.currentlyToCollect = fixedOrder ? (iterator.hasNext() ? iterator.next() : null) : null;
        this.fixedOrder = fixedOrder;
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
     * @return the current status (e.g. "3 out of 5 completed")
     */
    @NotNull
    public GoalCollector.CollectionStatus getTotalCollectionStatus() {
        int stillToComplete = (int) getToCollect().values().stream().filter(Predicate.not(Collect::isComplete)).count();
        return new CollectionStatus(stillToComplete, getToCollect().size());
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
                    ModelMapper.enum2Code(context.dataSourceContext().materialJSONList(), collectEntryFromMap.getKey())
            );
        }).toList();
    }

    public boolean isFixedOrder() {
        return fixedOrder;
    }

    public record CollectionStatus(int collectablesStillToComplete, int allCollectables) {}
}
