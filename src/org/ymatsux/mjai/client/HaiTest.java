package org.ymatsux.mjai.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.ymatsux.mjai.client.Hai.Ji;

public class HaiTest {

    @Test
    public void testParse() {
        assertEquals(new Hai(Hai.Type.MANZU, 1, null, false), Hai.parse("1m"));
        assertEquals(new Hai(Hai.Type.MANZU, 2, null, false), Hai.parse("2m"));
        assertEquals(new Hai(Hai.Type.MANZU, 3, null, false), Hai.parse("3m"));
        assertEquals(new Hai(Hai.Type.PINZU, 4, null, false), Hai.parse("4p"));
        assertEquals(new Hai(Hai.Type.PINZU, 5, null, false), Hai.parse("5p"));
        assertEquals(new Hai(Hai.Type.PINZU, 6, null, false), Hai.parse("6p"));
        assertEquals(new Hai(Hai.Type.SOZU, 7, null, false), Hai.parse("7s"));
        assertEquals(new Hai(Hai.Type.SOZU, 8, null, false), Hai.parse("8s"));
        assertEquals(new Hai(Hai.Type.SOZU, 9, null, false), Hai.parse("9s"));

        assertEquals(new Hai(Hai.Type.MANZU, 5, null, true), Hai.parse("5mr"));
        assertEquals(new Hai(Hai.Type.PINZU, 5, null, true), Hai.parse("5pr"));
        assertEquals(new Hai(Hai.Type.SOZU, 5, null, true), Hai.parse("5sr"));

        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.TON, false), Hai.parse("E"));
        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.NAN, false), Hai.parse("S"));
        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.SHA, false), Hai.parse("W"));
        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.PE, false), Hai.parse("N"));
        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.HAKU, false), Hai.parse("P"));
        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.HATSU, false), Hai.parse("F"));
        assertEquals(new Hai(Hai.Type.JIHAI, 0, Ji.CHUN, false), Hai.parse("C"));
    }

    @Test
    public void testToString() {
        assertEquals("1m", new Hai(Hai.Type.MANZU, 1, null, false).toString());
        assertEquals("5p", new Hai(Hai.Type.PINZU, 5, null, false).toString());
        assertEquals("9s", new Hai(Hai.Type.SOZU, 9, null, false).toString());

        assertEquals("5pr", new Hai(Hai.Type.PINZU, 5, null, true).toString());

        assertEquals("E", new Hai(Hai.Type.JIHAI, 0, Ji.TON, false).toString());
        assertEquals("S", new Hai(Hai.Type.JIHAI, 0, Ji.NAN, false).toString());
        assertEquals("W", new Hai(Hai.Type.JIHAI, 0, Ji.SHA, false).toString());
        assertEquals("N", new Hai(Hai.Type.JIHAI, 0, Ji.PE, false).toString());
        assertEquals("P", new Hai(Hai.Type.JIHAI, 0, Ji.HAKU, false).toString());
        assertEquals("F", new Hai(Hai.Type.JIHAI, 0, Ji.HATSU, false).toString());
        assertEquals("C", new Hai(Hai.Type.JIHAI, 0, Ji.CHUN, false).toString());
    }

    @Test
    public void testGetId() {
        assertEquals(0, new Hai(Hai.Type.MANZU, 1, null, false).getId());
        assertEquals(8, new Hai(Hai.Type.MANZU, 9, null, false).getId());
        assertEquals(9, new Hai(Hai.Type.PINZU, 1, null, false).getId());
        assertEquals(17, new Hai(Hai.Type.PINZU, 9, null, false).getId());
        assertEquals(18, new Hai(Hai.Type.SOZU, 1, null, false).getId());
        assertEquals(26, new Hai(Hai.Type.SOZU, 9, null, false).getId());
        assertEquals(27, new Hai(Hai.Type.JIHAI, 0, Ji.TON, false).getId());
        assertEquals(28, new Hai(Hai.Type.JIHAI, 0, Ji.NAN, false).getId());
        assertEquals(29, new Hai(Hai.Type.JIHAI, 0, Ji.SHA, false).getId());
        assertEquals(30, new Hai(Hai.Type.JIHAI, 0, Ji.PE, false).getId());
        assertEquals(31, new Hai(Hai.Type.JIHAI, 0, Ji.HAKU, false).getId());
        assertEquals(32, new Hai(Hai.Type.JIHAI, 0, Ji.HATSU, false).getId());
        assertEquals(33, new Hai(Hai.Type.JIHAI, 0, Ji.CHUN, false).getId());
    }

    @Test
    public void testOfId() {
        assertEquals(Hai.ofId(0), new Hai(Hai.Type.MANZU, 1, null, false));
        assertEquals(Hai.ofId(8), new Hai(Hai.Type.MANZU, 9, null, false));
        assertEquals(Hai.ofId(9), new Hai(Hai.Type.PINZU, 1, null, false));
        assertEquals(Hai.ofId(17), new Hai(Hai.Type.PINZU, 9, null, false));
        assertEquals(Hai.ofId(18), new Hai(Hai.Type.SOZU, 1, null, false));
        assertEquals(Hai.ofId(26), new Hai(Hai.Type.SOZU, 9, null, false));
        assertEquals(Hai.ofId(27), new Hai(Hai.Type.JIHAI, 0, Ji.TON, false));
        assertEquals(Hai.ofId(28), new Hai(Hai.Type.JIHAI, 0, Ji.NAN, false));
        assertEquals(Hai.ofId(29), new Hai(Hai.Type.JIHAI, 0, Ji.SHA, false));
        assertEquals(Hai.ofId(30), new Hai(Hai.Type.JIHAI, 0, Ji.PE, false));
        assertEquals(Hai.ofId(31), new Hai(Hai.Type.JIHAI, 0, Ji.HAKU, false));
        assertEquals(Hai.ofId(32), new Hai(Hai.Type.JIHAI, 0, Ji.HATSU, false));
        assertEquals(Hai.ofId(33), new Hai(Hai.Type.JIHAI, 0, Ji.CHUN, false));
    }
}
