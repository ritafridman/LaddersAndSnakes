package smartspace.dao;

import java.util.List;

import smartspace.data.ActionEntity;

public interface EnhancedActionDao<K> extends ActionDao {
	
	public List<ActionEntity> readAll (int size, int page);
	
	public List<ActionEntity> readAll (int size, int page, String sortBy);
	
	public List<ActionEntity> readByType(
			String type, 
			int size, int page);
	
	public List<ActionEntity> createBatch(List<ActionEntity> actionEntities);
}
