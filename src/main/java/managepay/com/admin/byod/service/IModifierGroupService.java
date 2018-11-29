package managepay.com.admin.byod.service;

import java.util.List;

import managepay.com.admin.byod.entity.ItemGroup;
import managepay.com.admin.byod.entity.ModifierGroup;

public interface IModifierGroupService {
	
	public List<ModifierGroup> findModifierGroups();

	public ModifierGroup findModifierGroupById(Long id);

	public int createModifierGroup(ModifierGroup modifierGroup);

	public int editModifierGroup(Long id, ModifierGroup modifierGroup);

	public int removeModifierGroup(Long id);

}
