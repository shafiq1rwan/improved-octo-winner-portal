package com.managepay.admin.byod.service;

import com.managepay.admin.byod.entity.Submenu;
import com.managepay.admin.byod.entity.SubmenuCreator;

public interface SubmenuService {

	public int createSubmenuCreator(SubmenuCreator submenuCreator);
	public int editSubmenuCreator(Long id, SubmenuCreator submenuCreator);
	public void removeSubmenuCreator(Long id);
	
	public int createSubmenu(Submenu submenu);
	public int editSubmenu(Long id, Submenu submenu);
	public void removeSubmenu(Long id);
	
}
