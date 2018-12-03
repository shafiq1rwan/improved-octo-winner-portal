package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.Category;
import com.managepay.admin.byod.entity.Item;
import com.managepay.admin.byod.entity.ItemGroup;
import com.managepay.admin.byod.entity.Tag;
import com.managepay.admin.byod.repository.CategoryRepository;
import com.managepay.admin.byod.repository.ItemGroupRepository;
import com.managepay.admin.byod.repository.ItemRepository;
import com.managepay.admin.byod.repository.TagRepository;

@Service
public class ItemService implements ItemServiceImp {

	private CategoryRepository categoryRepo;
	private ItemGroupRepository itemGroupRepo;
	private ItemRepository itemRepo;
	private TagRepository tagRepo;

	@Autowired
	public ItemService(CategoryRepository categoryRepo, ItemGroupRepository itemGroupRepo, ItemRepository itemRepo,
			TagRepository tagRepo) {
		this.categoryRepo = categoryRepo;
		this.itemGroupRepo = itemGroupRepo;
		this.itemRepo = itemRepo;
		this.tagRepo = tagRepo;
	}

	@Override
	public List<Item> findAllItem() {
		try {
			return itemRepo.findAllItem();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public Item findItemById(Long id) {
		try {
			return itemRepo.findItemById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Item();
		}
	}

	@Override
	public List<Item> findItemByItemGroupId(Long itemGroupId) {
		try {
			return itemRepo.findItemByItemGroupId(itemGroupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public int createItem(Item item) {
		try {
			Long generatedItemId = itemRepo.createItem(item);
			// add Tags if available
			/*
			 * if (!item.getTags().isEmpty()) { for (Tag tag : item.getTags()) {
			 * tagRepo.addTagToItem(generatedItemId, tag.getId()); } }
			 */
			return 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItem(Long id, Item item, Item existingItem) {
		try {
			return itemRepo.editItem(id, existingItem);
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
					List<Item> itemList = itemRepo.findItemByItemGroupId(itemGroup.getId());
					for (Item item : itemList) {
						List<ItemGroup> itemSets = itemRepo.findItemGroupFromItemSet(item.getId());
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

}
