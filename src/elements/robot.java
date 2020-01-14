package elements;

import org.json.JSONObject;

import utils.Point3D;

	public class robot {
		private int pos;
		private int id;
		
		public robot(int id) {
			this.setId(id);
			this.setPos(0);
	
	
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	
}
