package monster_data;

import object_data.ObjectDropRequest;

import java.util.Optional;

public final class LootDropResult {
    private final double roll;
    private final ObjectDropRequest dropRequest;

    LootDropResult(double roll, ObjectDropRequest dropRequest) {
        this.roll = roll;
        this.dropRequest = dropRequest;
    }

    public double roll() {
        return roll;
    }

    public Optional<ObjectDropRequest> dropRequest() {
        return Optional.ofNullable(dropRequest);
    }

    public boolean dropped() {
        return dropRequest != null;
    }
}
