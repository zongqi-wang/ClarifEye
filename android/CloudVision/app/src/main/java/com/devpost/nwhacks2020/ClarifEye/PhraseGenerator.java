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
        return "This is a test of the Clarify app.";
    }
}
