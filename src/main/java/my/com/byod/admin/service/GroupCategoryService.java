package my.com.byod.admin.service;

import java.util.List;

import my.com.byod.admin.entity.GroupCategory;

public interface GroupCategoryService {

	public List<GroupCategory> findAllGroupCategory();
	
	public GroupCategory findGroupCategory(Long id);
	
	public String createGroupCategory(GroupCategory groupCategory);
	
	public int editGroupCategory(Long id, GroupCategory groupCategory);
	
	public int removeGroupCategory(Long id);
}
