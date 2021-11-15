package smartspace.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import smartspace.data.ActionEntity;

public class ActionBoundary {
	
	private Map<String, String> key;
	private String type;
	private Date created;
	private Map<String, String> element;
	private Map<String, String> player;
	private Map<String, Object> properties;
	
	public ActionBoundary() {
	}
	
	public ActionBoundary(ActionEntity actionEntity) {
		this.key = new HashMap<String, String>();
		this.key.put("id", actionEntity.getActionId());
		this.key.put("smartspace", actionEntity.getActionSmartspace());
		this.element = new HashMap<String, String>();
		this.element.put("id", actionEntity.getElementId());
		this.element.put("smartspace", actionEntity.getElementSmartspace());
		this.player = new HashMap<String, String>();
		this.player.put("email", actionEntity.getPlayerEmail());
		this.player.put("smartspace", actionEntity.getPlayerSmartspace());
		this.type = actionEntity.getActionType();
		this.created = actionEntity.getCreationTimestamp();
		this.properties = actionEntity.getMoreAttributes();
	}
	
	
	public Map<String, String> getKey() {
		return key;
	}

	public Map<String, String> getActionKey() {
		return key;
	}
	
	public void setKey(Map<String, String> actionKey) {
		this.key = actionKey;
	}

	public Map<String, String> getElement() {
		return element;
	}
	
	public void setElement(Map<String, String> element) {
		this.element = element;
	}
	
	public Map<String, String> getPlayer() {
		return player;
	}
	
	public void setPlayer(Map<String, String> player) {
		this.player = player;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public ActionEntity convertToEntity() {
		ActionEntity actionEntity = new ActionEntity();
		
		if(this.key != null) {
			actionEntity.setKey(this.key.get("id") + "#" + this.key.get("smartspace"));
		}

		actionEntity.setElementSmartspace("" + this.element.get("smartspace"));
		actionEntity.setElementId("" + this.element.get("id"));
		actionEntity.setPlayerSmartspace("" + this.player.get("smartspace"));
		actionEntity.setPlayerEmail("" + this.player.get("email"));
		actionEntity.setActionType(this.type);
		
		if (this.created != null) {
			actionEntity.setCreationTimestamp(this.created);
		}
		actionEntity.setMoreAttributes(this.properties);
		
		return actionEntity;
	}
	
	
	@Override
	public String toString() {
		return "ActionBoundary "
				+ "[key=" + key  
				+ ", element=" + element
				+ ", player=" + player
				+ ", actionType=" + type
				+ ", creationTimestamp=" + created 
				+ ", moreAttributes=" + properties + "]";
	}
	
	

}
