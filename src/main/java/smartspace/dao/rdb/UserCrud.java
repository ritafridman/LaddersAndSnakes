package smartspace.dao.rdb;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public interface UserCrud extends PagingAndSortingRepository<UserEntity, String>{

	public List<UserEntity> findAllByRole(
			@Param("role") UserRole role, 
			Pageable pageable);

	public List<UserEntity> findAllByUsername(
			@Param("username") String username,
			Pageable pageable);
}
