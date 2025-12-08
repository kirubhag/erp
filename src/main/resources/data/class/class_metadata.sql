-- ERP Metadata for Class Entity
-- Insert these after creating the erp_class tables

-- 1. Add Class entity to erp_entities
INSERT INTO
    erp_entities (
        singular_name,
        plural_name,
        description,
        system_name,
        icon,
        route,
        table_name,
        pkid,
        display_column,
        presence,
        sequence,
        is_active
    )
VALUES (
        'Class',
        'Classes',
        'Academic classes and sections for student organization',
        'CLASS',
        'bi-building',
        '/classes',
        'erp_class',
        'class_id',
        'class_name',
        1,
        10,
        1
    );

-- 2. Create sections for Class entity
-- Get the entity_id first (assuming it's the last inserted)
SET @entity_id = LAST_INSERT_ID();

-- Basic Information Section
INSERT INTO
    erp_sections (
        entity_type,
        section_name,
        section_label,
        layout_type,
        display_order,
        is_collapsible,
        is_collapsed_by_default,
        show_in_create,
        show_in_edit,
        show_in_detail,
        section_icon,
        is_active
    )
VALUES (
        'CLASS',
        'basic_info',
        'Basic Information',
        'TWO_COLUMN',
        1,
        0,
        0,
        1,
        1,
        1,
        'bi-info-circle',
        1
    );

SET @basic_section_id = LAST_INSERT_ID();

-- Associations Section
INSERT INTO
    erp_sections (
        entity_type,
        section_name,
        section_label,
        layout_type,
        display_order,
        is_collapsible,
        is_collapsed_by_default,
        show_in_create,
        show_in_edit,
        show_in_detail,
        section_icon,
        description,
        is_active
    )
VALUES (
        'CLASS',
        'associations',
        'Associations',
        'FULL_WIDTH',
        2,
        1,
        0,
        0,
        1,
        1,
        'bi-link-45deg',
        'Manage teachers, students, and subjects associated with this class',
        1
    );

-- 3. Create fields for Class entity

-- Class Code
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'class_code',
        'Class Code',
        'VARCHAR',
        1,
        @basic_section_id,
        1,
        1,
        1,
        1,
        1,
        1,
        'medium',
        'Unique identifier for the class (e.g., G5-A, G10-B)',
        1
    );

-- Class Name
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'class_name',
        'Class Name',
        'VARCHAR',
        1,
        @basic_section_id,
        1,
        1,
        1,
        2,
        1,
        1,
        'large',
        'Full name of the class',
        1
    );

-- Grade Level
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        picklist_options,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'grade_level',
        'Grade Level',
        'VARCHAR',
        3,
        @basic_section_id,
        1,
        1,
        1,
        3,
        1,
        1,
        'medium',
        'KINDERGARTEN,GRADE_1,GRADE_2,GRADE_3,GRADE_4,GRADE_5,GRADE_6,GRADE_7,GRADE_8,GRADE_9,GRADE_10,GRADE_11,GRADE_12',
        'Academic grade level',
        1
    );

-- Section
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'section',
        'Section',
        'VARCHAR',
        1,
        @basic_section_id,
        0,
        1,
        1,
        4,
        1,
        1,
        'small',
        'Section identifier (e.g., A, B, C)',
        1
    );

-- Academic Year
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'academic_year',
        'Academic Year',
        'VARCHAR',
        1,
        @basic_section_id,
        1,
        1,
        1,
        5,
        1,
        1,
        'medium',
        'Academic year (e.g., 2023-2024)',
        1
    );

-- Capacity
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'capacity',
        'Capacity',
        'INT',
        2,
        @basic_section_id,
        0,
        0,
        1,
        6,
        1,
        1,
        'small',
        'Maximum number of students',
        1
    );

-- Room Number
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'room_number',
        'Room Number',
        'VARCHAR',
        1,
        @basic_section_id,
        0,
        1,
        1,
        7,
        1,
        1,
        'small',
        'Classroom/room number',
        1
    );

-- Class Teacher
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'class_teacher_id',
        'Class Teacher',
        'BIGINT',
        5,
        @basic_section_id,
        0,
        0,
        0,
        8,
        0,
        1,
        'medium',
        'Primary class teacher',
        1
    );

-- Description
INSERT INTO
    erp_fields (
        entity_type,
        field_name,
        field_label,
        field_type,
        ui_type,
        section_id,
        is_required,
        is_searchable,
        is_sortable,
        display_order,
        show_in_list,
        show_in_form,
        column_width,
        field_description,
        is_active
    )
VALUES (
        'CLASS',
        'description',
        'Description',
        'TEXT',
        4,
        @basic_section_id,
        0,
        1,
        0,
        9,
        0,
        1,
        'full',
        'Additional notes or description',
        1
    );