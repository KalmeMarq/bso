# BSO (Binary Storage Object) (I may still change the name)

Languages:
  - Java (Branch: [master](https://github.com/KalmeMarq/bso/tree/master))
  - Typescript (Branch: [deno](https://github.com/KalmeMarq/bso/tree/deno))

I wanted to make my own json binary thing, so I looked at NBT, BSON and CBOR to see what they were up to. It's simple like NBT but I wanted to see what other stuff I could add.

The length of lists/arrays/map can be store as a byte(1 byte), short(2 bytes) or int(4 bytes). In NBT, for example, is always int(4 bytes). It's a small saving but... every 0 and 1 is important.

Optionally, they can also have an indefinite length. Map uses it by default.

I was trying to also allow different length types for strings. When I was doing that, in my head, I was thinking the default (short) was just length of 255 (but it's actually 65535). I'm dumb :/. I'll still added it bc for the most part, for my projects where I'll use this, most strings won't have a bigger length than 255.

A BSO type can have addditional data which allows to save bytes as much as possible.

## Types

- 0x00 Null
  - 0x10 End
- 0x01 Byte
- 0x02 Short
- 0x03 Int
- 0x04 Long
- 0x05 Float
- 0x06 Double
- 0x07 String
- 0x08 Map
- 0x09 List
- 0x0A Byte Array
- 0x0B Short Array
- 0x0C Int Array
- 0x0D Long Array
- 0x0E Float Array
- 0x0F Double Array

A type can have additional data that will change how it is read.

## Additional Data

### Int
  
  - 0x30 If the value is in the Byte range it will be written/read as a byte
  - 0x20 If the value is in the Short range it will be written/read as a short

### Long

  - 0x30 If the value is in the Byte range it will be written/read as a byte
  - 0x20 If the value is in the Short range it will be written/read as a short
  - 0x10 If the value is in the Int range it will be written/read as a int
  
### Map/List/Byte Array/Short Array/Int Array/Long Array/Float Array/Double Array

  - 0x00 Write/Read list length as unsigned byte
  - 0x10 Write/Read list length as unsigned short
  - 0x20 Write/Read list length as int
  - 0x30 Indefinite length

### String (Only available with this TS lib version)

  - 0x00 Write/Read string length as unsigned short
  - 0x10 Write/Read string length as unsigned byte
  - 0x20 Read string until it reaches \0

Map keys can also use this but they have their own option because... I'm getting crazy with this stuff :(

## Extra

  - writeBSO - serialize
  - bsoToJson - bso to json
  - bsoToSBSO - bso to string bso
  - readBSO - deserialize
  - readSBSO - parse string bso
  - `deno run -A main.ts <bsofilepath> [--indent]` - read the bso file and shows it as sbso (+ansi) and with the indent flag being optional