using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOByte : BSOTag
    {
        public sbyte Value { get; set; }

        public BSOByte() { }

        public BSOByte(sbyte value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Byte;
        }

        public override void Write(BinaryWriter bw)
        {
            bw.Write(Value);
        }

        public override string ToString()
        {
            return Value + "b";
        }
    }

    public sealed class BSOUByte : BSOTag
    {
        public byte Value { get; set; }

        public BSOUByte() { }

        public BSOUByte(byte value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Byte;
        }

        public override byte GetAdditionalData()
        {
            return 0x40;
        }

        public override void Write(BinaryWriter bw)
        {
            bw.Write(Value);
        }

        public override string ToString()
        {
            return Value + "ub";
        }
    }

    public sealed class BSOBoolean : BSOTag
    {
        public bool Value { get; set; }

        public BSOBoolean(bool value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Byte;
        }

        public override byte GetAdditionalData()
        {
            return (byte)(Value ? 0x50 : 0x60);
        }

        public override void Write(BinaryWriter bw)
        {
        }

        public override string ToString()
        {
            return Value ? "true" : "false";
        }
    }
}
