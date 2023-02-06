using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOShortArray : BSOTag
    {
        public short[] Data { get; set; }

        public BSOShortArray() { this.Data = Array.Empty<short>(); }

        public BSOShortArray(short[] data) { this.Data = data; }

        public override BSOType GetTagType()
        {
            return BSOType.ShortArray;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) bw.Write((byte)Data.Length);
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) bw.Write((ushort)Data.Length);
            else bw.Write(Data.Length);

            byte r = CheckSByteRange();

            for (int i = 0; i < Data.Length; i++) {
                if (r == 1) bw.Write((sbyte)Data[i]);
                else bw.Write(Data[i]);
            }
        }

        public override byte GetAdditionalData()
        {
            byte b = 0x00;
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) b = 0x20;
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) b = 0x10;
            return (byte)(b + (CheckSByteRange() * 0x40));
        }

        private byte CheckSByteRange()
        {
            short min = 0;
            short max = 0;

            for (int i = 0; i < Data.Length; i++)
            {
                if (Data[i] < min) min = Data[i];
                if (Data[i] > max) max = Data[i];
            }

            if (min >= sbyte.MinValue && min <= sbyte.MaxValue) return 1;
            return 0;
        }

        public override string ToString()
        {
            var sb = new StringBuilder();

            sb.Append("[S;");
            
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
