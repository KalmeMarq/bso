# BSO (Binary Storage Object)

I wanted to make my own json binary thing, so I looked at NBT, BSON and CBOR to see what they were up to. It's simple like NBT but I wanted to see what other stuff I could add.

To identify a type there's a byte with its id (the 4 rightmost bits) and additional data (the 4 leftmost bits) (0x{AD}{ID}).

The additional data has information to tell if it's a subtype or/and to write the data in a more compact way.
For example, it may tell to write the length/size of a map as a u8 instead of i32, if the length is in the u8 range.
Another example, if a Long is in the i16 range it tells to write as a i16 (2 bytes) instead of (i64) 8 bytes.

## Types
- BsoByte
  - ```
    ID: 0b0001
    AD: 0b0000
    Write as: i8
    ```
- BsoBoolean
  - ```
    ID: 0b0001
    AD: if (true) 0b0010 else 0b0001
    ```
- BsoShort
  - ```
    ID: 0b0010
    AD: if (in i8 range) 0b0001
        else 0b0000
    Write as: if (in i8 range) i8
              else i16
    ``` 
- BsoInt
  - ```
    ID: 0b0011
    AD: if (in i8 range) 0b0001
        else if (in i16 range) 0b0010
        else 0b0000
    Write as: if (in i8 range) i8
              else if (in i16 range) i16
              else i32
    ```
- BsoLong
  - ```
    ID: 0b0100
    AD: if (in i8 range) 0b0001
        else if (in i16 range) 0b0010
        else if (in i32 range) 0b0011
        else 0b0000
    Write as: if (in i8 range) i8
              else if (in i16 range) i16
              else if (in i32 range) i32
              else i64
    ```
- BsoFloat
  - ```
    ID: 0b0101
    AD: 0b0000
    Write as: f32
    ```
- BsoDouble
  - ```
    ID: 0b0101
    AD: 0b0001
    Write as: f64
    ```
- BsoString
  - ```
    ID: 0b0110
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: Java UTF-8 Modified
    ```
- BsoMap
  - ```
    ID: 0b0111
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as:
        <ADID> <key> <value>
    
        key:
            length as u8
            Java UTF-8 Modified
    ```
- BsoList
  - ```
    ID: 0b1000
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    
    ```
- BsoByteArray
  - ```
    ID: 0b1001
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: i8
    ```
- BsoBooleanArray
  - ```
    ID: 0b1001
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as:
      Each value is a single bit.
      So the byte length is the real length divided by 4 ceil.
    
      Examples: [false, true, true, false]
                0b0110
                
                [true, true, true, false, true]
                0b1110 0b1000
    ```
- BsoShortArray
  - ```
    ID: 0b1010
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    
        BITWISE OR
    
        if (all values in i8 range) 0b0100
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: if (all values in i8 range) i8
              else i16
    ```
- BsoIntArray
  - ```
    ID: 0b1011
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    
        BITWISE OR
    
        if (all values in i8 range) 0b0100
        else if (all values in i16 range) 0b1000
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: if (all values in i8 range) i8
              else if (all values in i16 range) i16
              else i32
    ```
- BsoLongArray
  - ```
    ID: 0b1100
    AD: if (length in u8 range) 0b0001
        else if (length in u16 range) 0b0010
        else 0b0000
    
        BITWISE OR
    
        if (all values in i8 range) 0b0100
        else if (all values in i16 range) 0b1000
        else if (all values in i32 range) 0b1100
        else 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: if (all values in i8 range) i8
              else if (all values in i16 range) i16
              else if (all values in i32 range) i32
              else i64
- BsoFloatArray
  - ```
    ID: 0b1101
    AD: 0b0000
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: f32
    ```
- BsoDoubleArray
  - ```
    ID: 0b1101
    AD: 0b0001
    Write length as: if (in u8 range) u8
              else if (in u16 range) u16
              else i32
    Write as: f64
    ```

## How to read

You know how to write, right? do it in reverse ,-,

## Perfomance

It's Java. We all know how fast it really is.
