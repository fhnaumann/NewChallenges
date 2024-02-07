package wand555.github.io.challenges.criteria.goals;

import java.util.Arrays;

public class NewItemCollect {

    public static NewItemCollect ALL = new NewItemCollect(0, true, false);
    public static NewItemCollect NEVER = new NewItemCollect(0, false, true);


    private final int[] slots;
    private final boolean all;
    private final boolean never;

    private NewItemCollect(int slot, boolean all, boolean never) {
        this(new int[] { slot }, all, never);
    }

    private NewItemCollect(int[] slots, boolean all, boolean never) {
        this.slots = slots;
        this.all = all;
        this.never = never;
    }

    public boolean matches(int clickedSlot) {
        if(all) {
            return true;
        }
        if(never) {
            return false;
        }
        return Arrays.stream(slots).anyMatch(slot -> slot == clickedSlot);
    }

    public static NewItemCollect slots(int slot) {
        return new NewItemCollect(slot, false, false);
    }

    public static NewItemCollect slots(int... slots) {
        if(slots == null) {
            throw new RuntimeException();
        }
        return new NewItemCollect(slots, false, false);
    }
}
