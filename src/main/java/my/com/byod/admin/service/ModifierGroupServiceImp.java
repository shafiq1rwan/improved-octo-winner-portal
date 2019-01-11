package my.com.byod.admin.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import my.com.byod.admin.entity.ModifierGroup;
import my.com.byod.admin.repository.MenuItemRepository;
import my.com.byod.admin.repository.ModifierGroupRepository;

@Service
public class ModifierGroupServiceImp implements ModifierGroupService {

	private ModifierGroupRepository modifierGroupRepo;
	private MenuItemRepository itemRepo;
	
	@Autowired
	public ModifierGroupServiceImp(ModifierGroupRepository modifierGroupRepo,MenuItemRepository itemRepo) {
		this.modifierGroupRepo = modifierGroupRepo;
		this.itemRepo = itemRepo;
	}

	@Override
	public List<ModifierGroup> findModifierGroups() {
		try {
			return modifierGroupRepo.findModifierGroups();
		} catch(Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public ModifierGroup findModifierGroupById(Long id) {
		try {
			return modifierGroupRepo.findModifierGroupById(id);
		} catch(Exception ex) {
			ex.printStackTrace();
			return new ModifierGroup();
		}
	}

	@Override
	public int createModifierGroup(ModifierGroup modifierGroup) {
		try {
			return modifierGroupRepo.createModifierGroup(modifierGroup);			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		
	}

	@Override
	public int editModifierGroup(Long id, ModifierGroup modifierGroup) {
		try {
			return modifierGroupRepo.editModifierGroup(id, modifierGroup);			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeModifierGroup(Long id) {
		try {
			int affectedRow = modifierGroupRepo.removeModifierGroup(id);
			itemRepo.removeItemModifierGroupId(id);
			//Remember to remove the item modifier group
			return affectedRow;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
