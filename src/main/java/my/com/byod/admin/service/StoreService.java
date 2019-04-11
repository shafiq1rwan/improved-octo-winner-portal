package my.com.byod.admin.service;

import java.util.List;

import my.com.byod.admin.entity.Store;

public interface StoreService {

	public Store findStoreById(Long id);
	
	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId);

	public List<Store> findAllStore();

	public int createStore(Store store, String brandId);

	public int editStore(Long id, Store store, String brandId, String existingLogoPath);
	
	public int editStoreGroupCategoryId(Long groupCategoryId, Long id);
	
	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId);

	public int removeStore(Long id);

}
