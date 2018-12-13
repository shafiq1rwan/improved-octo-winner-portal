package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.Store;
import com.managepay.admin.byod.repository.StoreRepository;
import com.managepay.admin.byod.util.ByodUtil;

@Service
public class StoreServiceImp implements StoreService {

	private StoreRepository storeRepo;
	
	@Autowired 
	private ByodUtil byodUtil;
	
	@Autowired
	public StoreServiceImp(StoreRepository storeRepo) {
		this.storeRepo = storeRepo;
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
			store.setBackendId(byodUtil.createBackendId("S", 8));
			store.setLogoPath(byodUtil.saveImageFile(store.getLogoPath(), null));
			return storeRepo.createStore(store);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateKeyException("Duplication Found");
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editStore(Long id, Store store) {
		try {
			store.setLogoPath(byodUtil.saveImageFile(store.getLogoPath(), null));
			return storeRepo.editStore(id, store);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateKeyException("Duplication Found");
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeStore(Long id) {
		try {
			return storeRepo.removeStore(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editStoreGroupCategoryId(Long groupCategoryId, Long id) {
		try {
			return storeRepo.editStoreGroupCategoryId(groupCategoryId, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId) {
		try {
			return storeRepo.editStoreGroupCategoryIdInBatch(groupCategoryId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId) {
		try {
			return storeRepo.findStoresByGroupCategoryId(groupCategoryId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}
	
}
