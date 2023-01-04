import { TagIDs } from './constants.ts';

export const BSOTypes = {
  NULL: 'Null',
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
  DOUBLE_ARRAY: 'DoubleArray',
  END: 'End'
} as const;

export type BSOTypeEnum = keyof typeof BSOTypes;
export type BSOType = typeof BSOTypes[BSOTypeEnum];

export const BSOTypeIDs: Record<BSOType, number> = {
  Null: TagIDs.NULL,
  Byte: TagIDs.BYTE,
  Short: TagIDs.SHORT,
  Int: TagIDs.INT,
  Long: TagIDs.LONG,
  Float: TagIDs.FLOAT,
  Double: TagIDs.DOUBLE,
  String: TagIDs.STRING,
  Map: TagIDs.MAP,
  List: TagIDs.LIST,
  ByteArray: TagIDs.BYTE_ARRAY,
  ShortArray: TagIDs.SHORT_ARRAY,
  IntArray: TagIDs.INT_ARRAY,
  LongArray: TagIDs.LONG_ARRAY,
  FloatArray: TagIDs.FLOAT_ARRAY,
  DoubleArray: TagIDs.DOUBLE_ARRAY,
  End: TagIDs.END
} as const;

export const BSOIDsType: Record<number, BSOType> = {};
Object.entries(BSOTypeIDs).forEach(([k, v]) => {
  BSOIDsType[v] = k as BSOType;
});

export type BSONull = { type: 'Null'; value: number };
export type BSOByte = { type: 'Byte'; value: number };
export type BSOShort = { type: 'Short'; value: number };
export type BSOInt = { type: 'Int'; value: number };
export type BSOLong = { type: 'Long'; value: bigint };
export type BSOFloat = { type: 'Float'; value: number };
export type BSODouble = { type: 'Double'; value: number };
export type BSOString = { type: 'String'; value: string; indefiniteLength?: boolean };
export type BSOMap = { type: 'Map'; entries: Record<string, BSOElement>; indefiniteLength?: boolean };
export type BSOList = { type: 'List'; values: BSOElement[]; indefiniteLength?: boolean };
export type BSOByteArray = { type: 'ByteArray'; values: number[]; indefiniteLength?: boolean };
export type BSOShortArray = { type: 'ShortArray'; values: number[]; indefiniteLength?: boolean };
export type BSOIntArray = { type: 'IntArray'; values: number[]; indefiniteLength?: boolean };
export type BSOLongArray = { type: 'LongArray'; values: bigint[]; indefiniteLength?: boolean };
export type BSOFloatArray = { type: 'FloatArray'; values: number[]; indefiniteLength?: boolean };
export type BSODoubleArray = { type: 'DoubleArray'; values: number[]; indefiniteLength?: boolean };

export type BSOArray = BSOByteArray | BSOShortArray | BSOIntArray | BSOLongArray | BSOFloatArray | BSODoubleArray;

export type BSOElement =
  | BSONull
  | BSOByte
  | BSOShort
  | BSOInt
  | BSOLong
  | BSOFloat
  | BSODouble
  | BSOString
  | BSOMap
  | BSOList
  | BSOByteArray
  | BSOShortArray
  | BSOIntArray
  | BSOLongArray
  | BSOFloatArray
  | BSODoubleArray;

export interface WriteOptions {
  compress?: boolean;
  compressionLevel?: number;
  allowVarNum?: boolean;
  allowVarLength?: boolean;
  allowIndefinitiveLength?: boolean;
  allowStringVarLength?: boolean;
  allowMapKeyStringVarLength?: boolean;
}
