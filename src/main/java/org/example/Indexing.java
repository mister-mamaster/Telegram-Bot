package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class Indexing {
    private final ObjectMapper mapper = new ObjectMapper();

    public Indexing() {
    }

    public String getIndex(String name) throws IOException {
        name = name.replace(" ", "_");
        File file = new File("src/main/resources/Indexes.json");
        BufferedReader reader = new BufferedReader(new FileReader(file));

        JsonNode node = mapper.readTree(reader).get("indexes");

        int index;
        try {
            index = node.get(name).asInt();
        } catch (NullPointerException ex){
            return "1-hellish_rebuke";
        }


        return String.valueOf(index) + "-" + name;
    }
}
