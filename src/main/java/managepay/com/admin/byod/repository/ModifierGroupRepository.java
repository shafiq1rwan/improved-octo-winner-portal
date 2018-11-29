package managepay.com.admin.byod.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import managepay.com.admin.byod.entity.Item;
import managepay.com.admin.byod.entity.ModifierGroup;

@Repository
public class ModifierGroupRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ModifierGroupRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<ModifierGroup> rowMapper = (rs, rowNum) -> {
		ModifierGroup modifierGroup = new ModifierGroup();
		modifierGroup.setId(rs.getLong("id"));
		modifierGroup.setBackendId(rs.getString("backend_id"));
		modifierGroup.setName(rs.getString("modifier_group_name"));
		return modifierGroup;
	};

	private ResultSetExtractor<List<ModifierGroup>> resultSetExtractor = (rs) -> {
		Map<Long, ModifierGroup> map = new HashMap<Long, ModifierGroup>();

		while (rs.next()) {
			Long modifierGroupId = rs.getLong("mg_id");
			ModifierGroup modifierGroup = map.get(modifierGroupId);

			if (modifierGroup == null) {
				modifierGroup = new ModifierGroup();
				modifierGroup.setId(modifierGroupId);
				modifierGroup.setBackendId(rs.getString("mg_backend_id"));
				modifierGroup.setName(rs.getString("modifier_group_name"));
				map.put(modifierGroupId, modifierGroup);
			}
			
			List itemList = modifierGroup.getItems();
			if (itemList == null) {
				itemList = new ArrayList<Item>();
				modifierGroup.setItems(itemList);

				Item item = new Item();
				item.setId(rs.getLong("id"));
				item.setBackendId(rs.getString("backend_id"));
				item.setModifierGroupId(rs.getLong("modifier_group_id"));
				item.setName(rs.getString("item_name"));
				item.setDescription(rs.getString("item_description"));
				item.setImagePath(rs.getString("item_image_path"));
				item.setBasePrice(rs.getBigDecimal("item_base_price"));
				item.setTaxable(rs.getBoolean("taxable"));
				item.setModifiable(rs.getBoolean("modifiable"));
				item.setDiscountable(rs.getBoolean("discountable"));
				item.setPublished(rs.getBoolean("published"));

				itemList.add(item);
			}
			
		}

		return new ArrayList<ModifierGroup>(map.values());
	};

	public List<ModifierGroup> findModifierGroups() {
		return jdbcTemplate.query(
				"SELECT mg.id as mg_id, mg.modifier_group_name, mg.backend_id as mg_backend_id, i.* FROM modifier_group mg LEFT JOIN item i ON mg.id = i.modifier_group_id",
				resultSetExtractor);
	}

	public ModifierGroup findModifierGroupById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM modifier_group WHERE id = ?", new Object[] { id }, rowMapper);
	}

	public int createModifierGroup(ModifierGroup modifierGroup) {
		return jdbcTemplate.update("INSERT into modifier_group(backend_id, modifier_group_name) VALUES (?,?)",
				new Object[] { modifierGroup.getBackendId(), modifierGroup.getName() });
	}

	public int editModifierGroup(Long id, ModifierGroup modifierGroup) {
		return jdbcTemplate.update("UPDATE modifier_group SET backend_id =? , modifier_group_name =? WHERE id = ?",
				new Object[] { modifierGroup.getBackendId(), modifierGroup.getName(), id });
	}

	public int removeModifierGroup(Long id) {
		return jdbcTemplate.update("DELETE FROM modifier_group WHERE id = ?");
	}

}
