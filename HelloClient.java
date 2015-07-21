package com.test.jersey.client;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HelloClient {
	//Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "_id, name, type, latitude, longitude";

	public static void main(String args[]) throws JSONException{
		 if(args.length > 0){
			ClientConfig config= new ClientConfig();
			Client client=ClientBuilder.newClient(config);
			WebTarget target=client.target(getBaseURI(args[0]));
			JSONArray locationArray = new JSONArray(target.request().accept(MediaType.APPLICATION_JSON).get(String.class));
			writeCsvFile(args[0],locationArray);
	     }else{
	    	 System.out.println("Oops ! You should pass any place name as an argument to this program");
	    	 System.exit(1);
	     }

	}
	private static void writeCsvFile(String locationCode,JSONArray locationArray) {
		FileWriter fileWriter = null;
		try {
			System.out.println("Location Code:"+locationCode+"\n");
			System.out.println("Location Array:"+locationArray+"\n");
			JSONObject childJSON = null;
			String fileName = locationCode+"-location.csv";
			fileWriter = new FileWriter(System.getProperty("user.home")+"/"+fileName);

			//Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			while(locationArray.length()>0){
				fileWriter.append(String.valueOf(locationArray.getJSONObject(0).getInt("_id")));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(locationArray.getJSONObject(0).getString("name"));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(locationArray.getJSONObject(0).getString("type"));
				fileWriter.append(COMMA_DELIMITER);
				childJSON = locationArray.getJSONObject(0).getJSONObject("geo_position");
				fileWriter.append(String.valueOf(childJSON.getDouble("latitude")));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(childJSON.getDouble("longitude")));
				fileWriter.append(NEW_LINE_SEPARATOR);
				locationArray.remove(0);
				childJSON = null;
			}
			System.out.println("CSV file was created successfully at location:"+System.getProperty("user.home")+"/"+fileName+" !!!");
		}catch (Exception e) {
		    System.out.println("Error in CsvFileWriter !!!");
		    e.printStackTrace();
			} finally {
				try {
				      fileWriter.flush();
				      fileWriter.close();
	            } catch (IOException e) {
				    System.out.println("Error while flushing/closing fileWriter !!!");
				    e.printStackTrace();
				}
			}

	}

	private static URI getBaseURI(String locationCode) {
		return UriBuilder.fromUri("http://api.goeuro.com/api/v2/position/suggest/en/"+locationCode).build();
	}

}
