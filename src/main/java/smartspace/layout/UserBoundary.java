package smartspace.layout;

import java.util.HashMap;
import java.util.Map;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public class UserBoundary {
	
	private Map<String, String> key;
	private String username;
	private String avatar;
	private String role;
	private Long points;

	public UserBoundary() {
		
	}
	
	public UserBoundary(UserEntity entity) {
		this.key = new HashMap<String, String>();
		this.key.put("email", entity.getUserEmail());
		this.key.put("smartspace", entity.getUserSmartspace());
		this.username = entity.getUsername();
		this.role = entity.getRole().name();
		this.avatar = entity.getAvatar();
		this.points = entity.getPoints();
	}
	
	public Map<String, String> getKey() {
		return key;
	}

	public void setKey(Map<String, String> key) {
		this.key = key;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}
	
	public UserEntity convertToEntity() {

		UserEntity entity = new UserEntity();
		
		if (this.key != null) {
			entity.setKey(this.key.get("email") + "#" + this.key.get("smartspace"));
		}
		
		entity.setUsername(this.username);
		entity.setAvatar(this.avatar);
		
		if (this.role != null) {
			try {
				entity.setRole(UserRole.valueOf(this.role));
			}
			catch(IllegalArgumentException e) {
				throw new BadUserParametersException("Invalid role " + this.role);
			}
		}

		if (this.points != null) {
			entity.setPoints(this.points);
		}
		
		return entity;
	}
}
