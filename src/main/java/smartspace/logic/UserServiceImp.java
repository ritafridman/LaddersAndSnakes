package smartspace.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.BadImportRequestException;
import smartspace.layout.BadUserParametersException;
import smartspace.layout.UserNotFoundException;

@Service
public class UserServiceImp implements UserService {
	private EnhancedUserDao<String> userDao;

	@Value("${smartspace.name}")
	private String localSmartspaceName;

	@Autowired
	public UserServiceImp(EnhancedUserDao<String> userDao) {
		this.userDao = userDao;
	}

	@Override
	public List<UserEntity> getAll(int size, int page) {
		return this.userDao.readAll(size, page);
	}

	@Override
	@Transactional
	public UserEntity create(UserEntity user) {
		
		user.setUserSmartspace(this.localSmartspaceName);
		
		if (!validate(user, true)) {
			throw new BadUserParametersException("Bad element parameters.");
		} 

		return this.userDao.create(user);
	}

	@Override
	@Transactional
	public List<UserEntity> importUsers(List<UserEntity> users) {

		users.forEach(user -> {
			if (!validateImportedUser(user)) {
				throw new BadImportRequestException("Invalid user input or bad smartspace.");
			}
		});

		return this.userDao.createBatch(users);
	}

	private boolean validateImportedUser(UserEntity user) {
		
		
		return user.getUserEmail() != null &&
				!this.userDao.readById(user.getKey()).isPresent() &&
				user.getUserSmartspace() != null &&
				!user.getUserSmartspace().equals(localSmartspaceName) &&
				user.getRole() != null &&
				user.getAvatar() != null &&
				user.getUsername() != null &&
				this.userDao.readByUsername(user.getUsername(), 1, 0).size() == 0 &&
				user.getPoints() != null;
	}
	
	public boolean validate(UserEntity user, boolean isNew) {
		
		if (isNew) {
			return user.getUsername() != null && 
					this.userDao.readByUsername(user.getUsername(), 1, 0).size() == 0 &&
					user.getUserEmail() != null && 
					!this.userDao.readById(user.getUserEmail() + "#" + this.localSmartspaceName).isPresent() &&
					user.getAvatar() != null && 
					user.getRole() != null;
		}
		//	Else, if its an update
		
		//	Updating user points through API is forbidden.
		if (user.getPoints() != null || (user.getUsername() != null && 
				this.userDao.readByUsername(user.getUsername(), 1, 0).size() > 0)) {
			return false;
		}
		
		//	TODO: add more field validations, if needed.
		
		return true;
	}

	public boolean validateUserAdminAndSmartspace(String adminSmartspace, String adminEmail) {

		boolean isAdmin;
		boolean sameSmartspace;

		UserEntity admin = this.userDao.readById(adminEmail + "#" + adminSmartspace).get();
		isAdmin = admin.getRole() == UserRole.ADMIN;
		sameSmartspace = adminSmartspace.equals(localSmartspaceName);

		if (!isAdmin || !sameSmartspace) {
			return false;
		}

		return true;
	}

	
	public boolean validateUserPlayerAndSmartspace(String adminSmartspace, String adminEmail) {

		boolean isAdmin;
		boolean sameSmartspace;

		UserEntity admin = this.userDao.readById(adminEmail + "#" + adminSmartspace).get();
		isAdmin = admin.getRole() == UserRole.ADMIN;
		sameSmartspace = adminSmartspace.equals(localSmartspaceName);

		if (!isAdmin || !sameSmartspace) {
			return false;
		}

		return true;
	}
	@Override
	public List<UserEntity> getByRole(UserRole role, int size, int page) {
		return this.userDao.readByUserRole(role, size, page);
	}

	@Override
	public UserEntity getByKey(String key) {
		System.err.println(" UserEntity getByKey(String key)");
		UserEntity user = this.userDao.readById(key)
				.orElseThrow(() -> new UserNotFoundException("No user with key: " + key));

		return user;
	}

	@Override
	public List<UserEntity> getAllSorted(int size, int page, String sortBy) {
		return this.userDao
				.readAll(size, page, sortBy);
	}
	
	@Transactional
	public void update(UserEntity user, String userSmartspace, String userEmail) {
		
		if (!validate(user, false)) {
			throw new BadUserParametersException("Bad user parameters");
		}
		
		user.setKey(userEmail + "#" + userSmartspace);
		
		this.userDao.update(user);
	}

}
