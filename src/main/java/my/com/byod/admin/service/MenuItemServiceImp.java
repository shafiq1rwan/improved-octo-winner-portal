package my.com.byod.admin.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import my.com.byod.admin.entity.Category;
import my.com.byod.admin.entity.ItemGroup;
import my.com.byod.admin.entity.MenuItem;
import my.com.byod.admin.entity.ModifierGroup;
import my.com.byod.admin.entity.Tag;
import my.com.byod.admin.repository.CategoryRepository;
import my.com.byod.admin.repository.ItemGroupRepository;
import my.com.byod.admin.repository.MenuItemRepository;
import my.com.byod.admin.repository.ModifierGroupRepository;
import my.com.byod.admin.repository.TagRepository;

@Service
public class MenuItemServiceImp implements MenuItemService {

	private MenuItemRepository menuItemRepo;

	public MenuItemServiceImp(MenuItemRepository menuItemRepo) {
		this.menuItemRepo = menuItemRepo;
	}
	
	public List<MenuItem> findAllMenuItem(){
		try {
			return menuItemRepo.findAllMenuItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public MenuItem findMenuItemById(Long id) {
		try {
			return menuItemRepo.findMenuItemById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new MenuItem();
		}
	}
	
/*	public List<MenuItem> findMenuItemByCategoryId(){
		
	}*/


	
	
/*
	@Override
	public List<MenuItem> findAllItem() {
		try {
			return itemRepo.findAllItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public MenuItem findItemById(Long id) {
		try {
			return itemRepo.findItemById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new MenuItem();
		}
	}

	@Override
	public List<MenuItem> findItemByItemGroupId(Long itemGroupId) {
		try {
			return itemRepo.findItemByItemGroupId(itemGroupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public List<MenuItem> findItemByModifierGroupId(Long modifiergroupId) {
		try {
			return itemRepo.findItemByModifierGroupId(modifiergroupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public int createItem(MenuItem item) {
		try {
			Long generatedItemId = itemRepo.createItem(item);
			// add Tags if available
			if (!item.getTags().isEmpty()) {
				for (Tag tag : item.getTags()) {
					tagRepo.addTagToItem(generatedItemId, tag.getId());
				}
			}
			// add ModifierGroup(s) into Item
			if (!item.getModifierGroups().isEmpty()) {
				for (ModifierGroup modifierGroup : item.getModifierGroups()) {
					itemRepo.addModifierItemIntoModifierGroup(generatedItemId, modifierGroup.getId());
				}
			}
			return 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItem(Long id, MenuItem item, MenuItem existingItem) {
		try {
			int affectedRow = itemRepo.editItem(id, existingItem);

			if (affectedRow != 0) {

			}

			// query the tag repo

			// query item category group
			if (!existingItem.getModifierGroups().isEmpty()) {
				for (ModifierGroup modifierGroup : existingItem.getModifierGroups()) {
					Map<String, Object> modifierGroupMap = modifierGroupRepo
							.findModifierGroupByItemId(existingItem.getId(), modifierGroup.getId());
					if(!modifierGroupMap.isEmpty()) {
						
						//Deletion
						
						
					} else {
						//Insert into Group
						itemRepo.addModifierItemIntoModifierGroup(existingItem.getId(), modifierGroup.getId());
					}

				}

			}

			List<Map<String, Object>> itemModifierGroupMaps = findModifierGroupByItemId(existingItem.getId());
			if (!itemModifierGroupMaps.isEmpty()) {
				for (Map<String, Object> itemModifierGroupMap : itemModifierGroupMaps) {
					int modifierGroupId = (int) itemModifierGroupMap.get("modifier_group_id");

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeItem(Long id) {
		try {
			int affectedRow = itemRepo.removeItem(id);
			if (affectedRow != 0) {
				itemRepo.removeItemSetByItemId(id);
				itemRepo.removeItemGroupItemByItemId(id);
				itemRepo.removeItemModifierGroupByItemId(id);
			}
			return affectedRow;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	// Tag
	@Override
	public List<Tag> findAllTag() {
		try {
			return tagRepo.findAllTag();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public Tag findTagById(Long id) {
		try {
			return tagRepo.findTagById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Tag();
		}
	}

	@Override
	public int createTag(Tag tag) {
		try {
			return tagRepo.createTag(tag);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editTag(Long id, Tag tag) {
		try {
			return tagRepo.editTag(id, tag);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeTag(Long id) {
		try {
			return tagRepo.removeTag(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	// ItemSet
	@Override
	public List<ItemGroup> findItemGroupByItemSetItemId(Long itemId) {
		try {
			List<ItemGroup> itemGroupList = itemRepo.findItemGroupByItemSetItemId(itemId);
			// add item
			if (itemGroupList != null) {
				for (ItemGroup itemGroup : itemGroupList) {
					List<MenuItem> itemList = findItemByItemGroupId(itemGroup.getId());
					if (!itemList.isEmpty())
						itemGroup.setItems(itemList);
				}
			}
			return itemGroupList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public int addItemSet(String data) {
		try {
			JSONObject jsonObj = new JSONObject(data);
			Long itemId = jsonObj.getLong("itemId");
			Long itemGroupId = jsonObj.getLong("itemGroupId");

			int sequenceNumber = findItemSetSequence(itemId);
			return itemRepo.addItemSet(itemId, itemGroupId, ++sequenceNumber);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItemSet(String data) {
		try {

			JSONArray jsonArray = new JSONArray(data);
			int totalRowAffected = 0;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				Long itemSetId = jsonObj.getLong("id");
				int sequenceNumber = jsonObj.getInt("sequence");
				totalRowAffected += itemRepo.editItemSet(itemSetId, sequenceNumber);
			}
			return totalRowAffected;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeItemSet(Long id) {
		try {
			return itemRepo.removeItemSet(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int addTagToItem() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Category> findMasterMenu() {
		try {
			List<Category> categoryList = categoryRepo.findAllCategory();

			for (Category category : categoryList) {
				List<ItemGroup> itemGroupList = itemGroupRepo.findItemGroupByCategoryId(category.getId());
				for (ItemGroup itemGroup : itemGroupList) {
					List<MenuItem> itemList = itemRepo.findItemByItemGroupId(itemGroup.getId());
					for (MenuItem item : itemList) {
						List<ItemGroup> itemSets = itemRepo.findItemGroupByItemSetItemId(item.getId());
						if (itemSets != null)
							item.setItemSets(itemSets);
					}
					itemGroup.setItems(itemList);
				}
				category.setItemGroups(itemGroupList);
			}
			return categoryList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	// ItemGroupItem
	@Override
	public int addItemIntoItemGroup(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			Long itemId = jsonData.getLong("itemId");
			Long itemGroupId = jsonData.getLong("itemGroupId");
			int sequenceNumber = findItemGroupItemSequence(itemGroupId);
			return itemRepo.addItemIntoItemGroup(itemId, itemGroupId, ++sequenceNumber);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItemGroupItem(String data) {
		try {
			JSONArray jsonArray = new JSONArray(data);
			int totalRowAffected = 0;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				Long itemGroupItemId = jsonObj.getLong("id");
				int sequenceNumber = jsonObj.getInt("sequence");
				totalRowAffected += itemRepo.editItemIntoItemGroup(itemGroupItemId, sequenceNumber);
			}
			return totalRowAffected;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeItemGroupItem(Long id) {
		try {
			return itemRepo.removeItemGroupItem(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private int findItemGroupItemSequence(Long itemGroupId) {
		try {
			return itemRepo.getItemGroupItemSequence(itemGroupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private int findItemSetSequence(Long itemId) {
		try {
			return itemRepo.findItemSetSequence(itemId);
		} catch (Exception ex) {
			return 0;
		}
	}

	private int removeItemGroupItemInBatch(Long itemGroupId) {
		try {
			return itemRepo.removeItemGroupItemByItemGroupId(itemGroupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private Map<String, Object> findModifierGroupByItemId(Long itemId, Long modifierGroupId) {
		try {
			return modifierGroupRepo.findModifierGroupByItemId(itemId, modifierGroupId);
		} catch (Exception ex) {
			return Collections.emptyMap();
		}
	}*/

}
