package net.openhft.chronicle.map;

/**
 * Created by tolga on 05/02/16.
 */
public interface ChronicleMapAccessor {

    long findMaxTimeStamp(ChronicleMap map);
}
