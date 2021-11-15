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

import smartspace.dao.ActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactory;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ActionDaoTests {
	private ActionDao actionDao;
	private EntityFactory factory;
	
	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@Before
	public void setup() {
		this.actionDao.deleteAll();
	}

	@After
	public void teardown() {
		this.actionDao.deleteAll();
	}

	@Test
	public void testCreateAction() throws Exception{
		// GIVEN the database is clean
		
		// WHEN I insert new action to the database
		ActionEntity original = 
			this.factory.createNewAction("theId", "smartspace", "someType", new Date(), "test@test.com", "thePlayer", null);
		this.actionDao.create(original);
		
		// THEN the database contains an action with the id "theId"
		List<ActionEntity> res = this.actionDao.readAll();
		assertThat(res.get(0).getPlayerEmail().equals(original.getPlayerEmail()));
	}
	
	@Test
	public void testDeleteAllActions() throws Exception{
		ActionEntity action = 
			this.factory.createNewAction("theId", "smartspace", "someType", new Date(), "thePlayer", "test@test.com", null);
		
		ActionEntity additionalAction = 
				this.factory.createNewAction("theId2", "smartspace", "someType", new Date(), "thePlayer", "test@test.com", null);
		
		this.actionDao.create(action);
		this.actionDao.create(additionalAction);
		
		assertThat(this.actionDao.readAll())
			.hasSize(2);

	}
	
	

}
