package smartspace.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import smartspace.dao.rdb.MapToJsonConverter;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity implements SmartspaceEntity<String> {

	protected String elementSmartspace;
	String elementId;
	Location location;
	String name;
	String type;
	Date creationTimestamp;
	boolean expired;
	String creatorSmartspace;
	String creatorEmail;
	protected Map<String, Object> moreAttributes;
	

	
	public ElementEntity() {
		moreAttributes = new HashMap<>();
	}

	public ElementEntity(
			String name, 
			String type, 
			Location location, 
			boolean expired, 
			Date creationTimestamp,
			String creatorSmartspace, 
			String creatorEmail,
			Map<String, Object> moreAttributes) {
		//super();
		setName(name);
		setType(type);
		setLocation(location);
		setExpired(expired);
		setCreatorSmartspace(creatorSmartspace);
		setCreatorEmail(creatorEmail);
		setMoreAttributes(moreAttributes);
		setCreationTimestamp(creationTimestamp);

	}

	@Transient
	public String getElementSmartspace() {
		return elementSmartspace;
	}

	@Transient
	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}

	@Transient
	public String getElementId() {
		return elementId;
	}

	@Transient
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	@Embedded
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(java.util.Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public boolean getExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getCreatorSmartspace() {
		return creatorSmartspace;
	}

	public void setCreatorSmartspace(String creatorSmartspace) {
		this.creatorSmartspace = creatorSmartspace;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	@Lob
	@Convert(converter=MapToJsonConverter.class)
	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> moreAttributes) {
		this.moreAttributes = moreAttributes;
	}

	public void addToAttributesMap(String attributeName, Object attributeObj) {
		this.moreAttributes.put(attributeName, attributeObj);
	}
	
	@Column(name = "ID")
	@Id
	@Override
	public String getKey() {
		return this.elementId + "#" + this.elementSmartspace;
	}

	@Override
	public void setKey(String key) {
		String[] tmpArr = key.split("#");
		this.elementId = tmpArr[0];
		this.elementSmartspace = tmpArr[1];
	}

	@Override
	public String toString() {
		return 
				"ElementEntity [ Id" + this.elementId 
				+ ", SmartSpace - " + this.elementSmartspace 
				+ ", location - " + this.location 
				+ ", element name - " + this.name 
				+ ", element type - " + this.type 
				+ ", expired - " + this.expired 
				+ ", creationTimestamp - " + this.creationTimestamp 
				+ ", creatorSmartspace - " + this.creatorSmartspace 
				+ ", creatorEmail - " + this.creatorEmail 
				+ ", more attributes - "	+ (this.moreAttributes == null ? "" : this.moreAttributes.toString())
				+ "]";
	}


}
