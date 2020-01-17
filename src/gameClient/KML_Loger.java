package gameClient;

import java.util.ArrayList;

import utils.Point3D;

public class KML_Loger {
	public void createKMLFile(int senario){
		 

        String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n";

        String kmlelement ="\t<Placemark>\n" +
                            "\t<name>Simple placemark</name>\n" +
                            "\t<description>"+senario+"</description>\n" +
                            "\t<Point>\n" +
                            "\t\t<coordinates>"+3+","+2+"</coordinates>\n" +
                            "\t</Point>\n" +
                            "\t</Placemark>\n";

        String kmlend = "</kml>";

        ArrayList<String> content = new ArrayList<String>();
        //content.add(0,kmlstart);
        //content.add(1,kmlelement);
        //content.add(2,kmlend);

        String kmltest;


        //Zum Einsetzen eines Substrings (weitere Placemark)
        //String test = "</kml>";
        //int index = kml.lastIndexOf(test);

        File test = new File(datapath+"/"+name+".kml");
        Writer fwriter;

        if(test.exists() == false){
            try {
                content.add(0,kmlstart);
                content.add(1,kmlelement);
                content.add(2,kmlend);
                kmltest = content.get(0) + content.get(1) + content.get(2);

                fwriter = new FileWriter(datapath+"/"+name+".kml");
                fwriter.write(kmltest);
                //fwriter.append("HalloHallo", index, kml.length());
                fwriter.flush();
                fwriter.close();
            }catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }   
        }
        else{
            kmltest = content.get(0) + content.get(1) + content.get(2);
            StringTokenizer tokenize = new StringTokenizer(kmltest, ">");
            ArrayList<String> append = new ArrayList<String>();
            while(tokenize.hasMoreTokens()){

            append.add(tokenize.nextToken());
            append.add(1, kmlelement);

            String rewrite = append.toString();
            try {
                fwriter = new FileWriter(datapath+"/"+name+".kml");
                fwriter.write(rewrite);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            }

        }



    }
}
