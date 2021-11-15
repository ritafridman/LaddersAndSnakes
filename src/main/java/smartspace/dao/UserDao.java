package smartspace.dao;

import java.util.List;
import java.util.Optional;
import smartspace.data.UserEntity;

public interface UserDao<UserKey> {
	// INSERT - Create
	UserEntity create (UserEntity userEntity);
	// SELECT - Read
	Optional<UserEntity> readById(UserKey userKey);
	// SELECT - Read
	List <UserEntity> readAll();
	// UPDATE - Update
	void update(UserEntity userEntity);
	// DELETE - Delete
	void deleteAll();
	
	
}
