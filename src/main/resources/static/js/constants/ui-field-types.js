// UI Field Types Constants (100-199 range for custom entity fields)
angular.module('erpApp').constant('UI_FIELD_TYPES', {
    // Basic Input Types (100-106)
    SINGLE_LINE_TEXT: { id: 100, name: 'Single Line Text', dataType: 'VARCHAR', category: 'Basic Input', maxLength: 255 },
    MULTI_LINE_TEXT: { id: 101, name: 'Multi Line Text', dataType: 'TEXT', category: 'Basic Input' },
    RICH_TEXT_EDITOR: { id: 102, name: 'Rich Text Editor', dataType: 'TEXT', category: 'Basic Input' },
    NUMBER: { id: 103, name: 'Number', dataType: 'INT', category: 'Basic Input' },
    DECIMAL: { id: 104, name: 'Decimal', dataType: 'DECIMAL', category: 'Basic Input', decimalPlaces: 2 },
    CURRENCY: { id: 105, name: 'Currency', dataType: 'DECIMAL', category: 'Basic Input', decimalPlaces: 2, prefix: '$' },
    PERCENTAGE: { id: 106, name: 'Percentage', dataType: 'DECIMAL', category: 'Basic Input', decimalPlaces: 2, suffix: '%' },
    
    // Contact Information (107-109)
    EMAIL: { id: 107, name: 'Email', dataType: 'VARCHAR', category: 'Contact', maxLength: 255, validation: '^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$' },
    PHONE_NUMBER: { id: 108, name: 'Phone Number', dataType: 'VARCHAR', category: 'Contact', maxLength: 20 },
    URL: { id: 109, name: 'URL/Website', dataType: 'VARCHAR', category: 'Contact', maxLength: 500, validation: '^https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$' },
    
    // Date & Time (110-112)
    DATE: { id: 110, name: 'Date', dataType: 'DATE', category: 'Date & Time' },
    DATETIME: { id: 111, name: 'DateTime', dataType: 'DATETIME', category: 'Date & Time' },
    TIME: { id: 112, name: 'Time', dataType: 'TIME', category: 'Date & Time' },
    
    // Selection Types (113-117)
    PICKLIST: { id: 113, name: 'Picklist (Dropdown)', dataType: 'VARCHAR', category: 'Selection', maxLength: 255, hasOptions: true },
    MULTI_PICKLIST: { id: 114, name: 'Multi-Select Picklist', dataType: 'JSON', category: 'Selection', hasOptions: true, multiSelect: true },
    RADIO: { id: 115, name: 'Radio Button', dataType: 'VARCHAR', category: 'Selection', maxLength: 255, hasOptions: true },
    CHECKBOX: { id: 116, name: 'Checkbox', dataType: 'BOOLEAN', category: 'Selection' },
    BOOLEAN: { id: 117, name: 'Boolean (Yes/No)', dataType: 'BOOLEAN', category: 'Selection' },
    
    // File & Media (118-119)
    FILE_UPLOAD: { id: 118, name: 'File Upload', dataType: 'VARCHAR', category: 'File & Media', maxLength: 500 },
    IMAGE_UPLOAD: { id: 119, name: 'Image Upload', dataType: 'VARCHAR', category: 'File & Media', maxLength: 500, accept: 'image/*' }
});

// Helper functions for UI Field Types
angular.module('erpApp').service('UIFieldTypeService', function(UI_FIELD_TYPES) {
    
    // Get UI field type by ID
    this.getById = function(id) {
        for (var key in UI_FIELD_TYPES) {
            if (UI_FIELD_TYPES[key].id === parseInt(id)) {
                return UI_FIELD_TYPES[key];
            }
        }
        return null;
    };
    
    // Get UI field type name by ID
    this.getNameById = function(id) {
        var type = this.getById(id);
        return type ? type.name : 'Unknown Type';
    };
    
    // Get UI field types by category
    this.getByCategory = function(category) {
        var types = [];
        for (var key in UI_FIELD_TYPES) {
            if (UI_FIELD_TYPES[key].category === category) {
                types.push(UI_FIELD_TYPES[key]);
            }
        }
        return types;
    };
    
    // Get all categories
    this.getCategories = function() {
        var categories = [];
        for (var key in UI_FIELD_TYPES) {
            var category = UI_FIELD_TYPES[key].category;
            if (categories.indexOf(category) === -1) {
                categories.push(category);
            }
        }
        return categories.sort();
    };
    
    // Check if field type supports options (picklist, radio, etc.)
    this.hasOptions = function(id) {
        var type = this.getById(id);
        return type ? (type.hasOptions || false) : false;
    };
    
    // Check if field type supports multiple selections
    this.isMultiSelect = function(id) {
        var type = this.getById(id);
        return type ? (type.multiSelect || false) : false;
    };
    
    // Get suggested data type for UI field type
    this.getSuggestedDataType = function(id) {
        var type = this.getById(id);
        return type ? type.dataType : 'VARCHAR';
    };
    
    // Get validation pattern for UI field type
    this.getValidationPattern = function(id) {
        var type = this.getById(id);
        return type ? (type.validation || null) : null;
    };
    
    // Get all UI field types as array
    this.getAllTypes = function() {
        var types = [];
        for (var key in UI_FIELD_TYPES) {
            types.push(UI_FIELD_TYPES[key]);
        }
        return types.sort(function(a, b) {
            return a.id - b.id;
        });
    };
    
    // Get field types for dropdown/select options
    this.getSelectOptions = function() {
        var options = [];
        var categories = this.getCategories();
        
        categories.forEach(function(category) {
            var categoryTypes = this.getByCategory(category);
            categoryTypes.forEach(function(type) {
                options.push({
                    value: type.id,
                    label: type.name,
                    category: category
                });
            });
        }.bind(this));
        
        return options;
    };
    
    // Validate field configuration against UI type
    this.validateFieldConfig = function(field) {
        var errors = [];
        var type = this.getById(field.uiType);
        
        if (!type) {
            errors.push('Invalid UI field type');
            return errors;
        }
        
        // Validate data type compatibility
        if (field.dataType !== type.dataType) {
            errors.push('Data type should be ' + type.dataType + ' for ' + type.name);
        }
        
        // Validate max length for text types
        if (type.maxLength && field.maxLength > type.maxLength) {
            errors.push('Maximum length cannot exceed ' + type.maxLength + ' for ' + type.name);
        }
        
        // Validate options for picklist types
        if (type.hasOptions && (!field.picklistOptions || field.picklistOptions.length === 0)) {
            errors.push(type.name + ' requires at least one option');
        }
        
        // Validate decimal places for decimal types
        if (type.decimalPlaces !== undefined && field.decimalPlaces > 10) {
            errors.push('Decimal places cannot exceed 10');
        }
        
        return errors;
    };
    
    // Get default field configuration for UI type
    this.getDefaultFieldConfig = function(uiType) {
        var type = this.getById(uiType);
        if (!type) return {};
        
        var config = {
            uiType: type.id,
            dataType: type.dataType,
            isRequired: false,
            isUnique: false,
            showInList: true,
            showInForm: true,
            isSearchable: true
        };
        
        // Add type-specific defaults
        if (type.maxLength) {
            config.maxLength = type.maxLength;
        }
        
        if (type.decimalPlaces !== undefined) {
            config.decimalPlaces = type.decimalPlaces;
        }
        
        if (type.hasOptions) {
            config.picklistOptions = [];
        }
        
        if (type.validation) {
            config.validationPattern = type.validation;
        }
        
        if (type.accept) {
            config.acceptedFileTypes = type.accept;
        }
        
        return config;
    };
});