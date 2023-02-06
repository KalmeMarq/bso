using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOFloat : BSOTag
    {
        public float Value { get; set; }

        public BSOFloat() { }

        public BSOFloat(float value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.Float;
        }

        public override void Write(BinaryWriter bw)
        {
            bw.Write(Value);
        }

        public override string ToString()
        {
            return Value + "f";
        }
    }
}
