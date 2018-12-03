package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.ItemGroup;
import com.managepay.admin.byod.entity.ModifierGroup;

public interface ModifierGroupServiceImp {
	
	public List<ModifierGroup> findModifierGroups();

	public ModifierGroup findModifierGroupById(Long id);

	public int createModifierGroup(ModifierGroup modifierGroup);

	public int editModifierGroup(Long id, ModifierGroup modifierGroup);

	public int removeModifierGroup(Long id);

}
