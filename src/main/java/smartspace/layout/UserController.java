package smartspace.layout;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import smartspace.data.NewUserForm;
import smartspace.logic.UserService;

@RestController
public class UserController {
	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/smartspace")
	public ModelAndView userspost() {
		ModelAndView index = new ModelAndView("index");
		return index;
	}
	
	@GetMapping("/login")
	public ModelAndView login() {
		ModelAndView login = new ModelAndView("login");
		return login;
	}
	
	@GetMapping("/update")
	public ModelAndView update() {
		ModelAndView update = new ModelAndView("update");
		return update;
	}
	
	@GetMapping("/start")
	public ModelAndView start() {
		ModelAndView start = new ModelAndView("start");
		return start;
	}
	
	
	@GetMapping("/smartspace")
	public ModelAndView usersget() {
		ModelAndView index = new ModelAndView("index");
		return index;
	}
	
	@GetMapping("/smartspace/users")
	public ModelAndView signinform() {
		ModelAndView signin = new ModelAndView("signin");
		return signin;	
	}
	
	
	@PostMapping(
			path="/smartspace/users")		
	public ModelAndView registerNewUser(@ModelAttribute("NewUserForm") NewUserForm userForm ) {
		System.out.println(userForm.getRole());
		System.out.println(userForm.getAvatar());
		System.out.println(userForm.getUsername());
		System.out.println(userForm.getEmail());
		this.userService.create(userForm.convertToEntity());
		ModelAndView index = new ModelAndView("index");
		return index;
	}
	

	@RequestMapping(
			method=RequestMethod.GET,
			path="/smartspace/users/login/{userSmartspace}/{userEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail) {
		String key = userEmail + "#" + userSmartspace;
		return new UserBoundary(this.userService.getByKey(key));
	}

	@RequestMapping(
			method=RequestMethod.PUT,
			path="/smartspace/users/login/{userSmartspace}/{userEmail}", 
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail, 
			@RequestBody UserBoundary user) {
		this.userService.update(user.convertToEntity(), userSmartspace, userEmail);
	}
		
	@RequestMapping(
			method=RequestMethod.GET,
			path="/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] export (
			@PathVariable(name ="adminSmartspace" , required=true) String adminSmartspace,
			@PathVariable(name ="adminEmail" , required=true) String adminEmail,
			@RequestParam(name="size", required=false, defaultValue="10") int size, 
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		
		if (!this.userService.validateUserAdminAndSmartspace(adminSmartspace, adminEmail)) {
			throw new UnAuthorizedException("Export and import can only be done by an admin user that belongs to this smartspace.");
		}
		
		return this.userService
				.getAllSorted(size, page, "username")
				.stream()
				.map(UserBoundary::new)
				.collect(Collectors.toList())
				.toArray(new UserBoundary[0]);
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			path="/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] importUsers(
			@PathVariable(name ="adminSmartspace", required=true) String adminSmartspace,
			@PathVariable(name ="adminEmail", required=true) String adminEmail,
			@RequestBody UserBoundary[] users) {
		
		if (!this.userService.validateUserAdminAndSmartspace(adminSmartspace, adminEmail)) {
			throw new UnAuthorizedException("Export and import can only be done by an admin user that belongs to this smartspace.");
		}
		if (users.length != 0 &&
				adminSmartspace.equals(users[0].convertToEntity().getUserSmartspace())) {
			throw new BadImportRequestException("Cannot import elements from the same smartspace.");
		}
		
		return this.userService.importUsers(
					Arrays.stream(users)
						.map(user->user.convertToEntity())
						.collect(Collectors.toList()))
				.stream()
				.map(UserBoundary::new)
				.collect(Collectors.toList())
				.toArray(new UserBoundary[0]);

	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleException (UserNotFoundException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "message not found";
		}
		return new ErrorMessage(message);
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
	public ErrorMessage handleException (BadUserParametersException e) {
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

