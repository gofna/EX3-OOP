package gameClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class KML_Loger {
	private static ArrayList<String> content = new ArrayList<String>();
	private static String kmlelement;

	public static void createPlacemark(double x, double y, String element) {
		if (x == 0 & y == 0) {

		} else {
			kmlelement += "<Placemark>\n" + "<TimeStamp>\n" + "<when>" + time() + "</when>\n" + "</TimeStamp>\n"
					+ "<styleUrl>#" + element + "</styleUrl>\n" + "<Point>\n" + "<coordinates>" + x + "," + y
					+ "</coordinates>\n" + "</Point>\n" + "<TimeSpan>\n" + "<begin>" + 1 + "</begin>\n" + "<end>"
					+ 0 + "</end>\n" + "</TimeSpan>\n" + "</Placemark>\n";
		}
	}

	private static String time() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public static void createKMLFile(int senario) {
		char o = '"';

		String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" + "\t<Document>\n"
				+ "\t<name>robots</name>\n" +
				"\t<description>" + senario + "</description>\n" + 
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

		// Zum Einsetzen eines Substrings (weitere Placemark)
		// String test = "</kml>";
		// int index = kml.lastIndexOf(test);

		File test = new File("" + String.valueOf(senario) + ".kml");
		Writer fwriter;

		if (test.exists() == false) {
			try {
				content.add(0, kmlstart);
				content.add(1, kmlelement);
				content.add(2, kmlend);

				kmltest = content.get(0) + content.get(1) + content.get(2);

				fwriter = new FileWriter("KML_games/" + String.valueOf(senario) + ".kml");
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
					fwriter = new FileWriter("" + String.valueOf(senario) + ".kml");
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
