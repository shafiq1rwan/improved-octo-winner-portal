package com.managepay.admin.byod.service;

import java.util.List;
import java.util.Set;

import com.managepay.admin.byod.entity.Category;
import com.managepay.admin.byod.entity.Item;
import com.managepay.admin.byod.entity.Tag;

public interface ItemServiceImp {

	public List<Item> findAllItem();

	public Item findItemById(Long id);

	public List<Item> findItemByItemGroupId(Long itemGroupId);

	public int createItem(Item item);

	public int editItem(Long id, Item item, Item existingItem);

	public int removeItem(Long id);

	public List<Tag> findAllTag();

	public Tag findTagById(Long id);

	public int createTag(Tag tag);

	public int editTag(Long id, Tag tag);

	public int removeTag(Long id);

	public int addTagToItem();

	public int addItemSet(String data);

	public int editItemSet(String data);

	public int removeItemSet(Long id);

	public List<Category> findMasterMenu();

	public int addItemIntoItemGroup(String data);

	public int editItemGroupItem(String data);

	public int removeItemGroupItem(Long id);

}
