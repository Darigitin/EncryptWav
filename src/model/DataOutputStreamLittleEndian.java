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

import java.io.DataOutputStream;
import java.io.IOException;

class DataOutputStreamLittleEndian
{
    private final DataOutputStream systemStream;

    DataOutputStreamLittleEndian(DataOutputStream systemStream)
    {
        this.systemStream = systemStream;
    }

    void close() throws IOException
    {
        this.systemStream.close();
    }

    void writeString(String stringToWrite) throws IOException
    {
        this.systemStream.writeBytes(stringToWrite);
    }

    void writeBytes(byte[] bytesToWrite) throws IOException
    {        
        this.systemStream.write
        (
            bytesToWrite, 0, bytesToWrite.length
        );
    }

    void writeInt(int intToWrite) throws IOException
    {
            byte[] intToWriteAsBytesLittleEndian = new byte[]
        {
            (byte)(intToWrite & 0xFF),
                (byte)((intToWrite >> 8 ) & 0xFF),
                (byte)((intToWrite >> 16) & 0xFF),
                (byte)((intToWrite >> 24) & 0xFF),
        };

        this.systemStream.write(intToWriteAsBytesLittleEndian, 0, 4);
    }

    void writeShort(short shortToWrite) throws IOException
    {
            byte[] shortToWriteAsBytesLittleEndian = new byte[]
        {
            (byte)shortToWrite,
                (byte)(shortToWrite >>> 8 & 0xFF),
        };

        this.systemStream.write(shortToWriteAsBytesLittleEndian, 0, 2);
    }    
}
