using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOFloatArray : BSOTag
    {
        public float[] Data { get; set; }

        public BSOFloatArray() { Data = Array.Empty<float>(); }

        public BSOFloatArray(float[] data) { Data = data; }

        public override BSOType GetTagType()
        {
            return BSOType.FloatArray;
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

            sb.Append("[F;");

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
