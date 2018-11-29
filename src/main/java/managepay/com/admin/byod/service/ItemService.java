package managepay.com.admin.byod.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import managepay.com.admin.byod.entity.Category;
import managepay.com.admin.byod.entity.Item;
import managepay.com.admin.byod.entity.ItemGroup;
import managepay.com.admin.byod.entity.Tag;
import managepay.com.admin.byod.repository.CategoryRepository;
import managepay.com.admin.byod.repository.ItemGroupRepository;
import managepay.com.admin.byod.repository.ItemRepository;
import managepay.com.admin.byod.repository.TagRepository;

@Service
public class ItemService implements IItemService {

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
	public int createItem(Item item) {
		try {
			Long generatedItemId = itemRepo.createItem(item);
			// add Tags if available
			if (!item.getTags().isEmpty()) {
				for (Tag tag : item.getTags()) {
					tagRepo.addTagToItem(generatedItemId, tag.getId());
				}
			}
			return 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItem(Long id, Item item, Item existingItem) {
		try {
			// Make comparsion between old and new
			if (!item.getTags().isEmpty()) {

				if (!existingItem.getTags().isEmpty()) {

				}
			}

			return itemRepo.editItem(id, item);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeItem(Long id) {
		try {
			return itemRepo.removeItem(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

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

	@Override
	public int addItemSet(Long itemId, Long itemGroupId) {
		try {

			// return itemRepo.addItemSet(itemId, itemGroupId, sequenceNumber);
			return 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItemSet(Long id, Long itemGroupId) {
		try {

			// return itemRepo.editItemSet(id, itemGroupId, sequenceNumber);
			return 1;
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

}
