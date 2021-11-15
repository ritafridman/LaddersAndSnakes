package smartspace.plugins;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.UserNotFoundException;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@Component
public class RollCubePlugin implements PluginCommand {

	private ObjectMapper mapper;
	private EnhancedActionDao actionDB;

	private ElementService elementService;
	private UserService userService;
	private int cubeNum;

	public int getNum() {
		return cubeNum;
	}

	public void setNum(int num) {
		this.cubeNum = num;
	}
	
	public void roll() {
		Random r = new Random();
		this.setNum(r.nextInt(6)+1);
	}


	public ObjectMapper getMapper() {
		return mapper;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public EnhancedActionDao getActionDB() {
		return actionDB;
	}

	@Autowired
	public void setActionDB(EnhancedActionDao actionDB) {
		this.actionDB = actionDB;
	}

	public ElementService getElementService() {
		return elementService;
	}

	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public RollCubePlugin(EnhancedActionDao actionDB) {
		super();
		this.actionDB = actionDB;
		this.mapper = new ObjectMapper();
	}

	@Override
	public Object execute(ActionEntity action) {
		String elementSmartSpace = action.getElementSmartspace();
		String elementID = action.getElementId();
		String playerSmartSpace = action.getPlayerSmartspace();
		String playerEmail = action.getPlayerEmail();
		boolean alreadyInCheckInStatus=false;
		String userKey = playerEmail + "#" + playerSmartSpace;
		UserEntity userEntity;
		try {
			userEntity = this.userService.getByKey(playerEmail + "#" + playerSmartSpace);
		} catch (UserNotFoundException e) {
			action.getMoreAttributes().put("error", "User not found");
			return action.getMoreAttributes();
		}
		
		if(userEntity.getRole() != UserRole.PLAYER) {
			action.getMoreAttributes().put("error", "Only players can roll the cube");
			return action.getMoreAttributes();
		}
		
		roll();
		action.getMoreAttributes().put("roll_value", this.cubeNum + "");
		return action.getMoreAttributes();
	}

}
