package com.devpost.nwhacks2020.ClarifEye;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;

public class PhraseGenerator {
    private static String[] prepPhrases = {
            " and ", //generic
            " above ",
            " below ",
            " beside ",
            " with ",        //affects/disregards sentence emphasis
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

    private static String generatePhraseDescribe(AnnotateImageResponse annotateImageResponse) {
        //objects = create_objects (annotateImageResponse)

        //objects = array.sort(objects)

        //ppairs = build_preposition_pairs (objects)

        // return build_string(objects, ppairs)
        return "This is a test of the Clarify app.";
    }

    private static String generatePhraseRead(AnnotateImageResponse annotateImageResponse) {
        return "";
    }

    private class item{
        private item() {
            //calculate and store importance
            //overload comparison
        }
    }

    private class prepositionPair{
        private prepositionPair() {
            int prepositionIndex;
            int adjunctIndex;
            int subjectIndex; //rename variable
            double score;

            //overload comparison
        }
    }

    // create_objects (AnnotateImageResponse > item[])

    // build_preposition_pairs ( item[] ) > prepositionPair[]
        //foreach i in item.length
            //compare(item[i], item[i+1])

    //compare(item1, item2) > prepositionPair

    //build_string(item[], prepositionPair[])
        //foreach(item[])
            //get best prepositionPair with item[i]
}

