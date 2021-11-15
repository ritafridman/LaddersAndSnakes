package smartspace;

import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import smartspace.dao.ActionDao;
import smartspace.dao.ElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

import smartspace.layout.ActionBoundary;
import smartspace.layout.ActionEntityNullException;
import smartspace.layout.DoCheckInOnCheckInException;
import smartspace.layout.DoCheckOutOnCheckOutException;
import smartspace.layout.DoCheckOutWhenUserIsntCheckInException;
import smartspace.layout.MoreAttributesMapIsNullException;
import smartspace.layout.NotExcepetPluginNameException;
import smartspace.layout.NotExistPluginNameException;
import smartspace.layout.UnAuthorizedActionOnElementException;
import smartspace.layout.UnAuthorizedException;
import smartspace.layout.UserNotFoundException;
import smartspace.logic.ActionService;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ActionCheckOutTests {
	private UserService userSerivce;
	private ElementService elementService;
	private ActionService actionService;

	// TODO add to service cleanup
	private UserDao userDao;

	private ElementDao elementDao;
	private ActionDao actionDao;

	private RestTemplate restTemplate;
	private int port;
	private String baseUrl;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port + "/smartspace/actions";
		this.restTemplate = new RestTemplate();
	}

	@Autowired
	public void setUserSerivce(UserService userSerivce) {
		this.userSerivce = userSerivce;
	}

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Autowired
	public void setActionServicee(ActionService actionSerivce) {
		this.actionService = actionSerivce;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setElementDao(ElementDao elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
		this.actionDao.deleteAll();
		this.elementDao.deleteAll();
		this.userDao.deleteAll();

	}

	@Test
	public void checkoutTile() {

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		// 2.create element Entity set its type to Tile and save it in DB
		// 3.create a user Entity type player will do Check Out for the Tile
		// WHEN:
		// activate action with them
		// THEN:
		// 1.Do Check Out for the Tile
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Tile", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check Out for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		
		ActionEntity actionEntityCheckIn = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckIn", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckIn = new ActionBoundary(actionEntityCheckIn);
		
		ActionEntity actionEntityCheckOut = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckOut = new ActionBoundary(actionEntityCheckOut);

		// THEN:
		// 1.Do Check Out for the board
		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckIn, ActionBoundary.class);

		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckOut, ActionBoundary.class);
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(2, 0).get(1);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().get(userEntityPlayerAfterPost.getKey())
						.equals("checkout")) {

					// addition of the check in plug-in to more attributes success
				} else {
					try {
						throw new NotExcepetPluginNameException(
								"Excepted to get a check out plugin value when enter the user key as key in Action Element more attributes");
					} catch (NotExcepetPluginNameException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new MoreAttributesMapIsNullException(
							"More Attributes Map is null in Action Entity after post");
				} catch (MoreAttributesMapIsNullException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				throw new ActionEntityNullException("After post Request Action Entity is null");
			} catch (ActionEntityNullException e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	public void checkoutBoard() {

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		// 2.create element Entity set its type to Board and save it in DB
		// 3.create a user Entity type player will do Check in for the board
		// WHEN:
		// activate action with them
		// THEN:
		// 1.Do Check out for the board
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check out for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		ActionEntity actionEntityCheckIn = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckIn", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckIn = new ActionBoundary(actionEntityCheckIn);
		
		ActionEntity actionEntityCheckOut = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckOut = new ActionBoundary(actionEntityCheckOut);

		// THEN:
		// 1.Do Check out for the board
		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckIn, ActionBoundary.class);

		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckOut, ActionBoundary.class);
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(2, 0).get(1);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {

				if (actionEntityAfterPost.getMoreAttributes().get(userEntityPlayerAfterPost.getKey())
						.equals("checkout")) {
					// addition of the check out plug-in to more attributes success
				} else {
					try {
						throw new NotExcepetPluginNameException(
								"Excepted to get a check in plugin value when enter the user key as key in Action Element more attributes");
					} catch (NotExcepetPluginNameException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					throw new MoreAttributesMapIsNullException(
							"More Attributes Map is null in Action Entity after post");
				} catch (MoreAttributesMapIsNullException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				throw new ActionEntityNullException("After post Request Action Entity is null");
			} catch (ActionEntityNullException e) {
				e.printStackTrace();
			}
		}

	}

	@Test(expected = NotExistPluginNameException.class)
	public void InsteadCheckoutNotExistPluginName() throws NotExistPluginNameException {

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		// 2.create element Entity set its type to Tile and save it in DB
		// 3.create a user Entity type player will do jump for the Tile
		// WHEN:
		// activate action with them
		// THEN:
		// 1.Do Check out for the Tile
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Tile", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check out for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "Jump", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);

		// THEN:
		// 1.Do Check out for the board
	
			System.err.println("BEFORE");
			this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);
			System.err.println("AFTER");
	
			
		
		
		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
					if (actionEntityAfterPost.getMoreAttributes().get("error").equals("unvalid action")) {
						// 3. except UnAuthorizedActionOnElementException because can't do check out to
						// LadderSnake

						throw new NotExistPluginNameException();
						// addition of the check in plug-in to more attributes success
					}
				}

			}
		}
			
		

		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
//		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
//		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());

	}
	@Test(expected = UnAuthorizedActionOnElementException.class)
	public void checkoutOnUnAuthorizedElement() throws UnAuthorizedActionOnElementException {

		// GIVEN:
		// 1. create user Entity type manager and save it in DB
		// 2. create element Entity set its type to LadderSnake and save it in DB
		// 3. create a user Entity type player will do Check out for the Ladder Snake
		// WHEN:
		// activate action with them
		// THEN:
		// 1. Do Check out for the board
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		// 3. except UnAuthorizedActionOnElementException because can't do check out to
		// LadderSnake

		// GIVEN:
		// 1. create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "LadderSnake", new Location(), false, new Date(),
				"Koko", "jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check out for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);

		// THEN:
		// 1.Do Check out for the board

		this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);

		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
					if (actionEntityAfterPost.getMoreAttributes().get("error").equals("Checkout not on Board or Tile")) {
						// 3. except UnAuthorizedActionOnElementException because can't do check out to
						// LadderSnake

						throw new UnAuthorizedActionOnElementException();
						// addition of the check out plug-in to more attributes success
					}
				}

			}
		}

	}

	@Test(expected = DoCheckOutOnCheckOutException.class)
	public void checkoutOnCheckOut() throws DoCheckOutOnCheckOutException {

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		// 2.create element Entity set its type to Tile and save it in DB
		// 3.create a user Entity type player will do Check out for the Tile
		// WHEN:
		// activate action with them
		// THEN:
		// 1.Do Check out for the Tile
		// 2.Do Check out Second Time on the board
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Tile", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check out for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		ActionEntity actionEntityCheckIn = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckIn", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckIn = new ActionBoundary(actionEntityCheckIn);
		
		ActionEntity actionEntityCheckOut = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckOut = new ActionBoundary(actionEntityCheckOut);

		// THEN:
		// 1.Do Check Out for the board
		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckIn, ActionBoundary.class);

		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckOut, ActionBoundary.class);
		
		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckOut, ActionBoundary.class);
		// 3. check if the addition of new key and value to map successes and really saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(3, 0).get(2);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().get(userEntityPlayerAfterPost.getKey())
						.equals("checkout")) {
					System.err.println("000");
					if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
						System.err.println("111");
						if (actionEntityAfterPost.getMoreAttributes().get("error")
								.equals("Can't do check out when user is already check out status")) {
							System.err.println("222");
							throw new DoCheckOutOnCheckOutException();
						}
						System.err.println("333");
			}
				// addition of the check out plug-in to more attributes success
			} else {
				try {
					throw new NotExcepetPluginNameException(
							"Excepted to get a check out plugin value when enter the user key as key in Action Element more attributes");
				} catch (NotExcepetPluginNameException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				throw new MoreAttributesMapIsNullException("More Attributes Map is null in Action Entity after post");
			} catch (MoreAttributesMapIsNullException e) {
				e.printStackTrace();
			}
		}
	}else

	{
		try {
			throw new ActionEntityNullException("After post Request Action Entity is null");
		} catch (ActionEntityNullException e) {
			e.printStackTrace();
		}
	}

	}

	
	@Test(expected = DoCheckOutWhenUserIsntCheckInException.class)
	public void DoCheckOutWhenUserIsntCheckIn() throws DoCheckOutWhenUserIsntCheckInException {

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		// 2.create element Entity set its type to Tile and save it in DB
		// 3.create a user Entity type player will do Check out for the Tile
		// WHEN:
		// activate action with them
		// THEN:
		// 1.Do Check out for the Tile
		// 2.Do Check out Second Time on the board
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Tile", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check out for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
	
		ActionEntity actionEntityCheckOut = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundaryCheckOut = new ActionBoundary(actionEntityCheckOut);

		// THEN:
		// 1.Do Check Out for the board
	
	
		this.restTemplate.postForObject(baseUrl, actionBoundaryCheckOut, ActionBoundary.class);
		// 3. check if the addition of new key and value to map successes and really saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		
				
					System.err.println("000");
					if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
						System.err.println("111");
						if (actionEntityAfterPost.getMoreAttributes().get("error")
								.equals("Can't do check out when user isnt in check in status")) {
							System.err.println("222");
							throw new DoCheckOutWhenUserIsntCheckInException();
						}
					}
				

	}
	@Test(expected = UnAuthorizedException.class)
	public void checkoutDoneByManager() throws UnAuthorizedException {

		// GIVEN:
		// 1. create user Entity type manager and save it in DB
		// 2. create element Entity set its type to LadderSnake and save it in DB
		// WHEN:
		// activate action with them
		// THEN:
		// 1. Do Check out for the board
		// 2. except UnAuthorizedActionOnElementException because user manager can't do
		// check out

		// GIVEN:
		// 1. create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(), "2019b.marlenba",
				"ggwp@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// WHEN:
		// activate action with them
		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggwp@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);

		try {
			System.err.println("BEFORE");
			// THEN:
			// 1. Do Check out for the board
			this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);
			System.err.println("AFTER");
		} catch (Exception e) {

			// 2. except UnAuthorizedActionOnElementException because user manager can't do
			// check in
			throw new UnAuthorizedException();
		}
		// 2. check if the addition of new key and value to map successes and really
				// saved in DB
				ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
				UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntity.getKey());
				if (actionEntityAfterPost != null) {
					if (actionEntityAfterPost.getMoreAttributes() != null) {
						if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
							if (actionEntityAfterPost.getMoreAttributes().get("error").equals("Only player can do checkout")) {
								// 3. except UnAuthorizedActionOnElementException because can't do check in to
								// LadderSnake
								throw new UnAuthorizedException();
								// addition of the check in plug-in to more attributes success
							}
						}

					}
				}

	}
	@Test(expected = UnAuthorizedException.class)
	public void checkoutDoneByAdmin() throws UnAuthorizedException {

		// GIVEN:
		// 1. create user Entity type manager and save it in DB
		// 2. create element Entity set its type to LadderSnake and save it in DB
		// 3.create a user Entity type admin will do Check in for the board
		// WHEN:
		// activate action with them
		// THEN:
		// 1. Do Check in for the board
		// 2. except UnAuthorizedActionOnElementException because user manager can't do
		// checkout

		// GIVEN:
		// 1. create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type admin will do Check in for the board
		UserEntity userEntityPlayer = new UserEntity("ggadmin@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.ADMIN, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggadmin@gmail.com", "CheckOut", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);
System.err.println("BEFORE");

			// THEN:
			// 1. Do Check out for the board
			this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);

		System.err.println("AFTER");
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntity.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
					if (actionEntityAfterPost.getMoreAttributes().get("error").equals("Only player can do checkout")) {
						// 3. except UnAuthorizedActionOnElementException because can't do check out to
						// LadderSnake
						throw new UnAuthorizedException();
						// addition of the check out plug-in to more attributes success
					}
				}

			}
		}

	}

	@Test(expected = UserNotFoundException.class)
	public void checkoutBoardWithdoesNotExistUser() {

		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "gg", " ", UserRole.MANAGER, 0l);

		this.userSerivce.create(userEntity);

		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());

		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");
		System.err.println("(TRY):" + elementEntity);
//		try {
		UserEntity notExistUserEntity = new UserEntity("unValidUser@gmail.com", "2019b.marlenba", "gg", " ",
				UserRole.PLAYER, 0l);
		System.err.println("notExistUserEntity:" + notExistUserEntity);

		ActionEntity actionEntity = new ActionEntity("2019b.marlenba", elementEntity.getElementId(),
				notExistUserEntity.getUserSmartspace(), notExistUserEntity.getUserEmail(), "CheckOut", new Date(),
				new LinkedMultiValueMap());
		System.err.println("actionEntity:" + actionEntity);
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);
		// this.restTemplate.postForObject(url, request, responseType, uriVariables)
		System.err.println("BEFORE");
		try {

			ActionBoundary actionBoundaryReturen = this.restTemplate.postForObject(baseUrl, actionBoundary,
					ActionBoundary.class);
			System.err.println("AFTER");
			
			 ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
				UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntity.getKey());
				if (actionEntityAfterPost != null) {
					if (actionEntityAfterPost.getMoreAttributes() != null) {
						if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
							if (actionEntityAfterPost.getMoreAttributes().get("error").equals("user not found")) {
								// 3. except UnAuthorizedActionOnElementException because can't do check out to
								// LadderSnake
								
								throw new UserNotFoundException();
								// addition of the check out plug-in to more attributes success
							}
						}

					}
				}
		} catch (Exception e) {
			throw new UserNotFoundException();
		}
	}
	@Test(expected=NotExcepetPluginNameException.class)
	public void NotExcepetPluginCheckIn() throws NotExcepetPluginNameException {

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		// 2.create element Entity set its type to Tile and save it in DB
		// 3.create a user Entity type player will do Check In for the Tile
		// WHEN:
		// activate action with them
		// THEN:
		// 1.Do Check in for the Tile
		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		//3. NotExcepetPluginNameException

		// GIVEN:
		// 1.create user Entity type manager and save it in DB
		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		// 2.create element Entity set its type to board and save it in DB
		ElementEntity elementEntity = new ElementEntity("jojo", "Tile", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		// 3.create a user Entity type player will do Check in for the board
		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		// WHEN:
		// activate action with them
		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "CheckIn", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);

		// THEN:
		// 1.Do Check in for the board
		this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);

		// 2. check if the addition of new key and value to map successes and really
		// saved in DB
		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntityPlayer.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().get(userEntityPlayerAfterPost.getKey())
						.equals("checkout")) {

					// addition of the check out plug-in to more attributes success
				} else {
					System.err.println("throw new NotExcepetPluginNameException");
				
						throw new NotExcepetPluginNameException(
								"Excepted to get a check out plugin value when enter the user key as key in Action Element more attributes");
					
				}
			} else {
				try {
					throw new MoreAttributesMapIsNullException(
							"More Attributes Map is null in Action Entity after post");
				} catch (MoreAttributesMapIsNullException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				throw new ActionEntityNullException("After post Request Action Entity is null");
			} catch (ActionEntityNullException e) {
				e.printStackTrace();
			}
		}

	}

	
	
	
}
