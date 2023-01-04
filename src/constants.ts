export const Byte = {
  MIN_VALUE: -(0xff + 1) / 2,
  MAX_VALUE: (0xff - 1) / 2
};

export const UByte = {
  MIN_VALUE: 0,
  MAX_VALUE: 0xff
};

export const Short = {
  MIN_VALUE: -(0xffff + 1) / 2,
  MAX_VALUE: (0xffff - 1) / 2
};

export const UShort = {
  MIN_VALUE: 0,
  MAX_VALUE: 0xffff
};

export const Int = {
  MIN_VALUE: -(0xffffffff + 1) / 2,
  MAX_VALUE: (0xffffffff - 1) / 2
};

export const UInt = {
  MIN_VALUE: 0,
  MAX_VALUE: 0xffffffff
};

export const Long = {
  MIN_VALUE: -(0xffffffffffffffffn + 1n) / 2n,
  MAX_VALUE: (0xffffffffffffffffn - 1n) / 2n
};

export const ULong = {
  MIN_VALUE: 0,
  MAX_VALUE: 0xffffffffffffffffn
};

export const AdditionalData = {
  EMPTY: 0x00,
  // Map/Arrays/List
  BYTE_LENGTH: 0x20,
  SHORT_LENGTH: 0x10,
  INT_LENGTH: 0x00,
  INDEFINITE_LENGTH: 0x30,
  // Int/Long
  VARNUM_BYTE: 0x30,
  VARNUM_SHORT: 0x20,
  VARNUM_INT: 0x10,
  // String
  STR_SHORT_LENGTH: 0x00,
  STR_BYTE_LENGTH: 0x10,
  STR_INDEFINITE_LENGTH: 0x20
};

export const TagIDs = {
  NULL: 0x00,
  BYTE: 0x01,
  SHORT: 0x02,
  INT: 0x03,
  LONG: 0x04,
  FLOAT: 0x05,
  DOUBLE: 0x06,
  STRING: 0x07,
  MAP: 0x08,
  LIST: 0x09,
  BYTE_ARRAY: 0x0a,
  SHORT_ARRAY: 0x0b,
  INT_ARRAY: 0x0c,
  LONG_ARRAY: 0x0d,
  FLOAT_ARRAY: 0x0e,
  DOUBLE_ARRAY: 0x0f,
  END: 0x10 // More like Null with additional data but meh
};

export const ANSI = {
  Black: '\x1b[30m',
  Red: '\x1b[31m',
  Green: '\x1b[32m',
  Yellow: '\x1b[33m',
  Blue: '\x1b[34m',
  Magenta: '\x1b[35m',
  Cyan: '\x1b[36m',
  White: '\x1b[37m',
  Reset: '\x1b[0m',
  BrightBlack: '\x1b[30;1m',
  BrightRed: '\x1b[31;1m',
  BrightGreen: '\x1b[32;1m',
  BrightYellow: '\x1b[33;1m',
  BrightBlue: '\x1b[34;1m',
  BrightMagenta: '\x1b[35;1m',
  BrightCyan: '\x1b[36;1m',
  BrightWhite: '\x1b[37;1m'
};
