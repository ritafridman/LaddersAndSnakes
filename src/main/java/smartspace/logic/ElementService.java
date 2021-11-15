package smartspace.logic;

import java.util.List;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.layout.ActionBoundary;

public interface ElementService {

	public List<ElementEntity> getAll(int size, int page);
	public List<ElementEntity> getAllSorted(int size, int page, String sortBy);
	public ElementEntity create (ElementEntity element, String managerSmartspace, String managerEmail);
	public void update (String elementId, ElementEntity element, String elementSmartspace, 
			String managerSmartspace, String managerEmail);
	public List<ElementEntity> importElements (List<ElementEntity> elements);
	public List<ElementEntity> getByType(String type, int size, int page);
	public ElementEntity getByKey(String userSmartspace, String userEmail, String elementSmartspace, String elementId);
	public List<ElementEntity> getByName(String name, int size, int page);
	public boolean validate(ElementEntity element, boolean isNew);
	public boolean validateElementByAction(ActionBoundary[] actions);
	public List<ElementEntity> getByLocation(Location location, double distance , int size, int page);
	public void update(String elementId, ElementEntity element, String elementSmartspace);
}
