package com.bytegeist.enigmatestapp.enigma;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class RotorTest {
    private final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String ROTOR_1 = "ekmflgdqvzntowyhxuspaibrcj";
    private static final char[] ROTOR_1_CHARS = ROTOR_1.toCharArray();

    private Rotor mCut = new Rotor(ROTOR_1_CHARS);

    @Before
    public void setup() {
        mCut.setOffset(0);
    }

    @Test
    public void testWhenRotorMakesCompleteTurnThenReturnTrueOnAdvance() throws Exception {
        mCut.setOffset(ROTOR_1_CHARS.length - 1);
        boolean shouldNextRotorAdvance = mCut.advance();
        assertTrue("Next rotor should flag to advance", shouldNextRotorAdvance);
        assertTrue("Offset should be reset to 0", mCut.getOffset() == 0);
    }

    @Test(expected = RuntimeException.class)
    public void testWhenFindingInvalidCharThenExceptionThrown() throws Exception {
        char invalidChar = '!';
        mCut.findPositionOfChar(invalidChar);
    }

    @Test
    public void testForwardMappingsNoOffset() throws Exception {
        for (int i = 0; i < ROTOR_1_CHARS.length; i++) {
            char expected = ROTOR_1_CHARS[i];
            char input = ALPHABET.charAt(i);
            char actual = mCut.forwardMapCharToChar(input);
            assertEquals("Should have mapped to " + expected + " but mapped to " + actual, expected, actual);
        }
    }

    @Test
    public void testForwardMappingsWithOffset() throws Exception {
        int offset = 3;
        mCut.setOffset(offset);
        for (int i = 0; i < ROTOR_1_CHARS.length; i++) {
            char expected = ROTOR_1_CHARS[(i + offset) % ROTOR_1_CHARS.length];

            char input = ALPHABET.charAt(i);

            char actual = mCut.forwardMapCharToChar(input);
            assertEquals("Should have mapped to " + expected + " but mapped to " + actual, expected, actual);
        }
    }

    @Test
    public void testReverseMappingsNoOffset() throws Exception {
        for (int i = 0; i < ROTOR_1_CHARS.length; i++) {
            char currentRotorChar = ALPHABET.charAt(i);
            int expected = ROTOR_1.indexOf(currentRotorChar);
            int actual = mCut.reverseMapCharToChar(currentRotorChar);
            assertEquals("Should have mapped to " + expected + " but mapped to " + actual, expected, actual);
        }
    }

    @Test
    public void testReverseMappingsWithOffset() throws Exception {
        int offset = 3;
        mCut.setOffset(offset);
        for (int i = 0; i < ROTOR_1_CHARS.length; i++) {
            char currentRotorChar = ALPHABET.charAt((i + offset) % ROTOR_1_CHARS.length);
            int expected = ROTOR_1.indexOf(currentRotorChar) - offset;

            if (expected < 0) {
                expected = ROTOR_1_CHARS.length - 1 - Math.abs(expected);
            }

            int actual = mCut.reverseMapCharToChar(currentRotorChar);

            assertEquals("Should have mapped to " + expected + " but mapped to " + actual, expected, actual);
        }
    }

}