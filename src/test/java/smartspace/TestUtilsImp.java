package smartspace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.NewUserForm;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.ElementBoundary;
import smartspace.layout.UserBoundary;

@Component
public class TestUtilsImp implements TestUtils {

	private String localSmartspaceName;
	
	public TestUtilsImp() {
		
	}
	
	@Value("${smartspace.name}")
	public void setLocalSmartspaceName(String localSmartspaceName) {
		this.localSmartspaceName = localSmartspaceName;
	}

	@Override
	public UserEntity generateValidUserEntity(UserRole role) {
		
		String username = "coolestPlayer";
		
		return generateValidUserEntity(role, username);
	}
	
	@Override
	public UserEntity generateValidUserEntity(UserRole role, String username) {
		
		String userEmail = "user@game.com";
		String userSmartspace = this.localSmartspaceName;
		String userAvatar = ":>";
		
		return new UserEntity(userEmail, userSmartspace, username, userAvatar, role, new Long(0));
	}
	
	@Override
	public UserBoundary generateValidUserBoundary(String username) {
		
		String userSmartspace = this.localSmartspaceName;
		UserRole role = UserRole.PLAYER;
		
		return this.generateValidUserBoundary(role, userSmartspace, username);
	}
	
	@Override
	public UserBoundary generateValidUserBoundary(UserRole role) {
		
		String userSmartspace = this.localSmartspaceName;
		String username = "coolestPlayer";
		
		return this.generateValidUserBoundary(role, userSmartspace, username);

	}

	@Override
	public UserBoundary generateValidUserBoundary(UserRole role, String smartspace) {
		
		String username = "coolestPlayer";
		
		return this.generateValidUserBoundary(role, smartspace, username);
	}
	
	@Override
	public UserBoundary generateValidUserBoundary(UserRole role, String smartspace, String username) {
		
		String userEmail = "user@game.com";
		String userAvatar = ":>";
		
		return new UserBoundary(new UserEntity(userEmail, smartspace, username, userAvatar, role, new Long(0)));
	}
	
	@Override
	public ElementBoundary generateValidElementBoundary() {
		
		ElementBoundary newElementBoundary = new ElementBoundary();
		newElementBoundary.setElementType("Tile");
		newElementBoundary.setName("whatever");
		Map<String, Double> location = new HashMap<>();
		location.put("lat", 12.0);
		location.put("lng", 13.0);
		newElementBoundary.setLatlng(location);
		Map<String, Object> elementProperties = new HashMap<>();
		elementProperties.put("key1", "someValue");
		newElementBoundary.setElementProperties(elementProperties);
		
		return newElementBoundary;
	}

	@Override
	public ElementEntity generateValidElementEntity() {
		
		ElementEntity newElementEntity = new ElementEntity();
		newElementEntity.setName("entitush");
		newElementEntity.setElementSmartspace(this.localSmartspaceName);
		newElementEntity.setType("Tile");
		newElementEntity.setLocation(new Location(10, 12));
		newElementEntity.setExpired(false);
		newElementEntity.setCreatorEmail("manager@game.com");
		newElementEntity.setCreatorSmartspace(this.localSmartspaceName);
		newElementEntity.setCreationTimestamp(new Date());
		
		Map<String, Object> moreAttribues = new HashMap<>();
		moreAttribues.put("key1", "whatever");
		newElementEntity.setMoreAttributes(moreAttribues);
		
		return newElementEntity;
	}

	@Override
	public NewUserForm generateValidNewUserForm(UserRole role) {
		
		String userEmail = "user@game.com";
		String username = "username";
		String userAvatar = ":D";

		return new NewUserForm(userEmail, username, userAvatar, role.name());
	}

	@Override
	public ElementEntity generateValidElementEntityWithSpecifiedType(String type) {
		ElementEntity newElementEntity = new ElementEntity();
		newElementEntity.setName("entitush");
		newElementEntity.setElementSmartspace(this.localSmartspaceName);
		newElementEntity.setType(type);
		newElementEntity.setLocation(new Location(10, 12));
		newElementEntity.setExpired(false);
		newElementEntity.setCreatorEmail("manager@game.com");
		newElementEntity.setCreatorSmartspace(this.localSmartspaceName);
		newElementEntity.setCreationTimestamp(new Date());
		
		Map<String, Object> moreAttribues = new HashMap<>();
		moreAttribues.put("key1", "whatever");
		newElementEntity.setMoreAttributes(moreAttribues);
		
		return newElementEntity;
	}
	
	@Override
	public ElementEntity generateValidElementEntityWithSpecifiedName(String name) {
		ElementEntity newElementEntity = new ElementEntity();
		newElementEntity.setName(name);
		newElementEntity.setElementSmartspace(this.localSmartspaceName);
		newElementEntity.setType("Tile");
		newElementEntity.setLocation(new Location(10, 12));
		newElementEntity.setExpired(false);
		newElementEntity.setCreatorEmail("manager@game.com");
		newElementEntity.setCreatorSmartspace(this.localSmartspaceName);
		newElementEntity.setCreationTimestamp(new Date());
		
		Map<String, Object> moreAttribues = new HashMap<>();
		moreAttribues.put("key1", "whatever");
		newElementEntity.setMoreAttributes(moreAttribues);
		
		return newElementEntity;
	}

	@Override
	public ElementEntity generateValidElementEntityWithSpecifiedLocation(int x, int y) {
		ElementEntity newElementEntity = new ElementEntity();
		newElementEntity.setName("entitush");
		newElementEntity.setElementSmartspace(this.localSmartspaceName);
		newElementEntity.setType("Tile");
		newElementEntity.setLocation(new Location(x , y));
		newElementEntity.setExpired(false);
		newElementEntity.setCreatorEmail("manager@game.com");
		newElementEntity.setCreatorSmartspace(this.localSmartspaceName);
		newElementEntity.setCreationTimestamp(new Date());
		
		Map<String, Object> moreAttribues = new HashMap<>();
		moreAttribues.put("key1", "whatever");
		newElementEntity.setMoreAttributes(moreAttribues);
		
		return newElementEntity;
	}

}
