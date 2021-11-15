package smartspace.plugins;

import smartspace.data.ActionEntity;

public interface PluginCommand {
	public Object execute (ActionEntity message);
}