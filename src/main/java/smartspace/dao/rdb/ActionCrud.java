package smartspace.dao.rdb;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import smartspace.data.ActionEntity;

public interface ActionCrud extends PagingAndSortingRepository<ActionEntity, String>{
	
	public List<ActionEntity> findAllByActionType(
			@Param("actionType") String type, 
			Pageable pageable);

	
}
