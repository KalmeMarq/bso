import { gunzip } from '../deps.ts';
import { BSOByte, BSOByteArray, BSODouble, BSODoubleArray, BSOFloat, BSOFloatArray, BSOInt, BSOIntArray, BSOLong, BSOLongArray, BSOMap, BSOShort, BSOShortArray, BSOString } from './bso.ts';
import { BufferReader } from './BufferReader.ts';
import { AdditionalData, TagIDs } from './constants.ts';
import { BSOElement, BSOIDsType, BSOType } from './types.ts';
import { readLength } from './utils.ts';

function readBSOElement(input: BufferReader, type: BSOType, additionalData: number): BSOElement {
  switch (type) {
    case 'DoubleArray': {
      // console.log(type, additionalData);

      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readDouble()) != TagIDs.END) {
          vls.push(b);
        }

        return BSODoubleArray(vls);
      } else {
        const len = readLength(input, additionalData);
        let vls: number[] = [];

        for (let i = 0; i < len; i++) {
          vls.push(input.readDouble());
        }
        return BSODoubleArray(vls);
      }
    }
    case 'FloatArray': {
      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readFloat()) != TagIDs.END) {
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
      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const vls: bigint[] = [];

        let b = 0n;
        while ((b = input.readLong()) != BigInt(TagIDs.END)) {
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
      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readInt()) != TagIDs.END) {
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
      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readShort()) != TagIDs.END) {
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
      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const vls: number[] = [];

        let b = 0;
        while ((b = input.readByte()) != TagIDs.END) {
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
      if (additionalData === AdditionalData.INDEFINITE_LENGTH) {
        const map: Record<string, BSOElement> = {};

        let j = false;
        let b = 0;
        while ((b = input.readByte()) != TagIDs.END) {
          // console.log(b & 0x0f, b & 0xf0);
          const type = BSOIDsType[b & 0x0f];
          if (!j) j = true;
          else {
            console.log(input.getRemainingData());
          }

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
    case 'String': {
      if (additionalData === AdditionalData.STR_INDEFINITE_LENGTH) {
        return BSOString(input.readVarUTF('none'));
      } else if (additionalData == AdditionalData.STR_BYTE_LENGTH) {
        return BSOString(input.readVarUTF('ubyte'));
      }

      return BSOString(input.readUTF());
    }
    case 'Double':
      return BSODouble(input.readDouble());
    case 'Float':
      return BSOFloat(input.readFloat());
    case 'Long': {
      if (additionalData == AdditionalData.VARNUM_BYTE) return BSOLong(input.readByte());
      else if (additionalData == AdditionalData.VARNUM_SHORT) return BSOLong(input.readShort());
      else if (additionalData == AdditionalData.VARNUM_INT) return BSOLong(input.readInt());
      return BSOLong(input.readLong());
    }
    case 'Int': {
      if (additionalData == AdditionalData.VARNUM_BYTE) return BSOInt(input.readByte());
      else if (additionalData == AdditionalData.VARNUM_SHORT) return BSOInt(input.readShort());
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

export function readBSO(data: Uint8Array, compressed?: boolean, showSus?: boolean) {
  const input = new BufferReader(compressed ? gunzip(data) : data);
  const id = input.readByte();
  const element = readBSOElement(input, BSOIDsType[id & 0x0f], id & 0xf0);
  if (showSus && !input.isFullyRead()) {
    console.log("There's more stuff at the end of the file... that's kinda sus");
    console.log(input.readToEndAsString());
  }
  return element;
}
