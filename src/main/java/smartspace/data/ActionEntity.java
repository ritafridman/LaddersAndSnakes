package smartspace.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import smartspace.dao.rdb.MapToJsonConverter;

@Entity
@Table(name = "ACTIONS")
public class ActionEntity implements SmartspaceEntity<String> {

	String actionSmartspace;
	String actionId;
	String elementSmartspace;
	String elementId;
	String playerSmartspace;
	String playerEmail;
	String actionType;
	Date creationTimestamp;
	Map<String, Object> moreAttributes;

	public ActionEntity() {
		moreAttributes = new HashMap<>();
	}

	public ActionEntity(
			String elementSmartspace, 
			String elementId, 
			String playerSmartspace, 
			String playerEmail,
			String actionType, 
			Date creationTimestamp, 
			Map<String, Object> moreAttributes) {
		setElementSmartspace(elementSmartspace);
		setElementId(elementId);
		setPlayerSmartspace(playerSmartspace);
		setPlayerEmail(playerEmail);
		setActionId(actionType);
		setMoreAttributes(moreAttributes);
		setCreationTimestamp(creationTimestamp);
		setActionType(actionType);

	}

	@Transient
	public String getActionSmartspace() {
		return actionSmartspace;
	}

	@Transient
	public void setActionSmartspace(String actionSmartspace) {
		this.actionSmartspace = actionSmartspace;
	}

	@Transient
	public String getActionId() {
		return actionId;
	}

	@Transient
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getElementSmartspace() {
		return elementSmartspace;
	}

	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementid) {
		this.elementId = elementid;
	}

	public String getPlayerSmartspace() {
		return playerSmartspace;
	}

	public void setPlayerSmartspace(String playerSmartspace) {
		this.playerSmartspace = playerSmartspace;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(java.util.Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	@Lob
	@Convert(converter = MapToJsonConverter.class)
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
		return this.actionId + "#" + this.actionSmartspace;
	}

	@Override
	public void setKey(String key) {
		String[] tmpArr = key.split("#");
		this.actionId = tmpArr[0];
		this.actionSmartspace = tmpArr[1];
	}

	@Override
	public String toString() {
		return "Action [ Id" + this.actionId 
				+ ", SmartSpace - " + this.actionSmartspace 
				+ ", action type - " + this.actionType 
				+ ", element Smartspace - " + this.elementSmartspace 
				+ ", element ID - " + this.elementId 
				+ ", playerSmartspace - " + this.playerSmartspace 
				+ ", player email - " + this.playerEmail 
				+ ", creationTimestamp - " + this.creationTimestamp 
				+ ", more attributes - " + (this.moreAttributes == null ? "" : this.moreAttributes.toString()) 
				+ "]";
	}
}
