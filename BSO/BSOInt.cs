using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOInt : BSOTag
    {
        public int Value { get; set; }

        public BSOInt() { }

        public BSOInt(int value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Int;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Value >= sbyte.MinValue && Value <= sbyte.MaxValue) bw.Write((sbyte)(Value & 0xFF));
            else if (Value >= short.MinValue && Value <= short.MaxValue) bw.Write((short)(Value & 0xFFFF));
            else bw.Write(Value);
        }

        public override byte GetAdditionalData()
        {
            if (Value >= sbyte.MinValue && Value <= sbyte.MaxValue) return 0x30;
            else if (Value >= short.MinValue && Value <= short.MaxValue) return 0x20;
            return 0x00;
        }

        public override string ToString()
        {
            return Value.ToString() + "i";
        }
    }

    public sealed class BSOUInt : BSOTag
    {
        public uint Value { get; set; }

        public BSOUInt() { }

        public BSOUInt(uint value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Int;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Value >= byte.MinValue && Value <= byte.MaxValue) bw.Write((byte)(Value & 0xFF));
            else if (Value >= ushort.MinValue && Value <= ushort.MaxValue) bw.Write((ushort)(Value & 0xFFFF));
            else bw.Write(Value);
        }

        public override byte GetAdditionalData()
        {
            byte b = 0x00;
            if (Value >= byte.MinValue && Value <= byte.MaxValue) b = 0x30;
            else if (Value >= ushort.MinValue && Value <= ushort.MaxValue) b = 0x20;
            return (byte)(b + 0x40);
        }

        public override string ToString()
        {
            return Value.ToString() + "ui";
        }
    }
}
