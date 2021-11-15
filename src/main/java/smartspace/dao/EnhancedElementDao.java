package smartspace.dao;

import java.util.List;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserRole;

public interface EnhancedElementDao<K> extends ElementDao<K> {
	
	public List<ElementEntity> readAll (int size, int page);
	
	public List<ElementEntity> readAll (int size, int page, String sortBy);
	
	public List<ElementEntity> readByType(
			String type, 
			int size, int page);
	
	public List<ElementEntity> readByName(
			String name, 
			int size, int page);
	
	public List<ElementEntity> createBatch(List<ElementEntity> elementEntities);
	
	public ElementEntity readByKeyAndRole(String key, UserRole role, int size, int page);

	public List<ElementEntity> readByKey(String creatorSmartspace, int size, int page);
	
	public List<ElementEntity> readByLocation(Location location, double distnace ,int size, int page);

}
