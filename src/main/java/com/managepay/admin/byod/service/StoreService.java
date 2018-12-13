package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.Store;

public interface StoreService {

	public Store findStoreById(Long id);
	
	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId);

	public List<Store> findAllStore();

	public int createStore(Store store);

	public int editStore(Long id, Store store);
	
	public int editStoreGroupCategoryId(Long groupCategoryId, Long id);
	
	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId);

	public int removeStore(Long id);

}
