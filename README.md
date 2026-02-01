# BSO

I wanted to make my own json binary thing, so I looked at NBT, BSON and CBOR to see what they were up to. It's simple like NBT but I wanted to see what other stuff I could add.

There's also a string format (SBSO).

#### b2s - binary to string format
```
java -cp <libjar> io.github.kalmemarq.bso.BsoUtils b2s <inpath> <outpath> [--indent <int>]
```
#### s2b - string to binary format
```
java -cp <libjar> io.github.kalmemarq.bso.BsoUtils s2b <inpath> <outpath> [--le]
```

## Built-in types

- BsoByte / BsoUByte
  - (un)signed 8-bit integer
- BsoBool
  - boolean
- BsoShort / BsoUShort
  - (un)signed 16-bit integer
- BsoInt / BsoUInt
  - (un)signed 32-bit integer
- BsoLong / BsoULong
  - (un)signed 64-bit integer
- BsoFloat
  - 32-bit float
- BsoDouble
  - 64-bit float
- BsoString
  - UTF-8 string
- BsoMap
  - map
- BsoList
  - list
- BsoByteArray / BsoUByteArray
  - (un)signed 8-bit integer array
- BsoShortArray / BsoUShortArray
  - (un)signed 16-bit integer array
- BsoIntArray / BsoUIntArray
  - (un)signed 32-bit integer array
- BsoLongArray / BsoULongArray
  - (un)signed 64-bit integer array
- BsoFloatArray
  - 32-bit float array
- BsoDoubleArray
  - 64-bit float array

## SBSO

- BsoByte
  - **0**sb / **0b0**sb / **0x0**sb
- BsoUByte
  - **0**ub / **0b0**ub / **0x0**ub
- BsoBool
  - **true** or **false**
- BsoShort
  - **0**ss / **0b0**ss / **0x0**ss
- BsoUShort
  - **0**us / **0b0**us / **0x0**us
- BsoInt
  - **0** / **0b0** / **0x0**
- BsoUInt
  - **0**u / **0b0**u / **0x0**u
- BsoLong
  - **0**sl / **0b0**sl / **0x0**sl
- BsoULong
  - **0**ul / **0b0**ul / **0x0**ul
- BsoFloat
  - **0.0**f
- BsoDouble
  - **0.0** / **0.0**d
- BsoString
  - single line
    - "**text**"
  - multiline
    - """**text**"""
- BsoMap
  - { key: **value** , }
  - { "key": **value** , }
- BsoList
  - [**value** , ]
- BsoByteArray
  - [B; **value** , ]
- BsoUByteArray
  - [UB; **value** , ]
- BsoShortArray
  - [S; **value** , ]
- BsoUShortArray
  - [US; **value** , ]
- BsoIntArray
  - [I; **value** , ]
- BsoUIntArray
  - [UI; **value** , ]
- BsoLongArray
  - [L; **value** , ]
- BsoULongArray
  - [UL; **value** , ]
- BsoFloatArray
  - [F; **value** , ]
- BsoUFloatArray
  - [UF; **value** , ]
- BsoDoubleArray
  - [D; **value** , ]
- BsoUDoubleArray
  - [UD; **value** , ]

## Custom

BsoCustom\<T>
- BsoCustomType\<T>

### SBSO

(**\<name>**; **value**)