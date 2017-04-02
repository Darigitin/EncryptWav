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

/*

  @author Matt
 */
import java.io.*;

public class TriviumModel
{
    static final int BitsPerByte = 8;
    private static final int NumberOfBytesInRiffWaveAndFormatChunks = 36;

    public String filePath;
    private static boolean[] key, IV;
    private SamplingInfo samplingInfo;
    private Sample[][] samplesForChannels;

    public TriviumModel
            (
                    String filePath,
                    SamplingInfo samplingInfo,
                    Sample[][] samplesForChannels,
                    boolean[] key1,
                    boolean[] key2
            )
    {
        this.filePath = filePath;
        this.samplingInfo = samplingInfo;
        this.samplesForChannels = samplesForChannels;
        key = key1;
        IV = key2;
        readFromFilePath();
    }

    private void readFromFilePath()
    {


        try
        {            
            DataInputStream dataInputStream = new DataInputStream
            (
                new BufferedInputStream
                (
                    new FileInputStream(filePath)
                )
            );

            DataInputStreamLittleEndian reader;
            reader = new DataInputStreamLittleEndian
            (
                dataInputStream
            );

        readFromFilePath_ReadChunks(reader);

            reader.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }            

    }

    private void readFromFilePath_ReadChunks(DataInputStreamLittleEndian reader) 
        throws IOException
    {
        byte[] riff = new byte[4];
        reader.read(riff);

        reader.readInt();

        byte[] wave = new byte[4];
        reader.read(wave);

        this.readFromFilePath_ReadChunks_Format(reader);
        this.readFromFilePath_ReadChunks_Data(reader);
    }

    private void readFromFilePath_ReadChunks_Format(DataInputStreamLittleEndian reader)
        throws IOException
    {
        byte[] fmt = new byte[4];
        reader.read(fmt);
        int chunkSizeInBytes = reader.readInt();
        Short formatCode = reader.readShort();

        Short numberOfChannels = reader.readShort();
        int samplesPerSecond = reader.readInt();
        reader.readInt();
        reader.readShort();
        Short bitsPerSample = reader.readShort();

        this.samplingInfo = new SamplingInfo
        (
            "[from file]",
            chunkSizeInBytes,
            formatCode,
            numberOfChannels,
            samplesPerSecond,
            bitsPerSample
        );
    }
    private void readFromFilePath_ReadChunks_Data(DataInputStreamLittleEndian reader)
        throws IOException
    {
        byte[] data = new byte[4];
        reader.read(data);
        int subChunkToSize = reader.readInt();
        byte[] samplesForChannelsMixedAsBytes = new byte[subChunkToSize];
        reader.read(samplesForChannelsMixedAsBytes );
        this.samplesForChannels = Sample.buildManyFromBytes
        (
            samplingInfo,
            samplesForChannelsMixedAsBytes
        );
    }
    public void writeToFilePath()
    {
        try
        {
            DataOutputStream dataOutputStream = new DataOutputStream
            (
                new BufferedOutputStream
                (
                    new FileOutputStream
                    (
                        this.filePath
                    )
                )
            );
            DataOutputStreamLittleEndian writer;
            writer = new DataOutputStreamLittleEndian
            (
                dataOutputStream
            );            
            this.writeToFilePath_WriteChunks(writer);
            writer.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void writeToFilePath_WriteChunks(DataOutputStreamLittleEndian writer) 
        throws IOException
    {
        int numberOfBytesInSamples = this.samplesForChannels[0].length
        * this.samplingInfo.numberOfChannels
        * this.samplingInfo.bitsPerSample
        / TriviumModel.BitsPerByte;
        writer.writeString("RIFF");
        writer.writeInt
        (
                numberOfBytesInSamples
                + TriviumModel.NumberOfBytesInRiffWaveAndFormatChunks
        );
        writer.writeString("WAVE");
        this.writeToFilePath_WriteChunks_Format(writer);
        this.writeToFilePath_WriteChunks_Data(writer);
    }    
    private void writeToFilePath_WriteChunks_Format(DataOutputStreamLittleEndian writer) 
        throws IOException
    {
        writer.writeString("fmt ");
        writer.writeInt(this.samplingInfo.chunkSizeInBytes);
        writer.writeShort(this.samplingInfo.formatCode);
        writer.writeShort(this.samplingInfo.numberOfChannels);
        writer.writeInt(this.samplingInfo.samplesPerSecond);
        writer.writeInt(this.samplingInfo.bytesPerSecond());
        writer.writeShort
        (
            (short)
            (
                this.samplingInfo.numberOfChannels
                * this.samplingInfo.bitsPerSample
                / TriviumModel.BitsPerByte
            )
        );
        writer.writeShort(this.samplingInfo.bitsPerSample);
    }    
    private void writeToFilePath_WriteChunks_Data(DataOutputStreamLittleEndian writer) 
        throws IOException
    {
        writer.writeString("data");
        int numberOfBytesInSamples = this.samplesForChannels[0].length
        * this.samplingInfo.numberOfChannels
        * this.samplingInfo.bitsPerSample
        / TriviumModel.BitsPerByte;
        writer.writeInt(numberOfBytesInSamples);
        byte[] samplesAsBytes = Sample.convertManyToBytes
        (
            this.samplesForChannels,
            this.samplingInfo
        );    
        writer.writeBytes(samplesAsBytes);
    }
    // inner classes
    public static abstract class Sample
    {
        public abstract Sample buildFromBytes(byte[] valueAsBytes);

        public abstract byte[] convertToBytes();

        static Sample[][] buildManyFromBytes
                (
                        SamplingInfo samplingInfo,
                        byte[] bytesToConvert
                )
        {
            int numberOfBytes = bytesToConvert.length;
            int numberOfChannels = samplingInfo.numberOfChannels;
            Sample[][] returnSamples = new Sample[numberOfChannels][];
            int bytesPerSample = samplingInfo.bitsPerSample / TriviumModel.BitsPerByte;
            int samplesPerChannel =
                numberOfBytes
                / bytesPerSample
                / numberOfChannels;
            for (int c = 0; c < numberOfChannels; c++)
            {
                returnSamples[c] = new Sample[samplesPerChannel];
            }
            int b = 0;
          //  Math.pow
         //           (
         //                   2, TriviumModel.BitsPerByte * bytesPerSample - 1
         //           );
            Sample samplePrototype = samplingInfo.samplePrototype();
            byte[] sampleValueAsBytes = new byte[bytesPerSample];
            for (int s = 0; s < samplesPerChannel; s++)
            {                
                for (int c = 0; c < numberOfChannels; c++)
                {
                    for (int i = 0; i < bytesPerSample; i++)
                    {
                        sampleValueAsBytes[i] = bytesToConvert[b];
                        b++;
                    }
                    returnSamples[c][s] = samplePrototype.buildFromBytes
                    (
                        sampleValueAsBytes
                    );
                }
            }
    
            return returnSamples;
        }

        static byte[] convertManyToBytes
                (
                        Sample[][] samplesToConvert,
                        SamplingInfo samplingInfo
                )
        {
            byte[] returnBytes;
    
            int numberOfChannels = samplingInfo.numberOfChannels;
            int samplesPerChannel = samplesToConvert[0].length;
    
            int bitsPerSample = samplingInfo.bitsPerSample;
            int bytesPerSample = bitsPerSample / TriviumModel.BitsPerByte;
            int numberOfBytes =
                numberOfChannels
                * samplesPerChannel
                * bytesPerSample;
            returnBytes = new byte[numberOfBytes];
            Trivium tri = new Trivium(key, IV);

          //  Math.pow
       //             (
      //                      2, TriviumModel.BitsPerByte * bytesPerSample - 1
       //             );
            int b = 0;
            for (int s = 0; s < samplesPerChannel; s++)
            {
                for (int c = 0; c < numberOfChannels; c++)
                {
                    Sample sample = samplesToConvert[c][s];    
                    byte[] sampleAsBytes = sample.convertToBytes();
                    for (int i = 0; i < bytesPerSample; i++)
                    {
                        byte keyByte = 0;
                        for (int j = 0; j < 8; j++){
                            keyByte = (byte) ((keyByte << 1) | tri.getBit());
                        }
                        returnBytes[b] = (byte) (sampleAsBytes[i] ^ keyByte);
                        b++;
                    }
                }                        
            }
    
            return returnBytes;
        }
    }
    public static class Sample16 extends Sample
    {
        short value;
    
        Sample16(short value)
        {
            this.value = value;
        }
        // Sample members
        public Sample buildFromBytes(byte[] valueAsBytes)
        {
            short valueAsShort = (short)
            (
                ((valueAsBytes[0] & 0xFF))
                | (short)((valueAsBytes[1] & 0xFF) << 8 )
            );
            return new Sample16(valueAsShort);
        }

        public byte[] convertToBytes()
        {
            return new byte[]
            {
                (byte)((this.value) & 0xFF),
                (byte)((this.value >>> 8 ) & 0xFF),
            };
        }

    }
    public static class Sample24 extends Sample
    {
        int value;
        Sample24(int value)
        {
            this.value = value;
        }
        // Sample members
        public Sample buildFromBytes(byte[] valueAsBytes)
        {
            short valueAsShort = (short)
            (
                ((valueAsBytes[0] & 0xFF))
                | ((valueAsBytes[1] & 0xFF) << 8 )
                | ((valueAsBytes[2] & 0xFF) << 16)
            );
            return new Sample24(valueAsShort);
        }

        public byte[] convertToBytes()
        {
            return new byte[]
            {
                (byte)((this.value) & 0xFF),
                (byte)((this.value >>> 8 ) & 0xFF),
                (byte)((this.value >>> 16) & 0xFF),
            };
        }
    }
    public static class Sample32 extends Sample
    {
        int value;
        Sample32(int value)
        {
            this.value = value;
        }

        public Sample buildFromBytes(byte[] valueAsBytes)
        {
            short valueAsShort = (short)
            (
                ((valueAsBytes[0] & 0xFF))
                | ((valueAsBytes[1] & 0xFF) << 8 )
                | ((valueAsBytes[2] & 0xFF) << 16)
                | ((valueAsBytes[3] & 0xFF) << 24)
            );
    
            return new Sample32(valueAsShort);
        }

        public byte[] convertToBytes()
        {
            return new byte[]
            {
                (byte)((this.value) & 0xFF),
                (byte)((this.value >>> 8 ) & 0xFF),
                (byte)((this.value >>> 16) & 0xFF),
                (byte)((this.value >>> 24) & 0xFF),
            };
        }
    }
    
    public static class SamplingInfo
    {
        String name;
        int chunkSizeInBytes;
        short formatCode;
        short numberOfChannels;
        int samplesPerSecond;
        private short bitsPerSample;
        SamplingInfo
                (
                        String name,
                        int chunkSizeInBytes,
                        short formatCode,
                        short numberOfChannels,
                        int samplesPerSecond,
                        short bitsPerSample
                )
        {
            this.name = name;
            this.chunkSizeInBytes = chunkSizeInBytes;
            this.formatCode = formatCode;
            this.numberOfChannels = numberOfChannels;
            this.samplesPerSecond = samplesPerSecond;
            this.bitsPerSample = bitsPerSample;
        }

        int bytesPerSecond()
        {    
            return this.samplesPerSecond
                * this.numberOfChannels
                * this.bitsPerSample / TriviumModel.BitsPerByte;
        }
        Sample samplePrototype()
        {
            Sample returnValue = null;
    
            if (this.bitsPerSample == 16)
            {
                returnValue = new TriviumModel.Sample16((short)0);
            }
            else if (this.bitsPerSample == 24)
            {
                returnValue = new TriviumModel.Sample24(0);
            }
            else if (this.bitsPerSample == 32)
            {
                returnValue = new TriviumModel.Sample32(0);
            }
    
            return returnValue;
        }
        public String toString()
        {

            return "<SamplingInfo "
            + "chunkSizeInBytes='" + this.chunkSizeInBytes + "' "
            + "formatCode='" + this.formatCode + "' "
            + "numberOfChannels='" + this.numberOfChannels + "' "
            + "samplesPerSecond='" + this.samplesPerSecond + "' "
            + "bitsPerSample='" + this.bitsPerSample + "' "
            + "/>";
        }        
    }
}
