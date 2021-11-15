package smartspace;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import smartspace.dao.EnhancedUserDao;
import smartspace.data.NewUserForm;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.UserBoundary;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties={"spring.profiles.active=default"})
public class UserRestControllerTests {

	private int port;
	private String baseUrl;
	private TestUtils testUtils;
	private RestTemplate restTemplate;
	private String localSmartspaceName;
	private EnhancedUserDao<String> userDao;
	
	@Autowired
	public void setTestUtils(TestUtils testUtils) {
		this.testUtils = testUtils;
	}

	@Autowired
	public void setUserDao(EnhancedUserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@Value("${smartspace.name}")
	public void setLocalSmartspaceName(String localSmartspaceName) {
		this.localSmartspaceName = localSmartspaceName;
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port + "/smartspace";
		this.restTemplate = new RestTemplate();
	}

	@After
	public void teardown() {
		this.userDao.deleteAll();
	}

	@Test
	public void testCreateNewValidUser() {
		//	GIVEN there is no user in the database with same email
		
		//	WHEN a valid new user form is posted to the server
		NewUserForm newUserFrom = this.testUtils.generateValidNewUserForm(UserRole.PLAYER);
		
		String requestUrl = this.baseUrl + "/users";
		
		ModelAndView actualResult = this.restTemplate
				.postForObject(requestUrl, newUserFrom , ModelAndView.class);
		UserEntity form = newUserFrom.convertToEntity();
		
		String userKey = form.getKey();
		
		//	THEN  the database contains the new element
		assertThat(this.userDao.readById(userKey))
				.isPresent()
				.get()
				.extracting("username", "userEmail", "avatar", "role")
				.containsExactly(form.getUsername(), form.getUserEmail(),
						form.getAvatar(), form.getRole());
	}
	
	@Test(expected=Exception.class)
	public void testCreateNewInvalidUser() {
		//	GIVEN there is no user in the database with same email
		
		//	WHEN an invalid new user form is posted to the server
		NewUserForm newUserForm = this.testUtils.generateValidNewUserForm(UserRole.PLAYER);
		newUserForm.setRole("WHATEVER");
		
		String requestUrl = this.baseUrl + "/users";
		
		this.restTemplate.postForObject(requestUrl, newUserForm, UserBoundary.class);
		
		//	THEN an exception is thrown.
	}
	
	@Test(expected=Exception.class)
	public void testCreateNewValidUserExistingUserSameEmail() {
		//	GIVEN there is a user in the database
		UserEntity user = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		//	WHEN a valid new user form with the same email is posted to the server
		NewUserForm newUserForm = this.testUtils.generateValidNewUserForm(UserRole.PLAYER);
		newUserForm.setEmail(user.getUserEmail());
		
		String requestUrl = this.baseUrl + "/users";
		
		this.restTemplate.postForObject(requestUrl, newUserForm, UserBoundary.class);
		
		//	THEN an exception is thrown.
	}
	
	@Test(expected=Exception.class)
	public void testCreateNewValidUserExistingUserSameUsername() {
		//	GIVEN there is a user in the database
		UserEntity user = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		//	WHEN a valid new user form with the same username is posted to the server
		NewUserForm newUserForm = this.testUtils.generateValidNewUserForm(UserRole.PLAYER);
		newUserForm.setUsername(user.getUsername());
		
		String requestUrl = this.baseUrl + "/users";
		
		this.restTemplate.postForObject(requestUrl, newUserForm, UserBoundary.class);
		
		//	THEN an exception is thrown.
	}

	@Test
	public void testLoginExistingUser() {
		//	GIVEN there is an existing user in the database
		UserEntity user = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		//	WHEN the same user attempts to login
		String requestUrl = this.baseUrl + "/users/login/" + this.localSmartspaceName + "/" + user.getUserEmail();
		
		UserBoundary actualResult = this.restTemplate.getForObject(requestUrl, UserBoundary.class);
		
		//	THEN the server returns the user details
		assertThat(user)
			.extracting("username", "userEmail", "avatar", "role")
			.containsExactly(actualResult.getUsername(), actualResult.getKey().get("email"),
					actualResult.getAvatar(), UserRole.valueOf(actualResult.getRole()));
	}
	
	@Test(expected=Exception.class)
	public void testLoginNonExistingUser() {
		//	GIVEN nothing
		
		//	WHEN a non existing user attempts to login
		String requestUrl = this.baseUrl + "/users/login/SomeSmartspace/SomeUserEmail";
		
		this.restTemplate.getForObject(requestUrl, UserBoundary.class);
		
		//	THEN an exception is thrown
	}
	
	@Test
	public void testUpdateExistingUserWithValidParameters() {
		//	GIVEN there is a user in the database
		UserEntity user = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		//	WHEN the same user attempts to update with valid parameters
		UserBoundary update = new UserBoundary(user);
		update.setPoints(null);
		update.setUsername("newUsername");
		update.setRole(UserRole.ADMIN.name());
		update.setAvatar("whatever");
		
		String requestUrl = this.baseUrl + "/users/login/" + this.localSmartspaceName + "/" + user.getUserEmail();

		this.restTemplate.put(requestUrl, update);
		
		//	THEN the user in database is updated
		String userKey = user.getUserEmail() + "#" + user.getUserSmartspace();

		assertThat(this.userDao.readById(userKey))
			.isPresent()
			.get()
			.extracting("username", "role", "avatar")
			.containsExactly(update.getUsername(), UserRole.valueOf(update.getRole()), update.getAvatar());
	}
	
	@Test(expected=Exception.class)
	public void testUpdateExistingUserWithInvalidRole() {
		//	GIVEN there is a user in the database
		UserEntity user = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		//	WHEN the same user attempts to update with an invalid role
		UserBoundary update = new UserBoundary(user);
		update.setPoints(null);
		update.setRole("invalid");
		
		String requestUrl = this.baseUrl + "/users/login/" + this.localSmartspaceName + "/" + user.getUserEmail();

		this.restTemplate.put(requestUrl, update);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testUpdateExistingUserWithPoints() {
		//	GIVEN there is a user in the database
		UserEntity user = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		//	WHEN the same user attempts to update his points
		UserBoundary update = new UserBoundary(user);
		update.setPoints(new Long(10));

		String requestUrl = this.baseUrl + "/users/login/" + this.localSmartspaceName + "/" + user.getUserEmail();

		this.restTemplate.put(requestUrl, update);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testUpdateExistingUserWithInvalidUsername() {
		//	GIVEN there is more than one user in the database
		UserEntity user1 = this.userDao.create(this.testUtils.generateValidUserEntity(UserRole.PLAYER));
		
		UserEntity user2 = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		user2.setUsername("user2");
		user2.setUserEmail("user2@game.com");
		user2 = this.userDao.create(user2);
		
		//	WHEN a user attempts to update his username to a username that is already taken
		UserBoundary update = new UserBoundary(user1);
		update.setUsername(user2.getUsername());
		update.setPoints(null);

		String requestUrl = this.baseUrl + "/users/login/" + this.localSmartspaceName + "/" + user1.getUserEmail();

		this.restTemplate.put(requestUrl, update);
		
		//	THEN an exception is thrown
	}

	@Test
	public void testImportValidUsersByExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN valid users from another smartspace are being imported
		String otherSmartspace = "SomeOtherSmartspace";
		
		//	Create user boundaries with different usernames
		List<UserBoundary> usersToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserBoundary(UserRole.PLAYER, otherSmartspace, "" + i))
			.collect(Collectors.toList());
		
		UserBoundary[] usersToImportArr = usersToImport.toArray(new UserBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/users/" + admin.getUserSmartspace() + "/" + admin.getUserEmail();
		
		UserBoundary[] actualResult = this.restTemplate
				.postForObject(requestUrl, usersToImportArr, UserBoundary[].class);
		
		//	THEN the database contains the new imported users
		IntStream.range(0, actualResult.length)
			.forEach(i->{
				assertThat(this.userDao.readById(actualResult[i].convertToEntity().getKey()))
					.isPresent()
					.get()
					.extracting("username", "avatar", "role")
					.containsExactly(actualResult[i].getUsername(), actualResult[i].getAvatar(), 
							UserRole.valueOf(actualResult[i].getRole()));
			});
	}
	
	@Test(expected=Exception.class)
	public void testImportInvalidUsersByExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN invalid users from another smartspace are being imported
		String otherSmartspace = "SomeOtherSmartspace";
		
		//	Create user boundaries with same usernames
		List<UserBoundary> usersToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserBoundary(UserRole.PLAYER, otherSmartspace))
			.collect(Collectors.toList());
		
		UserBoundary[] usersToImportArr = usersToImport.toArray(new UserBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/users/" + admin.getUserSmartspace() + "/" + admin.getUserEmail();
		
		this.restTemplate
				.postForObject(requestUrl, usersToImportArr, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testImportValidUsersWithLocalSmartspaceByExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN valid users from local smartspace are being imported

		//	Create user boundaries with different usernames, but with local smartspace
		List<UserBoundary> usersToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserBoundary("" + i))
			.collect(Collectors.toList());
		
		UserBoundary[] usersToImportArr = usersToImport.toArray(new UserBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/users/" + admin.getUserSmartspace() + "/" + admin.getUserEmail();
		
		this.restTemplate
				.postForObject(requestUrl, usersToImportArr, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testImportValidUsersByExistingPlayer() throws Exception {
		//	GIVEN that the requesting user is a local player, and he exists in the database
		UserEntity player = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		this.userDao.create(player);
		
		//	WHEN valid users from another smartspace are being imported
		String otherSmartspace = "SomeOtherSmartspace";
		
		//	Create user boundaries with different usernames
		List<UserBoundary> usersToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserBoundary(UserRole.PLAYER, otherSmartspace, "" + i))
			.collect(Collectors.toList());
		
		UserBoundary[] usersToImportArr = usersToImport.toArray(new UserBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/users/" + player.getUserSmartspace() + "/" + player.getUserEmail();
		
		this.restTemplate
				.postForObject(requestUrl, usersToImportArr, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testImportValidUsersByExistingManager() throws Exception {
		//	GIVEN that the requesting user is a local manager, and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN valid users from another smartspace are being imported
		String otherSmartspace = "SomeOtherSmartspace";
		
		//	Create user boundaries with different usernames
		List<UserBoundary> usersToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserBoundary(UserRole.PLAYER, otherSmartspace, "" + i))
			.collect(Collectors.toList());
		
		UserBoundary[] usersToImportArr = usersToImport.toArray(new UserBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/users/" + manager.getUserSmartspace() + "/" + manager.getUserEmail();
		
		this.restTemplate
				.postForObject(requestUrl, usersToImportArr, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testImportValidUsersByNonExistingAdmin() throws Exception {
		//	GIVEN that the requesting user doesn't exist in the database
		
		//	WHEN valid users from another smartspace are being imported
		String otherSmartspace = "SomeOtherSmartspace";
		
		//	Create user boundaries with different usernames
		List<UserBoundary> usersToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserBoundary(UserRole.PLAYER, otherSmartspace, "" + i))
			.collect(Collectors.toList());
		
		UserBoundary[] usersToImportArr = usersToImport.toArray(new UserBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/users/" + this.localSmartspaceName + "/NonExistingAdminEmail";
		
		this.restTemplate
				.postForObject(requestUrl, usersToImportArr, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test
	public void testExportValidUsersByExistingAdmin() {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN the admin requests to export existing users
		
		//	Create user entities with different usernames
		IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserEntity(UserRole.PLAYER, "" + i))
			.peek(this.userDao::create);
		
		String requestUrl = this.baseUrl + "/admin/users/" + admin.getUserSmartspace() + "/" + 
				admin.getUserEmail() + "?page=0&size=3";
		
		UserBoundary[] actualResult = this.restTemplate
				.getForObject(requestUrl, UserBoundary[].class);
		
		//	THEN the server returns the requested users
		IntStream.range(0, actualResult.length)
		.forEach(i-> {
			assertThat(this.userDao.readById(actualResult[i].convertToEntity().getKey()))
				.isPresent()
				.get()
				.extracting("username", "avatar", "role")
				.containsExactly(actualResult[i].getUsername(), actualResult[i].getAvatar(), 
						UserRole.valueOf(actualResult[i].getRole()));
		});
	}
	
	@Test(expected=Exception.class)
	public void testExportValidUsersByExistingPlayer() {
		//	GIVEN that the requesting user is a local player, and he exists in the database
		UserEntity player = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		this.userDao.create(player);
		
		//	WHEN the player requests to export existing users
		
		//	Create user entities with different usernames
		IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserEntity(UserRole.PLAYER, "" + i))
			.peek(this.userDao::create);
	
		String requestUrl = this.baseUrl + "/admin/users/" + player.getUserSmartspace() + "/" + player.getUserEmail();
	
		this.restTemplate.getForObject(requestUrl, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testExportValidUsersByExistingManager() {
		//	GIVEN that the requesting user is a local manager, and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN the manager requests to export existing users
		
		//	Create user entities with different usernames
		IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserEntity(UserRole.PLAYER, "" + i))
			.peek(this.userDao::create);
	
		String requestUrl = this.baseUrl + "/admin/users/" + manager.getUserSmartspace() + "/" + manager.getUserEmail();
	
		this.restTemplate.getForObject(requestUrl, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testExportValidUsersByNonExistingAdmin() {
		//	GIVEN that the requesting user doesn't exist in the database

		//	WHEN the user requests to export existing users
		
		//	Create user entities with different usernames
		IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidUserEntity(UserRole.PLAYER, "" + i))
			.peek(this.userDao::create);
	
		String requestUrl = this.baseUrl + "/admin/users/" + this.localSmartspaceName + "/SomeEmail";
	
		this.restTemplate.getForObject(requestUrl, UserBoundary[].class);
		
		//	THEN an exception is thrown
	}
}
