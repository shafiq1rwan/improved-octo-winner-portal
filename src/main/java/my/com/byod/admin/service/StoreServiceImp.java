package my.com.byod.admin.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
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
	public int createStore(Store store, String brandId) {
		try {
			store.setBackendId(byodUtil.createUniqueBackendId("S"));
			store.setLogoPath(byodUtil.saveImageFile(brandId,"imgS", store.getLogoPath(), null));
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(store.getOperatingStartTime());
			startTime.set(Calendar.SECOND, 0);
			startTime.set(Calendar.MILLISECOND, 0);
			store.setOperatingStartTime(startTime.getTime());
			
			Calendar endTime = Calendar.getInstance();
			endTime.setTime(store.getOperatingEndTime());
			endTime.set(Calendar.SECOND, 0);
			endTime.set(Calendar.MILLISECOND, 0);
			store.setOperatingEndTime(endTime.getTime());
			
			return storeRepo.createStore(store);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateKeyException("Duplication Found");
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editStore(Long id, Store store, String brandId, String existingLogoPath) {
		try {
			if(store.getLogoPath()!=null) {
				if(existingLogoPath.equals("")) {
					existingLogoPath = null;
				}
				String image = byodUtil.saveImageFile(brandId, "imgS", store.getLogoPath(), existingLogoPath);
				return storeRepo.editStore(id, store, image);
			}
			else {
				return storeRepo.editStore(id, store);
			}
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
