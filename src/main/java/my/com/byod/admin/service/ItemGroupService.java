package my.com.byod.admin.service;

import java.util.List;

import my.com.byod.admin.entity.ItemGroup;

public interface ItemGroupService {

	public List<ItemGroup> findItemGroupByCategoryId(Long categoryId);
	
	public List<ItemGroup> findAllItemGroup();

	public ItemGroup findItemGroupById(Long id);

	public int createItemGroup(ItemGroup itemGroup);

	public int editItemGroup(Long id, ItemGroup itemGroup);

	public void removeItemGroup(Long id);

	public int addCategoryItemGroup(String data);

	public int editCategoryItemGroup(String data);

	public int removeCategoryItemGroup(Long id);
	
	public int removeCategoryItemGroupInBatch(Long categoryId);
}
