import { gzip } from '../deps.ts';
import { BufferWriter } from './BufferWriter.ts';
import { Byte, Int, Short, TagIDs, UByte } from './constants.ts';
import { BSOElement, BSOTypeIDs, WriteOptions } from './types.ts';
import { getElementAdditionalData, writeLength } from './utils.ts';

function writeBSOElement(output: BufferWriter, element: BSOElement, writeOptions: WriteOptions) {
  switch (element.type) {
    case 'DoubleArray':
      // if (writeOptions.allowIndefinitiveLength) {
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeDouble(element.values[i]);
      if (element.indefiniteLength) output.writeByte(TagIDs.END);
      // } else {
      //   if (writeOptions.allowVarLength) writeLength(output, element.values.length);
      //   else output.writeInt(element.values.length);

      //   for (let i = 0; i < element.values.length; i++) output.writeDouble(element.values[i]);
      // }
      break;
    case 'FloatArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeFloat(element.values[i]);
      if (element.indefiniteLength) output.writeByte(TagIDs.END);
      break;
    case 'LongArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeLong(element.values[i]);
      if (element.indefiniteLength) output.writeByte(TagIDs.END);
      break;
    case 'IntArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeInt(element.values[i]);
      if (element.indefiniteLength) output.writeByte(TagIDs.END);
      break;
    case 'ShortArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      for (let i = 0; i < element.values.length; i++) output.writeShort(element.values[i]);
      if (element.indefiniteLength) output.writeByte(TagIDs.END);
      break;
    case 'ByteArray':
      if (!element.indefiniteLength) writeLength(output, element.values.length);
      output.write(element.values);
      if (element.indefiniteLength) output.writeByte(TagIDs.END);
      break;
    case 'Map':
      if (!element.indefiniteLength) writeLength(output, Object.keys(element.entries).length);

      for (const [key, value] of Object.entries(element.entries)) {
        output.writeByte(BSOTypeIDs[value.type] + getElementAdditionalData(value, writeOptions));
        output.writeUTF(key);
        writeBSOElement(output, value, writeOptions);
      }

      if (element.indefiniteLength) output.writeByte(TagIDs.END);

      break;
    case 'String':
      // output.writeUTF(element.value);
      output.writeVarUTF(element.value, element.indefiniteLength ? 'none' : element.value.length <= UByte.MAX_VALUE ? 'ubyte' : 'ushort');

      if (element.indefiniteLength) output.writeByte(0);
      break;
    case 'Double':
      output.writeDouble(element.value);
      break;
    case 'Float':
      output.writeFloat(element.value);
      break;
    case 'Long': {
      if (writeOptions.allowVarNum) {
        if (element.value <= Byte.MAX_VALUE) {
          output.writeByte(Number(element.value & 0xffn));
        } else if (element.value <= Short.MAX_VALUE) {
          output.writeShort(Number(element.value & 0xffffn));
        } else if (element.value <= Int.MAX_VALUE) {
          output.writeInt(Number(element.value & 0xffffffffffffffffn));
        } else {
          output.writeLong(element.value);
        }
      } else {
        output.writeLong(element.value);
      }
      break;
    }
    case 'Int': {
      if (writeOptions.allowVarNum) {
        if (element.value <= Byte.MAX_VALUE) {
          output.writeByte(element.value & 0xff);
        } else if (element.value <= Short.MAX_VALUE) {
          output.writeShort(element.value & 0xffff);
        } else {
          output.writeInt(element.value);
        }
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

const _defaultWriteOptions: WriteOptions = {
  allowVarNum: true,
  allowIndefinitiveLength: true,
  allowVarLength: true,
  compress: false,
  allowStringVarLength: false,
  allowMapKeyStringVarLength: false
};

export function writeBSO(element: BSOElement, options?: WriteOptions) {
  const output = new BufferWriter();
  const writeOptions: WriteOptions = { ..._defaultWriteOptions, ...(options ?? {}) };

  output.writeByte(BSOTypeIDs[element.type] + getElementAdditionalData(element, writeOptions));
  writeBSOElement(output, element, writeOptions);

  if (options?.compress) {
    return gzip(output.finish(), {
      level: options?.compressionLevel
    });
  }

  return output.finish();
}
