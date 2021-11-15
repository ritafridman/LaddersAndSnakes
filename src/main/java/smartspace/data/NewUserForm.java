package smartspace.data;

import smartspace.layout.BadUserParametersException;

public class NewUserForm {

	private String email;
	private String username;
	private String avatar;
	private String role;

	
	public NewUserForm(String email, String username, String avatar, String role) {
		super();
		this.email = email;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
	}
	
	@Override
	public String toString() {
		return "NewUserForm [email=" + email + ", username=" + username + ", avatar=" + avatar + ", role="
				+ role + "]";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public NewUserForm() {
		super();
	}
	
	public UserEntity convertToEntity() {
		
		UserEntity entity = new UserEntity();
		entity.setUsername(this.username);
		entity.setAvatar(this.avatar);
		entity.setUserEmail(this.email);
		
		try {
			entity.setRole(UserRole.valueOf(this.getRole()));
		}
		catch(IllegalArgumentException e) {
			throw new BadUserParametersException("Invalid role " + this.role);
		}
		
		return entity;
	}

}
