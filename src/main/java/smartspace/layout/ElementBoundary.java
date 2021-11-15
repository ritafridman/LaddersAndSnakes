package smartspace.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import smartspace.data.ElementEntity;
import smartspace.data.Location;

public class ElementBoundary {
	
	private Map<String, String> key;
	private Map<String, Double> latlng;
	private String name;
	private String elementType;
	private Date created;
	private Boolean expired;
	private Map<String, String> creator;
	private Map<String, Object> elementProperties;
	
	public ElementBoundary() {
	}
	
	public ElementBoundary(ElementEntity entity) {
		this.key = new HashMap<String, String>();
		this.key.put("id", entity.getElementId());
		this.key.put("smartspace", entity.getElementSmartspace());
		this.creator = new HashMap<String, String>();
		this.creator.put("email", entity.getCreatorEmail());
		this.creator.put("smartspace", entity.getCreatorSmartspace());
		this.latlng = new HashMap<String, Double>();
		this.latlng.put("lat", entity.getLocation().getLat());
		this.latlng.put("lng", entity.getLocation().getLng());
		this.name = entity.getName();
		this.elementType = entity.getType();
		this.created = entity.getCreationTimestamp();
		this.expired = entity.getExpired();
		this.elementProperties=entity.getMoreAttributes();
	}

	public Map<String, String> getKey() {
		return key;
	}

	public void setKey(Map<String, String> key) {
		this.key = key;
	}
	
	public Map<String, String> getCreator() {
		return creator;
	}

	public void setCreator(Map<String, String> creator) {
		this.creator = creator;
	}

	public Map<String, Double> getLatlng() {
		return this.latlng;
	}

	public void setLatlng(Map<String, Double> latLng) {
		this.latlng = latLng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String type) {
		this.elementType = type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date creationTimestamp) {
		this.created = creationTimestamp;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public Map<String, Object> getElementProperties() {
		return elementProperties;
	}

	public void setElementProperties(Map<String, Object> elementProperties) {
		this.elementProperties = elementProperties;
	}

	public ElementEntity convertToEntity() {
		ElementEntity elementEntity = new ElementEntity();

		if (this.key != null) {
			elementEntity.setKey(this.key.get("id") + "#" + this.key.get("smartspace"));
		}
		
		elementEntity.setLocation(new Location(this.latlng.get("lat"), this.latlng.get("lng")));
		elementEntity.setName(this.name);
		elementEntity.setType(this.elementType);
		
		if (this.created != null) {
			elementEntity.setCreationTimestamp(this.created);
		}

		if (this.expired != null) {
			elementEntity.setExpired(this.expired);
		}
		
		if (this.creator != null) {
			elementEntity.setCreatorSmartspace("" + this.creator.get("smartspace"));
			elementEntity.setCreatorEmail("" + this.creator.get("email"));
		}
		
		elementEntity.setMoreAttributes(this.elementProperties);

		return elementEntity;
	}
	

	
	@Override
	public String toString() {
		return "ElementBoundary "
				+ "[key=" + key 
				+ ", latlng=" + latlng.toString() 
				+ ", name=" + name 
				+ ", elementType=" + elementType 
				+ ", created=" + created
				+ ", expired=" + expired 
				+ ", creator=" + creator
				+ ", elementProperties=" + elementProperties + "]";
	}
	

}
