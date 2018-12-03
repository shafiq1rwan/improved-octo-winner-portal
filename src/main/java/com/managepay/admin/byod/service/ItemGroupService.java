package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.CategoryItemGroup;
import com.managepay.admin.byod.entity.ItemGroup;
import com.managepay.admin.byod.repository.ItemGroupRepository;

@Service
public class ItemGroupService implements ItemGroupServiceImp {

	private ItemGroupRepository itemGroupRepo;

	public ItemGroupService(ItemGroupRepository itemGroupRepo) {
		this.itemGroupRepo = itemGroupRepo;
	}

	@Override
	public List<ItemGroup> findItemGroupByCategoryId(Long categoryId) {
		try {
			return itemGroupRepo.findItemGroupByCategoryId(categoryId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public List<ItemGroup> findAllItemGroup() {
		try {
			return itemGroupRepo.findAllItemGroup();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public ItemGroup findItemGroupById(Long id) {
		try {
			return itemGroupRepo.findItemGroupById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ItemGroup();
		}
	}

	@Override
	public int createItemGroup(ItemGroup itemGroup) {
		try {
			return itemGroupRepo.createItemGroup(itemGroup);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editItemGroup(Long id, ItemGroup itemGroup) {
		try {
			return itemGroupRepo.editItemGroup(id, itemGroup);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public void removeItemGroup(Long id) {
		try {
			int affectedRow = itemGroupRepo.removeItemGroup(id);
			if (affectedRow != 0) {
				List<CategoryItemGroup> categoryItemGroupList = findCategoryItemGroupByItemGroupId(id);
				if (!categoryItemGroupList.isEmpty()) {
					for (CategoryItemGroup categoryItemGroup : categoryItemGroupList) {
						removeCategoryItemGroup(categoryItemGroup.getId());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int addCategoryItemGroup(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			Long categoryId = jsonData.getLong("categoryId");
			Long itemGroupId = jsonData.getLong("itemGroupId");
			
			int sequenceNumber = checkSequenceNumber(categoryId);
			return itemGroupRepo.addCategoryItemGroup(itemGroupId, categoryId, ++sequenceNumber);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editCategoryItemGroup(String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			Long categoryId = jsonData.getLong("categoryId");
			removeCategoryItemGroupInBatch(categoryId); //Perform Deletion(s) before Insertion
			
			JSONArray jsonArray = jsonData.getJSONArray("itemGroups");
			int sequenceNumber = 1;
			int totalRowAffected = 0;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				Long itemGroupId = jsonObj.getLong("itemGroupId");
				totalRowAffected += itemGroupRepo.addCategoryItemGroup(itemGroupId, categoryId, sequenceNumber++);
			}
			return totalRowAffected;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeCategoryItemGroup(Long id) {
		try {
			return itemGroupRepo.removeCategoryItemGroup(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeCategoryItemGroupInBatch(Long categoryId) {
		try {
			return itemGroupRepo.removeCategoryItemGroupByCategoryId(categoryId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private int checkSequenceNumber(Long categoryId) {
		try {
			return itemGroupRepo.getCategoryItemGroupSequence(categoryId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private List<CategoryItemGroup> findCategoryItemGroupByItemGroupId(Long id) {
		try {
			return itemGroupRepo.findCategoryItemGroupByItemGroupId(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

}
