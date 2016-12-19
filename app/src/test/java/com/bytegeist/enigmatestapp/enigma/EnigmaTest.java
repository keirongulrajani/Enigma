package com.bytegeist.enigmatestapp.enigma;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import rx.observers.TestSubscriber;

import static com.bytegeist.enigmatestapp.enigma.Enigma.ROTOR_I;
import static com.bytegeist.enigmatestapp.enigma.Enigma.ROTOR_II;
import static com.bytegeist.enigmatestapp.enigma.Enigma.ROTOR_III;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class EnigmaTest {

    private Rotor mRotor1 = ROTOR_I;
    private Rotor mRotor2 = ROTOR_II;
    private Rotor mRotor3 = ROTOR_III;

    private static final String UNENCRYPTED_MESSAGE =
            "pangolinunderstoodovercastandohfargoodnessperfectabovesomesmoochedinsidethismorejoyfulscurrilouslythis";

    private static final String ENCRYPTED_MESSAGE =
            "wcunieyocgplpkdmyeilrxpilzdsnwdwgllmiifmywzgczyajytqtvjafluoxdnwxwrkolbtvemqidsyopjwqkohtcsurplxmzishp";

    @Before
    public void setup() {
        mRotor1.setOffset(0);
        mRotor2.setOffset(0);
        mRotor3.setOffset(0);

        Enigma.getInstance().setRotors(mRotor1, mRotor2, mRotor3);
    }

    @Test
    public void testWhenInputIsAThenOutputIsG() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        // Given
        // input is A
        String unencryptedMessage = "A";
        // When
        //do the test
        Enigma.getInstance().encrypt(unencryptedMessage).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        List<String> returnedString = testSubscriber.getOnNextEvents();
        String result = returnedString.get(0);

        // Then
        //output is G
        assertEquals("Output string should be \'g\'", "g", result);
    }


    @Test
    public void testWhenRotor1MakesCompleteTurnThenRotor2Moves1position() throws Exception {
        // Given
        //rotor 1 turns completely
        mRotor1.setOffset(26);
        mRotor2.setOffset(0);

        // When
        //do the test
        Enigma.getInstance().advanceRotors();

        // Then
        //rotor 2 position has moved 1 position
        assertTrue("rotor 1's offset should be 0 (i.e. full rotation)", mRotor1.getOffset() == 0);
        assertTrue("rotor 2's offset should be 1 (i.e. advanced 1 position)", mRotor2.getOffset() == 1);
    }

    @Test
    public void testWhenRotor2MakesCompleteTurnThenRotor3Moves1position() throws Exception {
        // Given
        //rotor 2 turns completely
        mRotor1.setOffset(26);
        mRotor2.setOffset(25);
        mRotor3.setOffset(0);

        // When
        //do the test
        Enigma.getInstance().advanceRotors();

        // Then
        //rotor 2 position has moved 1 position
        assertTrue("rotor 1's offset should be 0 (i.e. full rotation)", mRotor2.getOffset() == 0);
        assertTrue("rotor 2's offset should be 1 (i.e. advanced 1 position)", mRotor3.getOffset() == 1);
    }

    @Test
    public void testWhenUnencryptedMessageGoesInEncryptedMessageComesOut() throws Exception {
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        // Given
        //unencrypted message goes in
        String unencryptedMessage = "This is an unencryted message with CAPITALS, spaces and punctuation!!!";
        // When
        //do the test
        Enigma.getInstance().encrypt(unencryptedMessage).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        List<String> returnedString = testSubscriber.getOnNextEvents();
        String result = returnedString.get(0);

        // Then
        //encrypted message is the result
        assertTrue("Only 1 string should be encrypted", returnedString.size() == 1);
        assertFalse("the string should not be null", result == null);
        assertFalse("the string should not contain punctuation", Pattern.matches("\\p{Punct}", result));
        assertFalse("the string should not contain spaces", result.contains(" "));
        assertNotEquals("Output string is not encrypted, they should not be equal", unencryptedMessage, result);
    }

    @Test
    public void testWhenEncryptedMessageGoesInUnencryptedMessageComesOut() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        // Given
        //encrypted message goes in
        String encryptedMessage = ENCRYPTED_MESSAGE;
        // When
        //do the test
        Enigma.getInstance().encrypt(encryptedMessage).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        List<String> returnedString = testSubscriber.getOnNextEvents();
        String result = returnedString.get(0);

        // Then
        //encrypted message is the result
        assertTrue("Only 1 string should be encrypted", returnedString.size() == 1);
        assertFalse("the string should not be null", result == null);
        assertFalse("the string should not contain punctuation", Pattern.matches("\\p{Punct}", result));
        assertFalse("the string should not contain spaces", result.contains(" "));
        assertEquals("Output string should be unencrypted", UNENCRYPTED_MESSAGE, result);
    }
}