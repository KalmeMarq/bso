using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    public sealed class BSOMap : BSOTag, IEnumerable<KeyValuePair<string, BSOTag>>
    {
        private readonly Dictionary<string, BSOTag> map = new();

        public IEnumerator<KeyValuePair<string, BSOTag>> GetEnumerator()
        {
            return map.GetEnumerator();
        }

        public BSOTag this[string key]
        {
            get { return map[key]; }
            set { map[key] = value; }
        }

        public override BSOType GetTagType()
        {
            return BSOType.Map;
        }

        public override byte GetAdditionalData()
        {
            //return (byte)(map.Count <= byte.MaxValue ? 0x20 : map.Count <= ushort.MaxValue ? 0x10 : 0x00);
            return 0x30;
        }

        public override void Write(BinaryWriter bw)
        {
            //if (map.Count <= byte.MaxValue) bw.Write((byte)(map.Count & 0xFF));
            //else if (map.Count <= ushort.MaxValue) bw.Write((ushort)(map.Count & 0xFFFF));
            //else bw.Write(map.Count);

            foreach (var entry in map)
            {
                var value = entry.Value;
                bw.Write((byte)(BSOUtils.GetTypeID(value.GetTagType()) + value.GetAdditionalData()));
                bw.Write(entry.Key);
                value.Write(bw);
            }

            bw.Write((byte)0x10);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return map.GetEnumerator();
        }

        public int Count {
            get {
                return map.Count;
            }
        }

        public override string ToString()
        {
            var sb = new StringBuilder();

            sb.Append('{');

            int i = 0;

            foreach (var entry in map)
            {
                sb.Append(entry.Key).Append(':').Append(entry.Value.ToString());

                if (i < map.Count - 1) sb.Append(',');
                ++i;
            }

            sb.Append('}');

            return sb.ToString();
        }
    }
}
