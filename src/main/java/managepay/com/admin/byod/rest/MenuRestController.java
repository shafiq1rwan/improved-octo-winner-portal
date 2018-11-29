package managepay.com.admin.byod.rest;

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
import org.springframework.web.bind.annotation.RestController;

import managepay.com.admin.byod.entity.Category;
import managepay.com.admin.byod.entity.Item;
import managepay.com.admin.byod.entity.ItemGroup;
import managepay.com.admin.byod.entity.ModifierGroup;
import managepay.com.admin.byod.entity.Tag;
import managepay.com.admin.byod.service.ICategoryService;
import managepay.com.admin.byod.service.IItemGroupService;
import managepay.com.admin.byod.service.IItemService;
import managepay.com.admin.byod.service.IModifierGroupService;

@RestController
@RequestMapping("/menu")
public class MenuRestController {

	private ICategoryService categoryService;
	private IItemGroupService itemGroupService;
	private IItemService itemService;
	private IModifierGroupService modifierGroupService;

	@Autowired
	public MenuRestController(ICategoryService categoryService, IItemGroupService itemGroupService,
			IItemService itemService, IModifierGroupService modifierGroupService) {
		this.categoryService = categoryService;
		this.itemGroupService = itemGroupService;
		this.itemService = itemService;
		this.modifierGroupService = modifierGroupService;
	}
	
	//Category
	@GetMapping("/category/")
	public ResponseEntity<List<Category>> findAllCategory(){
		List categoryList = categoryService.findAllCategory();
		if(categoryList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
	}

	@GetMapping("/category/{id}")
	public ResponseEntity<Category> findCategoryById(@PathVariable Long id) {
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

	@PutMapping("/category/edit/{id}")
	public ResponseEntity<Void> editCategory(@PathVariable Long id, @RequestBody Category category) {
		Category existingCategory = categoryService.findCategoryById(id);
		if (existingCategory.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = categoryService.editCategory(id, category);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/category/delete/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		Category existingCategory = categoryService.findCategoryById(id);
		if (existingCategory.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = categoryService.removeCategory(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//ItemGroup
	@GetMapping("/itemgroup/")
	public ResponseEntity<List<ItemGroup>> findAllItemGroup() {
		List<ItemGroup> itemGroupList = itemGroupService.findAllItemGroup();
		if (itemGroupList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<ItemGroup>>(itemGroupList, HttpStatus.OK);
	}
	
	@GetMapping("/itemgroup/{id}")
	public ResponseEntity<ItemGroup> findItemGroupById(@PathVariable Long id) {
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

	@PutMapping("/itemgroup/edit/{id}")
	public ResponseEntity<Void> editItemGroup(@PathVariable Long id, @RequestBody ItemGroup itemGroup) {
		ItemGroup existingItemGroup = itemGroupService.findItemGroupById(id);
		if (existingItemGroup.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemGroupService.editItemGroup(id, itemGroup);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/itemgroup/delete/{id}")
	public ResponseEntity<Void> deleteItemGroup(@PathVariable Long id) {
		ItemGroup existingItemGroup = itemGroupService.findItemGroupById(id);
		if (existingItemGroup.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		itemGroupService.removeItemGroup(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	//CategoryItemGroup
	@GetMapping("/categoryitemgroup/{id}")
	public ResponseEntity<List<ItemGroup>> findItemGroupByCategoryId(@PathVariable Long id) {
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
	public ResponseEntity<Void> editCategoryItemGroup(@RequestBody String data){
		int rowAffected = itemGroupService.editCategoryItemGroup(data);
		if(rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/categoryitemgroup/delete/{id}")
	public ResponseEntity<Void> removeCategoryItemGroup(@PathVariable Long id){
		int rowAffected = itemGroupService.removeCategoryItemGroupInBatch(id);
		if(rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//Item
	@GetMapping("/item/")
	public ResponseEntity<List<Item>> findAllItem() {
		List<Item> itemList = itemService.findAllItem();
		if(itemList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Item>>(itemList, HttpStatus.OK);
	}
	
	@GetMapping("/item/{id}")
	public ResponseEntity<Item> findItemById(@PathVariable Long id) {
		Item existingItem = itemService.findItemById(id);
		if (existingItem.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<Item>(existingItem, HttpStatus.OK);
	}

	@PostMapping("/item/create")
	public ResponseEntity<Void> createItem(@RequestBody Item item) {
		int rowAffected = itemService.createItem(item);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/item/edit/{id}")
	public ResponseEntity<Void> editItem(@PathVariable Long id, @RequestBody Item item) {
		Item existingItem = itemService.findItemById(id);
		if (existingItem.getName() == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.editItem(id, item, existingItem);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/item/delete/{id}")
	public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
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

	@GetMapping("/tag/{id}")
	public ResponseEntity<Tag> findTagById(@PathVariable Long id) {
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

	@PutMapping("/tag/edit/{id}")
	public ResponseEntity<Void> editTag(@PathVariable Long id, @RequestBody Tag tag) {
		Tag existingTag = itemService.findTagById(id);
		if (existingTag.getName().equals(null))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.editTag(id, tag);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/tag/delete/{id}")
	public ResponseEntity<Void> removeTag(Long id) {
		Tag existingTag = itemService.findTagById(id);
		if (existingTag.getName().equals(null))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = itemService.removeTag(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//ModifierGroup
	@GetMapping("/modifiergroup/{id}")
	public ResponseEntity<ModifierGroup> findModifierGroupById(@PathVariable Long id){
		ModifierGroup existingModifierGroup = modifierGroupService.findModifierGroupById(id);
		if(existingModifierGroup.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<ModifierGroup>(existingModifierGroup,HttpStatus.OK);
	}
	
	@GetMapping("/modifiergroup/")
	public ResponseEntity<List<ModifierGroup>> findAllModifierGroup(){
		List<ModifierGroup> modifierGroupList = modifierGroupService.findModifierGroups();
		if(modifierGroupList.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<List<ModifierGroup>>(modifierGroupList,HttpStatus.OK);
	}
	
	@PostMapping("/modifiergroup/create")
	public ResponseEntity<Void> createModifierGroup(@RequestBody ModifierGroup modifierGroup){
		int affectedRow = modifierGroupService.createModifierGroup(modifierGroup);
		if(affectedRow == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		
		return new ResponseEntity<>(HttpStatus.OK); 
	}
	
	@PutMapping("/modifiergroup/edit/{id}")
	public ResponseEntity<Void> editModifierGroup(@PathVariable Long id, @RequestBody ModifierGroup modifierGroup){
		ModifierGroup existingModifierGroup = modifierGroupService.findModifierGroupById(id);
		if(existingModifierGroup.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		int affectedRow = modifierGroupService.editModifierGroup(id, modifierGroup);
		if(affectedRow == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		
		return new ResponseEntity<>(HttpStatus.OK); 
	}
	
	@DeleteMapping("/modifiergroup/delete/{id}")
	public ResponseEntity<Void> remove(Long id) {
		ModifierGroup existingModifierGroup = modifierGroupService.findModifierGroupById(id);
		if(existingModifierGroup.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		int rowAffected = modifierGroupService.removeModifierGroup(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//Find master menu
	@GetMapping("/mastermenu/")
	public ResponseEntity<List<Category>> findMasterMenu(){	
		List<Category> masterMenu = itemService.findMasterMenu();
		if(masterMenu.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<Category>>(masterMenu,HttpStatus.OK);
	}
	
	
	
	
	

}
