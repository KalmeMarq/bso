# BSO (Binary Storage Object) (I may still change the name)

I wanted to make my own json binary thing, so I looked at NBT, BSON and CBOR to see what they were up to. It's simple like NBT but I wanted to see what other stuff I could add.

The length of lists/arrays/map can be store as a byte(1 byte), short(2 bytes) or int(4 bytes). In NBT, for example, is always int(4 bytes). It's a small saving but... every 0 and 1 is important.

Optionally, they can also have an indefinite length. Map uses it by default.

I was trying to also allow different length types for strings. When I was doing that, in my head, I was thinking the default (short) was just length of 255 (but it's actually 65535). I'm dumb :/. I'll still added it bc for the most part, for my projects where I'll use this, most strings won't have a bigger length than 255.

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

## Extra

There's also a StringBSOWriter that write BSO to a string format and BSOJsonWriter that writes it as a json (stringified).