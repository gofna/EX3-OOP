package gameClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * this class create a KML file to the automatic game from the class "MyGameGui". 
 * the name of the file will be the scenario number of the game, and the file will save in a folder call "KML_games".
 * 
 * @author Gofna Ivry and Maor Ovadia
 */
public class KML_Loger {
	private static ArrayList<String> content = new ArrayList<String>();
	private static String kmlelement;

	/**
	 * this function get a position point on the graph and the element to document (robot, banana or apple)
	 * and add the string to copy this strings to the KML file
	 * @param x the x position
	 * @param y the y position
	 * @param element the element to document in the file(robot, banana or apple).
	 */
	public static void createPlacemark(double x, double y, String element, boolean even, long time) {
		if (x == 0 & y == 0) {

		} else {
			if(even) {
			kmlelement += "<Placemark>\n" + "<TimeStamp>\n" + "<when>" + time() + "</when>\n" + "</TimeStamp>\n"
					+ "<styleUrl>#" + element + "</styleUrl>\n" + "<Point>\n" + "<coordinates>" + x + "," + y
					+ "</coordinates>\n" + "</Point>\n" + "<TimeSpan>\n" + "<begin>" + (30-time/1000) + "</begin>\n" + "<end>"
					+ ((30-time/1000)+1) + "</end>\n" + "</TimeSpan>\n" + "</Placemark>\n";
			}
			else {
				kmlelement += "<Placemark>\n" + "<TimeStamp>\n" + "<when>" + time() + "</when>\n" + "</TimeStamp>\n"
						+ "<styleUrl>#" + element + "</styleUrl>\n" + "<Point>\n" + "<coordinates>" + x + "," + y
						+ "</coordinates>\n" + "</Point>\n" + "<TimeSpan>\n" + "<begin>" + (60-time/1000) + "</begin>\n" + "<end>"
						+ ((60-time/1000)+1) + "</end>\n" + "</TimeSpan>\n" + "</Placemark>\n";
			}
		}
	}

	/**
	 * the function return the present time in specific format.
	 * @return the string of the present time.
	 */
	private static String time() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	/**
	 * this function create the KML file with name of the scenario game. 
	 * the function add the start of the file, and all the elements using the string of the elements.
	 *  the function also insert elements in existing file.
	 * @param scenario will be the name of the file (the scenario of the game).
	 */

	public static void createKMLFile(int scenario) {
		char o = '"';

		String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" + "\t<Document>\n"
				+ "\t<name>robots</name>\n" +
				"\t<description>" + scenario + "</description>\n" + 
				"<Style id="+o+"ski"+o+">\n" +
				"<IconStyle>\n" +
				"<Icon>\n" +
				"<href>http://maps.google.com/mapfiles/kml/shapes/ski.png</href>\n" +
				" </Icon>\n" +
				"<hotSpot x="+o+'0'+o+ " y="+o+".5"+o+" xunits=" +o+ "fraction"+o+ " yunits="+o+"fraction"+o+"/>\n" +
				"</IconStyle>\n" +
				"</Style>\n"+
				"<Style id="+o+"apple"+o+">\n" +
				"<IconStyle>\n" +
				"<Icon>\n" +
				"<href>http://maps.google.com/mapfiles/kml/pal4/icon49.png</href>\n" +
				" </Icon>\n" +
				"<hotSpot x="+o+"32"+o+ " y="+o+"1"+o+" xunits=" +o+ "fraction"+o+ " yunits="+o+"fraction"+o+"/>\n" +
				"</IconStyle>\n" +
				"</Style>\n"+
				"<Style id="+o+"banana"+o+">\n" +
				"<IconStyle>\n" +
				"<Icon>\n" +
				"<href>http://maps.google.com/mapfiles/kml/pal4/icon47.png</href>\n" +
				" </Icon>\n" +
				"<hotSpot x="+o+"32"+o+ " y="+o+"1"+o+" xunits=" +o+ "fraction"+o+ " yunits="+o+"fraction"+o+"/>\n" +
				"</IconStyle>\n" +
				"</Style>\n";

		String kmlend = "</Document>\n"+
				"</kml>";

		content.add(0, kmlstart);
		if(kmlelement != null) {
			content.add(1, kmlelement);
		}
		content.add(2, kmlend);

		String kmltest;

		File test = new File("" + String.valueOf(scenario) + ".kml");
		Writer fwriter;

		if (test.exists() == false) {
			try {
				content.add(0, kmlstart);
				content.add(1, kmlelement);
				content.add(2, kmlend);

				kmltest = content.get(0) + content.get(1) + content.get(2);

				fwriter = new FileWriter("data/" + String.valueOf(scenario) + ".kml");
				fwriter.write(kmltest);
				fwriter.flush();
				fwriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			kmltest = content.get(0) + content.get(1) + content.get(2);
			StringTokenizer tokenize = new StringTokenizer(kmltest, ">");
			ArrayList<String> append = new ArrayList<String>();
			while (tokenize.hasMoreTokens()) {

				append.add(tokenize.nextToken());

				String rewrite = append.toString();
				try {
					fwriter = new FileWriter("data/" + String.valueOf(scenario) + ".kml");
					fwriter.write(rewrite);
					fwriter.flush();
					fwriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}

}
