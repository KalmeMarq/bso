using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BSO
{
    /// <summary> Indicates that the value of the marked element can never be <c>null</c>. </summary>
    [AttributeUsage(AttributeTargets.Method | AttributeTargets.Parameter |
                    AttributeTargets.Property | AttributeTargets.Delegate | AttributeTargets.Field)]
    public sealed class NotNullAttribute : Attribute { }

    public class BSOUtils
    {
        public static String ToSBSO(BSOTag bso, bool pretty = false)
        {
            var sb = new StringBuilder();
            ToSBSOElement(sb, bso, 0, pretty);
            return sb.ToString();
        }

        //private static string EscapeString(string value)
        //{
        //    var sb = new StringBuilder();

        //    int quote = 0;

        //    for (int i = 0; i < value.Length; i++)
        //    {
        //        int c = value.ToArray()[i];
        //    }

        //    return sb.ToString();
        //}

        private static readonly int INDENT = 2;

        private static void ToSBSOElement(StringBuilder sb, BSOTag bso, int level, bool pretty)
        {
            switch (bso.GetTagType())
            {
                case BSOType.Null:
                    sb.Append("null");
                    break;
                case BSOType.Byte:
                case BSOType.Short:
                case BSOType.Int:
                case BSOType.Long:
                case BSOType.Float:
                case BSOType.Double:
                    sb.Append(bso.ToString());
                    break;
                case BSOType.String:
                    sb.Append('"').Append(bso.ToString()).Append('"');
                    break;
                case BSOType.Map:
                    {
                        sb.Append('{');
                        if (pretty & ((BSOMap)bso).Count > 0) sb.Append('\n');

                        int i = 0;
                        foreach (var e in ((BSOMap)bso))
                        {
                            if (pretty)
                            {
                                for (int j = 0; j < (level + 1) * INDENT; j++)
                                {
                                    sb.Append(' ');
                                }
                            }

                            sb.Append(e.Key);
                            sb.Append(pretty ? ": " : ":");
                            ToSBSOElement(sb, e.Value, level + 1, pretty);

                            if (i < ((BSOMap)bso).Count - 1) sb.Append(pretty ? ",\n" : ",");
                            i++;
                        }

                        if (pretty & ((BSOMap)bso).Count > 0)
                        {
                            sb.Append('\n');
                            for (int j = 0; j < (level) * INDENT; j++)
                            {
                                sb.Append(' ');
                            }
                        };
                        sb.Append('}');

                        break;
                    }
                case BSOType.ByteArray:
                    {
                        sb.Append("[" + (bso is BSOUByteArray ? "UB" : "B") +";");

                        if (bso is BSOUByteArray)
                        {
                            var data = ((BSOUByteArray)bso).Data;

                            for (int i = 0; i < data.Length; i++)
                            {
                                sb.Append(data[i]);
                                if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                            }
                        } else
                        {
                            var data = ((BSOByteArray)bso).Data;

                            for (int i = 0; i < data.Length; i++)
                            {
                                sb.Append(data[i]);
                                if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                            }
                        }

                        sb.Append(']');
                        break;
                    }
                case BSOType.ShortArray:
                    {
                        sb.Append("[S;");

                        var data = ((BSOShortArray)bso).Data;

                        for (int i = 0; i < data.Length; i++)
                        {
                            sb.Append(data[i]);
                            if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                        }

                        sb.Append(']');
                        break;
                    }
                case BSOType.IntArray:
                    {
                        sb.Append("[I;");

                        var data = ((BSOIntArray)bso).Data;

                        for (int i = 0; i < data.Length; i++)
                        {
                            sb.Append(data[i]);
                            if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                        }

                        sb.Append(']');
                        break;
                    }
                case BSOType.LongArray:
                    {
                        sb.Append("[L;");

                        var data = ((BSOLongArray)bso).Data;

                        for (int i = 0; i < data.Length; i++)
                        {
                            sb.Append(data[i]);
                            if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                        }

                        sb.Append(']');
                        break;
                    }
                case BSOType.FloatArray:
                    {
                        sb.Append("[F;");

                        var data = ((BSOLongArray)bso).Data;

                        for (int i = 0; i < data.Length; i++)
                        {
                            sb.Append(data[i]);
                            if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                        }

                        sb.Append(']');
                        break;
                    }
                case BSOType.DoubleArray:
                    {
                        sb.Append("[D;");

                        var data = ((BSOLongArray)bso).Data;

                        for (int i = 0; i < data.Length; i++)
                        {
                            sb.Append(data[i]);
                            if (i < data.Length - 1) sb.Append(pretty ? ", " : ",");
                        }

                        sb.Append(']');
                        break;
                    }
            }
        }

        public static byte GetTypeID(BSOType type)
        {
            return type switch
            {
                BSOType.Null => 0x00,
                BSOType.Byte => 0x01,
                BSOType.Short => 0x02,
                BSOType.Int => 0x03,
                BSOType.Long => 0x04,
                BSOType.Float => 0x05,
                BSOType.Double => 0x06,
                BSOType.String => 0x07,
                BSOType.Map => 0x08,
                BSOType.List => 0x09,
                BSOType.ByteArray => 0x0A,
                BSOType.ShortArray => 0x0B,
                BSOType.IntArray => 0x0C,
                BSOType.LongArray => 0x0D,
                BSOType.FloatArray => 0x0E,
                BSOType.DoubleArray => 0x0F,
                _ => throw new NotImplementedException()
            };
        }

        public static BSOType GetIDType(byte id)
        {
            return id switch
            {
                0x01 => BSOType.Byte,
                0x02 => BSOType.Short,
                0x03 => BSOType.Int,
                0x04 => BSOType.Long,
                0x05 => BSOType.Float,
                0x06 => BSOType.Double,
                0x07 => BSOType.String,
                0x08 => BSOType.Map,
                0x09 => BSOType.List,
                0x0A => BSOType.ByteArray,
                0x0B => BSOType.ShortArray,
                0x0C => BSOType.IntArray,
                0x0D => BSOType.LongArray,
                0x0E => BSOType.FloatArray,
                0x0F => BSOType.DoubleArray,
                _ => throw new Exception("Unknown ID " + id)
            };
        }

        public static void WriteBSO(string filename, BSOTag bso)
        {
            using var stream = File.Open(filename, FileMode.Create);
            using var writer = new BinaryWriter(stream);
            writer.Write((byte)(GetTypeID(bso.GetTagType()) + bso.GetAdditionalData()));
            bso.Write(writer);
        }

        public static BSOTag ReadBSO(string filename)
        {
            using var stream = File.Open(filename, FileMode.Open);
            using var reader = new BinaryReader(stream);
            byte b = reader.ReadByte();
            if (b == 0) return new BSONull();
            return ReadBSOElement(reader, (byte)(b & 0x0F), (byte)(b & 0xF0));
        }

        private static BSOTag ReadBSOElement(BinaryReader br, byte id, byte additionalData)
        {
            switch (GetIDType(id))
            {
                case BSOType.Byte:
                    {
                        return additionalData switch
                        {
                            0x40 => new BSOUByte(br.ReadByte()),
                            0x50 => new BSOBoolean(true),
                            0x60 => new BSOBoolean(false),
                            _ => new BSOByte(br.ReadSByte())
                        };
                    }
                case BSOType.Short:
                    {
                        if (additionalData > 0x30)
                        {
                            return (additionalData & 0x30) == 0x30 ? new BSOUShort(br.ReadByte()) : new BSOUShort(br.ReadUInt16());
                        } else
                        {
                            return (additionalData & 0x30) == 0x30 ? new BSOShort(br.ReadSByte()) : new BSOShort(br.ReadInt16());
                        }
                    }
                case BSOType.Int:
                    {
                        if (additionalData == 0x10) throw new NotImplementedException("Int with arbitrary size, or BigNum, is not supported");

                        if (additionalData > 0x30)
                        {
                            uint vl;

                            if ((additionalData & 0x30) == 0x30) vl = br.ReadByte();
                            else if ((additionalData & 0x30) == 0x20) vl = br.ReadUInt16();
                            else vl = br.ReadUInt32();

                            return new BSOUInt(vl);
                        } else
                        {
                            int vl;

                            if ((additionalData & 0x30) == 0x30) vl = br.ReadSByte();
                            else if ((additionalData & 0x30) == 0x20) vl = br.ReadInt16();
                            else vl = br.ReadInt32();

                            return new BSOInt(vl);
                        }
                    }
                case BSOType.Long:
                    {
                        if (additionalData > 0x30)
                        {
                            ulong vl;

                            if ((additionalData & 0x30) == 0x30) vl = br.ReadByte();
                            else if ((additionalData & 0x30) == 0x20) vl = br.ReadUInt16();
                            else if ((additionalData & 0x30) == 0x10) vl = br.ReadUInt32();
                            else vl = br.ReadUInt64();

                            return new BSOULong(vl);
                        } else
                        {
                            long vl;

                            if ((additionalData & 0x30) == 0x30) vl = br.ReadSByte();
                            else if ((additionalData & 0x30) == 0x20) vl = br.ReadInt16();
                            else if ((additionalData & 0x30) == 0x10) vl = br.ReadInt32();
                            else vl = br.ReadInt64();

                            return new BSOLong(vl);
                        }
                    }
                case BSOType.Float:
                    {
                        if (additionalData == 0x10) throw new NotImplementedException("Float with arbitrary size is not supported");
                        return new BSOFloat(br.ReadSingle());
                    }
                case BSOType.Double:
                    {
                        return new BSODouble(br.ReadDouble());
                    }
                case BSOType.String:
                    {
                        return new BSOString(br.ReadString());
                    }
                case BSOType.Map:
                    {
                        var map = new BSOMap();

                        if (additionalData == 0x30)
                        {
                            byte b;
                            while ((b = br.ReadByte()) != 0x10)
                            {
                                Console.WriteLine(b);
                                map[br.ReadString()] = ReadBSOElement(br, (byte)(b & 0x0F), (byte)(b & 0xF0));
                            }

                            return map;
                        } else
                        {
                            int length = additionalData == 0x20 ? br.ReadByte() : additionalData == 0x10 ? br.ReadInt16() : br.ReadInt32();
                            for (int i = 0; i < length; i++)
                            {
                                byte b = br.ReadByte();
                                string key = br.ReadString();
                                map[key] = ReadBSOElement(br, (byte)(b & 0x0F), (byte)(b & 0xF0));
                            }
                        }

                        return map;
                    }
                case BSOType.ByteArray:
                    {
                        if (additionalData > 0x30)
                        {
                            int lenT = additionalData & 0x30;

                            if (lenT == 0x20)
                            {
                                return new BSOUByteArray(br.ReadBytes(br.ReadByte()));
                            }
                            else if (lenT == 0x10)
                            {
                                return new BSOUByteArray(br.ReadBytes(br.ReadUInt16()));
                            }
                            else
                            {
                                return new BSOUByteArray(br.ReadBytes(br.ReadInt32()));
                            }
                        } else
                        {
                            int lenT = additionalData & 0x30;

                            int len;

                            if (lenT == 0x20)
                            {
                                len = br.ReadByte();
                            }
                            else if (lenT == 0x10)
                            {
                                len = br.ReadUInt16();
                            }
                            else
                            {
                                len = br.ReadInt32();
                            }

                            sbyte[] data = new sbyte[len];
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadSByte();
                            }

                            return new BSOByteArray(data);
                        }
                    }
                case BSOType.ShortArray:
                    {
                        int len;
                        if ((additionalData & 0x30) == 0x20) len = br.ReadByte();
                        else if ((additionalData & 0x30) == 0x20) len = br.ReadUInt16();
                        else len = br.ReadInt32();

                        short[] data = new short[len];
                        
                        if (additionalData > 0x30)
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadSByte();
                            }
                        } else
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadInt16();
                            }
                        }

                        return new BSOShortArray(data);
                    }
                case BSOType.IntArray:
                    {
                        int len;
                        if ((additionalData & 0x30) == 0x20) len = br.ReadByte();
                        else if ((additionalData & 0x30) == 0x20) len = br.ReadUInt16();
                        else len = br.ReadInt32();

                        int[] data = new int[len];

                        if (additionalData > 0x70)
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadSByte();
                            }
                        } else if (additionalData > 0x30)
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadInt16();
                            }
                        }
                        else
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadInt32();
                            }
                        }

                        return new BSOIntArray(data);
                    }
                case BSOType.LongArray:
                    {
                        int len;
                        if ((additionalData & 0x30) == 0x20) len = br.ReadByte();
                        else if ((additionalData & 0x30) == 0x20) len = br.ReadUInt16();
                        else len = br.ReadInt32();

                        long[] data = new long[len];

                        if (additionalData > 0x70)
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadInt16();
                            }
                        }
                        else if (additionalData > 0x30)
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadInt32();
                            }
                        }
                        else
                        {
                            for (int i = 0; i < len; i++)
                            {
                                data[i] = br.ReadInt64();
                            }
                        }

                        return new BSOLongArray(data);
                    }
                case BSOType.FloatArray:
                    {
                        int len;
                        if ((additionalData & 0x30) == 0x20) len = br.ReadByte();
                        else if ((additionalData & 0x30) == 0x20) len = br.ReadUInt16();
                        else len = br.ReadInt32();

                        float[] data = new float[len];

                        for (int i = 0; i < len; i++)
                        {
                            data[i] = br.ReadSingle();
                        }

                        return new BSOFloatArray(data);
                    }
                case BSOType.DoubleArray:
                    {
                        int len;
                        if ((additionalData & 0x30) == 0x20) len = br.ReadByte();
                        else if ((additionalData & 0x30) == 0x20) len = br.ReadUInt16();
                        else len = br.ReadInt32();

                        double[] data = new double[len];

                        for (int i = 0; i < len; i++)
                        {
                            data[i] = br.ReadDouble();
                        }

                        return new BSODoubleArray(data);
                    }
                default: throw new Exception("Unknown ID " + id);
            }
        }
    }

    public enum BSOType
    {
        Null,
        Byte,
        Short,
        Int,
        Long,
        Float,
        Double,
        String,
        Map,
        List,
        ByteArray,
        ShortArray,
        IntArray,
        LongArray,
        FloatArray,
        DoubleArray
    }
}
