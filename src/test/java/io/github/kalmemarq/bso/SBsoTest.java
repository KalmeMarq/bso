package io.github.kalmemarq.bso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
