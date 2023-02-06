using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOString : BSOTag
    {
        [NotNull]
        public string Value { get; set; }

        public BSOString() { Value = ""; }

        public BSOString([NotNull] string value) { Value = value; }

        public override BSOType GetTagType()
        {
            return BSOType.String;
        }

        public override void Write(BinaryWriter bw)
        {
            bw.Write(Value);
        }

        public override string ToString()
        {
            return Value;
        }
    }
}
