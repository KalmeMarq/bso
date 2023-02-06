using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOLongArray : BSOTag
    {
        public long[] Data { get; set; }

        public BSOLongArray() { Data = Array.Empty<long>(); }
        
        public BSOLongArray(long[] data) { Data = data; }

        public override BSOType GetTagType()
        {
            return BSOType.LongArray;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) bw.Write((byte)Data.Length);
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) bw.Write((ushort)Data.Length);
            else bw.Write(Data.Length);

            byte r = CheckRange();

            for (int i = 0; i < Data.Length; i++)
            {
                if (r == 2) bw.Write((short)Data[i]);
                else if (r == 1) bw.Write((int)Data[i]);
                else bw.Write(Data[i]);
            }
        }

        public override byte GetAdditionalData()
        {
            byte b = 0x00;
            if (Data.Length >= byte.MinValue && Data.Length <= byte.MaxValue) b = 0x20;
            else if (Data.Length >= ushort.MinValue && Data.Length <= ushort.MaxValue) b = 0x10;
            return (byte)(b + (CheckRange() * 0x40));
        }

        private byte CheckRange()
        {
            long min = 0;
            long max = 0;

            for (int i = 0; i < Data.Length; i++)
            {
                if (Data[i] < min) min = Data[i];
                if (Data[i] > max) max = Data[i];
            }

            if (min >= short.MinValue && min <= short.MaxValue) return 2;
            if (min >= int.MinValue && min <= int.MaxValue) return 1;
            return 0;
        }

        public override string ToString()
        {
            var sb = new StringBuilder();

            sb.Append("[L;");

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
