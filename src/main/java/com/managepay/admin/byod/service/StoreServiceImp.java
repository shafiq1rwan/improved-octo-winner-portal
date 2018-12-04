package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.Store;
import com.managepay.admin.byod.repository.StoreRepository;
import com.managepay.admin.byod.repository.SubmenuRepository;

@Service
public class StoreServiceImp implements StoreService {

	private StoreRepository storeRepo;
	private SubmenuRepository submenuRepo;

	@Autowired
	public StoreServiceImp(StoreRepository storeRepo,SubmenuRepository submenuRepo) {
		this.storeRepo = storeRepo;
		this.submenuRepo = submenuRepo;
	}

	@Override
	public Store findStoreById(Long id) {
		try {
			return storeRepo.findStoreById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Store();
		}
	}

	@Override
	public List<Store> findAllStore() {
		try {
			return storeRepo.findAllStore();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public int createStore(Store store) {
		try {
			return storeRepo.createStore(store);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editStore(Long id, Store store) {
		try {
			return storeRepo.editStore(id, store);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeStore(Long id) {
		try {
			int affectedRow = storeRepo.removeStore(id);
			if(affectedRow !=0)
				submenuRepo.removeSubmenuByStoreId(id);
			return affectedRow;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
