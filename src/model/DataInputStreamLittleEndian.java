package model;/*
 * Copyright (C) 2015 Matt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Matt
 */

import java.io.DataInputStream;
import java.io.IOException;

class DataInputStreamLittleEndian
{
    private final DataInputStream systemStream;

    DataInputStreamLittleEndian(DataInputStream systemStream)
    {
        this.systemStream = systemStream;
    }

    void close() throws IOException
    {
        this.systemStream.close();
    }

    void read(byte[] byteBufferToReadInto) throws IOException
    {
        // no need to translate to little-endian here

        this.systemStream.read(byteBufferToReadInto);
    }

    int readInt() throws IOException
    {
        byte[] bytesLittleEndian = new byte[4];
        this.systemStream.read(bytesLittleEndian);

        long returnValueAsLong =
        (
            (bytesLittleEndian[0] & 0xFF)
            | ((bytesLittleEndian[1] & 0xFF) << 8 )
            | ((bytesLittleEndian[2] & 0xFF) << 16)
            | ((bytesLittleEndian[3] & 0xFF) << 24)
        );

        return (int)returnValueAsLong;
    }

    short readShort() throws IOException
    {
        byte[] bytesLittleEndian = new byte[2];
        this.systemStream.read(bytesLittleEndian);

        int returnValueAsInt =
        (
            (bytesLittleEndian[0] & 0xFF)
            | ((bytesLittleEndian[1] & 0xFF) << 8 )
        );

        return (short)returnValueAsInt;
    }
}
