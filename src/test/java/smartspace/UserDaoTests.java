package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import smartspace.dao.UserDao;
import smartspace.data.NewUserForm;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;
import smartspace.layout.UserBoundary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.web.server.LocalServerPort;







import smartspace.logic.UserServiceImp;


@SpringBootTest
@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT);
public class UserDaoTests {
	private UserDao<String> userDao;
	private EntityFactory factory;
	private RestTemplate rest;
	private String url;
	private int port;
	
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	
	

	
	@PostConstruct
	public void init() {
		this.url = "http://localhost:" +port+ "/smartspace/users";
		rest = new RestTemplate();
	}
	
	@Autowired
	private UserServiceImp userServices;
	
	
	@Autowired
	public void setUserDao(UserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@Before
	public void setup() {
		this.userDao.deleteAll();
	}

	@After
	public void teardown() {
		this.userDao.deleteAll();
	}
	
	@Test
	public void testCreateUser() throws Exception{
		// GIVEN the database is clean
		
		// WHEN I insert new user to the database
		UserEntity original = 
			this.factory.createNewUser("test@test.com", "smartSpace", "test", null, null, 0);
		this.userDao.create(original);
		
		// THEN the database contains a user with the email "test@test.com"
		List<UserEntity> res = this.userDao.readAll();
		assertThat(res.get(0).getUserEmail().equals(original.getUserEmail()));
	}
	
	@Test
	public void testUpdateUser() throws Exception{
		UserEntity original = 
				this.factory.createNewUser("test@test.com", "smartSpace", "test", null, null, 0);
		this.userDao.create(original);
		
		assertThat(this.userDao.readAll())
			.usingElementComparatorOnFields("username")
			.contains(original);
		
		original.setUsername("test2");
		this.userDao.update(original);
		
		assertThat(this.userDao.readAll())
		.usingElementComparatorOnFields("username")
		.contains(original);

	}
	
	

}
