package com.devpost.nwhacks2020.ClarifEye;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.LocalizedObjectAnnotation;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.*;

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
    protected class item {
        private String name;
        private double[][] vertices; //no tuples in Java :(. First array is x, y coordinates, second is each point in normalizedVertices
        private double prob;
        private double rel_imp;

        /**
         * Default constructor
         */
        public item() {
        }


        /**
         * @param name     label of the object
         * @param vertices four given vertices
         * @param prob     probability score
         */
        public item(String name, double[][] vertices, double prob) {
            this.name = name;
            this.vertices = vertices;
            this.prob = prob;
            setRel_imp();
        }

        /**
         * calculates the relative importance of this function
         */
        private void setRel_imp() {
            double centering = calculate_center(vertices);
            double area = calculate_area(vertices);
            this.rel_imp = centering * area;
        }

        /**
         * calculates how close the center of the item is to the center of the image
         * 0.5 is perfectly centered.
         * @param verts
         * @return
         */
        private double calculate_center(double[][] verts) {
            double sum = 0;
            for(double[] dim : verts) {
                double dimsum = 0;
                for(double coord : dim)
                {
                    dimsum += coord;
                }
                sum += dimsum / dim.length;
            }
            return sum / verts.length;
        }

        /**
         * calculates the area covered by a plane spread between the given vertices
         * @param verts
         * @return
         */
        private double calculate_area(double[][] verts) {
            return 1; //TODO:
        }
    }

    /**
     * used to store best guess of a relationship between two objects
     */
    private class prepositionPair {
        private int prepositionIndex; //index in prepPhrases array
        private item adjunct;     //index in objects array
        private item subject;     //index in objects array TODO: rename variable
        private double score;     //confidence in appropriateness of prepositional phrase

        private prepositionPair() {



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

    public static String generatePhraseRead(AnnotateImageResponse annotateImageResponse) {
        if(annotateImageResponse.getTextAnnotations() != null) {
            String phrase = annotateImageResponse.getTextAnnotations().get(0).getDescription().replaceAll("\n", " ");
            System.out.println(phrase);
            return phrase;
        }
        else {
            return "No text detected.";
        }
    }

    /**
     * This function converts the API returned JSON function into a list of item objects
     *
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


    // create_objects (AnnotateImageResponse > item[])

    // build_preposition_pairs ( item[] ) > prepositionPair[]
    //foreach i in item.length
    //compare(item[i], item[i+1])

    //compare(item1, item2) > prepositionPair

    //build_string(item[], prepositionPair[])
    //foreach(item[])
    //get best prepositionPair with item[i]
}

