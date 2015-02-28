/*
 * Copyright 2014 Higher Frequency Trading http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.map;

import net.openhft.chronicle.hash.impl.util.Objects;
import net.openhft.lang.io.Bytes;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Set;

public class BytesChronicleMap implements AbstractChronicleMap<Bytes, Bytes> {

    private final VanillaChronicleMap<?, ?, ?, ?, ?, ?> delegate;
    TcpReplicator.TcpSocketChannelEntryWriter output;

    public BytesChronicleMap(VanillaChronicleMap<?, ?, ?, ?, ?, ?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void putDefaultValue(VanillaContext context) {
        delegate.putDefaultValue(context);
    }

    @Override
    public int actualSegments() {
        return delegate.actualSegments();
    }

    @Override
    public VanillaContext<Bytes, ?, ?, Bytes, ?, ?> mapContext() {
        VanillaContext context = delegate.bytesMapContext();
        context.output = output;
        return context;
    }

    @Override
    public File file() {
        return delegate.file();
    }

    @Override
    public long longSize() {
        return delegate.longSize();
    }

    @Override
    public VanillaContext<Bytes, ?, ?, Bytes, ?, ?> context(Bytes key) {
        VanillaContext context = delegate.bytesMapContext();
        context.output = output;
        context.initKey(key);
        return context;
    }

    @Override
    public void checkValue(Bytes value) {
        Objects.requireNonNull(value);
    }

    @Override
    public Class<Bytes> keyClass() {
        return Bytes.class;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @NotNull
    @Override
    public MapKeyContext<Bytes, Bytes> acquireContext(
            @NotNull Bytes key, @NotNull Bytes usingValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bytes newValueInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bytes newKeyInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<Bytes> valueClass() {
        return Bytes.class;
    }

    final void putAll(Bytes entries) {
        long numberOfEntries = entries.readStopBit();
        long entryPosition = entries.position();
        while (numberOfEntries-- > 0) {
            long keySize = delegate.keySizeMarshaller.readSize(entries);
            entries.skip(keySize);
            long valueSize = delegate.valueSizeMarshaller.readSize(entries);
            long nextEntryPosition = entries.position() + valueSize;
            entries.position(entryPosition);
            put(entries, entries);
            entries.clear(); // because used as key, altering position and limit
            entryPosition = nextEntryPosition;
            entries.position(entryPosition);
        }
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @NotNull
    @Override
    public Set<Entry<Bytes, Bytes>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public Bytes put(Bytes key, Bytes value, long timestamp, byte id) {
        // todo
        try (ReplicatedChronicleMap.BytesReplicatedContext c = (ReplicatedChronicleMap.BytesReplicatedContext) context(key)) {
            // We cannot read the previous value using just a read lock, because then we will need
            // to release the read lock -> acquire write lock, the value might be updated in
            // between, that will break ConcurrentMap.put() atomicity guarantee. So, we acquire
            // update lock from the start:
            c.updateLock().lock();
            Bytes prevValue = prevValueOnPut(c);
            c.newTimestamp  = timestamp;
            c.newIdentifier = id;
            c.writeReplicationBytes();
            c.put(value);
            return prevValue;
        }
    }
}
