package smartspace.plugins;

import java.util.Random;

import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

public class TrampolineSkipToRolledCellPlugin implements PluginCommand {

	private Random random;
	private ElementService elementService;
	private UserService userService;

	public ElementService getElementService() {
		return elementService;
	}

	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Object execute(ActionEntity action) {
		String playerSmartSpace = action.getPlayerSmartspace();
		String playerEmail = action.getPlayerEmail();

		String userKey = playerEmail + "#" + playerSmartSpace;
		
		ElementEntity boardEntity = this.elementService.getByType("Board", 1, 1).get(0);
		
		Location initialLocation = boardEntity.getLocation();
		double initialCellNr = locationToCellNr(initialLocation);
		
		int rolledNumber = this.random.nextInt(100) + 1; // 1-100
		
		double updatedCellNr = rolledNumber;
		Location updatedLocation = cellNrToLocation(updatedCellNr);
		
		// Update board Location
		boardEntity.setLocation(updatedLocation);
		
		return action;
	}

	public double locationToCellNr(Location location) {
		return ((location.getLat() - 1) * 10 + location.getLng()); 
	}
	
	public Location cellNrToLocation(double cellNr) {
		double row = cellNr / 10 + ((cellNr % 10 > 0) ? 1 : 0);
		double col = (cellNr - 10*(cellNr / 10) > 0) ? (cellNr - 10*(cellNr / 10)) : 10;
		return new Location(row, col);
	}
}