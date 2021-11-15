package smartspace.dao.rdb;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import smartspace.data.ElementEntity;

public interface ElementCrud extends PagingAndSortingRepository<ElementEntity, String>{

	public List<ElementEntity> findAllByType(
			@Param("type") String type, 
			Pageable pageable);

	public List<ElementEntity> findAllByName(
			@Param("name") String name, 
			Pageable pageable);

	public List<ElementEntity> findAllByKey(
			@Param("key")String key,
			Pageable pageable);
	
	@Query(value = "SELECT * FROM elements WHERE elements.lat < (?#{#lat}+?#{#distance}) AND "
			+ "elements.lat > (?#{#lat}-?#{#distance}) AND elements.lng < (?#{#lng}+?#{#distance}) AND "
			+ "elements.lng > (?#{#lng}-?#{#distance}) ", nativeQuery = true)
	public List<ElementEntity> getByLocation(
			@Param("lat") double lat, 
			@Param("lng") double lng,
			@Param("distance") double distance,
			Pageable pageable);
}
