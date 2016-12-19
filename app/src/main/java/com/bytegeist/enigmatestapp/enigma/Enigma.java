package com.bytegeist.enigmatestapp.enigma;


import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import static com.bytegeist.enigmatestapp.enigma.Rotor.positionOfCharInAlphabet;

public final class Enigma {

    protected static final Rotor ROTOR_I = new Rotor("ekmflgdqvzntowyhxuspaibrcj".toCharArray());
    protected static final Rotor ROTOR_II = new Rotor("ajdksiruxblhwtmcqgznpyfvoe".toCharArray());
    protected static final Rotor ROTOR_III = new Rotor("bdfhjlcprtxvznyeiwgakmusqo".toCharArray());

    protected static final Reflector REFLECTOR_A = new Reflector("ejmzalyxvbwfcrquontspikhgd".toCharArray());

    private static Enigma mInstance;

    private List<Rotor> mRotors = new ArrayList<>();
    private Reflector mReflector;

    private Enigma() {
        mRotors.addAll(Arrays.asList(ROTOR_I, ROTOR_II, ROTOR_III));
        mReflector = REFLECTOR_A;
    }

    public static Enigma getInstance() {
        if (mInstance == null) {
            mInstance = new Enigma();
        }
        return mInstance;
    }

    /**
     * Encrypts a string
     *
     * @param unencryptedMessage The unencrypted input message
     * @return the encrypted message
     */
    public Single<String> encrypt(String unencryptedMessage) {
        return Observable.just(unencryptedMessage)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        //remove any chars that aren't a-z (e.g. spaces and punctuation)
                        return Observable.just(s.replaceAll("[^a-zA-Z]", "")
                                .toLowerCase());
                    }
                })
                .flatMap(new Func1<String, Observable<Character[]>>() {
                    @Override
                    public Observable<Character[]> call(String s) {
                        //map to array of chars
                        return Observable.just(ArrayUtils.toObject(s.toCharArray()));
                    }
                }).flatMap(new Func1<Character[], Observable<String>>() {
                    @Override
                    public Observable<String> call(Character[] characters) {
                        char[] encryptedChars = new char[characters.length];
                        for (int i = 0; i < characters.length; i++) {
                            encryptedChars[i] = getEncodedChar(characters[i]);
                        }
                        return Observable.just(new String(encryptedChars));
                    }
                }).toSingle();

    }

    @VisibleForTesting
    protected char getEncodedChar(char c) {
        char encodedChar = c;
        int encodedCharPosition = positionOfCharInAlphabet(c);

        //advance the rotor (before the character goes in as per the instructions)
        advanceRotors();

        //put through rotors til the end
        for (Rotor rotor : mRotors) {
            //encodedCharPosition = rotor.forwardMapCharPositionToPosition(encodedCharPosition);
            encodedChar = rotor.forwardMapCharToChar(encodedChar);
        }

        //put through reflector
        //encodedCharPosition = positionOfCharInAlphabet(mReflector.getCharAtPosition(encodedCharPosition));
        encodedChar = mReflector.forwardMapCharToChar(encodedChar);

        //reverse through rotors
        ListIterator<Rotor> li = mRotors.listIterator(mRotors.size());

        // Iterate in reverse.
        while (li.hasPrevious()) {
            Rotor previous = li.previous();
            //encodedCharPosition = previous.reverseMapCharPositionToPosition(encodedCharPosition);
            encodedChar = previous.reverseMapCharToChar(encodedChar);
        }
        //encodedChar = mRotors.get(0).getCharAtPosition(encodedCharPosition);
        return encodedChar;
    }

    protected void advanceRotors() {
        boolean shouldAdvanceNextRotor = false;
        for (int i = 0; i < mRotors.size(); i++) {
            Rotor rotor = mRotors.get(i);
            if (i == 0 || shouldAdvanceNextRotor) {
                shouldAdvanceNextRotor = rotor.advance();
            }
        }
    }

    @VisibleForTesting
    protected void setRotors(List<Rotor> rotors) {
        mRotors = rotors;
    }

    @VisibleForTesting
    protected void setRotors(Rotor... rotors) {
        mRotors = Arrays.asList(rotors);
    }
}
