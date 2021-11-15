package smartspace.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.UnAuthorizedException;
import smartspace.layout.UserNotFoundException;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@Component
public class CheckInPlugin implements PluginCommand {

	private ObjectMapper mapper;
	private EnhancedActionDao actionDB;

	private ElementService elementService;
	private UserService userService;

	@Autowired
	public void setUserService(UserService service) {
		this.userService = service;
	}

	@Autowired
	public void setElementService(ElementService service) {
		this.elementService = service;
	}

	@Autowired
	public CheckInPlugin(EnhancedActionDao actionDB) {
		super();
		this.actionDB = actionDB;
		this.mapper = new ObjectMapper();
	}

	public EnhancedActionDao getActionDB() {
		return actionDB;
	}

	public void setActionDB(EnhancedActionDao actionDB) {
		this.actionDB = actionDB;
	}

	@Override
	public Object execute(ActionEntity action) {
		String elementSmartSpace = action.getElementSmartspace();
		String elementID = action.getElementId();
		String playerSmartSpace = action.getPlayerSmartspace();
		String playerEmail = action.getPlayerEmail();
		boolean alreadyInCheckInStatus=false;
		String userKey = playerEmail + "#" + playerSmartSpace;
		
		System.err.println("userKey:"+userKey);
		ElementEntity et;
		try {
			System.err.println("AVIAMAZA");
			et = this.elementService.getByKey(playerSmartSpace, playerEmail, elementSmartSpace, elementID);

		} 
		catch (UnAuthorizedException e) {
			System.err.println("AVIAMAZA2");
			et = new ElementEntity();
			System.err.println("Only player can do checkin");
			et.getMoreAttributes().put("error", "Only player can do checkin");
			return et.getMoreAttributes();
		}
			 
			
		
		System.err.println("AAA:"+et);
		if (!(et.getType().equals("Board")) && !(et.getType().equals("Tile"))) {

			System.err.println("Checkin not on Board or Tile");
			et.getMoreAttributes().put("error", "Checkin not on Board or Tile");

		} else {
			UserEntity ue;
			try {
				
			
			 ue = this.userService.getByKey(userKey);
			} catch (Exception e) {
				et.getMoreAttributes().put("error", "user not found");
				return et.getMoreAttributes();
			}
			
			if (ue.getRole() == UserRole.PLAYER) {
				System.err.println("NO TRUE");
				if (et.getMoreAttributes().containsKey(userKey)) {
					if(et.getMoreAttributes().get(userKey).equals("checkin"))
					{
						
						alreadyInCheckInStatus=true;
						System.err.println("Can't do check in when user is already check in status");
						et.getMoreAttributes().put("error", "Can't do check in when user is already check in status");
					}
					
				} if(!alreadyInCheckInStatus)
				{
					System.err.println("--2--");
					et.getMoreAttributes().put(userKey, "checkin");
					if (et.getMoreAttributes().containsKey("error")) {

						et.getMoreAttributes().remove("error");
					}
					this.elementService.update(elementID, et, elementSmartSpace);
				}
				
			} else {
				System.err.println("SHAY");
				System.err.println("Only player can do checkin");

				et.getMoreAttributes().put("error", "Only player can do checkin");

			}
		}
		
		System.err.println("return et.getMoreAttributes()");
		return et.getMoreAttributes();

	}

}