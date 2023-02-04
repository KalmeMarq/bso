# BSO (Binary Storage Object)

Languages:
  - Java (Branch: [master](https://github.com/KalmeMarq/bso/tree/master))
  - Typescript (Branch: [deno](https://github.com/KalmeMarq/bso/tree/deno)) (not up to date)

I wanted to make my own json binary thing, so I looked at NBT, BSON and CBOR to see what they were up to. It's simple like NBT but I wanted to see what other stuff I could add.

The length of lists/arrays/map can be store as a byte(1 byte), short(2 bytes) or int(4 bytes). In NBT, for example, is always int(4 bytes). It's a small saving but... every 0 and 1 is important.

A BSO type can have addditional data which allows to save bytes as much as possible.

## Types

| ID   |      Tag     | Name            | Additional Data                                                                                                                                                                                | Min Byte Size                                       | Max Byte Size                                        | Byte Saving (Best case) |
|------|:------------:|-----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|------------------------------------------------------|-------------------------|
| 0x00 | Null         | TAG_Null        | 0x10 - End (It's a type and a subtype at the same time :/)                                                                                                                                     |                                                     |                                                      |                         |
| 0x01 | Byte         | TAG_Byte        | 0x40 - value is unsigned                                                                                                                                                                       | 1 byte                                              | 1 byte                                               |                         |
| 0x02 | Short        | TAG_Short       | 0x30 - write as byte<br><br>To the above can be added:<br>0x40 - value is unsigned                                                                                                             | 1 byte                                              | 2 bytes                                              | 1 byte                  |
| 0x03 | Int          | TAG_Int         | 0x30 - write as byte<br>0x20 - write as short<br><br>To the above can be added:<br>0x40 - value is unsigned                                                                                    | 1 byte                                              | 4 bytes                                              | 3 bytes                 |
| 0x04 | Long         | TAG_Long        | 0x30 - write as byte<br>0x20 - write as short<br>0x10 - write as int<br><br>To the above can be added:<br>0x40 - value is unsigned                                                             | 1 byte                                              | 8 bytes                                              | 7 bytes                 |
| 0x05 | Float        | TAG_Float       |                                                                                                                                                                                                | 4 bytes                                             | 4 bytes                                              |                         |
| 0x06 | Double       | TAG_Double      |                                                                                                                                                                                                | 8 bytes                                             | 8 bytes                                              |                         |
| 0x07 | String       | TAG_String      | 0x10 - use indefinite length<br>Without it the string has a max length of 65535                                                                                                                | (2 * len) + 1 bytes                                 | 2 + (2 * len) bytes                                  | 1 byte                  |
| 0x08 | Map          | TAG_Map         | 0x30 - use indefinite length (default)<br>0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int                                                        | key/value + 1 byte<br><br>key = (2 * len) + 1 bytes | 4 bytes + key/value<br><br>key = (2 * len) + 1 bytes | 3 bytes                 |
| 0x09 | List         | TAG_List        | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int                                                                                                  | (1 + 1 (list type)) bytes + values                  | (4 + 1 (list type)) bytes + 1 values                 | 3 bytes                 |
| 0x0A | Byte Array   | TAG_ByteArray   | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int                                                                                                  | 1 + (1 * len) bytes                                 | 4 + (1 * len) bytes                                  | 3 bytes                 |
| 0x0B | Short Array  | TAG_ShortArray  | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int<br><br>To the above can be added:<br>0x40 - write values as bytes                                | 1 + (1 * len) bytes                                 | 4 + (2 * len) bytes                                  | 3 + len bytes           |
| 0x0C | Int Array    | TAG_IntArray    | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int<br><br>To the above can be added:<br>0x80 - write values as byte<br>0x40 - write values as short | 1 + (1 * len) bytes                                 | 4 + (4 * len) bytes                                  | 3 + (3 * len) bytes     |
| 0x0D | Long Array   | TAG_LongArray   | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int<br><br>To the above can be added:<br>0x80 - write values as short<br>0x40 - write values as int  | 1 + (2 * len) bytes                                 | 4 + (8 * len) bytes                                  | 3 + (6 * len) bytes     |
| 0x0E | Float Array  | TAG_FloatArray  | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int                                                                                                  | 1 + (4 * len) bytes                                 | 4 + (4 * len) bytes                                  | 3 bytes                 |
| 0x0F | Double Array | TAG_DoubleArray | 0x20 - write length as byte<br>0x10 - write length as short<br>otherwise, write length as int                                                                                                  | 1 + (8 * len) bytes                                 | 4 + (8 * len) bytes                                  | 3 bytes                 |

If a List is only for numbers you should use the specific number array element to save bytes. Also, try to not use multiple types in lists if possible.

## Additional Data

### Short
  
  - 0x30 If the value is in the (signed) Byte range it will be written/read as a byte

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

#### Int Array

If all the values are in the signed byte range, write as byte. Saves 3 bytes per value.

Otherwise, if all the values are in the signed short range, write as short. Saves 2 bytes per value.

If none of the above are possible, write as int.

#### Long Array

If all the values are in the signed short range, write as short. Saves 6 bytes per value.

Otherwise, if all the values are in the signed int range, write as int. Saves 4 bytes per value.

If none of the above are possible, write as long.

## Extra

StringBSOReader - reads BSO from SBSO (bso string format)

StringBSOWriter - writes BSO in a string format

BSOJsonWriter - writes BSO in json format

BSOReader - it has methods to read BSO from a DataInput or InputStream

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
        - if value is in the signed byte range write 0x30 (+ 0x40 if value is unsigned)
        - otherwise, write 0x00 (+ 0x40 if value is unsigned)
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
      - If value is in the signed byte range write 0x30 (+ 0x40 if value is unsigned)
      - If value is in the signed short range write 0x20 (+ 0x40 if value is unsigned)
      - If value is in the signed int range write 0x10 (+ 0x40 if value is unsigned)
      - otherwuse write 0x00 (+ 0x40 if value is unsigned)
  - write long (value)

TAG_Float (ID 0x05)
  - write byte (ID)
  - write long (value)

TAG_Double (ID 0x06)
  - write byte (ID)
  - write long (value)

TAG_String (ID 0x07)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if uses indefinite length (in case length is bigger than 65535) write 0x10
  - if indefinite length (uses \0 terminator)
    - write string bytes using charset utf-8
    - write byte (\0)
  - otherwise
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
    - key uses string bytes using charset UTF-8 with \0 terminator
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

TAG_ShortArray (ID 0x0B)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20 
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
      - if all values are in the signed byte range add 0x40
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write values
    - as byte if they all are in the signed byte range
    - otherwise as short

TAG_IntArray (ID 0x0C)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
      - if all values are in the signed byte range add 0x80
      - if all values are in the signed short range add 0x40
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write values
    - as byte if they all are in the signed byte range
    - as short if they all are in the signed short range
    - otherwise as int

TAG_LongArray (ID 0x0D)
  - write byte (ADDITIONAL DATA + ID)
    - ADDITIONAL DATA
      - if length is in the usigned byte range write 0x20
      - if length is in the usigned short range write 0x10
      - otherwise write 0x00
      - if all values are in the signed short range add 0x80
      - if all values are in the signed int range add 0x40
  - write length
    - as unsigned byte if it's in the range
    - as unsigned short if it's in the range
    - otherwise as int
  - write values
    - as short if they all are in the signed short range
    - as int if they all are in the signed int range
    - otherwise as long

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

TAG_End (ID 0x10)
  - write byte (ID)

## How to read

You know how to write, right? do it in reverse ,-,

## Perfomance

It's Java. We all know how fast it really is.