package object_data;

import java.util.Optional;

public interface TeleportTarget {
    void setTeleportDestination(TeleportDestination destination);

    Optional<TeleportDestination> teleportDestination();
}
