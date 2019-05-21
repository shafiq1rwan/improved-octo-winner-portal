package my.com.byod.admin.service;

import java.util.List;

import my.com.byod.admin.entity.Store;

public interface StoreService {

	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId);

	public int editStoreGroupCategoryId(Long groupCategoryId, Long id);
	
	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId);

}
