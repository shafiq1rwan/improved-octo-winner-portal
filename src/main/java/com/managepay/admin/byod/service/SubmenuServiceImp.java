package com.managepay.admin.byod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.Submenu;
import com.managepay.admin.byod.entity.SubmenuCreator;
import com.managepay.admin.byod.repository.SubmenuRepository;

@Service
public class SubmenuServiceImp implements SubmenuService {

	private SubmenuRepository submenuRepo;
	
	@Autowired
	public SubmenuServiceImp(SubmenuRepository submenuRepo) {
		this.submenuRepo = submenuRepo;
	}

	@Override
	public int createSubmenuCreator(SubmenuCreator submenuCreator) {
		try {
			return submenuRepo.createSubmenuCreator(submenuCreator);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editSubmenuCreator(Long id, SubmenuCreator submenuCreator) {
		try {
			return submenuRepo.editSubmenuCreator(id, submenuCreator);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public void removeSubmenuCreator(Long id) {
		try {
			int affectedRow = submenuRepo.removeSubmenuCreator(id);
			if(affectedRow != 0)
				submenuRepo.removeSubmenuBySubmenuCreatorId(id);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int createSubmenu(Submenu submenu) {
		try {
			return submenuRepo.createSubmenu(submenu);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editSubmenu(Long id, Submenu submenu) {
		try {
			return submenuRepo.editSubmenu(id, submenu);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public void removeSubmenu(Long id) {
		try {
			submenuRepo.removeSubmenu(id);
			//Remove relevant table data
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
