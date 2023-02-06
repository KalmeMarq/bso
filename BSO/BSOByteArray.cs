using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOByteArray : BSOTag
    {
        public sbyte[] Data { get; set; }

        public BSOByteArray() { Data = Array.Empty<sbyte>(); }

        public BSOByteArray(sbyte[] data)
        {
            Data = data;
        }

        public override BSOType GetTagType()
        {
            return BSOType.ByteArray;
        }

        public sbyte this[int index]
        {
            get { return Data[index]; }
            set { Add(value); }
        }

        public void Add(sbyte value)
        {
            sbyte[] data = new sbyte[Data.Length + 1];
            
            for (int i = 0; i < Data.Length; i++)
            {
                data[i] = Data[i];
            }

            data[Data.Length] = value;

            Data = data;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) bw.Write((byte)Data.Length);
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) bw.Write((ushort)Data.Length);
            else bw.Write(Data.Length);

            for (int i = 0; i < Data.Length; i++)
            {
                bw.Write(Data[i]);
            }
        }

        public override byte GetAdditionalData()
        {
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) return 0x20;
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) return 0x10;
            return 0x00;
        }

        public override string ToString()
        {
            var sb = new StringBuilder();

            sb.Append("[B;");
            
            for (int i = 0; i < Data.Length; i++)
            {
                sb.Append(Data[i]);
            
                if (i < Data.Length - 1) sb.Append(',');
            }

            sb.Append(']');

            return sb.ToString();
        }
    }

    public sealed class BSOUByteArray : BSOTag
    {
        public byte[] Data { get; set; }

        public BSOUByteArray(byte[] data)
        {
            Data = data;
        }

        public override BSOType GetTagType()
        {
            return BSOType.ByteArray;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) bw.Write((byte)Data.Length);
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) bw.Write((ushort)Data.Length);
            else bw.Write(Data.Length);

            bw.Write(Data);
        }

        public override byte GetAdditionalData()
        {
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) return 0x20 + 0x40;
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) return 0x10 + 0x40;
            return 0x00 + 0x40;
        }

        public override string ToString()
        {
            var sb = new StringBuilder();

            sb.Append("[UB;");

            for (int i = 0; i < Data.Length; i++)
            {
                sb.Append(Data[i]);

                if (i < Data.Length - 1) sb.Append(',');
            }

            sb.Append(']');

            return sb.ToString();
        }
    }
}
