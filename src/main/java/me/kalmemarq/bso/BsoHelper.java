package me.kalmemarq.bso;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BsoHelper {
    public static void main(String[] args) {
        Path input = null;
        Path output = null;

        int indent = -1;
        BsoIo.Endianess endianess = BsoIo.Endianess.BIG;
        BsoIo.Compression compression = BsoIo.Compression.NONE;

        for (int i = 0; i < args.length; ++i) {
            if ("-i".equals(args[i]) && i + 1 < args.length) {
                input = Paths.get(args[++i]);
            }

            if ("-o".equals(args[i]) && i + 1 < args.length) {
                output = Paths.get(args[++i]);
            }

            if ("--indent".equals(args[i]) && i + 1 < args.length) {
                try {
                    indent = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ignored) {
                }
            }

            if ("--comp".equals(args[i]) && i + 1 < args.length) {
                String comp = args[++i];
                if ("gzip".equals(comp)) {
                    compression = BsoIo.Compression.GZIP;
                } else if ("zlib".equals(comp)) {
                    compression = BsoIo.Compression.ZLIB;
                }
            }

            if ("--endian".equals(args[i]) && i + 1 < args.length) {
                String comp = args[++i];
                if ("little".equals(comp)) {
                    endianess = BsoIo.Endianess.LITTLE;
                }
            }
        }

        if (input != null && output != null) {
            if (input.getFileName().toString().endsWith(".bso") && output.getFileName().toString().endsWith(".sbso")) {
                BsoElement element = BsoIo.read(input);
                System.out.println(element);
                if (element == null) {
                    System.out.println("Error reading file");
                } else {
                    try (BufferedWriter writer = Files.newBufferedWriter(output)) {
                        writer.write(new SBsoWriter().apply(element, indent));
                    } catch (IOException e) {
                        System.out.println("Error writing file");
                        e.printStackTrace();
                    }
                }
            } else if (input.getFileName().toString().endsWith(".sbso") && output.getFileName().toString().endsWith(".bso")) {
                try {
                    BsoElement element = new SBsoReader().read(String.join("\n", Files.readAllLines(input)));
                    if (element == null) {
                        System.out.println("Error parsing file");
                    } else {
                        BsoIo.write(output, element, compression, endianess);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading file");
                }
            } else {
                System.out.println("I don't know what to do");
            }
        }
    }
}