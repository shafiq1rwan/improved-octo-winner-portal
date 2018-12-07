package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.GroupCategory;

public interface GroupCategoryService {

	public List<GroupCategory> findAllGroupCategory();
	
	public GroupCategory findGroupCategory(Long id);
	
	public int createGroupCategory(GroupCategory groupCategory);
	
	public int editGroupCategory(Long id, GroupCategory groupCategory);
	
	public int removeGroupCategory(Long id);
}
