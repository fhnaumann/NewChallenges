package wand555.github.io.challenges.mapping;

import wand555.github.io.challenges.Context;

import java.util.List;

public interface MapPart<T,C> {

    public List<T> mapPart(Context context, C configPart);
}
