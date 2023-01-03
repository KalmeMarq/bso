import { BufferReader } from './BufferReader.ts';
import { BufferWriter } from './BufferWriter.ts';
import { gunzip, gzip } from './deps.ts';

const BSOTypes = {
  BYTE: 'Byte',
  SHORT: 'Short',
  INT: 'Int',
  LONG: 'Long',
  FLOAT: 'Float',
  DOUBLE: 'Double',
  STRING: 'String',
  MAP: 'Map',
  LIST: 'List',
  BYTE_ARRAY: 'ByteArray',
  SHORT_ARRAY: 'ShortArray',
  INT_ARRAY: 'IntArray',
  LONG_ARRAY: 'LongArray',
  FLOAT_ARRAY: 'FloatArray',
  DOUBLE_ARRAY: 'DoubleArray'
} as const;

type BSOTypeEnum = keyof typeof BSOTypes;
type BSOType = typeof BSOTypes[BSOTypeEnum];

const BSOTypeIDs: Record<string, number> = {
  Byte: 0x01,
  Short: 0x02,
  Int: 0x03,
  Long: 0x04,
  Float: 0x05,
  Double: 0x06,
  String: 0x07,
  Map: 0x08,
  List: 0x09,
  ByteArray: 0x0a,
  ShortArray: 0x0b,
  IntArray: 0x0c,
  LongArray: 0x0d,
  FloatArray: 0x0e,
  DoubleArray: 0x0f
};

const BSOIDsType: Record<number, BSOType> = {
  0x01: 'Byte',
  0x02: 'Short',
  0x03: 'Int',
  0x04: 'Long',
  0x05: 'Float',
  0x06: 'Double',
  0x07: 'String',
  0x08: 'Map',
  0x09: 'List',
  0x0a: 'ByteArray',
  0x0b: 'ShortArray',
  0x0c: 'IntArray',
  0x0d: 'LongArray',
  0x0e: 'FloatArray',
  0x0f: 'DoubleArray'
};

type BSONull = { type: 'Null'; value: number };
type BSOByte = { type: 'Byte'; value: number };
type BSOShort = { type: 'Short'; value: number };
type BSOInt = { type: 'Int'; value: number };
type BSOLong = { type: 'Long'; value: bigint };
type BSOFloat = { type: 'Float'; value: number };
type BSODouble = { type: 'Double'; value: number };
type BSOString = { type: 'String'; value: string };
type BSOMap = { type: 'Map'; entries: Record<string, BSOElement>; indefiniteLength?: boolean };
type BSOByteArray = { type: 'ByteArray'; values: number[]; indefiniteLength?: boolean };
type BSOShortArray = { type: 'ShortArray'; values: number[]; indefiniteLength?: boolean };
type BSOIntArray = { type: 'IntArray'; values: number[]; indefiniteLength?: boolean };
type BSOLongArray = { type: 'LongArray'; values: bigint[]; indefiniteLength?: boolean };
type BSOFloatArray = { type: 'FloatArray'; values: number[]; indefiniteLength?: boolean };
type BSODoubleArray = { type: 'DoubleArray'; values: number[]; indefiniteLength?: boolean };
type BSOElement =
  | BSONull
  | BSOByte
  | BSOShort
  | BSOInt
  | BSOLong
  | BSOFloat
  | BSODouble
  | BSOString
  | BSOMap
  | BSOByteArray
  | BSOShortArray
  | BSOIntArray
  | BSOLongArray
  | BSOFloatArray
  | BSODoubleArray;

const BYTE_MIN = -128;
const BYTE_MAX = 127;
const UBYTE_MAX = 0xff;
const SHORT_MIN = -32768;
const SHORT_MAX = 32767;
const USHORT_MAX = 0xffff;
const INT_MIN = -2147483648;
const INT_MAX = 2147483647;
const LONG_MIN = -9223372036854775808n;
const LONG_MAX = 9223372036854775807n;

function BSOByte(value: number): BSOByte;
function BSOByte(value: boolean): BSOByte;
function BSOByte(value: number | boolean): BSOByte {
  if (typeof value === 'number') {
    if (value < BYTE_MIN || value > BYTE_MAX) {
      throw RangeError(`Byte range is ${BYTE_MIN} to ${BYTE_MAX}. Number was ${value}`);
    }
    return { type: BSOTypes.BYTE, value };
  } else {
    return { type: BSOTypes.BYTE, value: value ? 1 : 0 };
  }
}

function BSOShort(value: number): BSOShort {
  if (value < SHORT_MIN || value > SHORT_MAX) {
    throw RangeError(`Short range is ${SHORT_MIN} to ${SHORT_MAX}. Number was ${value}`);
  }
  return { type: BSOTypes.SHORT, value };
}

function BSOInt(value: number): BSOInt {
  if (value < INT_MIN || value > INT_MAX) {
    throw RangeError(`Int range is ${INT_MIN} to ${INT_MAX}. Number was ${value}`);
  }
  return { type: BSOTypes.INT, value };
}

function BSOLong(value: bigint): BSOLong;
function BSOLong(value: number): BSOLong;
function BSOLong(value: bigint | number): BSOLong {
  if (value < LONG_MIN || value > LONG_MAX) {
    throw RangeError(`Long range is ${LONG_MIN} to ${LONG_MAX}. Number was ${value}`);
  }
  return { type: BSOTypes.LONG, value: typeof value === 'bigint' ? value : BigInt(value) };
}

function BSOFloat(value: number): BSOFloat {
  return { type: BSOTypes.FLOAT, value };
}

function BSODouble(value: number): BSODouble {
  return { type: BSOTypes.DOUBLE, value };
}

function BSOString(value: string): BSOString {
  if (value.length > USHORT_MAX) {
    throw RangeError(`The maximium lenght a string is allowed to have is ${USHORT_MAX}`);
  }
  return { type: BSOTypes.STRING, value };
}

function BSOMap(entries: Record<string, BSOElement>): BSOMap {
  return { type: BSOTypes.MAP, entries, indefiniteLength: true };
}

function BSOByteArray(values: number[]): BSOByteArray {
  return { type: BSOTypes.BYTE_ARRAY, values };
}

function BSOShortArray(values: number[]): BSOShortArray {
  return { type: BSOTypes.SHORT_ARRAY, values };
}

function BSOIntArray(values: number[]): BSOIntArray {
  return { type: BSOTypes.INT_ARRAY, values };
}

function BSOLongArray(values: bigint[]): BSOLongArray {
  return { type: BSOTypes.LONG_ARRAY, values };
}

function BSOFloatArray(values: number[]): BSOFloatArray {
  return { type: BSOTypes.FLOAT_ARRAY, values };
}

function BSODoubleArray(values: number[]): BSODoubleArray {
  return { type: BSOTypes.DOUBLE_ARRAY, values };
}

const BYTE_LENGTH = 0x20;
const SHORT_LENGTH = 0x10;
const INT_LENGTH = 0x00;
const INDEFINITE_LENGTH = 0x30;
const VARNUM_BYTE = 0x30;
const VARNUM_SHORT = 0x20;
const VARNUM_INT = 0x10;
const END_TYPE_ID = 0x10;

function writeLength(output: BufferWriter, length: number) {
  if (length <= UBYTE_MAX) {
    output.writeByte(length);
  } else if (length <= USHORT_MAX) {
    output.writeShort(length);
  } else {
    output.writeInt(length);
  }
}

function readLength(input: BufferReader, additionalData: number) {
  switch (additionalData) {
    case BYTE_LENGTH:
      return input.readUByte();
    case SHORT_LENGTH:
      return input.readUShort();
    case INT_LENGTH:
      return input.readInt();
    default: {
      throw new Error('Unknown length type');
    }
  }
}

function lengthAdditionalData(length: number, indefiniteLength?: boolean) {
  if (indefiniteLength) return INDEFINITE_LENGTH;
  if (length <= UBYTE_MAX) {
    return BYTE_LENGTH;
  } else if (length <= USHORT_MAX) {
    return SHORT_LENGTH;
  }
  return 0;
}

function getElementAdditionalData(element: BSOElement) {
  switch (element.type) {
    case 'Long': {
      if (element.value <= BYTE_MAX) {
        return VARNUM_BYTE;
      } else if (element.value <= SHORT_MAX) {
        return VARNUM_SHORT;
      } else if (element.value <= INT_MAX) {
        return VARNUM_INT;
      }
      return 0;
    }
    case 'Int': {
      if (element.value <= BYTE_MAX) {
        return VARNUM_BYTE;
      } else if (element.value <= SHORT_MAX) {
        return VARNUM_SHORT;
      }
      return 0;
    }
    case 'Map':
      return lengthAdditionalData(Object.keys(element.entries).length, element.indefiniteLength);
    case 'DoubleArray':
    case 'FloatArray':
    case 'LongArray':
    case 'IntArray':
    case 'ShortArray':
    case 'ByteArray':
      return lengthAdditionalData(element.values.length, element.indefiniteLength);
    default:
      return 0;
  }
}

function writeBSOElement(output: BufferWriter, element: BSOElement) {
  switch (element.type) {
    case 'DoubleArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeDouble(element.values[i]);
      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);
    case 'FloatArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeFloat(element.values[i]);
      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);
      break;
    case 'LongArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeLong(element.values[i]);
      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);
      break;
    case 'IntArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeInt(element.values[i]);
      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);
      break;
    case 'ShortArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeShort(element.values[i]);
      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);
    case 'ByteArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      output.write(element.values);
      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);
      break;
    case 'Map':
      if (!element.indefiniteLength) writeLength(output, Object.keys(element.entries).length);

      for (const [key, value] of Object.entries(element.entries)) {
        output.writeByte(BSOTypeIDs[value.type] + getElementAdditionalData(value));
        output.writeUTF(key);
        writeBSOElement(output, value);
      }

      if (element.indefiniteLength) output.writeByte(END_TYPE_ID);

      break;
    case 'String':
      output.writeUTF(element.value);
      break;
    case 'Double':
      output.writeDouble(element.value);
      break;
    case 'Float':
      output.writeFloat(element.value);
      break;
    case 'Long': {
      if (element.value <= BYTE_MAX) {
        output.writeByte(Number(element.value & 0xffn));
      } else if (element.value <= SHORT_MAX) {
        output.writeShort(Number(element.value & 0xffffn));
      } else if (element.value <= INT_MAX) {
        output.writeInt(Number(element.value & 0xffffffffffffffffn));
      } else {
        output.writeLong(element.value);
      }
      break;
    }
    case 'Int': {
      if (element.value <= BYTE_MAX) {
        output.writeByte(element.value & 0xff);
      } else if (element.value <= SHORT_MAX) {
        output.writeShort(element.value & 0xffff);
      } else {
        output.writeInt(element.value);
      }
      break;
    }
    case 'Short':
      output.writeShort(element.value);
      break;
    case 'Byte':
      output.writeByte(element.value);
      break;
  }
}

function writeBSO(element: BSOElement, options?: { compress?: boolean; compressionLevel?: number }) {
  const output = new BufferWriter();

  output.writeByte(BSOTypeIDs[element.type] + getElementAdditionalData(element));
  writeBSOElement(output, element);

  if (options?.compress) {
    return gzip(output.finish(), {
      level: options?.compressionLevel
    });
  }

  return output.finish();
}

const ANSI = {
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

function bsoToJson(element: BSOElement): string;
function bsoToJson<T = unknown>(element: BSOElement, parse: true): T;
function bsoToJson(element: BSOElement, parse?: boolean): any {
  let data = '';

  function bsoElToJson(element: BSOElement) {
    switch (element.type) {
      case 'DoubleArray':
      case 'FloatArray':
      case 'LongArray':
      case 'IntArray':
      case 'ShortArray':
      case 'ByteArray': {
        data += '[';
        let i = 0;
        for (const vl of element.values) {
          if (i != 0) data += ',';
          data += vl;
          i++;
        }
        data += ']';
        break;
      }
      case 'Map': {
        data += '{';

        let i = 0;
        for (const [key, value] of Object.entries(element.entries)) {
          if (i != 0) data += ',';
          data += `"${key}":`;
          bsoElToJson(value);
          i++;
        }

        data += '}';
        break;
      }
      case 'String':
        data += `"${element.value}"`;
        break;
      case 'Double':
      case 'Float':
      case 'Long':
      case 'Int':
      case 'Short':
      case 'Byte':
        data += element.value;
        break;
    }
  }

  bsoElToJson(element);

  if (parse) {
    return JSON.parse(data);
  } else {
    return JSON.stringify(JSON.parse(data));
    // return data;
  }
}

function bsoToSBSO(element: BSOElement, useAnsi?: boolean) {
  let output = '';

  function bsoElToSBSO(element: BSOElement) {
    switch (element.type) {
      case 'DoubleArray':
        if (useAnsi) output += `${ANSI.BrightWhite}[D;${ANSI.Reset}`;
        else output += '[D;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          bsoElToSBSO({ type: 'Double', value: element.values[i] });
        }

        if (useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'FloatArray':
        if (useAnsi) output += `${ANSI.BrightWhite}[F;${ANSI.Reset}`;
        else output += '[F;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          bsoElToSBSO({ type: 'Float', value: element.values[i] });
        }

        if (useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'LongArray':
        if (useAnsi) output += `${ANSI.BrightWhite}[L;${ANSI.Reset}`;
        else output += '[L;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          bsoElToSBSO({ type: 'Long', value: element.values[i] });
        }

        if (useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'IntArray':
        if (useAnsi) output += `${ANSI.BrightWhite}[I;${ANSI.Reset}`;
        else output += '[I;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          bsoElToSBSO({ type: 'Int', value: element.values[i] });
        }

        if (useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'ShortArray':
        if (useAnsi) output += `${ANSI.BrightWhite}[S;${ANSI.Reset}`;
        else output += '[S;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          bsoElToSBSO({ type: 'Short', value: element.values[i] });
        }

        if (useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'ByteArray':
        if (useAnsi) output += `${ANSI.BrightWhite}[B;${ANSI.Reset}`;
        else output += '[B;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          bsoElToSBSO({ type: 'Byte', value: element.values[i] });
        }

        if (useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'Map': {
        if (useAnsi) output += `${ANSI.BrightWhite}{${ANSI.Reset}`;
        else output += '{';

        let i = 0;
        for (const [key, value] of Object.entries(element.entries)) {
          if (i != 0) {
            if (useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';
          }

          if (useAnsi) output += `${ANSI.Cyan}${key}${ANSI.Reset}${ANSI.BrightWhite}:${ANSI.Reset}`;
          else output += `${key}:`;
          bsoElToSBSO(value);

          i++;
        }

        if (useAnsi) output += `${ANSI.BrightWhite}}${ANSI.Reset}`;
        else output += '}';
        break;
      }
      case 'String':
        if (useAnsi) output += `${ANSI.BrightWhite}"${ANSI.Reset}${ANSI.BrightGreen}${element.value}${ANSI.Reset}${ANSI.BrightWhite}"${ANSI.Reset}`;
        else output += `"${element.value}"`;
        break;
      case 'Double':
        if (useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}d${ANSI.Reset}`;
        else output += `${element.value}d`;
        break;
      case 'Float':
        if (useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}f${ANSI.Reset}`;
        else output += `${element.value}f`;
        break;
      case 'Long':
        if (useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}L${ANSI.Reset}`;
        else output += `${element.value}L`;
        break;
      case 'Int':
        if (useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Reset}`;
        else output += `${element.value}`;
        break;
      case 'Short':
        if (useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}b${ANSI.Reset}`;
        else output += `${element.value}s`;
        break;
      case 'Byte':
        if (useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}b${ANSI.Reset}`;
        else output += `${element.value}b`;
        break;
    }
  }

  bsoElToSBSO(element);

  if (useAnsi) {
    return output;
  } else {
    return output;
  }
}

function readBSOElement(input: BufferReader, type: BSOType, additionalData: number): BSOElement {
  switch (type) {
    case 'DoubleArray': {
      if (additionalData === INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readDouble()) != END_TYPE_ID) {
          vls.push(b);
        }

        return BSODoubleArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: number[] = [];
        for (let i = 0; i < len; i++) vls.push(input.readDouble());
        return BSODoubleArray(vls);
      }
    }
    case 'FloatArray': {
      if (additionalData === INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readFloat()) != END_TYPE_ID) {
          vls.push(b);
        }

        return BSOFloatArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: number[] = [];
        for (let i = 0; i < len; i++) vls.push(input.readFloat());
        return BSOFloatArray(vls);
      }
    }
    case 'LongArray': {
      if (additionalData === INDEFINITE_LENGTH) {
        const vls: bigint[] = [];

        let b = 0n;
        while ((b = input.readLong()) != BigInt(END_TYPE_ID)) {
          vls.push(b);
        }

        return BSOLongArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: bigint[] = [];
        for (let i = 0; i < len; i++) vls.push(input.readLong());
        return BSOLongArray(vls);
      }
    }
    case 'IntArray': {
      if (additionalData === INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readInt()) != END_TYPE_ID) {
          vls.push(b);
        }

        return BSOIntArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: number[] = [];
        for (let i = 0; i < len; i++) vls.push(input.readInt());
        return BSOIntArray(vls);
      }
    }
    case 'ShortArray': {
      if (additionalData === INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readShort()) != END_TYPE_ID) {
          vls.push(b);
        }

        return BSOShortArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: number[] = [];
        for (let i = 0; i < len; i++) vls.push(input.readShort());
        return BSOShortArray(vls);
      }
    }
    case 'ByteArray': {
      if (additionalData === INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readByte()) != END_TYPE_ID) {
          vls.push(b);
        }

        return BSOByteArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: number[] = [];
        for (let i = 0; i < len; i++) vls.push(input.readByte());
        return BSOByteArray(vls);
      }
    }
    case 'Map': {
      if (additionalData === INDEFINITE_LENGTH) {
        const map: Record<string, BSOElement> = {};

        let b = 0;
        while ((b = input.readByte()) != END_TYPE_ID) {
          const type = BSOIDsType[b & 0x0f];
          map[input.readUTF()] = readBSOElement(input, type, b & 0xf0);
        }

        return BSOMap(map);
      } else {
        const len = readLength(input, additionalData);
        const map: Record<string, BSOElement> = {};

        for (let i = 0; i < len; i++) {
          const b = input.readByte();
          const type = BSOIDsType[b & 0x0f];
          map[input.readUTF()] = readBSOElement(input, type, b & 0xf0);
        }
      }
    }
    case 'String':
      return BSOString(input.readUTF());
    case 'Double':
      return BSODouble(input.readDouble());
    case 'Float':
      return BSOFloat(input.readFloat());
    case 'Long': {
      if (additionalData == VARNUM_BYTE) return BSOLong(input.readByte());
      else if (additionalData == VARNUM_SHORT) return BSOLong(input.readShort());
      else if (additionalData == VARNUM_INT) return BSOLong(input.readInt());
      return BSOLong(input.readLong());
    }
    case 'Int': {
      if (additionalData == VARNUM_BYTE) return BSOInt(input.readByte());
      else if (additionalData == VARNUM_SHORT) return BSOInt(input.readShort());
      return BSOInt(input.readInt());
    }
    case 'Short':
      return BSOShort(input.readShort());
    case 'Byte':
      return BSOByte(input.readByte());
    default: {
      throw new Error(`Unreachable code ` + type);
    }
  }
}

function readBSO(data: Uint8Array, compressed?: boolean, showSus?: boolean) {
  const input = new BufferReader(compressed ? gunzip(data) : data);
  const id = input.readByte();
  const element = readBSOElement(input, BSOIDsType[id & 0x0f], id & 0xf0);
  if (showSus && !input.isFullyRead()) {
    console.log("There's more stuff at the end of the file... that's kinda sus:");
    console.log('    ' + input.readToEndAsString());
  }
  return element;
}

export {
  BSOByte,
  BSOShort,
  BSOInt,
  BSOLong,
  BSOFloat,
  BSODouble,
  BSOString,
  BSOMap,
  BSOByteArray,
  BSOShortArray,
  BSOIntArray,
  BSOLongArray,
  BSOFloatArray,
  BSODoubleArray,
  // Helper Functions
  writeBSO,
  bsoToJson,
  bsoToSBSO,
  readBSO
};
