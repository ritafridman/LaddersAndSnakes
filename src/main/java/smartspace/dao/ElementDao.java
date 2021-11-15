package smartspace.dao;
import java.util.List;
import java.util.Optional;

import smartspace.data.ElementEntity;

public interface ElementDao<K> {
	// INSERT - Create
	ElementEntity create(ElementEntity elementEntity);
	// SELECT - Read
	Optional<ElementEntity> readById(K elementKey);
	// SELECT - Read
	List<ElementEntity> readAll();
	// UPDATE - Update
	void update(ElementEntity elementEntity);
	// DELETE - Delete
	void deleteByKey(K elementKey);
	// DELETE - Delete
	void delete(ElementEntity elementEntity);
	// DELETE - Delete
	void deleteAll();

}
