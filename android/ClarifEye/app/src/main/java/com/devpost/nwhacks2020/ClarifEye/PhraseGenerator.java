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
            " to the left of ",
            " to the right of ",
            " with ",        //affects/disregards sentence emphasis
            " in front of ", //distinguish from below
            " behind "       //distinguish from above
    };


    /**
     * used to store objects in image
     */
    protected class Item implements Comparable<Item>{
        protected class ItemSides{
            protected double top;
            protected double bottom;
            protected double right;
            protected double left;
        }

        private String name;
        private ItemSides sides;
        private double prob;
        private double rel_imp;

        /**
         * @param name     label of the object
         * @param sides    four given sides
         * @param prob     probability score
         */
        public Item(String name, double[] sides, double prob) { //sides = top, bottom, right, left
            this.name = name;

            this.sides = new ItemSides();
            this.sides.top = sides[0];
            this.sides.bottom = sides[1];
            this.sides.right = sides[2];
            this.sides.left = sides[3];

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

        public double get_center(boolean isX)
        {
           if(isX) return (sides.left + sides.right) / 2;
           else return (sides.top + sides.bottom) / 2;
        }

        public double get_width(boolean isX) {
            if(isX) return sides.left - sides.right;
            else return sides.top - sides.bottom;
        }

        /**
         * calculates how close the center of the Item is to the center of the image
         * @return
         */
        private double twoD_center() {
            return 1 - Math.abs((get_center(true) + get_center(false) / 2) - 0.5);
        }

        /**
         * calculates the area covered by a plane spread between the given vertices
         * @return
         */
        private double calculate_area() {
            return 1000 * get_width(true) * get_width(false); //multiply by 1000 to avoid issues with insufficient precision
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

            double[] scores = new double[prepPhrases.length];
            for(double s : scores)
                s = 0;
            //and
            scores[0] = 0.5;
            //above
//            if(topic.sides.bottom < adjunct.sides.top)
//                scores[1] = 1 - (topic.sides.bottom - adjunct.sides.top);
//            //below
//            if(topic.sides.top > adjunct.sides.bottom)
//                scores[2] = 1 - (adjunct.sides.bottom - topic.sides.top);
//            // to the left of
//            if(topic.sides.right < adjunct.sides.left)
//                scores[3] = 1 - (adjunct.sides.left - topic.sides.right);
//            //to the right of
//            if(topic.sides.left > adjunct.sides.right)
//                scores[4] = 1 - (topic.sides.left - adjunct.sides.right);

            //above
            if(adjunct.get_center(false) > topic.get_center(false))
                scores[1] = 1 - (adjunct.get_center(false) - topic.get_center(false));
            else
                scores[2] = 1 - (topic.get_center(false) - adjunct.get_center(false));
            //left
            if(adjunct.get_center(true) > topic.get_center(true))
                scores[3] = 1 - (adjunct.get_center(true) - topic.get_center(true));
            else
                scores[4] = 1 - (topic.get_center(true) - adjunct.get_center(true));


            int topscore = 0;
            for(int i = 1; i < scores.length; i++) {
                if (scores[i] > scores[topscore])
                    topscore = i;
            }

            this.score = scores[topscore];
            this.prepositionIndex = topscore;
        }
    }

    public String generatePhrase(AnnotateImageResponse annotateImageResponse, VisionRequestor.Mode mode) {
        String phrase = "Nothing has been detected.";
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

    private String generatePhraseDescribe(AnnotateImageResponse annotateImageResponse) {

        List<Item> objects = convertJSONtoitem(annotateImageResponse);

        sortObjects(objects);

        Item[] temp = new Item[objects.size()];
        temp = objects.toArray(temp);
        List<PrepositionPair> ppairs = build_preposition_pairs(temp);

        return buildPhrase(objects, ppairs);
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
    private List<Item> convertJSONtoitem(AnnotateImageResponse response){
        List<Item> objects = new ArrayList<Item>();

        Object r = new JsonParser().parse(response.toString());
        JsonObject parsedResponse = (JsonObject) r;
        JsonArray  annotations = (JsonArray) parsedResponse.get("localizedObjectAnnotations");

        if(annotations == null) {
            return objects;
        }

        for(JsonElement a : annotations)
        {
            JsonObject annotation = a.getAsJsonObject();
            String name = annotation.get("name").getAsString();
            double score = annotation.get("score").getAsDouble();

            double[][] vertices = new double[2][];
            vertices[0] = new double[4]; //x dimension
            vertices[1] = new double[4]; //y dimension

            JsonObject boundingPoly = annotation.get("boundingPoly").getAsJsonObject();
            JsonArray verts = boundingPoly.get("normalizedVertices").getAsJsonArray();

            try {
                int i = 0;
                for (JsonElement vert : verts) {
                    JsonObject vertice = (JsonObject) vert;
                    vertices[0][i] = vertice.get("x").getAsDouble();
                    vertices[1][i] = vertice.get("y").getAsDouble();
                    i++;
                }
            }
            catch (NullPointerException e) {
                continue;
            }

            double[] sides = new double[4];
            //the first and third item in the list are always opposite
            sides[0] = Math.max(vertices[1][0], vertices[1][2]);
            sides[1] = Math.min(vertices[1][0], vertices[1][2]);
            sides[2] = Math.max(vertices[0][0], vertices[0][2]);
            sides[3] = Math.min(vertices[0][0], vertices[0][2]);


            Item newItem = new Item(name, sides, score);
            objects.add(newItem);
        }
        return objects;
    }

    private List<PrepositionPair> build_preposition_pairs(Item[] objects) {
        List<PrepositionPair> pairs = new ArrayList<PrepositionPair>();
        for(int i = 0; i < objects.length; i++){
            for(int j = i + 1; j < objects.length; j++)
                pairs.add(new PrepositionPair(objects[i], objects[j]));
        }
        return pairs;
    }

    private String buildPhrase(List<Item> objects, List<PrepositionPair> ppairs) {

        System.out.println("# of objects = " + objects.size());
        System.out.println("# of ppairs = " + ppairs.size());

        if(objects.size() == 0) {
            return "No objects recognized.";
        }
        else if (objects.size() == 1) {
            return "There is a " + objects.get(0).name;
        }
        else {
            List<PrepositionPair> pps = new ArrayList<PrepositionPair>();
            for(int i = 0; i < objects.size(); i++) {
                int topP = 0;
                boolean found = false;
                for(int p = 0; p < ppairs.size(); p++) {
                    if(ppairs.get(p).topic.name.equals(objects.get(i).name) || ppairs.get(p).adjunct.name.equals(objects.get(i).name)) {
                        if(!found) {
                            topP = p;
                            found = true;
                        }
                        else if (ppairs.get(p).score > ppairs.get(topP).score) {
                            topP = p;
                        }
                    }
                }
                if (found) {
                    if(!pps.contains(ppairs.get(topP))) {
                        pps.add(ppairs.get(topP));
                    }
                }
            }
            String phrase = "";
            boolean and = true;
            Item lastAdjunct = null;
            boolean whichIs = false;
            for(int i = 0; i < objects.size(); i++) {
                String tempPhrase = "";
                if(whichIs) {
                    tempPhrase = "which is ";
                    whichIs = false;
                }
                else {
                    tempPhrase = "There is a " + objects.get(i).name;
                }
                boolean topic = false;
                for (int p = 0; p < pps.size(); p++) {
                    if (pps.get(p).topic.name.equals(objects.get(i).name)) {
                        if(topic) {
                            tempPhrase = tempPhrase + " and";
                        }
                        else {
                            topic = true;
                        }
                        tempPhrase = tempPhrase + prepPhrases[pps.get(p).prepositionIndex] + "a " + pps.get(p).adjunct.name;
                        lastAdjunct = pps.get(p).adjunct;
                        if(i != objects.size()-1 && objects.get(i+1).equals(lastAdjunct)) {
                            whichIs = true;
                        }
                    }

                }
                if(topic) {
                    if(!and || i == objects.size()-1) {
                        tempPhrase = tempPhrase + ". ";
                        and = true;
                    }
                    else {
                        tempPhrase = tempPhrase + " and ";
                        and = false;
                    }
                    phrase = phrase + tempPhrase;
                }
            }
            System.out.println(phrase.subSequence(phrase.length()-5,phrase.length()-1));
            if(phrase.subSequence(phrase.length()-5,phrase.length()-1).equals(" and")) {
                phrase = phrase.substring(0,phrase.length()-5) + ". ";
            }
            System.out.println(phrase);
            return phrase;
        }
    }
}

