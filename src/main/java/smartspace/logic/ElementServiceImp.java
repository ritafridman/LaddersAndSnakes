package smartspace.logic;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.BadElementParametersException;
import smartspace.layout.ActionBoundary;
import smartspace.layout.BadImportRequestException;
import smartspace.layout.UnAuthorizedException;
import smartspace.layout.UserNotFoundException;

@Service
public class ElementServiceImp implements ElementService {

	private EnhancedElementDao<String> elementDao;
	private UserService userService;
	
	@Value("${smartspace.name}")
	private String localSmartspaceName;
	
	private final String[] elementTypes = { "LadderSnake", "Tile", "Board", "Springboard","Cell" };
	@Autowired
	public ElementServiceImp(EnhancedElementDao<String> elementDao, UserService userService) {
		this.elementDao = elementDao;
		this.userService = userService;
	}

	@Override
	public List<ElementEntity> getAll(int size, int page) {
		return this.elementDao
				.readAll(size, page);
	}

	@Override
	@Transactional
	public ElementEntity create(ElementEntity element, String managerSmartspace, String managerEmail) {
		System.err.println("In ElementEntity create: " +element.toString());
		UserEntity user;
		String managerKey = managerEmail + "#" + managerSmartspace;
		
		try {
			user = this.userService.getByKey(managerKey);
		}
		catch(UserNotFoundException e) {
			throw new UnAuthorizedException("Not authorized to create element entities.");
		}
		
		if (user.getRole() != UserRole.MANAGER) {
			throw new UnAuthorizedException("Only managers are authorized to create element entities.");
		}
		else if (!validate(element, true)) {
			throw new BadElementParametersException("Bad element parameters.");
		}
		
		element.setElementSmartspace(this.localSmartspaceName);
		element.setCreationTimestamp(new Date());
		element.setCreatorEmail(managerEmail);
		element.setCreatorSmartspace(managerSmartspace);
		
		return this.elementDao.create(element);
	}

	@Override
	@Transactional
	public List<ElementEntity> importElements(List<ElementEntity> elements) {
		elements
			.forEach(element-> {
				if (!validateImportedElement(element) || element.getElementSmartspace().equals(localSmartspaceName)) {
					throw new BadImportRequestException("Invalid element input or same smartspace.");
				}
			});

		return this.elementDao.createBatch(elements);
	}
	
	private boolean validateImportedElement(ElementEntity element) {

		return element.getElementId() != null &&
				element.getElementSmartspace() != null &&
				element.getType() != null &&
				element.getCreatorEmail() != null;
	}

	@Override
	public List<ElementEntity> getByType(String type, int size, int page) {
		return this.elementDao
				.readByType(type, size, page);
	}

	@Override
	public ElementEntity getByKey(String userSmartspace, String userEmail, String elementSmartspace, String elementId) {
		System.err.println("ElementEntity getByKey");
		System.err.println("who getByKey is:" );
		UserEntity user;
		String userKey = userEmail + "#" + userSmartspace;
		

			user = this.userService.getByKey(userKey);
	
	
		
		if (user.getRole() == UserRole.ADMIN) {
			System.err.println("user.getRole() :"+user.getRole() );
			throw new UnAuthorizedException("Admins are not authorized to request element entities.");
		}

		String elementKey = elementId + "#" + elementSmartspace;
		
		return this.elementDao.readByKeyAndRole(elementKey, user.getRole(), 1, 0);
	}

	@Override
	public void update(String elementId, ElementEntity element, String elementSmartspace, 
			String managerSmartspace, String managerEmail) {
		
		UserEntity user;
		String managerKey = managerEmail + "#" + managerSmartspace;
		
		try {
			user = this.userService.getByKey(managerKey);
		}
		catch(UserNotFoundException e) {
			throw new UnAuthorizedException("Not authorized to update element entities.");
		}
		
		if (user.getRole() != UserRole.MANAGER) {
			throw new UnAuthorizedException("Only managers are authorized to update element entities.");
		}
		else if (!validate(element, false)) {
			throw new BadElementParametersException("Bad element parameters.");
		}
		
		String elementKey = elementId + "#" + elementSmartspace;
		element.setKey(elementKey);
		
		this.elementDao.update(element);
	}

	
	@Override
	public void update(String elementId, ElementEntity element, String elementSmartspace) {
		
	
		
	
		if (!validate(element, false)) {
			throw new BadElementParametersException("Bad element parameters.");
		}
		
		String elementKey = elementId + "#" + elementSmartspace;
		element.setKey(elementKey);
		
		this.elementDao.update(element);
	}

	@Override
	public boolean validate(ElementEntity element, boolean isNew) {
		
		if (isNew) {
			return element.getType() != null && Arrays.asList(this.elementTypes).contains(element.getType()) &&
					element.getName() != null &&
					element.getLocation() != null;
		}
		//	Else, if its an update
			
		//	If update has type, validate it
		if (element.getType() != null && !Arrays.asList(this.elementTypes).contains(element.getType())) {
			return false;
		}
		
		//	TODO: add more field validations, if needed.
			
		return true;
	}
	
	public boolean validateElementByAction(ActionBoundary[] actions) {
	
	
		Arrays.stream(actions).forEach(action-> {
			boolean exist=validateElement(action);
			if (!exist) {
				throw new BadImportRequestException("Their is an action with no Element");
			}
		});
		return true;
	}

	public boolean validateElement(ActionBoundary action) {

		int size = 5;
		int page = 0;
		boolean exist = false;

		List<ElementEntity> elementsList;

		do {
			//	Get all elements by smartspace.
			elementsList = this.elementDao.readByKey(
					action.getElement().get("id")+"#"+
							action.getElement().get("smartspace"), size, page++);

			//	Iterate over the elements and filter by id, then check count.
			if (elementsList.size() > 0) {
				exist = elementsList.stream()
						.filter(x->x.getKey().equals(
								action.getElement().get("id")+"#"+
										action.getElement().get("smartspace")))
						.count() > 0;

						if (exist) {
							return true;
						}
			}

		} while(elementsList.size() == size);

		return false;
	}

	@Override
	public List<ElementEntity> getByName(String name, int size, int page) {
		return this.elementDao
				.readByName(name, size, page);
	}

	@Override
	public List<ElementEntity> getAllSorted(int size, int page, String sortBy) {
		return this.elementDao
				.readAll(size, page, sortBy);
	}

	@Override
	public List<ElementEntity> getByLocation(Location location, double distance , int size, int page) {
		return this.elementDao.readByLocation(location, distance , size, page);
	}
	
}
