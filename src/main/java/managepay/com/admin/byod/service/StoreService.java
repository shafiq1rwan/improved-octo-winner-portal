package managepay.com.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import managepay.com.admin.byod.entity.Store;
import managepay.com.admin.byod.repository.StoreRepository;

@Service
public class StoreService implements IStoreService {

	private StoreRepository storeRepo;
	
	@Autowired
	public StoreService(StoreRepository storeRepo) {
		this.storeRepo = storeRepo;
	}

	@Override
	public Store findStoreById(Long id) {
		try {
			return storeRepo.findStoreById(id);	
		} catch(Exception ex) {
			ex.printStackTrace();
			return new Store();
		}
	}

	@Override
	public List<Store> findAllStore() {
		try {
			return storeRepo.findAllStore();
		} catch(Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public int createStore(Store store) {
		try {
			return storeRepo.createStore(store);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editStore(Long id, Store store) {
		try {
			return storeRepo.editStore(id, store);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeStore(Long id) {
		try {
			return storeRepo.removeStore(id);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
