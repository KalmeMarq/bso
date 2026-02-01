package io.github.kalmemarq.bso;

import io.github.kalmemarq.bso.custom.UUIDType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class SBsoTest {
    @Test
    void map1() {
        BsoMap map = new BsoMap();
        map.putUByte("age", 21);
        map.putString("name", "Kalme");
        map.putFloat(".hey.", 0.55f);

        BsoList hobbies = new BsoList();
        hobbies.addString("coding");
        hobbies.addString("sleeping a ton");
        map.put("hobbies", hobbies);

        String expected = "{age:21sb,name:\"Kalme\",\".hey.\":0.55f,hobbies:[\"coding\",\"sleeping a ton\"]}";

        Assertions.assertEquals(expected.length(), SBsoUtils.stringify(map).length());
    }

    @Test
    void customUUID1() {
        BsoUtils.registerCustomType(UUIDType.INSTANCE);

        UUID uuid = UUID.fromString("3df8b598-0c8c-4e0f-af96-b107f3b9934a");
        BsoCustom<UUID> node = new BsoCustom<>(UUIDType.INSTANCE, uuid);

        Assertions.assertEquals("(uuid;3df8b598-0c8c-4e0f-af96-b107f3b9934a)", SBsoUtils.stringify(node));

        BsoUtils.unregisterAllCustomTypes();
    }
}
