package com.devpost.nwhacks2020.ClarifEye;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;

public class PhraseGenerator {
    private String[] prepPhrases = {
        " and ", //generic
        " above ",
        " below ",
        " beside ",
        " in front of ", //distinguish from below
        " behind "       //distinguish from above
    };

    public static String generatePhrase(AnnotateImageResponse annotateImageResponse, VisionRequestor.Mode mode) {
        String phrase = "This is a test of the Clarify app.";
        switch (mode) {
            case DESCRIBE:
                phrase = generatePhraseDescribe(annotateImageResponse);
                break;
            case READ:
                phrase = generatePhraseRead(annotateImageResponse);
                break;
        }
        return phrase;
    }

    public static String generatePhraseDescribe(AnnotateImageResponse annotateImageResponse) {
        return "";
    }
    
    public static String generatePhraseRead(AnnotateImageResponse annotateImageResponse) {
        return "";
    }
}
