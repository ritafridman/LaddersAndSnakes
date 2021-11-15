package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.UserNotFoundException;

@Repository
public class RdbUserDao implements EnhancedUserDao<String>{
	private UserCrud userCrud;

	@Autowired
	public RdbUserDao(UserCrud userCrud) {
		this.userCrud = userCrud;
	}
	
	@Override
	@Transactional
	public UserEntity create(UserEntity userEntity) {
		userEntity.setPoints(new Long(0));
		return this.userCrud.save(userEntity);
	}
	
	@Override
	@Transactional
	public List<UserEntity> createBatch(List<UserEntity> userEntities) {
		
		List<UserEntity> users = userEntities
				.stream()
				.map(userEntity-> {
					userEntity.setKey(userEntity.getUserEmail() + "#" + userEntity.getUserSmartspace());
					return userEntity;
				})
				.collect(Collectors.toList());
		
		//	Convert the iterable returned from saveAll to a stream, then to list.
		return StreamSupport.stream(userCrud.saveAll(users).spliterator(), false)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Optional<UserEntity> readById(String key) {
		return this.userCrud.findById(key);
		
	}

	@Override
	@Transactional
	public List<UserEntity> readAll() {
		// SELECT * FROM USERS 
		List<UserEntity> rv = new ArrayList<>();
		this.userCrud
		.findAll()
		.forEach(user->rv.add(user));
		return rv;
	}

	@Override
	@Transactional
	public void update(UserEntity update) {
		
		UserEntity existing = 
				this.readById(update.getKey())
				  .orElseThrow(()->new UserNotFoundException("no user with id: " + update.getKey()));

		// Patching
		if (update.getAvatar() != null) {
			existing.setAvatar(update.getAvatar());
		}

		if (update.getPoints() != null) {
			existing.setPoints(update.getPoints());
		}

		if (update.getRole() != null) {
			existing.setRole(update.getRole());
		}

		if (update.getUsername() != null) {
			existing.setUsername(update.getUsername());
		}
		
		this.userCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.userCrud.deleteAll();
	}

	@Override
	@Transactional(readOnly=true)
	public List<UserEntity> readAll(int size, int page) {
		return this.userCrud
				.findAll(PageRequest.of(page, size))
				.getContent();
	}

	@Override
	public List<UserEntity> readAll(int size, int page, String sortBy) {
		return this.userCrud
				.findAll(PageRequest.of(page, size, Direction.ASC, sortBy))
				.getContent();
	}

	@Override
	@Transactional
	public List<UserEntity> readByUserRole(UserRole role, int size, int page) {
		return this.userCrud.findAllByRole(role, PageRequest.of(page, size));
	}

	@Override
	public List<UserEntity> readByUsername(String username, int size, int page) {
		return this.userCrud.findAllByUsername(username, PageRequest.of(page, size));
	}

}
