package smartspace.layout;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smartspace.data.Location;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@RestController
public class ElementController {
	private ElementService elementService;
	private UserService userService;

	@Autowired
	public ElementController(ElementService elementService, UserService userService) {
		this.elementService = elementService;
		this.userService = userService;
	}

	@RequestMapping(
			method=RequestMethod.GET,
			path="/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] export (
			@PathVariable(name="adminSmartspace", required=true) String adminSmartspace,
			@PathVariable(name="adminEmail", required=true) String adminEmail,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		
		if (!this.userService.validateUserAdminAndSmartspace(adminSmartspace, adminEmail)) {
			throw new UnAuthorizedException("Export and import can only be done by an admin user that belongs to this smartspace.");
		}
		
		return this.elementService
				.getAllSorted(size, page, "name")
				.stream()
				.map(ElementBoundary::new)
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] importElements(
			@PathVariable(name="adminSmartspace", required=true) String adminSmartspace,
			@PathVariable(name="adminEmail", required=true) String adminEmail,
			@RequestBody ElementBoundary[] elements) {
		
		if (!this.userService.validateUserAdminAndSmartspace(adminSmartspace, adminEmail)) {
			throw new UnAuthorizedException("Export and import can only be done by an admin user that belongs to this smartspace.");
		}
		if (elements.length != 0 &&
				adminSmartspace.equals(elements[0].convertToEntity().getElementSmartspace())) {
			throw new BadImportRequestException("Cannot import elements from the same smartspace.");
		}

		return this.elementService.importElements(
				Arrays.stream(elements)
					.map(element->element.convertToEntity())
					.collect(Collectors.toList()))
			.stream()
			.map(ElementBoundary::new)
			.collect(Collectors.toList())
			.toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/smartspace/elements/{managerSmartspace}/{managerEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary create (
			@PathVariable(name="managerSmartspace", required=true) String managerSmartspace,
			@PathVariable(name="managerEmail", required=true) String managerEmail,
			@RequestBody ElementBoundary element) {
		return new ElementBoundary(this.elementService.create(element.convertToEntity(), managerSmartspace, managerEmail));
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/smartspace/elements/{userSmartspace}/{userEmail}/{elementSmartspace}/{elementId}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getElementById(
			@PathVariable(name="userSmartspace", required=true) String userSmartspace,
			@PathVariable(name="userEmail", required=true) String userEmail,
			@PathVariable(name="elementSmartspace", required=true) String elementSmartspace,
			@PathVariable(name="elementId", required=true) String elementId) {
		return new ElementBoundary(this.elementService.getByKey(userSmartspace, userEmail, elementSmartspace, elementId));
	}
	
	@RequestMapping(
			method=RequestMethod.PUT,
			path="/smartspace/elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}",
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateElement (
			@PathVariable(name="managerSmartspace", required=true) String managerSmartspace,
			@PathVariable(name="managerEmail", required=true) String managerEmail,
			@PathVariable(name="elementSmartspace", required=true) String elementSmartspace,
			@PathVariable(name="elementId", required=true) String elementId,
			@RequestBody ElementBoundary element) {
		this.elementService.update(elementId, element.convertToEntity(), elementSmartspace, 
				managerSmartspace, managerEmail);
	}
	
	@RequestMapping(
			method= RequestMethod.GET,
			path="/smartspace/elements/{userSmartspace}/{userEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] get (
			@PathVariable(name="userSmartspace", required=true) String userSmartspace,
			@PathVariable(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page,
			@RequestParam(name="search", required=false) String search,
			@RequestParam(name="value", required=false) String value,
			@RequestParam(name="x", required=false) Double x,
			@RequestParam(name="y", required=false) Double y,
			@RequestParam(name="distance", required=false) Double distance) {
		
		if (search != null) {
			
			switch(search) {
			
			case "location":
				if (x != null && y != null && distance != null) {
					System.err.println("Make a search by location with x " + x + ", y " + y + " and distance " + distance);
					return this.elementService
							.getByLocation(new Location(x,y), distance , size , page)
							.stream()
							.map(ElementBoundary::new)
							.collect(Collectors.toList())
							.toArray(new ElementBoundary[0]);
				}
				else {
					System.err.println("Need x, y and distance to search by location, throw a bad request exception.");
				}
				break;
			case "name":
				if (value != null) {
					System.err.println("Make a search by name " + value);
					return this.elementService
							.getByName(value, size, page)
							.stream()
							.map(ElementBoundary::new)
							.collect(Collectors.toList())
							.toArray(new ElementBoundary[0]);
				}
				else {
					System.err.println("No value, throw a bad request exception.");
				}
				break;
			case "type":
				if (value != null) {
					System.err.println("Make a search by type " + value);
					return this.elementService
							.getByType(value, size, page)
							.stream()
							.map(ElementBoundary::new)
							.collect(Collectors.toList())
							.toArray(new ElementBoundary[0]);
				}
				else {
					System.err.println("No value, throw a bad request exception.");
				}
				break;
				default:
					System.err.println("No such search type, need to throw a bad request exception.");
			}
		}
		else {
			System.err.println("Get all elements using pagination.");
			return this.elementService
					.getAll(size, page)
					.stream()
					.map(ElementBoundary::new)
					.collect(Collectors.toList())
					.toArray(new ElementBoundary[0]);
		}
		return null;
	}

	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorMessage handleException (UnAuthorizedException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "message not found";
		}
		return new ErrorMessage(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException (BadElementParametersException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "message not found";
		}
		return new ErrorMessage(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException (BadImportRequestException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "message not found";
		}
		return new ErrorMessage(message);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (ElementNotFoundException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "message not found";
		}
		return new ErrorMessage(message);
	}
	
}

