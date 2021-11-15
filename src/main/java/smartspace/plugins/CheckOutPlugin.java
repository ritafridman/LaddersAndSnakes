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
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@Component
public class CheckOutPlugin implements PluginCommand {

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
	public CheckOutPlugin(EnhancedActionDao actionDB) {
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
		boolean alreadyInCheckOutStatus = false;
		boolean DoCheckOutWhenUserIsntCheckIn = false;
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
			System.err.println("Only player can do checkout");
			et.getMoreAttributes().put("error", "Only player can do checkout");
			return et.getMoreAttributes();
		}
			 
			
		
		System.err.println("AAA:"+et);
		if (!(et.getType().equals("Board")) && !(et.getType().equals("Tile"))) {

			alreadyInCheckOutStatus=true;
			System.err.println("Checkout not on Board or Tile");
			et.getMoreAttributes().put("error", "Checkout not on Board or Tile");

		} else {
			UserEntity ue;
			try {
				
			
			 ue = this.userService.getByKey(userKey);
			} catch (Exception e) {
				et.getMoreAttributes().put("error", "user not found");
				return et.getMoreAttributes();
			}
			//ue = this.userService.getByKey(userKey);
			if (ue.getRole() == UserRole.PLAYER) {
				if (et.getMoreAttributes().containsKey(userKey)) {
					if(et.getMoreAttributes().get(userKey).equals("checkout"))
					{
						
						alreadyInCheckOutStatus=true;
						System.err.println("Can't do check out when user is already check out status");
						et.getMoreAttributes().put("error", "Can't do check out when user is already check out status");
					}
					else if(!(et.getMoreAttributes().get(userKey).equals("checkin")))
					{
						
						DoCheckOutWhenUserIsntCheckIn=true;
						System.err.println("Can't do check out when user isnt in check in status");
						et.getMoreAttributes().put("error", "Can't do check out when user isnt in check in status");
					}
				}else///!et.getMoreAttributes().containsKey(userKey) means user isn't in check in status
				{
					DoCheckOutWhenUserIsntCheckIn=true;
					System.err.println("Can't do check out when user isnt in check in status");
					et.getMoreAttributes().put("error", "Can't do check out when user isnt in check in status");
				System.err.println("*******");
				}
				if (!alreadyInCheckOutStatus && !DoCheckOutWhenUserIsntCheckIn) {
					et.getMoreAttributes().put(userKey, "checkout");
					if (et.getMoreAttributes().containsKey("error")) {

						et.getMoreAttributes().remove("error");
					}
					this.elementService.update(elementID, et, elementSmartSpace);
				}
			} else {
				System.err.println("Only player can do checkout");

				et.getMoreAttributes().put("error", "Only player can do checkout");

			}
		}return et.getMoreAttributes();

}

}