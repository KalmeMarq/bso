import { readBSO } from './src/read.ts';
import { bsoToSBSO } from './src/utils.ts';

export { BufferReader } from './src/BufferReader.ts';
export { BufferWriter } from './src/BufferWriter.ts';
export * from './src/constants.ts';
export { readBSO } from './src/read.ts';
export * from './src/types.ts';
export { bsoToJson, bsoToSBSO, readSBSO } from './src/utils.ts';
export { writeBSO } from './src/write.ts';

if (import.meta.main) {
  if (Deno.args.length > 0) {
    try {
      const data = Deno.readFileSync(Deno.args[0]);

      if (Deno.args.includes('--raw')) {
        console.log(data);
        Deno.exit(0);
      }

      const tn = performance.now();

      const sbso = bsoToSBSO(readBSO(data), {
        useAnsi: true,
        indent: Deno.args.includes('--indent') ? 2 : undefined
      });

      console.log(sbso);

      console.log(performance.now() - tn + ' ms');
    } catch (e) {
      console.log(e);

      // throw new Error('Invalid BSO file');
    }
  }
}
