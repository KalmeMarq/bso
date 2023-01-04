import { Byte, Int, Long, Short, UShort } from './constants.ts';
import {
  BSOByte,
  BSOByteArray,
  BSODouble,
  BSODoubleArray,
  BSOElement,
  BSOFloat,
  BSOFloatArray,
  BSOInt,
  BSOIntArray,
  BSOList,
  BSOLong,
  BSOLongArray,
  BSOMap,
  BSOShort,
  BSOShortArray,
  BSOString,
  BSOType,
  BSOTypes
} from './types.ts';

function BSOByte(value: number): BSOByte;
function BSOByte(value: boolean): BSOByte;
function BSOByte(value: number | boolean): BSOByte {
  if (typeof value === 'number') {
    if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
      throw RangeError(`Byte range is ${Byte.MIN_VALUE} to ${Byte.MAX_VALUE}. Number was ${value}`);
    }
    return { type: BSOTypes.BYTE, value };
  } else {
    return { type: BSOTypes.BYTE, value: value ? 1 : 0 };
  }
}

function BSOShort(value: number): BSOShort {
  if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
    throw RangeError(`Short range is ${Short.MIN_VALUE} to ${Short.MAX_VALUE}. Number was ${value}`);
  }
  return { type: BSOTypes.SHORT, value };
}

function BSOInt(value: number): BSOInt {
  if (value < Int.MIN_VALUE || value > Int.MAX_VALUE) {
    throw RangeError(`Int range is ${Int.MIN_VALUE} to ${Int.MAX_VALUE}. Number was ${value}`);
  }
  return { type: BSOTypes.INT, value };
}

function BSOLong(value: bigint): BSOLong;
function BSOLong(value: number): BSOLong;
function BSOLong(value: bigint | number): BSOLong {
  if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
    throw RangeError(`Long range is ${Long.MIN_VALUE} to ${Long.MAX_VALUE}. Number was ${value}`);
  }
  return { type: BSOTypes.LONG, value: typeof value === 'bigint' ? value : BigInt(value) };
}

function BSOFloat(value: number): BSOFloat {
  return { type: BSOTypes.FLOAT, value };
}

function BSODouble(value: number): BSODouble {
  return { type: BSOTypes.DOUBLE, value };
}

function BSOString(value: string, indefiniteLength?: boolean): BSOString {
  if (value.length > UShort.MAX_VALUE) {
    throw RangeError(`The maximium length a string is allowed to have is ${UShort.MAX_VALUE}`);
  }
  return { type: BSOTypes.STRING, value, indefiniteLength };
}

function BSOMap(entries: Record<string, BSOElement | string | bigint | number | boolean>): BSOMap {
  const map: Record<string, BSOElement> = {};

  for (const [key, value] of Object.entries(entries)) {
    switch (typeof value) {
      case 'bigint':
        map[key] = BSOLong(value);
        break;
      case 'number':
        map[key] = Number.isInteger(value) ? BSOInt(value) : BSODouble(value);
        break;
      case 'string':
        map[key] = BSOString(value);
        break;
      case 'boolean':
        map[key] = BSOByte(value);
        break;
      default:
        map[key] = value;
        break;
    }
  }

  return { type: BSOTypes.MAP, entries: map, indefiniteLength: true };
}

function BSOList(values: BSOElement[], indefiniteLength?: boolean): BSOList {
  if (values.length > 0) {
    const type: BSOType = values[0].type;
    let nottype: BSOType = values[0].type;

    if (
      !values.every((v) => {
        if (v.type === type) {
          return true;
        } else {
          nottype = v.type;
          return false;
        }
      })
    ) {
      throw new Error('List can only contain a single type. First element was ' + type + ' but ' + nottype + ' was found in later items');
    }
  }

  return { type: BSOTypes.LIST, values, indefiniteLength };
}

function BSOByteArray(values: number[], indefiniteLength?: boolean): BSOByteArray {
  return { type: BSOTypes.BYTE_ARRAY, values, indefiniteLength };
}

function BSOShortArray(values: number[], indefiniteLength?: boolean): BSOShortArray {
  return { type: BSOTypes.SHORT_ARRAY, values, indefiniteLength };
}

function BSOIntArray(values: number[], indefiniteLength?: boolean): BSOIntArray {
  return { type: BSOTypes.INT_ARRAY, values, indefiniteLength };
}

function BSOLongArray(values: bigint[], indefiniteLength?: boolean): BSOLongArray {
  return { type: BSOTypes.LONG_ARRAY, values, indefiniteLength };
}

function BSOFloatArray(values: number[], indefiniteLength?: boolean): BSOFloatArray {
  return { type: BSOTypes.FLOAT_ARRAY, values, indefiniteLength };
}

function BSODoubleArray(values: number[], indefiniteLength?: boolean): BSODoubleArray {
  return { type: BSOTypes.DOUBLE_ARRAY, values, indefiniteLength };
}

export { BSOByte, BSOShort, BSOInt, BSOLong, BSOFloat, BSODouble, BSOString, BSOMap, BSOList, BSOByteArray, BSOShortArray, BSOIntArray, BSOLongArray, BSOFloatArray, BSODoubleArray };
