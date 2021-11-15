
package smartspace.logic;

import java.util.List;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public interface UserService {
	public List<UserEntity> getAll(int size, int page);

	public UserEntity create(UserEntity user);

	public List<UserEntity> importUsers(List<UserEntity> users);

	public List<UserEntity> getByRole(UserRole role, int size, int page);

	public UserEntity getByKey(String key);

	public boolean validateUserAdminAndSmartspace(String adminSmartspace, String adminEmail);
	
	public boolean validate(UserEntity user, boolean isNew);
	
	public List<UserEntity> getAllSorted(int size, int page, String sortBy);
	
	public void update(UserEntity user, String userSmartspace, String userEmail);
}
