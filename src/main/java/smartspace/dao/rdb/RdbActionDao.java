package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort.Direction;

import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;

@Repository
public class RdbActionDao implements EnhancedActionDao<String>{
	private ActionCrud actionCrud;
	//private AtomicLong nextActionId; // Changed to row below
	private IdGeneratorCrud idGeneratorCrud;
	
	@Autowired
	public RdbActionDao(ActionCrud actionCrud, IdGeneratorCrud idGeneratorCrud) {
		this.actionCrud = actionCrud;
		//this.nextActionId = new AtomicLong(1L); // Changed to row below
		this.idGeneratorCrud = idGeneratorCrud;
	}

	@Override
	@Transactional
	public ActionEntity create(ActionEntity actionEntity) {
		//actionEntity.setKey(""+ nextActionId.getAndIncrement()); // Changed to row below
		IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
		actionEntity.setKey(""+nextId.getNextId() + "#" + actionEntity.getActionSmartspace());
		this.idGeneratorCrud.delete(nextId);
		return this.actionCrud.save(actionEntity);
	}
	
	@Override
	@Transactional
	public List<ActionEntity> createBatch(List<ActionEntity> actionEntities) {
		
		List<ActionEntity> actions = actionEntities
				.stream()
				.map(actionEntity-> {
					IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
					actionEntity.setKey(nextId.getNextId() + "#" + actionEntity.getActionSmartspace());
					this.idGeneratorCrud.delete(nextId);
					return actionEntity;
				})
				.collect(Collectors.toList());
		
		//	Convert the iterable returned from saveAll to a stream, then to list.
		return StreamSupport.stream(actionCrud.saveAll(actions).spliterator(), false)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<ActionEntity> readAll() {
		// SELECT * FROM ACTIONS 
		List<ActionEntity> rv = new ArrayList<>();
		this.actionCrud
		.findAll()
		.forEach(action->rv.add(action));
		return rv;
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.actionCrud.deleteAll();
	}

	@Override
	public List<ActionEntity> readAll(int size, int page) {
		return this.actionCrud
		.findAll(PageRequest.of(page, size))
		.getContent();
	}

	@Override
	public List<ActionEntity> readAll(int size, int page, String sortBy) {
 		return this.actionCrud
		.findAll(PageRequest.of(page, size, Direction.ASC, sortBy))
		.getContent();
	}

	@Override
	public List<ActionEntity> readByType(String type, int size, int page) {
		return this.actionCrud.findAllByActionType(type, PageRequest.of(page, size));
	}

}
