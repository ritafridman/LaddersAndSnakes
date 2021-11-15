package smartspace.logic;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import smartspace.data.ActionEntity;
import smartspace.layout.ActionBoundary;
import smartspace.layout.NotExistPluginNameException;

public interface ActionService {
	public List<ActionEntity> getAll(int size, int page);
//	public ActionEntity store (ActionEntity action);
	public List<ActionEntity> importActions (List<ActionEntity> actions);
	public List<ActionEntity> getByType(String type, int size, int page);
	public List<ActionEntity> getAllSorted(int size, int page, String sortBy);
	public ActionEntity echo(ActionEntity action) ;
	public ActionEntity addAction(ActionEntity action);
	boolean validateType(String type);
}
