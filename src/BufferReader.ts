export class BufferReader {
  private data: Uint8Array;
  private view: DataView;
  private cursor = 0;

  public constructor(data: Uint8Array) {
    this.data = data;
    this.view = new DataView(data.buffer);
  }

  public readUByte() {
    this.cursor += 1;
    return this.view.getUint8(this.cursor - 1);
  }

  public readByte() {
    this.cursor += 1;
    return this.view.getInt8(this.cursor - 1);
  }

  public readUShort() {
    this.cursor += 2;
    return this.view.getUint16(this.cursor - 2);
  }

  public readShort() {
    this.cursor += 2;
    return this.view.getInt16(this.cursor - 2);
  }

  public readUInt() {
    this.cursor += 4;
    return this.view.getUint32(this.cursor - 4);
  }

  public readInt() {
    this.cursor += 4;
    return this.view.getInt32(this.cursor - 4);
  }

  public readULong() {
    this.cursor += 8;
    return this.view.getBigUint64(this.cursor - 8);
  }

  public readLong() {
    this.cursor += 8;
    return this.view.getBigInt64(this.cursor - 8);
  }

  public readFloat() {
    this.cursor += 4;
    return this.view.getFloat32(this.cursor - 4);
  }

  public readUTF() {
    const len = this.readUShort();

    let str = '';

    for (let i = 0; i < len; i++) {
      str += String.fromCharCode(this.readUByte());
    }

    return str;
  }

  public readVarUTF(type: 'ubyte' | 'ushort' | 'none' = 'ushort') {
    if (type === 'none') {
      let str = '';

      let b = 0;
      while ((b = this.readUByte()) != 0) {
        str += String.fromCharCode(b);
      }

      return str;
    }

    const len = type === 'ushort' ? this.readUShort() : this.readUByte();
    let str = '';

    for (let i = 0; i < len; i++) {
      str += String.fromCharCode(this.readUByte());
    }

    return str;
  }

  public readDouble() {
    this.cursor += 8;
    return this.view.getFloat64(this.cursor - 8);
  }

  public isFullyRead() {
    return this.cursor === this.data.length;
  }

  public readToEndAsString() {
    let str = '';

    while (this.cursor < this.data.length) {
      str += String.fromCharCode(this.readUByte());
    }

    return str;
  }

  public getRemainingData() {
    return this.data.subarray(this.cursor);
  }
}
