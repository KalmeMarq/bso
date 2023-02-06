using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSODouble : BSOTag
    {
        public double Value { get; set; }

        public BSODouble() { }

        public BSODouble(double value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Double;
        }

        public override void Write(BinaryWriter bw)
        {
            bw.Write(Value);
        }

        public override string ToString()
        {
            return Value + "D";
        }
    }
}
