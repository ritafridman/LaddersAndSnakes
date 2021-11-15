package smartspace.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "USERS")
public class UserEntity implements SmartspaceEntity<String> {

	private String userSmartspace;
	private String userEmail;
	private String username;
	private String avatar;
	private UserRole role;
	private Long points;
	
	public UserEntity() {

	}
	
	public UserEntity(
			String userEmail, 
			String userSmartspace, 
			String username, 
			String avatar,
			UserRole role, 
			Long points) {
		super();
		setUserEmail(userEmail);
		setUserSmartspace(userSmartspace);
		setUsername(username);
		setAvatar(avatar);
		setRole(role);
		setPoints(points); 
	}

	@Transient
	public String getUserSmartspace() {
		return userSmartspace;
	}

	@Transient
	public void setUserSmartspace(String userSmartspace) {
		this.userSmartspace = userSmartspace;
	}

	@Transient
	public String getUserEmail() {
		return userEmail;
	}

	@Transient
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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

	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}

	
	public void setRole(UserRole role) {
		this.role = role;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}
	
	@Override
	public String toString() {
		return 	"UserEntity [SmartSpace - " + this.userSmartspace 
				+ ", avatar - " + this.avatar 
				+ ", username - " + this.username 
				+ ", UserRole - " + (this.role == null ? "" : this.role.toString()) 
				+ ", points - " + this.points 
				+ ", userEmail - " + this.userEmail 
				+ "]";		
	}

	@Column(name = "ID")
	@Id
	@Override
	public String getKey() {
		return this.userEmail + "#" + this.userSmartspace;
	}

	@Override
	public void setKey(String key) {
		String[] tmpArray = key.split("#");
		this.userEmail=tmpArray[0];
		this.userSmartspace=tmpArray[1];	
	}
}
