package com.bytegeist.enigmatestapp.enigma;


import android.support.annotation.VisibleForTesting;

public class Rotor {
    private final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private int mOffset;
    private char[] mChars;

    public Rotor(char[] chars) {
        mChars = chars;
        mOffset = 0;
    }

    public boolean advance() {
        boolean fullTurn = false;
        mOffset++;

        if (mOffset >= mChars.length) {
            mOffset = 0;
            fullTurn = true;
        }
        return fullTurn;
    }

    @VisibleForTesting
    protected void setOffset(int offset) {
        mOffset = offset;
    }

    @VisibleForTesting
    protected int getOffset() {
        return mOffset;
    }

    public char getCharAtPosition(int position) {
        return mChars[position];
    }

    public int findPositionOfChar(char c) {
        int position = -1;
        for (int i = 0; i < mChars.length; i++) {
            if (c == mChars[i]) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            throw new RuntimeException("Char \'" + c + "\' not found in rotor");
        }

        return position;
    }

    public char forwardMapCharToChar(char inputChar) {
        return getCharAtPosition((positionOfCharInAlphabet(inputChar) + mOffset) % mChars.length);
    }

    public char reverseMapCharToChar(char inputChar) {
        int positionOfChar;

        positionOfChar = findPositionOfChar(inputChar);

        //input would be the output of a forward transition, so we reverse the actions
        positionOfChar -= mOffset;
        if (positionOfChar < 0) {
            positionOfChar = mChars.length - Math.abs(positionOfChar);
        }

        return charAtPositionInAlphabet(positionOfChar);
    }

    public static int positionOfCharInAlphabet(char c) {
        return ALPHABET.indexOf(c);
    }

    public static char charAtPositionInAlphabet(int position) {
        return ALPHABET.charAt(position);
    }


}
