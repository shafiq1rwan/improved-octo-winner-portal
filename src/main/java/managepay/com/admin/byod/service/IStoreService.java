package managepay.com.admin.byod.service;

import java.util.List;

import managepay.com.admin.byod.entity.Store;

public interface IStoreService {

	public Store findStoreById(Long id);

	public List<Store> findAllStore();

	public int createStore(Store store);

	public int editStore(Long id, Store store);

	public int removeStore(Long id);

}
