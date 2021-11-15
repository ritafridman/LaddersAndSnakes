package smartspace;

import smartspace.data.ElementEntity;
import smartspace.data.NewUserForm;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.ElementBoundary;
import smartspace.layout.UserBoundary;

public interface TestUtils {

	UserEntity generateValidUserEntity(UserRole role, String username);
	
	UserEntity generateValidUserEntity(UserRole role);
	
	UserBoundary generateValidUserBoundary(UserRole role);
	
	UserBoundary generateValidUserBoundary(UserRole role, String smartspace);
	
	UserBoundary generateValidUserBoundary(UserRole role, String smartspace, String username);
	
	UserBoundary generateValidUserBoundary(String username);
	
	ElementBoundary generateValidElementBoundary();
	
	ElementEntity generateValidElementEntity();
	
	ElementEntity generateValidElementEntityWithSpecifiedType(String type);

	public ElementEntity generateValidElementEntityWithSpecifiedName(String name);
	
	NewUserForm generateValidNewUserForm(UserRole role);

	ElementEntity generateValidElementEntityWithSpecifiedLocation(int x, int y);
}
