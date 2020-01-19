package elements;

import org.json.JSONObject;
import utils.Point3D;

/**
 * this class represent a fruit in the game from the server.
 * 
 * @author Gofna Ivry and Maor Ovadia
 *
 */
public class fruit {
	private Point3D location;
	private String image;
	private double value;
	private int type;

	/**
	 * constructor function to initialize the parameters of the fruit according to the server.
	 * @param s the string of the information of the fruit from the server.
	 */
	public fruit(String s) {
		try {
			JSONObject fruits = new JSONObject(s);
			JSONObject fruit = fruits.getJSONObject("Fruit");
			String pos = fruit.getString("pos");
			this.setLocation(new Point3D(pos));
			this.type = fruit.getInt("type");
			this.setValue(fruit.getDouble("value"));
			if (this.type == -1) {
				this.image = "data/banana.PNG";
			} else {
				this.image = "data/apple.PNG";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Point3D getLocation() {
		return location;
	}

	public void setLocation(Point3D location) {
		this.location = location;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
