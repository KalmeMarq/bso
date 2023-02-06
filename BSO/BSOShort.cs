using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOShort : BSOTag
    {
        public short Value { get; set; }

        public BSOShort() { }

        public BSOShort(short value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Short;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Value >= sbyte.MinValue && Value <= sbyte.MaxValue) bw.Write((sbyte)(Value & 0xFF));
            else bw.Write(Value);
        }

        public override byte GetAdditionalData()
        {
            return Value >= sbyte.MinValue && Value <= sbyte.MaxValue ? (byte)0x30 : (byte)0x00;
        }

        public override string ToString()
        {
            return Value + "s";
        }
    }

    public sealed class BSOUShort : BSOTag
    {
        public ushort Value { get; set; }

        public BSOUShort() { }

        public BSOUShort(ushort value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Short;
        }

        public override void Write(BinaryWriter bw)
        {
            if (Value >= byte.MinValue && Value <= byte.MaxValue) bw.Write((byte)(Value & 0xFF));
            else bw.Write(Value);
        }

        public override byte GetAdditionalData()
        {
            return (byte)(((Value >= byte.MinValue && Value <= byte.MaxValue ? (byte)0x30 : (byte)0x00)) + 0x40);
        }

        public override string ToString()
        {
            return Value + "us";
        }
    }
}