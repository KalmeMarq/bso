export class BufferWriter {
  private data = new Uint8Array(65535);
  private view = new DataView(this.data.buffer);
  private cursor = 0;

  public write(values: number[]) {
    for (let i = 0; i < values.length; i++) {
      this.view.setInt8(this.cursor, values[i]);
      this.cursor += 1;
    }
  }

  public writeUByte(value: number) {
    this.view.setUint8(this.cursor, value);
    this.cursor += 1;
  }

  public writeByte(value: number) {
    this.view.setInt8(this.cursor, value);
    this.cursor += 1;
  }

  public writeUShort(value: number) {
    this.view.setUint16(this.cursor, value);
    this.cursor += 2;
  }

  public writeShort(value: number) {
    this.view.setInt16(this.cursor, value);
    this.cursor += 2;
  }

  public writeUInt(value: number) {
    this.view.setUint32(this.cursor, value);
    this.cursor += 4;
  }

  public writeInt(value: number) {
    this.view.setInt32(this.cursor, value);
    this.cursor += 4;
  }

  public writeULong(value: bigint) {
    this.view.setBigUint64(this.cursor, value);
    this.cursor += 8;
  }

  public writeLong(value: bigint) {
    this.view.setBigInt64(this.cursor, value);
    this.cursor += 8;
  }

  public writeFloat(value: number) {
    this.view.setFloat32(this.cursor, value);
    this.cursor += 4;
  }

  public writeDouble(value: number) {
    this.view.setFloat64(this.cursor, value);
    this.cursor += 8;
  }

  public writeUTF(value: string) {
    this.writeUShort(value.length);

    for (let i = 0; i < value.length; i++) {
      this.writeByte(value.charCodeAt(i));
    }
  }

  public setCursor(offset: number) {
    this.cursor = offset;
  }

  public finish() {
    return this.data.subarray(0, this.cursor);
  }
}
