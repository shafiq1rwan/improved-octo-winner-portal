package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.GroupCategory;
import com.managepay.admin.byod.entity.Store;
import com.managepay.admin.byod.repository.GroupCategoryRepository;

@Service
public class GroupCategoryServiceImp implements GroupCategoryService {

	private GroupCategoryRepository groupCategoryRepo;
	private StoreService storeService;
	private CategoryService categoryService;

	@Autowired
	public GroupCategoryServiceImp(GroupCategoryRepository groupCategoryRepo, StoreService storeService,
			CategoryService categoryService) {
		this.groupCategoryRepo = groupCategoryRepo;
		this.storeService = storeService;
		this.categoryService = categoryService;
	}
	
	@Override
	public List<GroupCategory> findAllGroupCategory() {
		try {
			return groupCategoryRepo.findAllGroupCategory();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public GroupCategory findGroupCategory(Long id) {
		try {
			return groupCategoryRepo.findGroupCategoryById(id);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return new GroupCategory();
		}
	}

	@Override
	public int createGroupCategory(GroupCategory groupCategory) {
		try {
			List<Store> stores = groupCategory.getStores();
			if(!stores.isEmpty() || stores != null) {
				for(Store store: stores) {
					storeService.editStoreGroupCategoryId(groupCategory.getId(), store.getId());
				}
			}
			return groupCategoryRepo.createGroupCategory(groupCategory);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editGroupCategory(Long id, GroupCategory groupCategory) {
		try {
			return groupCategoryRepo.editGroupCategory(id, groupCategory);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeGroupCategory(Long id) {
		// TODO Not yet done
		try {
			return groupCategoryRepo.removeGroupCategory(id);
			//set store group c id = 0;
			//set category group c id = 0;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
