package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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

import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.ElementBoundary;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties={"spring.profiles.active=default"})
public class ElementRestControllerTests {
	
	private String localSmartspaceName;
	private String baseUrl;
	private int port;
	private TestUtils testUtils;
	private EnhancedElementDao<String> elementDao;
	private EnhancedUserDao<String> userDao;
	private ElementService elementService;
	private UserService userService;
	private RestTemplate restTemplate;
	
	@Autowired
	public void setTestUtils(TestUtils testUtils) {
		this.testUtils = testUtils;
	}
	
	@Autowired
	public void setElementDao(EnhancedElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}
	
	@Autowired
	public void setUserDao(EnhancedUserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public String getLocalSmartspaceName() {
		return localSmartspaceName;
	}

	@Value("${smartspace.name}")
	public void setLocalSmartspaceName(String localSmartspaceName) {
		this.localSmartspaceName = localSmartspaceName;
	}

	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port + "/smartspace";
		this.restTemplate = new RestTemplate();
	}
	
	@After
	public void tearDown() {
		this.userDao.deleteAll();
		this.elementDao.deleteAll();
	}
	
	// Create
	@Test
	public void testCreateValidElementWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN a new and valid element is posted to the server
		ElementBoundary newElementBoundary = this.testUtils.generateValidElementBoundary();

		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail();

		ElementBoundary actualResult = this.restTemplate
				.postForObject(requestUrl, newElementBoundary, ElementBoundary.class);
		
		//	THEN the database contains the new element
		assertThat(this.elementDao.readById(actualResult.convertToEntity().getElementId() + "#" + 
				actualResult.convertToEntity().getElementSmartspace()))
			.isPresent()
			.get()
			.extracting("elementId", "elementSmartspace", "name", "type", "creatorSmartspace", 
					"creatorEmail", "expired", "moreAttributes")
			.containsExactly(actualResult.convertToEntity().getElementId(), actualResult.convertToEntity().getElementSmartspace(), 
					actualResult.getName(), actualResult.getElementType(), actualResult.getCreator().get("smartspace"),
					actualResult.getCreator().get("email"), Boolean.valueOf(actualResult.getExpired()), 
					actualResult.getElementProperties());
	}
	
	@Test(expected=Exception.class)
	public void testCreateValidElementWithExistingPlayer() throws Exception {
		//	GIVEN that the requesting user is a player and he exists in the database
		UserEntity player = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		this.userDao.create(player);
		
		//	WHEN a new and valid element is posted to the server
		ElementBoundary newElementBoundary = this.testUtils.generateValidElementBoundary();

		String requestUrl = this.baseUrl + "/elements/" + player.getUserSmartspace() + "/" + player.getUserEmail();

		this.restTemplate
				.postForObject(requestUrl, newElementBoundary, ElementBoundary.class);

		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testCreateValidElementWithExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is an admin and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN a new and valid element is posted to the server
		ElementBoundary newElementBoundary = this.testUtils.generateValidElementBoundary();

		String requestUrl = this.baseUrl + "/elements/" + admin.getUserSmartspace() + "/" + admin.getUserEmail();

		this.restTemplate
				.postForObject(requestUrl, newElementBoundary, ElementBoundary.class);

		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testCreateValidElementWithNonExistingUser() throws Exception {
		//	GIVEN that the requesting user doesn't exist in the database
		
		//	WHEN a new and valid element is posted to the server
		ElementBoundary newElementBoundary = this.testUtils.generateValidElementBoundary();

		String requestUrl = this.baseUrl + "/elements/" + "someSmartspace" + "/" + "someEmail@gmail.com";

		this.restTemplate
				.postForObject(requestUrl, newElementBoundary, ElementBoundary.class);

		//	THEN an exception is thrown
	}
	
	// Update
	@Test
	public void testUpdateValidElementWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN an existing element with valid updated fields is being put to the server
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		ElementBoundary newElementBoundary = new ElementBoundary(existingElementEntity);
		newElementBoundary.setName("new name");
		newElementBoundary.setElementType("Board");
		newElementBoundary.setExpired(true);
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + 
				manager.getUserEmail() + "/" + existingElementEntity.getElementSmartspace() + "/" + 
				existingElementEntity.getKey();
		
		this.restTemplate.put(requestUrl, newElementBoundary);
		
		//	THEN the database is updated with the updated fields
		assertThat(this.elementDao.readById(existingElementEntity.getKey()))
			.isPresent()
			.get()
			.extracting("name", "type", "expired")
			.containsExactly(newElementBoundary.getName(), newElementBoundary.getElementType(), 
					Boolean.valueOf(newElementBoundary.getExpired()));
	}
	
	@Test(expected=Exception.class)
	public void testUpdateNonExistingElementFieldsWithExistingManager() throws Exception {
			//	GIVEN that the requesting user is a manager and he exists in the database
			UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
			this.userDao.create(manager);
			
			//	WHEN a non existing element with valid updated fields is being put to the server
			ElementBoundary newElementBoundary = this.testUtils.generateValidElementBoundary();

			String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + 
					manager.getUserEmail() + "/" + "SomeSmartspace" + "/" + 
					"SomeKey";
			
			this.restTemplate.put(requestUrl, newElementBoundary);
			
			//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testUpdateInvalidElementFieldsWithExistingManager() throws Exception {
			//	GIVEN that the requesting user is a manager and he exists in the database
			UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
			this.userDao.create(manager);
			
			//	WHEN an existing element with an invalid updated field is being put to the server
			ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
			existingElementEntity = this.elementDao.create(existingElementEntity);
			
			ElementBoundary newElementBoundary = new ElementBoundary(existingElementEntity);
			newElementBoundary.setElementType("SomeInvalidType");

			String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + 
					manager.getUserEmail() + "/" + existingElementEntity.getElementSmartspace() + "/" + 
					existingElementEntity.getKey();
			
			this.restTemplate.put(requestUrl, newElementBoundary);
			
			//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testUpdateValidElementWithExistingPlayer() throws Exception {
		//	GIVEN that the requesting user is a player and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		this.userDao.create(manager);
		
		//	WHEN an existing element with valid updated fields is being put to the server
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		ElementBoundary newElementBoundary = new ElementBoundary(existingElementEntity);
		newElementBoundary.setName("new name");
		newElementBoundary.setElementType("Board");
		newElementBoundary.setExpired(true);
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + 
				manager.getUserEmail() + "/" + existingElementEntity.getElementSmartspace() + "/" + 
				existingElementEntity.getKey();
		
		this.restTemplate.put(requestUrl, newElementBoundary);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testUpdateValidElementWithExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is a player and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(manager);
		
		//	WHEN an existing element with valid updated fields is being put to the server
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		ElementBoundary newElementBoundary = new ElementBoundary(existingElementEntity);
		newElementBoundary.setName("new name");
		newElementBoundary.setElementType("Board");
		newElementBoundary.setExpired(true);
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + 
				manager.getUserEmail() + "/" + existingElementEntity.getElementSmartspace() + "/" + 
				existingElementEntity.getKey();
		
		this.restTemplate.put(requestUrl, newElementBoundary);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testUpdateValidElementFieldsWithNonExistingUser() throws Exception {
		//	GIVEN that the requesting user doesn't exist in the database
			
		//	WHEN an existing element with valid updated fields is being put to the server
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
			
		ElementBoundary newElementBoundary = new ElementBoundary(existingElementEntity);
		newElementBoundary.setElementType("Board");

		String requestUrl = this.baseUrl + "/elements/" + "SomeSmartspace" + "/" + 
				"SomeEmail" + "/" + existingElementEntity.getElementSmartspace() + "/" + 
				existingElementEntity.getKey();
			
		this.restTemplate.put(requestUrl, newElementBoundary);
			
		//	THEN an exception is thrown
	}
	
	// Get by key
	@Test
	public void testGetExistingElementWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN an existing element is being requested from the server using its key
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail() + "/" +
				existingElementEntity.getElementSmartspace() + "/" + existingElementEntity.getElementId();
		
		ElementBoundary actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary.class);

		//	THEN the server returns the exact element
		assertThat(existingElementEntity)
			.extracting("elementId", "elementSmartspace", "name", "type", "creatorSmartspace", 
					"creatorEmail", "expired", "moreAttributes")
			.containsExactly(actualResult.convertToEntity().getElementId(), actualResult.convertToEntity().getElementSmartspace(), 
					actualResult.getName(), actualResult.getElementType(), actualResult.getCreator().get("smartspace"),
					actualResult.getCreator().get("email"), Boolean.valueOf(actualResult.getExpired()), 
					actualResult.getElementProperties());
	}
	
	@Test
	public void testGetExistingElementWithExistingPlayer() throws Exception {
		//	GIVEN that the requesting user is a player and he exists in the database
		UserEntity player = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		this.userDao.create(player);
		
		//	WHEN an existing element is being requested from the server using its key
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		String requestUrl = this.baseUrl + "/elements/" + player.getUserSmartspace() + "/" + player.getUserEmail() + "/" +
				existingElementEntity.getElementSmartspace() + "/" + existingElementEntity.getElementId();
		
		ElementBoundary actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary.class);

		//	THEN the server returns the exact element
		assertThat(existingElementEntity)
			.extracting("elementId", "elementSmartspace", "name", "type", "creatorSmartspace", 
					"creatorEmail", "expired", "moreAttributes")
			.containsExactly(actualResult.convertToEntity().getElementId(), actualResult.convertToEntity().getElementSmartspace(),
					actualResult.getName(), actualResult.getElementType(), actualResult.getCreator().get("smartspace"),
					actualResult.getCreator().get("email"), Boolean.valueOf(actualResult.getExpired()), 
					actualResult.getElementProperties());
	}
	
	@Test(expected=Exception.class)
	public void testGetExistingElementWithExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is an admin and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN an existing element is being requested from the server using its key
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		String requestUrl = this.baseUrl + "/elements/" + admin.getUserSmartspace() + "/" + admin.getUserEmail() + "/" +
				existingElementEntity.getElementSmartspace() + "/" + existingElementEntity.getElementId();
		
		this.restTemplate.getForObject(requestUrl, ElementBoundary.class);

		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testGetExistingElementWithNonExistingUser() throws Exception {
		//	GIVEN that the requesting user doesn't exist in the database
		
		//	WHEN an existing element is being requested from the server using its key
		ElementEntity existingElementEntity = this.testUtils.generateValidElementEntity();
		existingElementEntity = this.elementDao.create(existingElementEntity);
		
		String requestUrl = this.baseUrl + "/elements/" + "SomeSmartspace" + "/" + "SomeEmail" + "/" +
				existingElementEntity.getElementSmartspace() + "/" + existingElementEntity.getElementId();
		
		this.restTemplate.getForObject(requestUrl, ElementBoundary.class);

		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testGetNonExistingElementWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN a non existing element is being requested from the server using its key
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail() + "/" +
				"SomeSmartspace" + "/" + "SomeId";
		
		this.restTemplate.getForObject(requestUrl, ElementBoundary.class);

		//	THEN an exception is thrown
	}
	
	@Test
	public void testGetExistingAllElementWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		// WHEN an existing element with valid updated fields is being put to the server
		ElementEntity existingElementEntity1 = this.testUtils.generateValidElementEntity();
		existingElementEntity1 = this.elementDao.create(existingElementEntity1);
		
		ElementEntity existingElementEntity2 = this.testUtils.generateValidElementEntity();
		existingElementEntity2 = this.elementDao.create(existingElementEntity2);
		
		ElementEntity existingElementEntity3 = this.testUtils.generateValidElementEntity();
		existingElementEntity3 = this.elementDao.create(existingElementEntity3);
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail()
				+ "&page=0&size=2";
		
		int size = 10;
		int page = 0;
		
		ElementBoundary[] actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary[].class,page,size);

		//	THEN the server returns a list containing existingElementEntity
		List<ElementEntity> expectedResult = new ArrayList<ElementEntity>();
		expectedResult.add(existingElementEntity1);
		expectedResult.add(existingElementEntity2);
		expectedResult.add(existingElementEntity3);
		
		assertThat(actualResult)
			.hasSize(3);
	}
	
	// Get by search & value
	@Test
	public void testGetExistingElementByTypeWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN an existing element is being requested from the server using its type
		ElementEntity existingElementEntityWithMyType = this.testUtils.generateValidElementEntityWithSpecifiedType("myType");
		existingElementEntityWithMyType = this.elementDao.create(existingElementEntityWithMyType);
		
		ElementEntity existingElementEntityWithOtherType = this.testUtils.generateValidElementEntityWithSpecifiedType("otherType");
		existingElementEntityWithOtherType = this.elementDao.create(existingElementEntityWithOtherType);
		
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail()
				+ "?search=" + "type" + "&value=" + existingElementEntityWithMyType.getType()
				+ "&page=0&size=3";
		
		String type = "type";
		String value = "myType";
		int size = 10;
		int page = 0;
		
		ElementBoundary[] actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary[].class, type,value,page,size);

		//	THEN the server returns a list containing existingElementEntityWithMyType
		List<ElementEntity> expectedResult = new ArrayList<ElementEntity>();
		expectedResult.add(existingElementEntityWithMyType);
		
		assertThat(actualResult)
			.hasSize(1);
	}
	
	@Test
	public void testGetExistingElementByNameWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN an existing element is being requested from the server using its name
		ElementEntity existingElementEntityWithMyName = this.testUtils.generateValidElementEntityWithSpecifiedName("myName");
		existingElementEntityWithMyName = this.elementDao.create(existingElementEntityWithMyName);
		
		ElementEntity existingElementEntityWithOtherName = this.testUtils.generateValidElementEntityWithSpecifiedName("otherName");
		existingElementEntityWithOtherName = this.elementDao.create(existingElementEntityWithOtherName);
		
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail()
				+ "?search=" + "name" + "&value=" + existingElementEntityWithMyName.getName()
				+ "&page=0&size=3";
		
		String name = "name";
		String value = "myName";
		int size = 10;
		int page = 0;
		
		ElementBoundary[] actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary[].class, name,value,page,size);

		//	THEN the server returns a list containing existingElementEntityWithMyName
		List<ElementEntity> expectedResult = new ArrayList<ElementEntity>();
		expectedResult.add(existingElementEntityWithMyName);
		
		assertThat(actualResult)
			.hasSize(1);
	
	}
	
	@Test
	public void testGetExistingElementByLocationWithExistingManager() throws Exception {
		//	GIVEN that the requesting user is a manager and he exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN an existing element is being requested from the server using its location
		ElementEntity existingElementEntityLocation1 = this.testUtils.generateValidElementEntityWithSpecifiedLocation(20,20);
		existingElementEntityLocation1 = this.elementDao.create(existingElementEntityLocation1);
		
		ElementEntity existingElementEntityLocation2 = this.testUtils.generateValidElementEntityWithSpecifiedLocation(10,20);
		existingElementEntityLocation2 = this.elementDao.create(existingElementEntityLocation2);
	
		ElementEntity existingElementEntityLocation3 = this.testUtils.generateValidElementEntityWithSpecifiedLocation(50,70);
		existingElementEntityLocation3 = this.elementDao.create(existingElementEntityLocation3);
		
		String name = "location";
		int x = 10;
		int y = 10;
		int distance = 25;
		int size = 10;
		int page = 0;
		
		String requestUrl = this.baseUrl + "/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail()
				+ "?search=" + "location" + "&x=" + x + "&y=" + y	+ "&distance=" + distance
				+ "&page=0&size=3";
		
		
		
		ElementBoundary[] actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary[].class, name,x,y,distance,page,size);

		//	THEN the server returns a list containing existingElementEntityWithMyName
		List<ElementEntity> expectedResult = new ArrayList<ElementEntity>();
		expectedResult.add(existingElementEntityLocation1);
		expectedResult.add(existingElementEntityLocation2);
		
		assertThat(actualResult)
			.hasSize(2);
	
	}
	
	// Import
	@Test
	public void testImportValidElementsByExistingAdmin() throws Exception {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN valid elements from another smartspace are being imported
		List<ElementBoundary> elementsToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidElementEntity())
			.peek(elementBoundary->elementBoundary.setElementSmartspace("SomeOtherSmartspace"))
			.map(ElementBoundary::new)
			.collect(Collectors.toList());
		
		ElementBoundary[] elementsToImportArr = elementsToImport.toArray(new ElementBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + admin.getUserSmartspace() + "/" + admin.getUserEmail();
		
		ElementBoundary[] actualResult = this.restTemplate
				.postForObject(requestUrl, elementsToImportArr, ElementBoundary[].class);
		
		//	THEN the database contains the new imported elements
		IntStream.range(0, actualResult.length)
			.forEach(i->{
				assertThat(this.elementDao.readById(actualResult[i].convertToEntity().getKey()))
					.isPresent()
					.get()
					.extracting("name", "type", "expired")
					.containsExactly(actualResult[i].getName(), actualResult[i].getElementType(), 
							Boolean.valueOf(actualResult[i].getExpired()));
			});
	}
	
	@Test(expected=Exception.class)
	public void testImportInvalidElementsByExistingAdmin() {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN valid elements from local smartspace are being imported
		List<ElementBoundary> elementsToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidElementEntity())
			.map(ElementBoundary::new)
			.collect(Collectors.toList());
		
		ElementBoundary[] elementsToImportArr = elementsToImport.toArray(new ElementBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + admin.getUserSmartspace() + "/" + admin.getUserEmail();
		
		this.restTemplate.postForObject(requestUrl, elementsToImportArr, ElementBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testImportValidElementsByNonExistingAdmin() {
		//	GIVEN that the requesting user doesn't exist in the database
		
		//	WHEN valid elements from another smartspace are being imported
		List<ElementBoundary> elementsToImport = IntStream.range(0, 3)
			.mapToObj(i->this.testUtils.generateValidElementEntity())
			.peek(elementBoundary->elementBoundary.setElementSmartspace("SomeOtherSmartspace"))
			.map(ElementBoundary::new)
			.collect(Collectors.toList());
		
		ElementBoundary[] elementsToImportArr = elementsToImport.toArray(new ElementBoundary[0]);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + "SomeSmartspace" + "/" + "SomeEmail";
		
		this.restTemplate.postForObject(requestUrl, elementsToImportArr, ElementBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	// Export
	@Test
	public void testExportValidElementsByExistingAdmin() {
		//	GIVEN that the requesting user is a local admin, and he exists in the database
		UserEntity admin = this.testUtils.generateValidUserEntity(UserRole.ADMIN);
		this.userDao.create(admin);
		
		//	WHEN the admin requests to export existing elements
		IntStream.range(0, 3)
				.mapToObj(i->this.testUtils.generateValidElementEntity())
				.peek(this.elementDao::create);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + admin.getUserSmartspace() + "/" + admin.getUserEmail() +
				"?page=0&size=3";
		
		ElementBoundary[] actualResult = this.restTemplate
				.getForObject(requestUrl, ElementBoundary[].class);
		
		//	THEN the server returns the requested elements
		IntStream.range(0, actualResult.length)
		.forEach(i->{
			assertThat(this.elementDao.readById(actualResult[i].convertToEntity().getKey()))
				.isPresent()
				.get()
				.extracting("name", "type", "expired")
				.containsExactly(actualResult[i].getName(), actualResult[i].getElementType(), 
						Boolean.valueOf(actualResult[i].getExpired()));
		});
	}
	
	@Test(expected=Exception.class)
	public void testExportValidElementsByExistingPlayer() {
		//	GIVEN that the requesting player exists in the database
		UserEntity player = this.testUtils.generateValidUserEntity(UserRole.PLAYER);
		this.userDao.create(player);
		
		//	WHEN the player requests to export existing elements
		IntStream.range(0, 3)
				.mapToObj(i->this.testUtils.generateValidElementEntity())
				.peek(this.elementDao::create)
				.map(ElementBoundary::new);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + player.getUserSmartspace() + "/" + player.getUserEmail() +
				"?page=0&size=3";
		
		this.restTemplate.getForObject(requestUrl, ElementBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testExportValidElementsByExistingManager() {
		//	GIVEN that the requesting manager exists in the database
		UserEntity manager = this.testUtils.generateValidUserEntity(UserRole.MANAGER);
		this.userDao.create(manager);
		
		//	WHEN the manager requests to export existing elements
		IntStream.range(0, 3)
				.mapToObj(i->this.testUtils.generateValidElementEntity())
				.peek(this.elementDao::create)
				.map(ElementBoundary::new);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + manager.getUserSmartspace() + "/" + manager.getUserEmail() +
				"?page=0&size=3";
		
		this.restTemplate.getForObject(requestUrl, ElementBoundary[].class);
		
		//	THEN an exception is thrown
	}
	
	@Test(expected=Exception.class)
	public void testExportValidElementsByNonExistingAdmin() {
		//	GIVEN that the requesting user doesn't exist in the database
		
		//	WHEN the user requests to export existing elements
		IntStream.range(0, 3)
				.mapToObj(i->this.testUtils.generateValidElementEntity())
				.peek(this.elementDao::create)
				.map(ElementBoundary::new);
		
		String requestUrl = this.baseUrl + "/admin/elements/" + "SomeSmartspace" + "/" + "SomeEmail" +
				"?page=0&size=3";
		
		this.restTemplate.getForObject(requestUrl, ElementBoundary[].class);
		
		//	THEN an exception is thrown
	}

}
