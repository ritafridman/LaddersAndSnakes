package smartspace.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.layout.BadImportRequestException;
import smartspace.layout.NotExcepetPluginNameException;
import smartspace.layout.NotExistPluginNameException;
import smartspace.plugins.PluginCommand;

@Service
public class ActionServiceImp implements ActionService {
	private ApplicationContext ctx;
	private EnhancedActionDao<String> actionDao;
	private ApplicationContext spring;
	private ObjectMapper mapper;
	private final String[] actionTypes = { "CheckIn", "CheckOut", "RollCube", "ChangeCellSnakeLadder","trampolineSkipCellsByRolledNumber","SnakeLadderGoBack4Cells","trampolineSkipToRolledCell(1-100)","Echo"};
	
	
	@Autowired
	public void setApplicationContext(ApplicationContext spring) {
		this.spring = spring;
	}

	@Autowired
	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Value("${smartspace.name}")
	private String localSmartspaceName;

	@Autowired
	public ActionServiceImp(EnhancedActionDao<String> actionDao, 
			ApplicationContext ctx) {
		this.actionDao = actionDao;
		this.ctx = ctx;
		
	}

	@Override
	public List<ActionEntity> getAll(int size, int page) {
		return this.actionDao.readAll(size, page);
	}

//	@Override
//	@Transactional
//	public ActionEntity store(ActionEntity action) {
//		if (validate(action)) {
//			action.setCreationTimestamp(new Date());
//			action.setElementId(action.getElementId());
//			return this.actionDao
//					.create(action);
//		} else
//			throw new RuntimeException("Invalid action input");
//	}

	@Override
	@Transactional
	public List<ActionEntity> importActions(List<ActionEntity> actions) {

		actions.forEach(action -> {
			if (!validate(action) || action.getActionSmartspace().equals(localSmartspaceName)) {
				throw new BadImportRequestException("Invalid action input or bad smartspace.");
			}
		});

		return this.actionDao.createBatch(actions);
	}

	private boolean validate(ActionEntity action) {
		return /* action.getActionId() != null && */
		action.getActionSmartspace() != null && action.getActionType() != null && action.getPlayerEmail() != null
				&& action.getPlayerSmartspace() != null;
	}

	@Override
	public List<ActionEntity> getByType(String type, int size, int page) {
		return this.actionDao.readByType(type, size, page);
	}

	@Override
	public List<ActionEntity> getAllSorted(int size, int page, String sortBy) {
		return this.actionDao.readAll(size, page, sortBy);
	}

	@Override
	@Transactional
	public ActionEntity echo(ActionEntity action)  {
//		if (validate(action)) {
		action.setCreationTimestamp(new Date());
		action.setElementId(action.getElementId());
		System.err.println("DO action");
		addAction(action);
		System.err.println("FINISH action");
		System.err.println("THE action:"+action);
		return action;
//		} else
//			throw new RuntimeException("Invalid action input");
	}

	@Override
	public ActionEntity addAction(ActionEntity action) {

		
		Object activity;
		try {
			Class<?> theClass;
		
			String type = action.getActionType();
			System.err.println("BEFORE NotExistPluginNameException()");
			if(!validateType(type)) {
				action.getMoreAttributes().put("error", "unvalid action");
				this.actionDao.create(action);
				return action;
			}
				
			System.err.println("AFTER NotExistPluginNameException()");
			String className = "smartspace.plugins." + type + "Plugin";

			
			theClass = Class.forName(className);
			
			
			PluginCommand plugin = (PluginCommand) this.spring.getBean(theClass);
			activity = plugin.execute(action);

			Map<String, Object> rvMap = this.mapper.readValue(this.mapper.writeValueAsString(activity), Map.class);

			action.getMoreAttributes().putAll(rvMap);

			this.actionDao.create(action);
System.err.println("Action before return: "+action);
			return action;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return action;

	}
	
	
	@Override
	public boolean validateType(String type) {
		boolean valid=false;
		for (int i = 0; i < actionTypes.length; i++) {
			if (type.equals(actionTypes[i])) {
				valid=true;
				break;
			}
		}
		return valid;
	}

}
