package managepay.com.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import managepay.com.admin.byod.entity.ModifierGroup;
import managepay.com.admin.byod.repository.ModifierGroupRepository;

@Service
public class ModifierGroupService implements IModifierGroupService {

	private ModifierGroupRepository modifierGroupRepo;
	
	@Autowired
	public ModifierGroupService(ModifierGroupRepository modifierGroupRepo) {
		this.modifierGroupRepo = modifierGroupRepo;
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
			return modifierGroupRepo.removeModifierGroup(id);			
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
