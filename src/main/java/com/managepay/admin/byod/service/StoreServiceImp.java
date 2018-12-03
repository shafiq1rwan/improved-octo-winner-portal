package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.Store;

public interface StoreServiceImp {

	public Store findStoreById(Long id);

	public List<Store> findAllStore();

	public int createStore(Store store);

	public int editStore(Long id, Store store);

	public int removeStore(Long id);

}
