package com.devpost.nwhacks2020.ClarifEye;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    protected static class Item implements Comparable<Item>{
        private String name;
        private double[][] vertices; //no tuples in Java :(. First array is x, y coordinates, second is each point in normalizedVertices
        private double prob;
        private double rel_imp;

        /**
         * @param name     label of the object
         * @param vertices four given vertices
         * @param prob     probability score
         */
        public Item(String name, double[][] vertices, double prob) {
            this.name = name;
            this.vertices = vertices;
            this.prob = prob;
            setRel_imp();
        }

        /**
         * calculates the relative importance of this item
         */
        private void setRel_imp() {
            double centering = twoD_center();
            double area = calculate_area();
            this.rel_imp = centering * area;
        }

        /**
         * returns the relative importance of this item
         * @return
         */
        public double getRel_imp() {
            return rel_imp;
        }

        /**
         * return the center of the object in the x (0) or y (1) dimension
         * @param dimension
         * @return
         */
        public double get_center(int dimension)
        {
            double sum = 0;
            for(double v : vertices[dimension]){
                sum += (v / vertices[dimension].length);
            }
            return sum;
        }

        /**
         * return the width of the object in the x (0) or y (1) dimension
         * @param dimension
         * @return
         */
        public double get_width(int dimension) {
            double w = 0;
            double center = get_center(dimension);
            for(double v : vertices[dimension]){
                w += Math.abs((v - center) / 2);
            }
            return w;
        }

        /**
         * calculates how close the center of the Item is to the center of the image
         * @return
         */
        private double twoD_center() {
            double sum = get_center(0) + get_center(1) / vertices.length;
            return Math.abs((sum / vertices.length) - 0.5);
        }

        /**
         * calculates the area covered by a plane spread between the given vertices
         * @return
         */
        private double calculate_area() {
            return 1000 * get_width(0) * get_width(1); //multiply by 1000 to avoid issues with insufficient precision
        }


        public int compareTo(Item compareItem) {

            //ascending order
            return (int) (getRel_imp() - compareItem.getRel_imp());

            //descending order
            //return compareQuantity - this.quantity;

        }
    }

    /**
     * used to store best guess of a relationship between two objects
     */
    private class PrepositionPair {
        private int prepositionIndex; //index in prepPhrases array
        private Item adjunct;     //index in objects array
        private Item topic;     //index in objects array
        private double score;     //confidence in appropriateness of prepositional phrase

        private PrepositionPair(Item topic, Item adjunct) {
            this.topic = topic;
            this.adjunct = adjunct;

            prepositionIndex = 0;
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
        List<Item> objects = convertJSONtoitem(annotateImageResponse);

        sortObjects(objects);

        //ppairs = build_preposition_pairs (objects)

        // return build_string(objects, ppairs)
        return "This is a test of the Clarify app.";
    }

    private static void sortObjects(List<Item> objects){
        Collections.sort(objects);
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
     * This function converts the API returned JSON function into a list of Item objects
     * @param response output from vision
     * @return
     */
    private static List<Item> convertJSONtoitem(AnnotateImageResponse response){
        List<Item> objects = new ArrayList<Item>();

        Object r = new JsonParser().parse(response.toString());
        JsonObject parsedResponse = (JsonObject) r;
        JsonArray  annotations = (JsonArray) parsedResponse.get("localizedObjectAnnotations");

        for(JsonElement a : annotations)
        {
            JsonObject annotation = a.getAsJsonObject();
            String name = annotation.get("name").getAsString();
            double score = annotation.get("score").getAsDouble();

            double[][] vertices = new double[2][];
            for(double[] v : vertices)
                v = new double[4];

            JsonObject boundingPoly = annotation.get("boundingPoly").getAsJsonObject();
            JsonArray verts = boundingPoly.get("normalizedVertices").getAsJsonArray();

            int i = 0;
            for(JsonElement vert : verts) {
                JsonObject vertice = (JsonObject) vert;
                vertices[0][i] = vertice.get("x").getAsDouble();
                vertices[1][i] = vertice.get("y").getAsDouble();
            }

            Item newItem = new Item(name, vertices, score);
            objects.add(newItem);
        }
        return objects;
    }

    //build_preposition_pairs ( Item[] ) > PrepositionPair[]
    //foreach i in Item.length
    //new PrepositionPhrase(Item[i], Item[i+1])

    //build_string(Item[], PrepositionPair[])
    //foreach(Item[])
    //get best PrepositionPair with Item[i]
}

