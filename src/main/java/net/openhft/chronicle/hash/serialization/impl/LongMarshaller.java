/*
 *      Copyright (C) 2015  higherfrequencytrading.com
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.openhft.chronicle.hash.serialization.impl;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.serialization.BytesWriter;
import net.openhft.chronicle.hash.serialization.SizedReader;
import net.openhft.chronicle.hash.serialization.SizedWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LongMarshaller
        implements SizedReader<Long>, BytesReader<Long>,
        SizedWriter<Long>, BytesWriter<Long>, EnumMarshallable<LongMarshaller> {
    INSTANCE;

    @Override
    public long size(@NotNull Long toWrite) {
        return 8L;
    }

    @NotNull
    @Override
    public Long read(@NotNull Bytes in, long size, @Nullable Long using) {
        return in.readLong();
    }

    @Override
    public void write(@NotNull Bytes out, long size, @NotNull Long toWrite) {
        out.writeLong(toWrite);
    }

    @NotNull
    @Override
    public Long read(Bytes in, @Nullable Long using) {
        return in.readLong();
    }

    @Override
    public void write(Bytes out, @NotNull Long toWrite) {
        out.writeLong(toWrite);
    }

    @Override
    public LongMarshaller readResolve() {
        return INSTANCE;
    }
}
