package my.com.byod.admin.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import my.com.byod.admin.entity.Store;
import my.com.byod.admin.repository.StoreRepository;
import my.com.byod.admin.util.ByodUtil;

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
