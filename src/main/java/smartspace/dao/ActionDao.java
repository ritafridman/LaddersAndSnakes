package smartspace.dao;

import java.util.List;
import smartspace.data.ActionEntity;

public interface ActionDao {
	// INSERT - Create
	ActionEntity create(ActionEntity actionEntity);
	
	// SELECT - Read
	List<ActionEntity> readAll();
	
	// DELETE - Delete
	void deleteAll();
}
