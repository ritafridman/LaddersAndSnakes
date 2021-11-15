package smartspace.dao;

import java.util.List;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public interface EnhancedUserDao<UserKey> extends UserDao<UserKey> {
	
	public List<UserEntity> readAll (int size, int page);
	
	public List<UserEntity> readAll (int size, int page, String sortBy);
	
	public List<UserEntity> readByUserRole(UserRole role, int size, int page);
	
	public List<UserEntity> readByUsername(String username, int size, int page);
	
	public List<UserEntity> createBatch(List<UserEntity> userEntities);
}
