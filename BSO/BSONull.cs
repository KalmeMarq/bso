using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSONull : BSOTag
    {
        public override BSOType GetTagType()
        {
            return BSOType.Null;
        }

        public override void Write(BinaryWriter bw)
        {
        }

        public override string ToString()
        {
            return "Null";
        }
    }

    public sealed class BSOEnd : BSOTag
    {
        public override BSOType GetTagType()
        {
            return BSOType.Null;
        }

        public override void Write(BinaryWriter bw)
        {
        }

        public override byte GetAdditionalData()
        {
            return 0x10;
        }

        public override string ToString()
        {
            return "End";
        }
    }
}
