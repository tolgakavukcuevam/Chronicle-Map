package net.openhft.chronicle.map;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by tolga on 05/02/16.
 */
public class ReplicatedChronicleMapAccessor implements ChronicleMapAccessor{

    @Override
    public long findMaxTimeStamp(ChronicleMap map){
        ReplicatedChronicleMap replicatedMap = (ReplicatedChronicleMap) map;
        Iterator<Map.Entry> mapIterator = replicatedMap.entrySet().iterator();

        long maxTimestamp = Long.MIN_VALUE;
        while (mapIterator.hasNext()){
            ReplicatedChronicleMap.TimestampTrackingEntry trackingEntry = (ReplicatedChronicleMap.TimestampTrackingEntry) mapIterator.next();
            if(trackingEntry.timestamp > maxTimestamp){
                maxTimestamp = trackingEntry.timestamp;
            }
        }
        return maxTimestamp;
    }
}
