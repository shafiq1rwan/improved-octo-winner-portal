package com.managepay.admin.byod.rest;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.managepay.admin.byod.entity.Category;
import com.managepay.admin.byod.entity.Item;
import com.managepay.admin.byod.entity.ItemGroup;
import com.managepay.admin.byod.entity.ModifierGroup;
import com.managepay.admin.byod.entity.Store;
import com.managepay.admin.byod.entity.Tag;
import com.managepay.admin.byod.service.CategoryServiceImp;
import com.managepay.admin.byod.service.ItemGroupServiceImp;
import com.managepay.admin.byod.service.ItemServiceImp;
import com.managepay.admin.byod.service.ModifierGroupServiceImp;
import com.managepay.admin.byod.service.StoreService;
import com.managepay.admin.byod.service.StoreServiceImp;

@RestController
@RequestMapping("/menu")
public class MenuRestController {

	private CategoryServiceImp categoryService;
	private ItemGroupServiceImp itemGroupService;
	private ItemServiceImp itemService;
	private ModifierGroupServiceImp modifierGroupService;
	private StoreServiceImp storeService;

	@Autowired
	public MenuRestController(CategoryServiceImp categoryService, ItemGroupServiceImp itemGroupService,
			ItemServiceImp itemService, ModifierGroupServiceImp modifierGroupService, StoreServiceImp storeService) {
		this.categoryService = categoryService;
		this.itemGroupService = itemGroupService;
		this.itemService = itemService;
		this.modifierGroupService = modifierGroupService;
		this.storeService = storeService;
	}

	// Category
	@GetMapping("/category/")
	public ResponseEntity<List<Category>> findAllCategory() {
		List categoryList = categoryService.findAllCategory();
		if (categoryList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
	}

	@GetMapping("/category")
	public ResponseEntity<Category> findCategoryById(@RequestParam("categoryId") Long id) {
		Category existingCategory = categoryService.findCategoryById(id);
		if (existingCategory.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Category>(existingCategory, HttpStatus.OK);
	}

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
		Category existingCategory = categoryService.findCategoryById(id);
		if (existingCategory.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = categoryService.removeCategory(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// ItemGroup
	@GetMapping("/itemgroup/")
	public ResponseEntity<List<ItemGroup>> findAllItemGroup() {
		List<ItemGroup> itemGroupList = itemGroupService.findAllItemGroup();
		if (itemGroupList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<ItemGroup>>(itemGroupList, HttpStatus.OK);
	}

	@GetMapping("/itemgroup")
	public ResponseEntity<ItemGroup> findItemGroupById(@RequestParam("itemgroupId") Long id) {
		ItemGroup existingItemGroup = itemGroupService.findItemGroupById(id);
		if (existingItemGroup.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<ItemGroup>(existingItemGroup, HttpStatus.OK);
	}

	@PostMapping("/itemgroup/create")
	public ResponseEntity<Void> createItemGroup(@RequestBody ItemGroup itemGroup) {
		int rowAffected = itemGroupService.createItemGroup(itemGroup);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/itemgroup/edit")
	public ResponseEntity<Void> editItemGroup(@RequestBody ItemGroup itemGroup) {
		ItemGroup existingItemGroup = itemGroupService.findItemGroupById(itemGroup.getId());
		if (existingItemGroup.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemGroupService.editItemGroup(itemGroup.getId(), itemGroup);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/itemgroup/delete")
	public ResponseEntity<Void> deleteItemGroup(@RequestParam("itemgroupId") Long id) {
		ItemGroup existingItemGroup = itemGroupService.findItemGroupById(id);
		if (existingItemGroup.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		itemGroupService.removeItemGroup(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// CategoryItemGroup
	@GetMapping("/categoryitemgroup")
	public ResponseEntity<List<ItemGroup>> findItemGroupByCategoryId(@RequestParam("categoryitemgroupId") Long id) {
		List<ItemGroup> itemGroupList = itemGroupService.findItemGroupByCategoryId(id);
		if (itemGroupList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<ItemGroup>>(itemGroupList, HttpStatus.OK);
	}

	@PostMapping("/categoryitemgroup/create")
	public ResponseEntity<Void> createCategoryItemGroup(@RequestBody String data) {
		int rowAffected = itemGroupService.addCategoryItemGroup(data);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/categoryitemgroup/edit")
	public ResponseEntity<Void> editCategoryItemGroup(@RequestBody String data) {
		int rowAffected = itemGroupService.editCategoryItemGroup(data);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/categoryitemgroup/delete")
	public ResponseEntity<Void> removeCategoryItemGroup(@RequestParam("categoryitemgroupId") Long id) {
		int rowAffected = itemGroupService.removeCategoryItemGroupInBatch(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Item
	@GetMapping("/item/")
	public ResponseEntity<List<Item>> findAllItem() {
		List<Item> itemList = itemService.findAllItem();
		if (itemList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Item>>(itemList, HttpStatus.OK);
	}

	@GetMapping("/item")
	public ResponseEntity<Item> findItemById(@RequestParam("itemId") Long id) {
		Item existingItem = itemService.findItemById(id);
		if (existingItem.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Item>(existingItem, HttpStatus.OK);
	}

	@GetMapping("/item")
	public ResponseEntity<List<Item>> findItemByItemGroupId(@RequestParam("itemGroupId") Long itemGroupId) {
		List<Item> itemList = itemService.findItemByItemGroupId(itemGroupId);
		if (itemList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Item>>(itemList, HttpStatus.OK);
	}

	@PostMapping("/item/create")
	public ResponseEntity<Void> createItem(@RequestBody Item item) {
		int rowAffected = itemService.createItem(item);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/item/edit")
	public ResponseEntity<Void> editItem(@RequestBody Item item) {
		Item existingItem = itemService.findItemById(item.getId());
		if (existingItem.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.editItem(item.getId(), item, existingItem);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/item/delete")
	public ResponseEntity<Void> deleteItem(@RequestParam("itemId") Long id) {
		Item existingItem = itemService.findItemById(id);
		if (existingItem.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.removeItem(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Tag
	@GetMapping("/tag/")
	public ResponseEntity<List<Tag>> findAllTag() {
		List<Tag> tags = itemService.findAllTag();
		if (tags.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK);
	}

	@GetMapping("/tag")
	public ResponseEntity<Tag> findTagById(@RequestParam("tagId") Long id) {
		Tag existingTag = itemService.findTagById(id);
		if (existingTag.getName().equals(null))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Tag>(existingTag, HttpStatus.OK);
	}

	@PostMapping("/tag/create")
	public ResponseEntity<Void> createTag(@RequestBody Tag tag) {
		int rowAffected = itemService.createTag(tag);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/tag/edit")
	public ResponseEntity<Void> editTag(@RequestBody Tag tag) {
		Tag existingTag = itemService.findTagById(tag.getId());
		if (existingTag.getName().equals(null))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.editTag(tag.getId(), tag);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/tag/delete")
	public ResponseEntity<Void> removeTag(@RequestParam("tagId") Long id) {
		Tag existingTag = itemService.findTagById(id);
		if (existingTag.getName().equals(null))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.removeTag(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// ModifierGroup
	@GetMapping("/modifiergroup/{id}")
	public ResponseEntity<ModifierGroup> findModifierGroupById(@PathVariable Long id) {
		ModifierGroup existingModifierGroup = modifierGroupService.findModifierGroupById(id);
		if (existingModifierGroup.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<ModifierGroup>(existingModifierGroup, HttpStatus.OK);
	}

	@GetMapping("/modifiergroup/")
	public ResponseEntity<List<ModifierGroup>> findAllModifierGroup() {
		List<ModifierGroup> modifierGroupList = modifierGroupService.findModifierGroups();
		if (modifierGroupList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		for (ModifierGroup modifierGroup : modifierGroupList) {
			List<Item> itemList = itemService.

					modifierGroup.setItems(items);
		}

		return new ResponseEntity<List<ModifierGroup>>(modifierGroupList, HttpStatus.OK);
	}

	@PostMapping("/modifiergroup/create")
	public ResponseEntity<Void> createModifierGroup(@RequestBody ModifierGroup modifierGroup) {
		int affectedRow = modifierGroupService.createModifierGroup(modifierGroup);
		if (affectedRow == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/modifiergroup/edit/{id}")
	public ResponseEntity<Void> editModifierGroup(@PathVariable Long id, @RequestBody ModifierGroup modifierGroup) {
		ModifierGroup existingModifierGroup = modifierGroupService.findModifierGroupById(id);
		if (existingModifierGroup.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int affectedRow = modifierGroupService.editModifierGroup(id, modifierGroup);
		if (affectedRow == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/modifiergroup/delete/{id}")
	public ResponseEntity<Void> remove(Long id) {
		ModifierGroup existingModifierGroup = modifierGroupService.findModifierGroupById(id);
		if (existingModifierGroup.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = modifierGroupService.removeModifierGroup(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Find master menu
	@GetMapping("/mastermenu/")
	public ResponseEntity<List<Category>> findMasterMenu() {
		List<Category> masterMenu = itemService.findMasterMenu();
		if (masterMenu.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Category>>(masterMenu, HttpStatus.OK);
	}

	// Store CRUD
	@GetMapping("/store/")
	public ResponseEntity<List<Store>> findAllStore() {
		List<Store> stores = storeService.findAllStore();
		if (stores.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Store>>(stores, HttpStatus.OK);
	}

	@GetMapping("/store")
	public ResponseEntity<Store> findStoreById(@RequestParam("storeId") Long id) {
		Store existingStore = storeService.findStoreById(id);
		if (existingStore.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Store>(existingStore, HttpStatus.OK);
	}

	@PostMapping("/store/create")
	public ResponseEntity<Void> createStore(@RequestBody Store store) {
		int rowAffected = storeService.createStore(store);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/store/edit")
	public ResponseEntity<Void> editStore(@RequestBody Store store) {
		Store existingStore = storeService.findStoreById(store.getId());
		if (existingStore.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = storeService.editStore(store.getId(), store);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/store/delete")
	public ResponseEntity<Void> removeStore(@RequestParam("storeId") Long id) {
		Store existingStore = storeService.findStoreById(id);
		if (existingStore.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = storeService.removeStore(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// ItemGroup Item
	@PostMapping("/itemgroupitem/create")
	public ResponseEntity<Void> createItemGroupItem(@RequestBody String data) {
		int rowAffected = itemService.addItemIntoItemGroup(data);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/itemgroupitem/edit")
	public ResponseEntity<Void> editItemGroupItem(@RequestBody String data) {
		int rowAffected = itemService.editItemGroupItem(data);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/itemgroupitem/delete")
	public ResponseEntity<Void> editItemGroupItem(@RequestParam("itemgroupitemId") Long id) {
		int rowAffected = itemService.removeItemGroupItem(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
