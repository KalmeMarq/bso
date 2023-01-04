import { BSOByte, BSOByteArray, BSODouble, BSODoubleArray, BSOFloat, BSOFloatArray, BSOInt, BSOIntArray, BSOList, BSOLong, BSOLongArray, BSOMap, BSOShort, BSOShortArray, BSOString } from './bso.ts';
import { BufferReader } from './BufferReader.ts';
import { BufferWriter } from './BufferWriter.ts';
import { AdditionalData, ANSI, Byte, Int, Short, UByte, UShort } from './constants.ts';
import { BSOElement, BSOType, WriteOptions } from './types.ts';

export function writeLength(output: BufferWriter, length: number) {
  if (length <= UByte.MAX_VALUE) {
    output.writeByte(length);
  } else if (length <= UShort.MAX_VALUE) {
    output.writeShort(length);
  } else {
    output.writeInt(length);
  }
}

export function readLength(input: BufferReader, additionalData: number) {
  switch (additionalData) {
    case AdditionalData.BYTE_LENGTH:
      return input.readUByte();
    case AdditionalData.SHORT_LENGTH:
      return input.readUShort();
    case AdditionalData.INT_LENGTH:
      return input.readInt();
    default: {
      throw new Error('Unknown length type');
    }
  }
}

export function lengthAdditionalData(length: number, writeOptions: WriteOptions, indefiniteLength?: boolean) {
  if (/* writeOptions.allowIndefinitiveLength &&  */ indefiniteLength) return AdditionalData.INDEFINITE_LENGTH;
  // if (!writeOptions.allowVarLength) return 0;

  if (length <= UByte.MAX_VALUE) {
    return AdditionalData.BYTE_LENGTH;
  } else if (length <= UShort.MAX_VALUE) {
    return AdditionalData.SHORT_LENGTH;
  }
  return 0;
}

export function getElementAdditionalData(element: BSOElement, writeOptions: WriteOptions) {
  switch (element.type) {
    case 'String': {
      if (/* writeOptions.allowStringVarLength && writeOptions.allowIndefinitiveLength &&  */ element.indefiniteLength) return AdditionalData.STR_INDEFINITE_LENGTH;

      // if (writeOptions.allowVarLength) {
      if (element.value.length <= UByte.MAX_VALUE) {
        return AdditionalData.STR_BYTE_LENGTH;
      } else if (element.value.length <= UShort.MAX_VALUE) {
        return AdditionalData.STR_SHORT_LENGTH;
      }
      // }

      return 0;
    }
    case 'Long': {
      if (!writeOptions.allowVarNum) return 0;

      if (element.value <= Byte.MAX_VALUE) {
        return AdditionalData.VARNUM_BYTE;
      } else if (element.value <= Short.MAX_VALUE) {
        return AdditionalData.VARNUM_SHORT;
      } else if (element.value <= Int.MAX_VALUE) {
        return AdditionalData.VARNUM_INT;
      }
      return 0;
    }
    case 'Int': {
      if (!writeOptions.allowVarNum) return 0;

      if (element.value <= Byte.MAX_VALUE) {
        return AdditionalData.VARNUM_BYTE;
      } else if (element.value <= Short.MAX_VALUE) {
        return AdditionalData.VARNUM_SHORT;
      }
      return 0;
    }
    case 'Map':
      return lengthAdditionalData(Object.keys(element.entries).length, writeOptions, element.indefiniteLength);
    case 'DoubleArray':
    case 'FloatArray':
    case 'LongArray':
    case 'IntArray':
    case 'ShortArray':
    case 'ByteArray':
      return lengthAdditionalData(element.values.length, writeOptions, element.indefiniteLength);
    default:
      return 0;
  }
}

function bsoToJson(element: BSOElement): string;
function bsoToJson<T = unknown>(element: BSOElement, parse: true): T;
function bsoToJson(element: BSOElement, parse?: boolean): unknown {
  let data = '';

  function bsoElToJson(element: BSOElement) {
    switch (element.type) {
      case 'List':
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

function bsoToSBSO(element: BSOElement, options?: { useAnsi?: boolean; indent?: number }) {
  let output = '';

  function bsoElToSBSO(element: BSOElement, level = 0) {
    switch (element.type) {
      case 'DoubleArray':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[D;${ANSI.Reset}`;
        else output += '[D;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO({ type: 'Double', value: element.values[i] });
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'FloatArray':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[F;${ANSI.Reset}`;
        else output += '[F;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO({ type: 'Float', value: element.values[i] });
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'LongArray':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[L;${ANSI.Reset}`;
        else output += '[L;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO({ type: 'Long', value: element.values[i] });
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'IntArray':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[I;${ANSI.Reset}`;
        else output += '[I;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO({ type: 'Int', value: element.values[i] });
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'ShortArray':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[S;${ANSI.Reset}`;
        else output += '[S;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO({ type: 'Short', value: element.values[i] });
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'ByteArray':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[B;${ANSI.Reset}`;
        else output += '[B;';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO({ type: 'Byte', value: element.values[i] });
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'List':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}[${ANSI.Reset}`;
        else output += '[';

        for (let i = 0; i < element.values.length; i++) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += ' ';
          }

          bsoElToSBSO(element.values[i]);
        }

        if (options?.useAnsi) output += `${ANSI.BrightWhite}]${ANSI.Reset}`;
        else output += ']';
        break;
      case 'Map': {
        if (options?.useAnsi) output += `${ANSI.BrightWhite}{${ANSI.Reset}`;
        else output += '{';

        if (options?.indent != null && Object.keys(element.entries).length) {
          output += '\n';
        }

        let i = 0;
        for (const [key, value] of Object.entries(element.entries)) {
          if (i != 0) {
            if (options?.useAnsi) output += `${ANSI.BrightWhite},${ANSI.Reset}`;
            else output += ',';

            if (options?.indent != null) output += '\n';
          }

          if (options?.indent != null) output += ' '.repeat((level + 1) * options.indent);

          if (options?.useAnsi) output += `${ANSI.Cyan}${key}${ANSI.Reset}${ANSI.BrightWhite}:${ANSI.Reset}`;
          else output += `${key}:`;

          if (options?.indent != null) output += ' ';

          bsoElToSBSO(value, level + 1);

          i++;
        }

        if (options?.indent != null && Object.keys(element.entries).length) output += '\n' + ' '.repeat(level * options.indent);

        if (options?.useAnsi) output += `${ANSI.BrightWhite}}${ANSI.Reset}`;
        else output += '}';
        break;
      }
      case 'String':
        if (options?.useAnsi) output += `${ANSI.BrightWhite}"${ANSI.Reset}${ANSI.BrightGreen}${element.value}${ANSI.Reset}${ANSI.BrightWhite}"${ANSI.Reset}`;
        else output += `"${element.value}"`;
        break;
      case 'Double':
        if (options?.useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}d${ANSI.Reset}`;
        else output += `${element.value}d`;
        break;
      case 'Float':
        if (options?.useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}f${ANSI.Reset}`;
        else output += `${element.value}f`;
        break;
      case 'Long':
        if (options?.useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}L${ANSI.Reset}`;
        else output += `${element.value}L`;
        break;
      case 'Int':
        if (options?.useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Reset}`;
        else output += `${element.value}`;
        break;
      case 'Short':
        if (options?.useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}b${ANSI.Reset}`;
        else output += `${element.value}s`;
        break;
      case 'Byte':
        if (options?.useAnsi) output += `${ANSI.Yellow}${element.value}${ANSI.Reset}${ANSI.Red}b${ANSI.Reset}`;
        else output += `${element.value}b`;
        break;
    }
  }

  bsoElToSBSO(element, 0);

  if (options?.useAnsi) {
    return output;
  } else {
    return output;
  }
}

function readSBSO(data: string) {
  let cursor = 0;
  let currChar: string | null = data[cursor];

  const peek = (amount = 0): string | null => {
    return data[cursor + amount];
  };

  const expect = (chr: string) => {
    if (peek() != chr) {
      throw new SyntaxError('Expected ' + chr + ' but found ' + peek());
    } else {
      currChar = data[++cursor];
    }
  };

  const skipWhitespace = () => {
    while (currChar != null && /\s/.test(currChar)) {
      currChar = data[++cursor];
    }
  };

  const readString = (requiresQuotes?: boolean) => {
    let str = '';

    if (currChar === '"') {
      currChar = data[++cursor];

      do {
        str += currChar;
        currChar = data[++cursor];
      } while (currChar != null && currChar != '"' && /\w/.test(currChar));
      currChar = data[++cursor];
    } else if (currChar === "'") {
      currChar = data[++cursor];

      do {
        str += currChar;
        currChar = data[++cursor];
      } while (currChar != null && currChar != "'" && /\w/.test(currChar));
      currChar = data[++cursor];
    } else {
      if (requiresQuotes) throw new SyntaxError('Expected quoted string');
      do {
        str += currChar;
        currChar = data[++cursor];
      } while (currChar != null && /\w/.test(currChar));
    }

    return str;
  };

  const skip = (amount = 1) => {
    cursor += amount;
    currChar = data[cursor];
  };

  function readNumber() {
    let vl = '';
    let dots = 0;
    do {
      if (currChar === '.') {
        ++dots;

        if (dots > 1) {
          throw new SyntaxError('Float numbers can only have one decimal point');
        }
      }

      vl += currChar;
      currChar = data[++cursor];
    } while (currChar != null && /(\d|\.)/.test(currChar));

    if (peek() === 'b') {
      skip();
      return BSOByte(parseInt(vl));
    } else if (peek() === 's') {
      skip();
      return BSOShort(parseInt(vl));
    } else if (peek() === 'L') {
      skip();
      return BSOLong(BigInt(vl));
    } else if (peek() === 'f') {
      skip();
      return BSOFloat(parseFloat(vl));
    } else if (peek() === 'd') {
      skip();
      return BSODouble(parseFloat(vl));
    } else {
      return BSOInt(parseInt(vl));
    }
  }

  function readMap() {
    expect('{');
    skipWhitespace();

    const map: Record<string, BSOElement> = {};

    while (currChar != null && peek() != '}') {
      const key = readString();
      skipWhitespace();
      expect(':');
      skipWhitespace();

      map[key] = readElement();

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === '}') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== '}') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect('}');
    skipWhitespace();

    return BSOMap(map);
  }

  function readByteArray() {
    expect('[');
    expect('B');
    expect(';');

    const vls: number[] = [];

    while (currChar != null && peek() != ']') {
      const vl = readNumber();
      if (vl.type != 'Byte') {
        throw new SyntaxError('Byte Array can only contain Bytes');
      }

      vls.push(vl.value);

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSOByteArray(vls);
  }

  function readShortArray() {
    expect('[');
    expect('S');
    expect(';');

    const vls: number[] = [];

    while (currChar != null && peek() != ']') {
      const vl = readNumber();
      if (vl.type != 'Short') {
        throw new SyntaxError('Short Array can only contain Shorts');
      }

      vls.push(vl.value);

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSOShortArray(vls);
  }

  function readIntArray() {
    expect('[');
    expect('I');
    expect(';');

    const vls: number[] = [];

    while (currChar != null && peek() != ']') {
      const vl = readNumber();
      if (vl.type != 'Int') {
        throw new SyntaxError('Int Array can only contain Ints');
      }

      vls.push(vl.value);

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSOIntArray(vls);
  }

  function readLongArray() {
    expect('[');
    expect('L');
    expect(';');

    const vls: bigint[] = [];

    while (currChar != null && peek() != ']') {
      const vl = readNumber();
      if (vl.type != 'Long') {
        throw new SyntaxError('Long Array can only contain Longs');
      }

      vls.push(vl.value);

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSOLongArray(vls);
  }

  function readFloatArray() {
    expect('[');
    expect('F');
    expect(';');

    const vls: number[] = [];

    while (currChar != null && peek() != ']') {
      const vl = readNumber();
      if (vl.type != 'Float') {
        throw new SyntaxError('Float Array can only contain Floats');
      }

      vls.push(vl.value);

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSOFloatArray(vls);
  }

  function readDoubleArray() {
    expect('[');
    expect('D');
    expect(';');

    const vls: number[] = [];

    while (currChar != null && peek() != ']') {
      const vl = readNumber();
      if (vl.type != 'Double') {
        throw new SyntaxError('Double Array can only contain Doubles');
      }

      vls.push(vl.value);

      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSODoubleArray(vls);
  }

  function readList() {
    expect('[');
    skipWhitespace();

    const vls: BSOElement[] = [];

    let type: BSOType | null = null;
    while (currChar != null && peek() != ']') {
      const vl = readElement();
      if (type == null) type = vl.type;
      else if (type !== vl.type) {
        throw new SyntaxError('List can only contain a single type. First element was ' + type + ' but ' + vl.type + ' was found in later items.');
      }

      vls.push(vl);
      skipWhitespace();

      if (peek() === ',') {
        skip();
        skipWhitespace();
        if (peek() === ']') {
          throw new SyntaxError('Trailing comma');
        }

        skipWhitespace();
      } else if (peek() !== ']') {
        throw new SyntaxError('Missing comma');
      }
    }

    expect(']');
    skipWhitespace();

    return BSOList(vls);
  }

  function readElement(): BSOElement {
    skipWhitespace();
    if (peek() === '{') {
      return readMap();
    } else if (peek() === '"' || peek() === "'") {
      return BSOString(readString(true));
    } else if (peek() === '[' && peek(1) === 'B' && peek(2) === ';') {
      return readByteArray();
    } else if (peek() === '[' && peek(1) === 'S' && peek(2) === ';') {
      return readShortArray();
    } else if (peek() === '[' && peek(1) === 'I' && peek(2) === ';') {
      return readIntArray();
    } else if (peek() === '[' && peek(1) === 'L' && peek(2) === ';') {
      return readLongArray();
    } else if (peek() === '[' && peek(1) === 'F' && peek(2) === ';') {
      return readFloatArray();
    } else if (peek() === '[' && peek(1) === 'D' && peek(2) === ';') {
      return readDoubleArray();
    } else if (peek() === '[') {
      return readList();
    } else if (currChar && /\d/.test(currChar)) {
      return readNumber();
    } else {
      throw new SyntaxError('Unknown type started by ' + data.substring(cursor, Math.min(cursor + 5, data.length)));
    }
  }

  return readElement();
}

export { bsoToJson, bsoToSBSO, readSBSO };
