-- Sample Data for Assets & Supplies Module

-- Insert more Assets
INSERT INTO erp_inventory_assets (name, asset_tag, serial_number, type, purchase_date, status, location, created_by, created_time, is_active) VALUES
('Dell Laptop XPS 15', 'AST-001', 'DL-XPS-2024-001', 'Electronics', '2024-01-15', 'AVAILABLE', 'IT Lab', 1, NOW(), 1),
('HP Desktop Pro', 'AST-002', 'HP-DT-2024-002', 'Electronics', '2024-02-20', 'ASSIGNED', 'Admin Office', 1, NOW(), 1),
('Projector Epson EB-X51', 'AST-003', 'EP-PJ-2024-003', 'Electronics', '2024-03-10', 'AVAILABLE', 'Conference Room', 1, NOW(), 1),
('Office Chair Ergonomic', 'AST-004', 'CH-ERG-2024-004', 'Furniture', '2024-01-05', 'AVAILABLE', 'Staff Room', 1, NOW(), 1),
('Meeting Table 8-seater', 'AST-005', 'TBL-MT-2024-005', 'Furniture', '2024-02-15', 'AVAILABLE', 'Conference Room', 1, NOW(), 1),
('Canon Printer MF746Cx', 'AST-006', 'CN-PR-2024-006', 'Electronics', '2024-04-01', 'AVAILABLE', 'Print Room', 1, NOW(), 1),
('Air Conditioner LG 2T', 'AST-007', 'LG-AC-2024-007', 'Appliances', '2024-03-20', 'AVAILABLE', 'Server Room', 1, NOW(), 1),
('Whiteboard Interactive', 'AST-008', 'WB-INT-2024-008', 'Electronics', '2024-05-10', 'AVAILABLE', 'Training Room', 1, NOW(), 1),
('Filing Cabinet 4-drawer', 'AST-009', 'FC-4D-2024-009', 'Furniture', '2024-01-25', 'AVAILABLE', 'Admin Office', 1, NOW(), 1),
('CCTV Camera System', 'AST-010', 'CCTV-2024-010', 'Security', '2024-02-28', 'AVAILABLE', 'Main Entrance', 1, NOW(), 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert more Consumables
INSERT INTO erp_inventory_consumables (name, code, category, unit, reorder_level, current_stock, created_by, created_time, is_active) VALUES
('A4 Paper (500 sheets)', 'CON-001', 'Stationery', 'Ream', 20, 150, 1, NOW(), 1),
('Whiteboard Markers', 'CON-002', 'Stationery', 'Box', 10, 45, 1, NOW(), 1),
('Ballpoint Pens Blue', 'CON-003', 'Stationery', 'Box', 15, 80, 1, NOW(), 1),
('Sticky Notes 3x3', 'CON-004', 'Stationery', 'Pack', 25, 60, 1, NOW(), 1),
('Printer Ink Black', 'CON-005', 'Printing', 'Cartridge', 5, 12, 1, NOW(), 1),
('Printer Ink Color', 'CON-006', 'Printing', 'Cartridge', 5, 8, 1, NOW(), 1),
('Hand Sanitizer 500ml', 'CON-007', 'Cleaning', 'Bottle', 30, 100, 1, NOW(), 1),
('Floor Cleaner 5L', 'CON-008', 'Cleaning', 'Can', 10, 25, 1, NOW(), 1),
('Tissue Paper Box', 'CON-009', 'Cleaning', 'Box', 50, 200, 1, NOW(), 1),
('Stapler Pins', 'CON-010', 'Stationery', 'Box', 20, 75, 1, NOW(), 1),
('Paper Clips', 'CON-011', 'Stationery', 'Box', 15, 50, 1, NOW(), 1),
('Folders Manila', 'CON-012', 'Stationery', 'Pack', 20, 65, 1, NOW(), 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert more Vendors
INSERT INTO erp_inventory_vendors (name, contact_person, email, phone, tin_gstin, address, created_by, created_time, is_active) VALUES
('Tech Solutions Pvt Ltd', 'Rajesh Kumar', 'rajesh@techsolutions.com', '+91-9876543210', 'GSTIN123456789', '123 Tech Park, Bangalore', 1, NOW(), 1),
('Office Mart India', 'Priya Sharma', 'priya@officemart.in', '+91-9876543211', 'GSTIN987654321', '456 Commercial St, Chennai', 1, NOW(), 1),
('Cleaning Supplies Co', 'Amit Patel', 'amit@cleaningsupplies.com', '+91-9876543212', 'GSTIN456789123', '789 Industrial Area, Mumbai', 1, NOW(), 1),
('Furniture World', 'Sunita Reddy', 'sunita@furnitureworld.com', '+91-9876543213', 'GSTIN789123456', '101 Furniture Lane, Hyderabad', 1, NOW(), 1),
('Print Solutions', 'Karthik Nair', 'karthik@printsolutions.in', '+91-9876543214', 'GSTIN321654987', '202 Print Plaza, Pune', 1, NOW(), 1),
('Security Systems Inc', 'Mohammed Ali', 'ali@securitysystems.com', '+91-9876543215', 'GSTIN654321789', '303 Security Complex, Delhi', 1, NOW(), 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert more Purchase Orders
INSERT INTO erp_inventory_purchase_orders (po_number, vendor_id, order_date, total_amount, status, created_by, created_time, is_active) VALUES
('PO-2026-001', 1, '2026-01-05', 125000.00, 'RECEIVED', 1, NOW(), 1),
('PO-2026-002', 2, '2026-01-08', 45000.00, 'RECEIVED', 1, NOW(), 1),
('PO-2026-003', 3, '2026-01-10', 15000.00, 'ORDERED', 1, NOW(), 1),
('PO-2026-004', 4, '2026-01-12', 85000.00, 'DRAFT', 1, NOW(), 1),
('PO-2026-005', 5, '2026-01-15', 22000.00, 'ORDERED', 1, NOW(), 1),
('PO-2026-006', 1, '2026-01-16', 75000.00, 'DRAFT', 1, NOW(), 1),
('PO-2026-007', 6, '2026-01-17', 35000.00, 'DRAFT', 1, NOW(), 1),
('PO-2026-008', 2, '2026-01-18', 18500.00, 'ORDERED', 1, NOW(), 1)
ON DUPLICATE KEY UPDATE po_number=VALUES(po_number);
