package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
	public String createGroupCategory(GroupCategory groupCategory) {
		
		JSONObject jsonObj = new JSONObject();
		try {
			
			int duplicateNameCount = checkDuplicationGroupCategoryName(groupCategory.getName());
			if(duplicateNameCount == 1) {
				jsonObj.put("responseCode", "01");
				jsonObj.put("responseMessage", "Duplication Found");
			}
			else {
				Long generatedId = groupCategoryRepo.createGroupCategory(groupCategory);
				System.out.println("generated Id :" + generatedId);
				
				
				if(generatedId != 0) {
					List<Store> stores = groupCategory.getStores();
					if(!stores.isEmpty() || stores != null) {
						for(Store store: stores) {
							System.out.println("store: " + store.getId());
							storeService.editStoreGroupCategoryId(generatedId, store.getId());
						}
					}
					
					jsonObj.put("responseCode", "00");
					jsonObj.put("responseMessage", "Success");
				} else {
					jsonObj.put("responseCode", "02");
					jsonObj.put("responseMessage", "Fail to Create Group");
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return jsonObj.toString();	
	}
	
	@Override
	public int editGroupCategory(Long id, GroupCategory groupCategory) {

		try {
			return groupCategoryRepo.editGroupCategory(id, groupCategory);
		}
		catch(DuplicateKeyException ex) {
			ex.printStackTrace();
			throw new DuplicateKeyException("Duplication Found");
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
			int affectedRow = groupCategoryRepo.removeGroupCategory(id);
			storeService.editStoreGroupCategoryIdInBatch(id);
			
			//set category group c id = 0;
			return affectedRow;
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	private int checkDuplicationGroupCategoryName(String name) {
		try {
			return groupCategoryRepo.checkGroupCategoryNameDuplication(name);
		}
		catch(DataAccessException ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	

}
