package smartspace.layout;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import smartspace.data.ActionEntity;
import smartspace.logic.ActionService;
import smartspace.logic.ElementService;
import smartspace.logic.UserService;

@RestController
public class ActionController {
	private ActionService actionService;
	private UserService userService;
	private ElementService elementService;

	@Autowired
	public ActionController(ActionService actionService, UserService userService
			, ElementService elemantService) {
		this.userService = userService;
		this.actionService = actionService;
		this.elementService = elemantService;
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/smartspace/admin/actions/{adminSmartspace}/{adminEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] export (
			@PathVariable(name ="adminSmartspace" , required=true) String adminSmartspace,
			@PathVariable(name ="adminEmail" , required=true) String adminEmail,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		
		if (!this.userService.validateUserAdminAndSmartspace(adminSmartspace, adminEmail)) {
			throw new UnAuthorizedException("Export and import can only be done by an admin user that belongs to this smartspace.");
		}
		
		return this.actionService
				.getAllSorted(size, page, "actionId")
				.stream()
				.map(ActionBoundary::new)
				.collect(Collectors.toList())
				.toArray(new ActionBoundary[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/smartspace/admin/actions/{adminSmartspace}/{adminEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] importActions(
			@PathVariable(name="adminSmartspace", required=true) String adminSmartspace,
			@PathVariable(name="adminEmail", required=true) String adminEmail,
			@RequestBody ActionBoundary[] actions) {
		
		if (!this.userService.validateUserAdminAndSmartspace(adminSmartspace, adminEmail)) {
			throw new UnAuthorizedException("Export and import can only be done by an admin user that belongs to this smartspace.");
		}
		if (actions.length != 0 &&
				adminSmartspace.equals(actions[0].convertToEntity().getActionSmartspace())) {
			throw new BadImportRequestException("Cannot import elements from the same smartspace.");
		}
		
		if(!this.elementService.validateElementByAction(actions)){
			throw new BadImportRequestException("Can not import actions without an element");		
		}
		
		
		return this.actionService.importActions(
				Arrays.stream(actions)
					.map(action->action.convertToEntity())
					.collect(Collectors.toList()))
			.stream()
			.map(ActionBoundary::new)
			.collect(Collectors.toList())
			.toArray(new ActionBoundary[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.GET,
			path="/smartspace/actions",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] getAll (
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		return this.actionService
			.getAll(size, page)
			.stream()
			.map(ActionBoundary::new)
			.collect(Collectors.toList())
			.toArray(new ActionBoundary[0]);
	}
	
	
//	@RequestMapping(
//			method=RequestMethod.POST,
//			path="/smartspace/actions",
//			produces=MediaType.APPLICATION_JSON_VALUE,
//			consumes=MediaType.APPLICATION_JSON_VALUE)
//	public Object store (
//			@RequestBody ActionBoundary action) {
//				System.err.println("action in control:" +action);
//		return  this.actionService
//					.addAction(action.convertToEntity());
//							
	
	
//	}

	@RequestMapping(
			method=RequestMethod.POST,
			path="/smartspace/actions",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary echo(
			@RequestBody ActionBoundary action) {
		System.err.println("action in control:" +action);
		return new ActionBoundary(
						this.actionService.echo(action.convertToEntity()));
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
	public ErrorMessage handleException (BadImportRequestException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "message not found";
		}
		return new ErrorMessage(message);
	}
	
}

