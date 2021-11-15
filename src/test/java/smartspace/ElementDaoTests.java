package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ElementDaoTests {
	private ElementDao<String> elementDao;
	private EntityFactory factory;
	
	@Autowired
	public void setElementDao(ElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@Before
	public void setup() {
		this.elementDao.deleteAll();
	}

	@After
	public void teardown() {
		this.elementDao.deleteAll();
	}
	
	@Test
	public void testCreateElement() throws Exception{
		// GIVEN the database is clean
		
		// WHEN I insert new user to the database
		ElementEntity original = 
			this.factory.createNewElement("test", "theType", new Location(), new Date(), "test@test.com", "smartSpace", false, null);
		this.elementDao.create(original);
		
		// THEN the database contains a element with the name "test"
		assertThat(this.elementDao.readAll())
			.usingElementComparatorOnFields("name")
			.contains(original);
	}
	
	@Test
	public void testUpdateElement() throws Exception{
		ElementEntity original = 
				this.factory.createNewElement("test", "theType", new Location(), new Date(), "test@test.com", "smartSpace", false, null);
		this.elementDao.create(original);
		
		assertThat(this.elementDao.readAll())
			.usingElementComparatorOnFields("name")
			.contains(original);
		
		original.setExpired(true);
		this.elementDao.update(original);
		

		List<ElementEntity> res = this.elementDao.readAll();
		assertThat(res.get(0).getCreatorEmail().equals(original.getCreatorEmail()));

	}
	
	

}
