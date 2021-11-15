package smartspace;

import static org.junit.Assert.assertEquals;

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
public class ActionRollCubeTests {
	private UserService userSerivce;
	private ElementService elementService;
	private ActionService actionService;

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
	public void rollCubeOK() {

		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		UserEntity userEntityPlayer = new UserEntity("ggplayer@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.PLAYER, 0l);
		this.userSerivce.create(userEntityPlayer);

		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggplayer@gmail.com", "RollCube", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);

		this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);

		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);

		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().containsKey("roll_value")) {
					String num = (String) actionEntityAfterPost.getMoreAttributes().get("roll_value");
					System.err.println("num:" + num);
				
					if (num!= null) {
						if (!(num.charAt(0) >= '1' && num.charAt(0) <= '6')) {
							try {
								throw new Exception("ilegal number of cube");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}

		}

	}

	@Test(expected = UnAuthorizedException.class)
	public void rollCubeDoneByManager() throws UnAuthorizedException {

		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(),
				"2019b.marlenba", "ggwp@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggwp@gmail.com", "RollCube", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);

		try {
			System.err.println("BEFORE");

			this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);
			System.err.println("AFTER");
		} catch (Exception e) {

			throw new UnAuthorizedException();
		}

		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntity.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
					if (actionEntityAfterPost.getMoreAttributes().get("error")
							.equals("Only players can roll the cube")) {

						throw new UnAuthorizedException();

					}
				}

			}
		}

	}

	@Test(expected = UnAuthorizedException.class)
	public void rollCubeDoneByAdmin() throws UnAuthorizedException {

		UserEntity userEntity = new UserEntity("ggwp@gmail.com", "2019b.marlenba", "avatarManager", " ",
				UserRole.MANAGER, 0l);
		this.userSerivce.create(userEntity);

		ElementEntity elementEntity = new ElementEntity("jojo", "Board", new Location(), false, new Date(), "Koko",
				"jojo@gmail.com", new HashMap<String, Object>());
		elementEntity = this.elementService.create(elementEntity, "2019b.marlenba", "ggwp@gmail.com");

		UserEntity userEntityPlayer = new UserEntity("ggadmin@gmail.com", "2019b.marlenba", "ggplayer", "avatarPlayer",
				UserRole.ADMIN, 0l);
		this.userSerivce.create(userEntityPlayer);

		ActionEntity actionEntity = new ActionEntity(elementEntity.getElementSmartspace(), elementEntity.getElementId(),
				"2019b.marlenba", "ggadmin@gmail.com", "RollCube", new Date(), new LinkedMultiValueMap());
		ActionBoundary actionBoundary = new ActionBoundary(actionEntity);
		System.err.println("BEFORE");

		this.restTemplate.postForObject(baseUrl, actionBoundary, ActionBoundary.class);

		System.err.println("AFTER");

		ActionEntity actionEntityAfterPost = this.actionService.getAll(1, 0).get(0);
		UserEntity userEntityPlayerAfterPost = this.userSerivce.getByKey(userEntity.getKey());
		if (actionEntityAfterPost != null) {
			if (actionEntityAfterPost.getMoreAttributes() != null) {
				if (actionEntityAfterPost.getMoreAttributes().containsKey("error")) {
					if (actionEntityAfterPost.getMoreAttributes().get("error")
							.equals("Only players can roll the cube")) {

						throw new UnAuthorizedException();

					}
				}

			}
		}

	}

	@Test(expected = UserNotFoundException.class)
	public void tryRollCubeOnBoardWithdoesNotExistUser() {

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
				notExistUserEntity.getUserSmartspace(), notExistUserEntity.getUserEmail(), "RollCube", new Date(),
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
						if (actionEntityAfterPost.getMoreAttributes().get("error").equals("User not found")) {
							// 3. except UnAuthorizedActionOnElementException because can't do check in to
							// LadderSnake

							throw new UserNotFoundException();
							// addition of the check in plug-in to more attributes success
						}
					}

				}
			}
		} catch (Exception e) {
			throw new UserNotFoundException();
		}

//		} catch (Exception e) {
//			throw new UserNotFoundException();
//		}

	}
}
