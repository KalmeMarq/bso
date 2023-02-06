using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed unsafe class BSOBinaryWriter
    {
        readonly Stream stream;
        readonly bool swapNeed;

        readonly byte[] buffer;

        public BSOBinaryWriter([NotNull] Stream input, BSOByteOrder endianess = BSOByteOrder.BigEndian)
        {
            if (input == null) throw new ArgumentNullException(nameof(input));
            if (!input.CanWrite) throw new ArgumentException("Stream is not writable", nameof(input));
            stream = input;
            swapNeed = BitConverter.IsLittleEndian == (endianess == BSOByteOrder.LittleEndian);

            buffer = new byte[256];
        }

        public void Write(byte value)
        {
            stream.WriteByte(value);
        }

        public void Write(sbyte value)
        {
            stream.WriteByte((byte)value);
        }

        public void Write(ushort value)
        {
            if (swapNeed)
            {
                buffer[0] = (byte)(value >> 8);
                buffer[1] = (byte)(value);
            }
            else
            {
                buffer[1] = (byte)(value >> 8);
                buffer[0] = (byte)(value);
            }

            stream.Write(buffer, 0, 2);
        }

        public void Write(short value)
        {
            Write((ushort)value);
        }

        public void Write(uint value)
        {
            if (swapNeed)
            {
                buffer[0] = (byte)(value >> 24);
                buffer[1] = (byte)(value >> 16);
                buffer[2] = (byte)(value >> 8);
                buffer[3] = (byte)value;
            }
            else
            {
                buffer[3] = (byte)(value >> 24);
                buffer[2] = (byte)(value >> 16);
                buffer[1] = (byte)(value >> 8);
                buffer[0] = (byte)value;
            }

            stream.Write(buffer, 0, 4);
        }

        public void Write(int value)
        {
            Write((uint)value);
        }

        public void Write(ulong value)
        {
            if (swapNeed)
            {
                buffer[0] = (byte)(value >> 56);
                buffer[1] = (byte)(value >> 48);
                buffer[2] = (byte)(value >> 40);
                buffer[3] = (byte)(value >> 32);
                buffer[4] = (byte)(value >> 24);
                buffer[5] = (byte)(value >> 16);
                buffer[6] = (byte)(value >> 8);
                buffer[7] = (byte)value;
            }
            else
            {
                buffer[7] = (byte)(value >> 56);
                buffer[6] = (byte)(value >> 48);
                buffer[5] = (byte)(value >> 40);
                buffer[4] = (byte)(value >> 32);
                buffer[3] = (byte)(value >> 24);
                buffer[2] = (byte)(value >> 16);
                buffer[1] = (byte)(value >> 8);
                buffer[0] = (byte)value;
            }

            stream.Write(buffer, 0, 8);
        }

        public void Write(long value)
        {
            Write((ulong)value);
        }

        public void Write(float value)
        {
            ulong vl = *(uint*)&value;

            if (swapNeed)
            {
                buffer[0] = (byte)(vl >> 24);
                buffer[1] = (byte)(vl >> 16);
                buffer[2] = (byte)(vl >> 8);
                buffer[3] = (byte)vl;
            }
            else
            {
                buffer[3] = (byte)(vl >> 24);
                buffer[2] = (byte)(vl >> 16);
                buffer[1] = (byte)(vl >> 8);
                buffer[0] = (byte)vl;
            }

            stream.Write(buffer, 0, 4);
        }
        
        public void Write(double value)
        {
            ulong vl = *(ulong*)&value;

            if (swapNeed)
            {
                buffer[0] = (byte)(vl >> 56);
                buffer[1] = (byte)(vl >> 48);
                buffer[2] = (byte)(vl >> 40);
                buffer[3] = (byte)(vl >> 32);
                buffer[4] = (byte)(vl >> 24);
                buffer[5] = (byte)(vl >> 16);
                buffer[6] = (byte)(vl >> 8);
                buffer[7] = (byte)vl;
            }
            else
            {
                buffer[7] = (byte)(vl >> 56);
                buffer[6] = (byte)(vl >> 48);
                buffer[5] = (byte)(vl >> 40);
                buffer[4] = (byte)(vl >> 32);
                buffer[3] = (byte)(vl >> 24);
                buffer[2] = (byte)(vl >> 16);
                buffer[1] = (byte)(vl >> 8);
                buffer[0] = (byte)vl;
            }

            stream.Write(buffer, 0, 8);
        }

        public void Write([NotNull] string value)
        {
            if (value == null) throw new ArgumentNullException(nameof(value));

            int len = Encoding.UTF8.GetByteCount(value);

            if (len > 65535)
            {
            }
        }
    }

    public enum BSOByteOrder : byte
    {
        BigEndian,
        LittleEndian
    }
}
