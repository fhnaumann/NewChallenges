package wand555.github.io.challenges.mapping;

import java.util.List;

public class DataSource<T> {

    private String version;

    private List<T> data;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
