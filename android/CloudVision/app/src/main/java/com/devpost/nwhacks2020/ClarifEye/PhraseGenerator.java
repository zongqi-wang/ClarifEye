package com.devpost.nwhacks2020.ClarifEye;

public class PhraseGenerator {
    private String[] prepPhrases = {
        " and ", //generic
        " above ",
        " below ",
        " beside ",
        " in front of ", //distinguish from below
        " behind "       //distinguish from above
    };

    public static String generatePhrase() {
        return "This is a test of the Clarify app.";
    }
}
