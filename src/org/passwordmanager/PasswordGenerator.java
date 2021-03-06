/*
    Author: Jose Eduardo Soto
    Description:    This program generates a pseudo-random string. It's intention is to be used for passwords.
        It utilizes the Builder Design so that the user can easily use multiple variances of settings when
        generating the passwords.

                        1.) The user first calls makes a builder:
                            PasswordGenerator.PasswordGeneratorBuilder builder = new
                                PasswordGenerator.PasswordGeneratorBuilder(minLength, maxLength, InvalidChars);

                        2.) The user may set the minimums individually or use setMinimums()

                        3.) Optionally the user may force a word that will be inside of the password we generate for
                                convenience but it is not recommended for security purposes.

                        4.) The user calls the builder.build() to generate an instance of Password Generator:
                                PasswordGenerator generator = builder.build();

                        5.) The user may now use the generator.generatePassword() function that returns the string.

                     The algorithm starts after the settings have been set up. We populate categories of characters
                     into stacks my randomly choosing them from predefined arrays. After those necessary ones have
                     been populated we then start to randomly populate our stacks from any of the categories. Once
                     they are all populated. We then randomly pop from each stack and populate our string.
*/

package org.passwordmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.lang.IllegalArgumentException;

public class PasswordGenerator {

    private static final char[] DIGIT_CHARS_ARRAY = {'0', '1', '2', '3', '4', '5', '6','7', '8', '9'};
    private static final char[] LOWER_CASE_CHARS_ARRAY = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
    'p','q','r','s','t','u','v','w','x','y','z'};
    private static final char[] UPPER_CASE_CHARS_ARRAY = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O',
    'P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static final char[] SYMBOLS_CHAR_ARRAY = {'@','!','#','$','%','^','&','*','(',')','_','+','-','=',',','<',
    '.','>',';',':','/','?'};
    private static final char[][] CHAR_ARRAY_ARRAY = {DIGIT_CHARS_ARRAY, LOWER_CASE_CHARS_ARRAY,
    UPPER_CASE_CHARS_ARRAY, SYMBOLS_CHAR_ARRAY};

    private final int minimumLength;
    private final int maximumLength;
    private final int minimumCapsRequired;
    private final int minimumLowerRequired;
    private final int minimumSymbolsRequired;
    private final int minimumDigitsRequired;
    private final String invalidCharacters;
    private StringBuilder passwordBuilder;
    private boolean refreshed = false;
    private PasswordStrategy strategy;

    Random r = new Random();

    public static class PasswordGeneratorBuilder {
        // Required parameters
        private final int minimumLength;
        private final int maximumLength;
        private final String invalidCharacters;

        // Optional parameters
        private int minimumCapsRequired = 0;
        private int minimumLowerRequired = 0;
        private int minimumSymbolsRequired = 0;
        private int minimumDigitsRequired = 0;
        private String forcedWord;
        private PasswordStrategy strategy;

        public PasswordGeneratorBuilder(int minLength, int maxLength, String invalidChars){
            if (minLength < 6){
                throw new IllegalArgumentException("Minimum length must be greater than 6");
            }
            if (maxLength < minLength){
                throw new IllegalArgumentException("Maximum length must be greather than or equal to minLength");
            }
            minimumLength = minLength;
            maximumLength = maxLength;
            invalidCharacters = ((invalidChars == null) ? "" : invalidChars);
        }

        public PasswordGeneratorBuilder setCapsMinimum(int minCaps){
            if (zeroIsGreater(minCaps)){
                throw new IllegalArgumentException("Minimum Caps cannot be less than zero");
            }
            minimumCapsRequired = minCaps;
            return this;
        }
        public PasswordGeneratorBuilder setLowerCaseMinimum(int minLower){
            if (zeroIsGreater(minLower)){
                throw new IllegalArgumentException("Minimum lower cannot be less that zero");
            }
            minimumLowerRequired = minLower;
            return this;
        }
        public PasswordGeneratorBuilder setSymbolsMinimum(int minSymbols){
            if (zeroIsGreater(minSymbols)){
                throw new IllegalArgumentException("Minimum Symbols cannot be less than zero");
            }
            minimumSymbolsRequired = minSymbols;
            return this;
        }
        public PasswordGeneratorBuilder setDigitsMinimum(int minDigits){
            if (zeroIsGreater(minDigits)){
                throw new IllegalArgumentException("Minimum Digits cannot be less than zero");
            }
            minimumDigitsRequired = minDigits;
            return this;
        }
        public PasswordGenerator build(){
            if ((minimumCapsRequired + minimumDigitsRequired + minimumSymbolsRequired) > maximumLength) {
                throw new IllegalArgumentException("Maximum Length exceeded by minimum characters arguements");
            }
            return new PasswordGenerator(this);
        }

        private boolean zeroIsGreater(int n) {return 0 > n;}

        public void setMinimums(int caps, int symbols, int digits, int lowerCase) {
            setCapsMinimum(caps);
            setSymbolsMinimum(symbols);
            setDigitsMinimum(digits);
            setLowerCaseMinimum(lowerCase);
        }

        public void forceWord(String word) {
            this.forcedWord = word;
        }

        public void setStrategy(PasswordStrategy strategy) {
            this.strategy = strategy;
        }
    }

    public PasswordGenerator(PasswordGeneratorBuilder ourPGBuilder){
        this.minimumLength = ourPGBuilder.minimumLength;
        this.maximumLength = ourPGBuilder.maximumLength;
        this.invalidCharacters = ourPGBuilder.invalidCharacters;
        this.minimumCapsRequired = ourPGBuilder.minimumCapsRequired;
        this.minimumLowerRequired = ourPGBuilder.minimumLowerRequired;
        this.minimumSymbolsRequired = ourPGBuilder.minimumSymbolsRequired;
        this.minimumDigitsRequired = ourPGBuilder.minimumDigitsRequired;
        this.strategy = ourPGBuilder.strategy;
    }


    public String generatePassword(){
        return strategy.makePassword();
    }


}
