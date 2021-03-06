package my.com.byod.admin.rest;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.entity.Category;
import my.com.byod.admin.entity.GroupCategory;
import my.com.byod.admin.entity.ItemGroup;
import my.com.byod.admin.entity.MenuItem;
import my.com.byod.admin.entity.ModifierGroup;
import my.com.byod.admin.entity.Store;
import my.com.byod.admin.entity.Tag;
import my.com.byod.admin.entity.TaxCharge;
import my.com.byod.admin.service.CategoryService;
import my.com.byod.admin.service.ChargeConfigService;
import my.com.byod.admin.service.GroupCategoryService;
import my.com.byod.admin.service.ItemGroupService;
import my.com.byod.admin.service.MenuItemService;
import my.com.byod.admin.service.ModifierGroupService;
import my.com.byod.admin.service.StoreService;
import my.com.byod.admin.service.StoreServiceImp;

@RestController
@RequestMapping("/menu")
public class MenuRestController {

	private GroupCategoryService groupCategoryService;
	private StoreService storeService;
	private CategoryService categoryService;
	private MenuItemService menuItemService;
	
	@Value("${upload-path}")
	private String filePath;
	
	@Autowired
	public MenuRestController(GroupCategoryService groupCategoryService, StoreService storeService,
			CategoryService categoryService, MenuItemService menuItemService) {
		this.groupCategoryService = groupCategoryService;
		this.storeService = storeService;
		this.categoryService = categoryService;
		this.menuItemService = menuItemService;
	}

	// Group Category
	@GetMapping("/groupcategory/")
	public ResponseEntity<List<GroupCategory>> findAllGroupCategory() {
		List<GroupCategory> groupCategories = groupCategoryService.findAllGroupCategory();
		return new ResponseEntity<List<GroupCategory>>(groupCategories, HttpStatus.OK);
	}

	@GetMapping("/groupcategorybyid")
	public ResponseEntity<GroupCategory> findGroupCategoryById(@RequestParam("id") Long id) {
		GroupCategory groupCategory = groupCategoryService.findGroupCategory(id);
		if (groupCategory.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		List<Store> stores = storeService.findStoresByGroupCategoryId(groupCategory.getId());
		if (!stores.isEmpty())
			groupCategory.setStores(stores);

		List<Category> categories = categoryService.findCategoriesByGroupCategoryId(groupCategory.getId());
		if (!categories.isEmpty())
			groupCategory.setCategories(categories);

		return new ResponseEntity<GroupCategory>(groupCategory, HttpStatus.OK);
	}

	@PostMapping("/groupcategory/create")
	public ResponseEntity<String> createGroupCategory(@RequestBody GroupCategory groupCategory) {
		ResponseEntity<String> response = null;
		try {
			String responseObject = groupCategoryService.createGroupCategory(groupCategory);

			if (responseObject == null)
				throw new Exception();

			JSONObject jsonData = new JSONObject(responseObject);
			if (jsonData.getString("responseCode") == "01")
				response = new ResponseEntity<String>(jsonData.getString("responseMessage"), HttpStatus.BAD_REQUEST);
			else
				response = new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@PostMapping("/groupcategory/edit")
	public ResponseEntity<?> editGroupCategory(@RequestBody GroupCategory groupCategory) {
		try {
			GroupCategory existingGroupCategory = groupCategoryService.findGroupCategory(groupCategory.getId());
			if (existingGroupCategory.getId() == 0)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			int rowAffected = groupCategoryService.editGroupCategory(groupCategory.getId(), groupCategory);
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DuplicateKeyException ex) {
			return new ResponseEntity<String>(ex.toString(), HttpStatus.CONFLICT);
		}

	}

	@DeleteMapping("/groupcategory/delete")
	public ResponseEntity<Void> removeGroupCategory(@RequestParam("id") Long id) {
		int rowAffected = groupCategoryService.removeGroupCategory(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*// Store
	@GetMapping("/store/")
	public ResponseEntity<List<Store>> findAllStore() {
		List<Store> stores = storeService.findAllStore();
		return new ResponseEntity<List<Store>>(stores, HttpStatus.OK);
	}

	@GetMapping("/storebyid")
	public ResponseEntity<Store> findStoreById(@RequestParam("id") Long id) {
		Store existingStore = storeService.findStoreById(id);
		if (existingStore.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		existingStore.setLogoPath(filePath + existingStore.getLogoPath());
		return new ResponseEntity<Store>(existingStore, HttpStatus.OK);
	}

	@PostMapping("/store/create")
	public ResponseEntity<?> createStore(@RequestBody Store store) {
		try {
			int rowAffected = storeService.createStore(store);
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DuplicateKeyException ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/store/edit")
	public ResponseEntity<?> editStore(@RequestBody Store store) {
		try {
			Store existingStore = storeService.findStoreById(store.getId());
			if (existingStore.getId() == 0)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			int rowAffected = storeService.editStore(store.getId(), store);
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DuplicateKeyException ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/store/edit/groupcategory")
	public ResponseEntity<Void> editStoreGroupCategoryId(@RequestParam("storeId") Long storeId,
			@RequestParam("groupCategoryId") Long groupCategoryId) {
		storeService.editStoreGroupCategoryId(groupCategoryId, storeId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/store/delete")
	public ResponseEntity<Void> removeStore(@RequestParam("id") Long id) {
		int rowAffected = storeService.removeStore(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}*/

	// Category - All Working No Worry
	@GetMapping("/category/")
	public ResponseEntity<List<Category>> findAllCategory() {
		List<Category> categoryList = categoryService.findAllCategory();
		return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
	}
	
	@GetMapping("/category/active/")
	public ResponseEntity<List<Category>> findAllActiveCategory(){
		List<Category> categoryList = categoryService.findAllActiveCategory();
		return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
	}
	

	/*
	 * @GetMapping("/categorybyid") public ResponseEntity<Category>
	 * findCategoryById(@RequestParam("id") Long id) { Category existingCategory =
	 * categoryService.findCategoryById(id); if (existingCategory.getId() == 0)
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * List<MenuItem> menuItems = menuItemService.findItemByItemGroupId(itemGroupId)
	 * 
	 * return new ResponseEntity<Category>(existingCategory, HttpStatus.OK); }
	 */

	@PostMapping("/category/create")
	public ResponseEntity<Void> createCategory(@RequestBody Category category) {
		int rowAffected = categoryService.addCategory(category);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/category/edit")
	public ResponseEntity<Void> editCategory(@RequestBody Category category) {
		Category existingCategory = categoryService.findCategoryById(category.getId());
		if (existingCategory.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = categoryService.editCategory(category.getId(), category);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/category/delete")
	public ResponseEntity<Void> deleteCategory(@RequestParam("categoryId") Long id) {
		int rowAffected = categoryService.removeCategory(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/category/status")
	public ResponseEntity<?> updateCategoryStatus(@RequestParam("categoryId") Long categoryId, @RequestParam("activeFlag") boolean activeFlag){
		Category existingCategory = categoryService.findCategoryById(categoryId);
		if (existingCategory.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		categoryService.updateCategoryStatus(categoryId, activeFlag);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/category/reordersequence")
	public ResponseEntity<?> reorderCategorySequence(@RequestBody String data){
		try {
			JSONObject jsonResult = new JSONObject();
			JSONObject jsonData = new JSONObject(data);
			JSONArray jArray = jsonData.getJSONArray("category_sequences");
			
			for(int i=0;i<jArray.length();i++) {
				JSONObject jsonObj = jArray.getJSONObject(i);
				Long categoryId = jsonObj.getLong("id");
				int sequenceNumber = jsonObj.getInt("sequence");
				categoryService.updateCategorySequence(categoryId, sequenceNumber);
			}
			
			jsonResult.put("response_code", "00");
			jsonResult.put("response_message", "Success");
			
			return new ResponseEntity<String>(jsonResult.toString(),HttpStatus.OK); 
		}catch(Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		}
	}

	/*
	 * // Charge Config - All Working No Worry
	 * 
	 * @GetMapping("/chargeconfig/") public ResponseEntity<List<TaxCharge>>
	 * findAllChargeConfig() { List<TaxCharge> chargeConfigList =
	 * chargeConfigService.findAllChargeConfig(); if (chargeConfigList.isEmpty())
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<TaxCharge>>(chargeConfigList, HttpStatus.OK);
	 * }
	 * 
	 * @GetMapping("/chargeconfigbyid") public ResponseEntity<TaxCharge>
	 * findChargeConfigById(@RequestParam("chargeconfigId") Long chargeconfigId) {
	 * TaxCharge existingChargeConfig =
	 * chargeConfigService.findChargeConfigById(chargeconfigId); if
	 * (existingChargeConfig.getId() == 0) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<TaxCharge>(existingChargeConfig, HttpStatus.OK); }
	 * 
	 * @PostMapping("/chargeconfig/create") public ResponseEntity<Void>
	 * createChargeConfig(@RequestBody TaxCharge chargeConfig) { int rowAffected =
	 * chargeConfigService.createChargeConfig(chargeConfig); if (rowAffected == 0)
	 * return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/chargeconfig/edit") public ResponseEntity<Void>
	 * editChargeConfig(@RequestBody TaxCharge chargeConfig) { int rowAffected =
	 * chargeConfigService.editChargeConfig(chargeConfig.getId(), chargeConfig); if
	 * (rowAffected == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/chargeconfig/delete") public ResponseEntity<Void>
	 * removeChargeConfig(@RequestParam("chargeconfigId") Long chargeconfigId) { int
	 * rowAffected = chargeConfigService.removeChargeConfig(chargeconfigId); if
	 * (rowAffected == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * 
	 * // ItemGroup - All Working No Worry
	 * 
	 * @GetMapping("/itemgroup/") public ResponseEntity<List<ItemGroup>>
	 * findAllItemGroup() { List<ItemGroup> itemGroupList =
	 * itemGroupService.findAllItemGroup(); if (itemGroupList.isEmpty()) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<ItemGroup>>(itemGroupList, HttpStatus.OK); }
	 * 
	 * @GetMapping("/itemgroup/category") public ResponseEntity<List<ItemGroup>>
	 * findItemGroupByCategoryId(@RequestParam("categoryId") Long categoryId) {
	 * List<ItemGroup> itemGroupList =
	 * itemGroupService.findItemGroupByCategoryId(categoryId); if
	 * (itemGroupList.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<ItemGroup>>(itemGroupList, HttpStatus.OK); }
	 * 
	 * @GetMapping("/itemgroupbyid") public ResponseEntity<ItemGroup>
	 * findItemGroupById(@RequestParam("itemgroupId") Long id) { ItemGroup
	 * existingItemGroup = itemGroupService.findItemGroupById(id); if
	 * (existingItemGroup.getName() == null) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<ItemGroup>(existingItemGroup, HttpStatus.OK); }
	 * 
	 * @PostMapping("/itemgroup/create") public ResponseEntity<Void>
	 * createItemGroup(@RequestBody ItemGroup itemGroup) { int rowAffected =
	 * itemGroupService.createItemGroup(itemGroup); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/itemgroup/edit") public ResponseEntity<Void>
	 * editItemGroup(@RequestBody ItemGroup itemGroup) { ItemGroup existingItemGroup
	 * = itemGroupService.findItemGroupById(itemGroup.getId()); if
	 * (existingItemGroup.getName() == null) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = itemGroupService.editItemGroup(itemGroup.getId(),
	 * itemGroup); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/itemgroup/delete") public ResponseEntity<Void>
	 * deleteItemGroup(@RequestParam("itemgroupId") Long id) { ItemGroup
	 * existingItemGroup = itemGroupService.findItemGroupById(id); if
	 * (existingItemGroup.getName() == null) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * itemGroupService.removeItemGroup(id); return new
	 * ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // CategoryItemGroup - No Worry can use
	 * 
	 * @PostMapping("/categoryitemgroup/create") public ResponseEntity<Void>
	 * createCategoryItemGroup(@RequestBody String data) { int rowAffected =
	 * itemGroupService.addCategoryItemGroup(data); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/categoryitemgroup/edit") public ResponseEntity<Void>
	 * editCategoryItemGroup(@RequestBody String data) { int rowAffected =
	 * itemGroupService.editCategoryItemGroup(data); if (rowAffected == 0) return
	 * new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/categoryitemgroup/delete") public ResponseEntity<Void>
	 * removeCategoryItemGroup(@RequestParam("categoryitemgroupId") Long
	 * categoryitemgroupId) { int rowAffected =
	 * itemGroupService.removeCategoryItemGroup(categoryitemgroupId); if
	 * (rowAffected == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // Item
	 * 
	 * @GetMapping("/item/") public ResponseEntity<List<MenuItem>> findAllItem() {
	 * List<MenuItem> itemList = itemService.findAllItem(); if (itemList.isEmpty())
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<MenuItem>>(itemList, HttpStatus.OK); }
	 * 
	 * @GetMapping("/itembyid") public ResponseEntity<MenuItem>
	 * findItemById(@RequestParam("itemId") Long id) { MenuItem existingItem =
	 * itemService.findItemById(id);
	 * 
	 * 
	 * 
	 * if (existingItem.getName() == null) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<MenuItem>(existingItem, HttpStatus.OK); }
	 * 
	 * @GetMapping("/itembyitemgroupid") public ResponseEntity<List<MenuItem>>
	 * findItemByItemGroupId(@RequestParam("itemGroupId") Long itemGroupId) {
	 * List<MenuItem> itemList = itemService.findItemByItemGroupId(itemGroupId); if
	 * (itemList.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<MenuItem>>(itemList, HttpStatus.OK); }
	 * 
	 * @PostMapping("/item/create") public ResponseEntity<Void>
	 * createItem(@RequestBody MenuItem item) { int rowAffected =
	 * itemService.createItem(item); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/item/edit") public ResponseEntity<Void> editItem(@RequestBody
	 * MenuItem item) { MenuItem existingItem =
	 * itemService.findItemById(item.getId()); if (existingItem.getName() == null)
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = itemService.editItem(item.getId(), item, existingItem); if
	 * (rowAffected == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/item/delete") public ResponseEntity<Void>
	 * deleteItem(@RequestParam("itemId") Long id) { MenuItem existingItem =
	 * itemService.findItemById(id); if (existingItem.getName() == null) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = itemService.removeItem(id); if (rowAffected == 0) return
	 * new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // Tag
	 * 
	 * @GetMapping("/tag/") public ResponseEntity<List<Tag>> findAllTag() {
	 * List<Tag> tags = itemService.findAllTag(); if (tags.isEmpty()) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK); }
	 * 
	 * @GetMapping("/tag") public ResponseEntity<Tag>
	 * findTagById(@RequestParam("tagId") Long id) { Tag existingTag =
	 * itemService.findTagById(id); if (existingTag.getName().equals(null)) return
	 * new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<Tag>(existingTag, HttpStatus.OK); }
	 * 
	 * @PostMapping("/tag/create") public ResponseEntity<Void>
	 * createTag(@RequestBody Tag tag) { int rowAffected =
	 * itemService.createTag(tag); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/tag/edit") public ResponseEntity<Void> editTag(@RequestBody
	 * Tag tag) { Tag existingTag = itemService.findTagById(tag.getId()); if
	 * (existingTag.getName().equals(null)) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = itemService.editTag(tag.getId(), tag); if (rowAffected ==
	 * 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/tag/delete") public ResponseEntity<Void>
	 * removeTag(@RequestParam("tagId") Long id) { Tag existingTag =
	 * itemService.findTagById(id); if (existingTag.getName().equals(null)) return
	 * new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = itemService.removeTag(id); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // ModifierGroup - No Worry but delete not yet complete
	 * 
	 * @GetMapping("/modifiergroupbyid") public ResponseEntity<ModifierGroup>
	 * findModifierGroupById(@RequestParam("modifiergroupId") Long modifiergroupId)
	 * { ModifierGroup existingModifierGroup =
	 * modifierGroupService.findModifierGroupById(modifiergroupId); if
	 * (existingModifierGroup.getId() == 0) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * List<MenuItem> itemList =
	 * itemService.findItemByModifierGroupId(existingModifierGroup.getId());
	 * if(!itemList.isEmpty()) existingModifierGroup.setItems(itemList);
	 * 
	 * return new ResponseEntity<ModifierGroup>(existingModifierGroup,
	 * HttpStatus.OK); }
	 * 
	 * @GetMapping("/modifiergroup/") public ResponseEntity<List<ModifierGroup>>
	 * findAllModifierGroup() { List<ModifierGroup> modifierGroupList =
	 * modifierGroupService.findModifierGroups(); if (modifierGroupList.isEmpty())
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * for (ModifierGroup modifierGroup : modifierGroupList) { List<MenuItem>
	 * itemList = itemService.findItemByModifierGroupId(modifierGroup.getId());
	 * if(!itemList.isEmpty()) modifierGroup.setItems(itemList); } return new
	 * ResponseEntity<List<ModifierGroup>>(modifierGroupList, HttpStatus.OK); }
	 * 
	 * @PostMapping("/modifiergroup/create") public ResponseEntity<Void>
	 * createModifierGroup(@RequestBody ModifierGroup modifierGroup) { int
	 * affectedRow = modifierGroupService.createModifierGroup(modifierGroup); if
	 * (affectedRow == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/modifiergroup/edit") public ResponseEntity<Void>
	 * editModifierGroup(@RequestBody ModifierGroup modifierGroup) { ModifierGroup
	 * existingModifierGroup =
	 * modifierGroupService.findModifierGroupById(modifierGroup.getId()); if
	 * (existingModifierGroup.getId() == 0) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int affectedRow =
	 * modifierGroupService.editModifierGroup(modifierGroup.getId(), modifierGroup);
	 * if (affectedRow == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/modifiergroup/delete") public ResponseEntity<Void>
	 * removeModifierGroup(@RequestParam("modifiergroupId") Long modifiergroupId) {
	 * int rowAffected = modifierGroupService.removeModifierGroup(modifiergroupId);
	 * if (rowAffected == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // Find master menu
	 * 
	 * @GetMapping("/mastermenu/") public ResponseEntity<List<Category>>
	 * findMasterMenu() { List<Category> masterMenu = itemService.findMasterMenu();
	 * if (masterMenu.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * return new ResponseEntity<List<Category>>(masterMenu, HttpStatus.OK); }
	 * 
	 * // Item Set CRUD
	 * 
	 * @GetMapping("/itemset") public ResponseEntity<List<ItemGroup>>
	 * findItemGroupByItemSetItemId(@RequestParam("itemId") Long itemId) {
	 * 
	 * List<ItemGroup> itemGroupList =
	 * itemService.findItemGroupByItemSetItemId(itemId); if
	 * (itemGroupList.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<ItemGroup>>(itemGroupList, HttpStatus.OK); }
	 * 
	 * @PostMapping("/itemset/create") public ResponseEntity<Void>
	 * createItemSet(@RequestBody String data) { int affectedRow =
	 * itemService.addItemSet(data); if (affectedRow == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/itemset/edit") public ResponseEntity<Void>
	 * editItemSetSequence(@RequestBody String data) { int affectedRow =
	 * itemService.editItemSet(data); if (affectedRow == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("itemset/delete") public ResponseEntity<Void>
	 * removeItemSet(@RequestParam("itemsetId") Long itemSetId) { int affectedRow =
	 * itemService.removeItemSet(itemSetId); if (affectedRow == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // Store CRUD
	 * 
	 * @GetMapping("/store/") public ResponseEntity<List<Store>> findAllStore() {
	 * List<Store> stores = storeService.findAllStore(); if (stores.isEmpty())
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<List<Store>>(stores, HttpStatus.OK); }
	 * 
	 * @GetMapping("/store") public ResponseEntity<Store>
	 * findStoreById(@RequestParam("storeId") Long id) { Store existingStore =
	 * storeService.findStoreById(id); if (existingStore.getId() == 0) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * return new ResponseEntity<Store>(existingStore, HttpStatus.OK); }
	 * 
	 * @PostMapping("/store/create") public ResponseEntity<Void>
	 * createStore(@RequestBody Store store) { int rowAffected =
	 * storeService.createStore(store); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/store/edit") public ResponseEntity<Void>
	 * editStore(@RequestBody Store store) { Store existingStore =
	 * storeService.findStoreById(store.getId()); if (existingStore.getId() == 0)
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = storeService.editStore(store.getId(), store); if
	 * (rowAffected == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/store/delete") public ResponseEntity<Void>
	 * removeStore(@RequestParam("storeId") Long id) { Store existingStore =
	 * storeService.findStoreById(id); if (existingStore.getId() == 0) return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * 
	 * int rowAffected = storeService.removeStore(id); if (rowAffected == 0) return
	 * new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * // ItemGroup Item
	 * 
	 * @PostMapping("/itemgroupitem/create") public ResponseEntity<Void>
	 * createItemGroupItem(@RequestBody String data) { int rowAffected =
	 * itemService.addItemIntoItemGroup(data); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @PostMapping("/itemgroupitem/edit") public ResponseEntity<Void>
	 * editItemGroupItem(@RequestBody String data) { int rowAffected =
	 * itemService.editItemGroupItem(data); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 * 
	 * @DeleteMapping("/itemgroupitem/delete") public ResponseEntity<Void>
	 * editItemGroupItem(@RequestParam("itemgroupitemId") Long id) { int rowAffected
	 * = itemService.removeItemGroupItem(id); if (rowAffected == 0) return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST);
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK); }
	 */
}
