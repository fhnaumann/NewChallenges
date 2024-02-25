package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.ModelMapper;

import javax.annotation.Nullable;
import java.util.*;

public class GoalCollector<T extends Enum<T>> implements Storable<List<CollectableEntryConfig>> {

    private final Context context;
    private final Map<T, Collect> toCollect;
    private Map.Entry<T, Collect> currentlyToCollect;
    private final Iterator<Map.Entry<T, Collect>> iterator;

    public GoalCollector(Context context, List<CollectableEntryConfig> collectables, Class<T> enumType) {
        this.context = context;
        this.toCollect = ModelMapper.str2Collectable(collectables, enumType);
        this.iterator = this.toCollect.entrySet().iterator();
        this.currentlyToCollect = iterator.next();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Map.Entry<T, Collect> next() {
        currentlyToCollect = iterator.next();
        return currentlyToCollect;
    }

    @Nullable
    public Map.Entry<T, Collect> getCurrentlyToCollect() {
        return currentlyToCollect;
    }

    public Map<T, Collect> getToCollect() {
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
                    new CollectableDataConfig(collect.getAmountNeeded(), collect.getCurrentAmount()),
                    ModelMapper.enum2Code(context.dataSourceContext().materialJSONList(), collectEntryFromMap.getKey())
            );
        }).toList();
    }
}
