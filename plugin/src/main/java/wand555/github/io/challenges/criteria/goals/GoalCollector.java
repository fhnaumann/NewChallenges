package wand555.github.io.challenges.criteria.goals;

import org.bukkit.Keyed;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.mapping.ModelMapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class GoalCollector<K extends Keyed> implements Storable<List<CollectableEntryConfig>> {

    private final Context context;
    private final Map<K, Collect> toCollect;
    private Map.Entry<K, Collect> currentlyToCollect;
    private final Iterator<Map.Entry<K, Collect>> iterator;

    private final Map<String, CompletionConfig> completionConfigs;

    public GoalCollector(Context context, List<CollectableEntryConfig> collectables, Class<K> enumType) {
        this.context = context;
        this.toCollect = ModelMapper.str2Collectable(collectables, context.dataSourceContext(), enumType);
        this.iterator = this.toCollect.entrySet().iterator();
        this.currentlyToCollect = iterator.hasNext() ? iterator.next() : null;
        this.completionConfigs = collectables.stream()
                .collect(Collectors.toMap(
                        CollectableEntryConfig::getCollectableName,
                        collectableEntryConfig -> collectableEntryConfig.getCompletion() != null ? collectableEntryConfig.getCompletion() : new CompletionConfig()
                        )
                );
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Map.Entry<K, Collect> next() {
        currentlyToCollect = iterator.next();
        return currentlyToCollect;
    }

    @Nullable
    public Map.Entry<K, Collect> getCurrentlyToCollect() {
        return currentlyToCollect;
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
                    new CollectableDataConfig(collect.getAmountNeeded(), collect.getCurrentAmount()),
                    ModelMapper.enum2Code(context.dataSourceContext().materialJSONList(), collectEntryFromMap.getKey()),
                    completionConfigs.get(collectEntryFromMap.getKey().key()) // TODO: this is ugly. I need to find a better way to handle completions. They should not be in this class in the first place
            );
        }).toList();
    }

    public List<CompletionConfig> getCompletionConfigs() {
        return new ArrayList<>(completionConfigs.values());
    }
}
