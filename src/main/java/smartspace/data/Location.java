package smartspace.data;

import javax.persistence.Embeddable;

@Embeddable
public class Location {
	
	double lat;
	double lng;
	
	public Location() {
		// TODO Auto-generated constructor stub
	}
	
	public Location(double lat , double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	@Override
	public String toString() {
		return 	"Location [Latitude - " + this.lat 
				+ ", Longitude - " + this.lng 
				+ "]";		
	}

}
