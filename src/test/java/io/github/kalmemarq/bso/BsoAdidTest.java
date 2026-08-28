package io.github.kalmemarq.bso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BsoAdidTest {
    @Test
    void oneByteAdid() throws IOException {
        assertAdidRoundtrip(0b0011, 0b1001);
    }

    @Test
    void twoByteAdid() throws IOException {
        assertAdidRoundtrip(0b0101, 100);
    }

    @Test
    void threeByteAdidKeepsAdAndIdSeparate() throws IOException {
        assertAdidRoundtrip(0b1_0011, 300);
    }

    @Test
    void fourByteAdidKeepsAdAndIdSeparate() throws IOException {
        assertAdidRoundtrip(0b10_0101, 50_000);
    }

    private static void assertAdidRoundtrip(int ad, int id) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BsoUtils.writeADID(new DataOutputStream(bytes), ad, id);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        long adid = BsoUtils.readADID(in);
        Assertions.assertEquals(ad, (int) (adid >> 32));
        Assertions.assertEquals(id, (int) adid);
        Assertions.assertEquals(-1, in.read());
    }
}
