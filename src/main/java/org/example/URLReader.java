package org.example;

import java.net.*;
import java.io.*;

public class URLReader {

    private static URL oracle;

    static {
        try {
            oracle = new URL("https://dnd.su/spells/287-false_life/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static String getData() throws Exception {


        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine = in.readLine();

        in.close();
        return inputLine;
    }
}
