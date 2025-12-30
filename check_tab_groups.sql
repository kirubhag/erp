USE IAM_MasterDB;
SELECT COUNT(*) as total FROM erp_tab_groups;
SELECT erp_tab_group_id, name, code, sequence FROM erp_tab_groups ORDER BY sequence;
SELECT COUNT(*) as entity_mappings FROM erp_tab_group_entity_rel;
SELECT tg.name as tab_group, COUNT(rel.entity_id) as entity_count 
FROM erp_tab_groups tg 
LEFT JOIN erp_tab_group_entity_rel rel ON tg.erp_tab_group_id = rel.tab_group_id 
GROUP BY tg.erp_tab_group_id, tg.name 
ORDER BY tg.sequence;
