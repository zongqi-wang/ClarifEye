package com.devpost.nwhacks2020.ClarifEye;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.LocalizedObjectAnnotation;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * used to store objects in image
     */
    protected class item{
        private String name;
        private int[][] vertices; //no tuples in Java :(. First array is x, y coordinates, second is each point in normalizedVertices
        private int prob;
        private int rel_imp;

        /**
         * Default constructor
         */
        public item() {


        }


        /**
         *
         * @param name label of the object
         * @param vertices four given vertices
         * @param prob probability score
         */
        public item(String name, int[][] vertices, int prob){
            this.name = name;
            this.vertices = vertices;
            this.prob = prob;
            setRel_imp();
        }

        /**
         * calculates the relative importance of this function
         */
        private void setRel_imp(){
            //TODO:
        }


    }


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
        List<item> objects = convertJSONtoitem(annotateImageResponse);

        //objects = array.sort(objects)

        //ppairs = build_preposition_pairs (objects)

        // return build_string(objects, ppairs)
        return "This is a test of the Clarify app.";
    }

    private static String generatePhraseRead(AnnotateImageResponse annotateImageResponse) {
        return "";
    }

    /**
     * This function converts the API returned JSON function into a list of item objects
     * @param annotateImageResponse response from google cloud API
     * @return ArrayList of objects in the photo
     */
    private static List<item> convertJSONtoitem(AnnotateImageResponse response){
        List<item> objects = new ArrayList<item>();

        //TODO: parse JSON file
        List<EntityAnnotation> labels = response.getLabelAnnotations();
        List<LocalizedObjectAnnotation> local_obj = response.getLocalizedObjectAnnotation();
        return objects;
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

