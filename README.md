# BSO (Binary Storage Object)

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
  - If the list is only for numbers you should use the specific number array element to save bytes.
  - try to not use multiple types in lists if possible.
- 0x0A Byte Array
- 0x0B Short Array
- 0x0C Int Array
- 0x0D Long Array
- 0x0E Float Array
- 0x0F Double Array

A type can have additional data that will change how it is read.

## Additional Data

### Int
  
  - 0x30 If the value is in the (signed) Byte range it will be written/read as a byte
  - 0x20 If the value is in the (signed) Short range it will be written/read as a short

### Long

  - 0x30 If the value is in the (signed) Byte range it will be written/read as a byte
  - 0x20 If the value is in the (signed) Short range it will be written/read as a short
  - 0x10 If the value is in the (signed) Int range it will be written/read as a int
  
### Map

  - 0x00 Write/Read list length as unsigned byte
  - 0x10 Write/Read list length as unsigned short
  - 0x20 Write/Read list length as int
  - 0x30 Indefinite length

### List

  - 0x00 Write/Read list length as unsigned byte
  - 0x10 Write/Read list length as unsigned short
  - 0x20 Write/Read list length as int
  - 0x30 Indefinite length

### Byte Array/Short Array/Int Array/Long Array/Float Array/Double Array

  - 0x00 Write/Read list length as unsigned byte
  - 0x10 Write/Read list length as unsigned short
  - 0x20 Write/Read list length as int

## Extra

StringBSOReader - reads BSO from SBSO (bso string format)

StringBSOWriter - writes BSO in a string format

BSOJsonWriter - writes BSO in json format

Both Writers allow to set the indentation and style (minify, spaced minify and beautify).

## How to write :)

Byte/Short/Int/Long in Java are signed but an additional data can tell if the number to be written/read is suppose to be unsigned (positive).

In this Java implementation unsigned numbers aren't supported. I'm not secure about allowing unsigned numbers here. I think it's better not. Nonetheless, it's still allowed by the """specification""" (not really one but good enough)

Tag_Null (ID 0x00)
  - write byte (ID)

TAG_Byte (ID 0x01)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - write 0x40 if value is unsigned
  - write byte (value)

TAG_Short (ID 0x02)
  - write byte (ADDITIONAL DATA + ID)
      - ADDITIONAL DATA
      - write 0x40 if value is unsigned
  - write short (value)

TAG_Int (ID 0x03)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - If value is in the signed byte range write 0x30 (+ 0x40 if value is unsigned)
      - If value is in the signed short range write 0x20 (+ 0x40 if value is unsigned)
      - otherwuse write 0x00 (+ 0x40 if value is unsigned)
  - write int (value)

TAG_Long (ID 0x04)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - If value is in the signed byte range write 0x30
      - If value is in the signed short range write 0x20
      - If value is in the signed int range write 0x10
      - otherwuse write 0x00
  - write long (value)

TAG_Float (ID 0x05)
  - write byte (ID)
  - write long (value)

TAG_Double (ID 0x06)
  - write byte (ID)
  - write long (value)

TAG_String (ID 0x07)
  - write byte (ID)
  - write short (length)
  - write modified UTF-8

TAG_Map (ID 0x08)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if uses indefinite length (default) write 0x30
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - if it's not indefinite length, write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write for each element (additional data + id) + key + value
    - key uses modified UTF-8
  - if it's indefinite length, write TAG_End

TAG_List (ID 0x09)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if uses indefinite length (disabled by default) write 0x30
      - if length is in the usigned byte range write 0x20 (+ 0x40 if it has multiple types)
      - if length is in the usigned short range write 0x10 (+ 0x40 if it has multiple types)
      - otherwise write 0x00 (+ 0x40 if it has multiple types)
  - if it uses indefinite length (each element has to write its id + additional data before it. indefinite length wouldn't work with single type)
    - write elements
      - for each element (additional data + id) + value
    - write TAG_End
  - otherwise
    - write length
      - as unsigned byte if it's in the range
      - as unsigned short if it's in the range
      - otherwise as int
    - if contains a single type, write list type
      - if length > 0. there's no point in writing the type if there's no elements :/
    - write elements

TAG_ByteArray (ID 0x0A)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write byte values
  - if it's indefinite length, write TAG_End

TAG_ShortArray (ID 0x0B)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write short values
  - if it's indefinite length, write TAG_End

TAG_IntArray (ID 0x0C)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write int values
  - if it's indefinite length, write TAG_End

TAG_LongArray (ID 0x0D)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write long values
  - if it's indefinite length, write TAG_End

TAG_FloatArray (ID 0x0E)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write float values
  - if it's indefinite length, write TAG_End

TAG_DoubleArray (ID 0x0F)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write double values
  - if it's indefinite length, write TAG_End

TAG_End (ID 0x10)
  - write byte (ID)

## How to read

You know how to write, right? do it in reverse ,-,