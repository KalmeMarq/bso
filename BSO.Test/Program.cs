using System;

namespace BSO.Test
{
    static class Program
    {
        static void Main(string[] args)
        {
            //using (var stream = File.Open("D:/Trash/csharp_data.bso", FileMode.Create))
            //{
            //    using (var writer = new BinaryWriter(stream))
            //    {
            //        ushort vl = 31888;
            //        short vl1 = 31888;
            //        writer.Write(vl1);
            //    }
            //}

            var map = new BSOMap()
            {
                ["a"] = new BSOByte(1),
                ["b"] = new BSOUByte(2),
                ["c"] = new BSOBoolean(true),
                ["d"] = new BSOBoolean(false),
                ["e"] = new BSOShort(3),
                ["f"] = new BSOUShort(4),
                ["g"] = new BSOInt(5),
                ["h"] = new BSOUInt(6),
                ["i"] = new BSOLong(7L),
                ["k"] = new BSOULong(8L),
                ["l"] = new BSOByteArray(new sbyte[] { 20, -10, 10 }),
                ["m"] = new BSOUByteArray(new byte[] { 20, 10, 30 }),
                ["n"] = new BSOShortArray(new short[] { 20, -10, 10 }),
                ["o"] = new BSOIntArray(new int[] { 20, -10, 10 }),
                ["p"] = new BSOLongArray(new long[] { 20, -10, 10 }),
                ["q"] = new BSOMap()
                {
                    ["r"] = new BSOString("KalmeMarq")
                }
            };

            BSOUtils.WriteBSO("D:/Trash/csharp_data.bso", map);

            Console.WriteLine(map.ToString());

            Console.WriteLine("------------");

            Console.WriteLine(BSOUtils.ToSBSO(BSOUtils.ReadBSO("D:/Trash/csharp_data.bso"), true));

            using (var stream = File.Open("D:/Trash/csharp_data.bso", FileMode.Open))
            {
                using (var reader = new BinaryReader(stream))
                {
                    Console.Write("[");
                    try
                    {
                        int i = 0;
                        while (true)
                        {
                            if (i != 0) Console.Write(',');
                            Console.Write(reader.ReadByte());
                            i++;
                        }
                    }
                    catch (Exception)
                    {
                        Console.CursorLeft = Console.GetCursorPosition().Left - 1;
                        Console.WriteLine(']');
                    }
                }
            }
        }
    }
}