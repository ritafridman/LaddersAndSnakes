package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserRole;
import smartspace.layout.ElementNotFoundException;

@Repository
public class RdbElementDao implements EnhancedElementDao<String>{
	private ElementCrud elementCrud;
	private IdGeneratorCrud idGeneratorCrud;

	@Autowired
	public RdbElementDao(ElementCrud entityCrud , IdGeneratorCrud idGeneratorCrud) {
		this.elementCrud = entityCrud;
		this.idGeneratorCrud = idGeneratorCrud;
	}

	@Override
	@Transactional
	public ElementEntity create(ElementEntity elementEntity) {	
		IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
		elementEntity.setKey(""+nextId.getNextId() + "#" + elementEntity.getElementSmartspace());
		this.idGeneratorCrud.delete(nextId);
		return this.elementCrud.save(elementEntity);
	}
	
	@Override
	@Transactional
	public List<ElementEntity> createBatch(List<ElementEntity> elementEntities) {

		List<ElementEntity> elements = elementEntities
				.stream()
				.map(elementEntity-> {
					// TODO: When importing entities you must store them in your database AS IS without modifying their keys or timestamp
					IdGenerator nextId = this.idGeneratorCrud.save(new IdGenerator());
					elementEntity.setKey(nextId.getNextId() + "#" + elementEntity.getElementSmartspace());
					this.idGeneratorCrud.delete(nextId);
					return elementEntity;
				})
				.collect(Collectors.toList());
		
		//	Convert the iterable returned from saveAll to a stream, then to list.
		return StreamSupport.stream(elementCrud.saveAll(elements).spliterator(), false)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Optional<ElementEntity> readById(String elementKey) {
		return this.elementCrud.findById(elementKey);
	}

	@Override
	@Transactional
	public List<ElementEntity> readAll() {
		// SELECT * FROM ELEMENTS 
		List<ElementEntity> rv = new ArrayList<>();
		this.elementCrud
		.findAll()
		.forEach(element->rv.add(element));
		return rv;
	}

	@Override
	@Transactional
	public void update(ElementEntity update) {
		ElementEntity existing = 
				this.readById(update.getKey())
				  .orElseThrow(()->new ElementNotFoundException("No element with id: " + update.getKey()));

		// Patching
		if (update.getLocation() != null) {
			existing.setLocation(update.getLocation());
		}
		
		if (update.getMoreAttributes() != null) {
			existing.setMoreAttributes(update.getMoreAttributes());
		}
		
		if (update.getName() != null) {
			existing.setName(update.getName());
		}
		
		if (update.getType() != null) {
			existing.setType(update.getType());
		}
		
		if (update.getExpired() == (false || true)) {
			existing.setExpired(update.getExpired());
		}

		this.elementCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteByKey(String elementKey) {
		this.elementCrud.deleteById(elementKey);
	}

	@Override
	@Transactional
	public void delete(ElementEntity elementEntity) {
		this.elementCrud.delete(elementEntity);

	}

	@Override
	@Transactional
	public void deleteAll() {
		this.elementCrud.deleteAll();

	}
	
	@Override
	public List<ElementEntity> readAll(int size, int page) {
		return this.elementCrud
		.findAll(PageRequest.of(page, size))
		.getContent();
	}

	@Override
	public List<ElementEntity> readAll(int size, int page, String sortBy) {
 		return this.elementCrud
		.findAll(PageRequest.of(page, size, Direction.ASC, sortBy))
		.getContent();
	}

	@Override
	public List<ElementEntity> readByType(String type, int size, int page) {
		return this.elementCrud.findAllByType(type, PageRequest.of(page, size));
	}
	
	@Override
	public List<ElementEntity> readByName(String name, int size, int page) {
		return this.elementCrud.findAllByName(name, PageRequest.of(page, size));
	}

	@Override
	public ElementEntity readByKeyAndRole(String key, UserRole role, int size, int page) {

		ElementEntity element = this.elementCrud.findById(key)
				.orElseThrow(()->new ElementNotFoundException("No such element with key: " + key));
		
		//	If expired == true and role is player, don't return the element.
		if (element.getExpired() && role == UserRole.PLAYER) {
			throw new ElementNotFoundException("No such element with key: " + key);
		}
		
		return element;
	}

	@Override
	public List<ElementEntity> readByKey(String key, int size, int page) {
		return this.elementCrud.findAllByKey(key , PageRequest.of(page, size));
	}
	
	@Override
	public List<ElementEntity> readByLocation(Location location, double distance,int size, int page) {
	    return this.elementCrud.getByLocation(location.getLat(), location.getLng(), distance ,PageRequest.of(page, size));
	}
	
	
	

}
