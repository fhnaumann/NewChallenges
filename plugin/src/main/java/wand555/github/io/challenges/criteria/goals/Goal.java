package wand555.github.io.challenges.criteria.goals;

public interface Goal {

    public boolean isComplete();

    public void setComplete(boolean complete);

    public boolean determineComplete();

    public abstract void onComplete();
}
