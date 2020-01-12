package com.devpost.nwhacks2020.ClarifEye;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
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
         * Default constructor
         */
        public Item() {
        }


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
         * calculates the relative importance of this function
         */
        private void setRel_imp() {
            double centering = calculate_center(vertices);
            double area = calculate_area(vertices);
            this.rel_imp = centering * area;
        }

        public double getRel_imp() {
            return rel_imp;
        }

        /**
         * calculates how close the center of the Item is to the center of the image
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
        private Item subject;     //index in objects array TODO: rename variable
        private double score;     //confidence in appropriateness of prepositional phrase

        private PrepositionPair() {



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
     *
     * @param annotateImageResponse response from google cloud API TODO: update documentation
     * @return ArrayList of objects in the photo
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


    // create_objects (AnnotateImageResponse > Item[])

    // build_preposition_pairs ( Item[] ) > PrepositionPair[]
    //foreach i in Item.length
    //compare(Item[i], Item[i+1])

    //compare(item1, item2) > PrepositionPair

    //build_string(Item[], PrepositionPair[])
    //foreach(Item[])
    //get best PrepositionPair with Item[i]
}

