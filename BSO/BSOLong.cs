using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOLong : BSOTag
    {
        public long Value { get; set; }

        public BSOLong() { }

        public BSOLong(long value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Long;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Value >= sbyte.MinValue && Value <= sbyte.MaxValue) bw.Write((sbyte)(Value & 0xFFL));
            else if (Value >= short.MinValue && Value <= short.MaxValue) bw.Write((short)(Value & 0xFFFFL));
            else if (Value >= int.MinValue && Value <= int.MaxValue) bw.Write((int)(Value & 0xFFFFFFFFL));
            else bw.Write(Value);
        }

        public override byte GetAdditionalData()
        {
            if (Value >= sbyte.MinValue && Value <= sbyte.MaxValue) return 0x30;
            else if (Value >= short.MinValue && Value <= short.MaxValue) return 0x20;
            else if (Value >= int.MinValue && Value <= int.MaxValue) return 0x10;
            return 0x00;
        }

        public override string ToString()
        {
            return Value + "L";
        }
    }

    public sealed class BSOULong : BSOTag
    {
        public ulong Value { get; set; }

        public BSOULong() { }

        public BSOULong(ulong value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Long;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Value >= byte.MinValue && Value <= byte.MaxValue) bw.Write((byte)(Value & 0xFFL));
            else if (Value >= ushort.MinValue && Value <= ushort.MaxValue) bw.Write((ushort)(Value & 0xFFFFL));
            else if (Value >= uint.MinValue && Value <= uint.MaxValue) bw.Write((uint)(Value & 0xFFFFFFFFL));
            else bw.Write(Value);
        }

        public override byte GetAdditionalData()
        {
            byte b = 0x00;
            if (Value >= byte.MinValue && Value <= byte.MaxValue) b = 0x30;
            else if (Value >= ushort.MinValue && Value <= ushort.MaxValue) b = 0x20;
            else if (Value >= uint.MinValue && Value <= uint.MaxValue) b = 0x10;
            return (byte)(b + 0x40);
        }

        public override string ToString()
        {
            return Value + "UL";
        }
    }
}
