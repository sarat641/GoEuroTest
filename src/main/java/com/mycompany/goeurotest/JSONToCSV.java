package com.mycompany.goeurotest;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author SARATH
 */
public class JSONToCSV {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String CSV_FILE_HEADER = "_id,name,type,latitude,longitude";
    private static final Logger LOGGER = Logger.getLogger(JSONToCSV.class.getName());
    private static final String URL = "http://api.goeuro.com/api/v2/position/suggest/en/";
    private static final String CSV_FILE_NAME = "cityData.csv";

    public static void main(String args[]) {

        String city = null;
        if (args.length == 1) {
            city = args[0];
        } else {
            LOGGER.log(Level.INFO, "Invalid arguments");
            System.exit(0);
        }
        try {

            JSONArray jSONArray = readJsonFromUrl(URL.concat(city));
            writeToCSVFile(jSONArray);

        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Invalid URL ", e);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "", ex);
        } catch (JSONException ex) {
            LOGGER.log(Level.SEVERE, "In valid JSON", ex);
        }

    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        }
    }

    public static void writeToCSVFile(JSONArray jSONArray) {

        if (jSONArray.length() == 0) {
            LOGGER.log(Level.INFO, "No JSON Data found");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(CSV_FILE_NAME)) {
            fileWriter.append(CSV_FILE_HEADER);
            fileWriter.append(NEW_LINE_SEPARATOR);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                JSONObject geo_position = jSONObject.getJSONObject("geo_position");

                fileWriter.append(String.valueOf(jSONObject.get("_id")));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(jSONObject.getString("name"));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(jSONObject.getString("type"));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(geo_position.get("latitude")));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(geo_position.getDouble("longitude")));
                fileWriter.append(NEW_LINE_SEPARATOR);

            }
            try {
                fileWriter.flush();
                LOGGER.log(Level.INFO, "CSV File created successfully");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to create CSV File. Error while flushing {0}", e);
            }
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Unable to create CSV File. Error while parsing json", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to create CSV File Error in csv file writer", e);
        }
    }

}
