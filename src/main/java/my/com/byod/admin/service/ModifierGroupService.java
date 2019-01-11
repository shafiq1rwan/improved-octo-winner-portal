package my.com.byod.admin.service;

import java.util.List;

import my.com.byod.admin.entity.ItemGroup;
import my.com.byod.admin.entity.ModifierGroup;

public interface ModifierGroupService {
	
	public List<ModifierGroup> findModifierGroups();

	public ModifierGroup findModifierGroupById(Long id);

	public int createModifierGroup(ModifierGroup modifierGroup);

	public int editModifierGroup(Long id, ModifierGroup modifierGroup);

	public int removeModifierGroup(Long id);

}
