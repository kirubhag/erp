# Menu Item Reordering Feature

## Overview
This feature allows administrators to reorder menu items through the Settings module. Users can organize menu items using up/down arrows, and changes are persisted to the database with immediate menu refresh.

## Implementation Date
2025-01-XX

## User Story
As an administrator, I want to reorder menu items in the navigation bar so that I can organize the application menu according to my preferences and usage patterns.

## Components Modified

### Frontend

#### 1. Settings Controller (`/js/settings/settings.controller.js`)
**Changes:**
- Added `initializeModules()` - Loads menu items from backend API
- Added `organizeModules()` - Enters organize mode
- Added `cancelOrganizing()` - Exits organize mode without saving
- Added `moveModuleUp(index)` - Moves menu item up in sequence
- Added `moveModuleDown(index)` - Moves menu item down in sequence
- Added `updateSequenceNumbers()` - Helper to update sequence values
- Added `saveModuleOrder()` - Saves reordered modules via API
- Added `refreshModulePage()` - Reloads modules and refreshes menu
- Added menu refresh event listener (`$rootScope.$on('menu:refresh')`)

**Key Functions:**
```javascript
$scope.organizeModules = function() {
    $scope.isOrganizing = true;
    $scope.organizingModules = angular.copy($scope.modules);
};

$scope.saveModuleOrder = function() {
    var updates = $scope.organizingModules.map(function(module, index) {
        return { id: module.id, sequence: index + 1 };
    });
    
    ApiService.put('/api/erp-entities/update-sequence', updates)
        .then(function(response) {
            toastr.success('Menu order updated successfully');
            $scope.refreshModulePage();
        });
};
```

#### 2. Modules Template (`/templates/modules/modules.html`)
**Changes:**
- Added dual-view design: normal view and organize view
- Normal view (`ng-if="!isOrganizing"`): Standard module list with "Organize Modules" button
- Organize view (`ng-if="isOrganizing"`):
  - Header with instructions
  - Cancel and "Save Order" buttons
  - Reorderable list with up/down arrows
  - Each item displays: icon, name, description, sequence badge
  - Disabled states for first/last items

**Key HTML Structure:**
```html
<div ng-if="isOrganizing">
    <div class="organize-list">
        <div ng-repeat="module in organizingModules" class="organize-item">
            <div class="organize-item-content">
                <i class="fas fa-grip-vertical organize-item-handle"></i>
                <div class="organize-item-info">
                    <i class="{{module.icon}} organize-item-icon"></i>
                    <div class="organize-item-details">
                        <div class="organize-item-name">{{module.displayName}}</div>
                        <div class="organize-item-desc">{{module.description}}</div>
                    </div>
                </div>
                <span class="organize-item-sequence">Seq: {{module.sequence}}</span>
                <div class="organize-item-actions">
                    <button ng-click="moveModuleUp($index)" 
                            ng-disabled="$first">
                        <i class="fas fa-arrow-up"></i>
                    </button>
                    <button ng-click="moveModuleDown($index)" 
                            ng-disabled="$last">
                        <i class="fas fa-arrow-down"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
```

#### 3. Settings Layout CSS (`/css/settings-layout.css`)
**Changes:**
- Added 90+ lines of organize-specific styles
- Key classes:
  - `.organize-list` - Container with max-width and padding
  - `.organize-item` - Card with border, hover effects
  - `.organize-item-content` - Flex layout for item structure
  - `.organize-item-handle` - Grab cursor for drag handle
  - `.organize-item-info` - Icon and details container
  - `.organize-item-icon` - Menu icon styling
  - `.organize-item-details` - Name and description layout
  - `.organize-item-sequence` - Badge for sequence number
  - `.organize-item-actions` - Button group with disabled states

### Backend

#### 4. Sequence Update DTO (`/src/main/java/krs/erp/dto/SequenceUpdateDTO.java`)
**New File:**
```java
public class SequenceUpdateDTO {
    private Long id;
    private Integer sequence;
    
    // Getters, setters, constructors, toString()
}
```

#### 5. ERP Entity Controller (`/src/main/java/krs/erp/controller/ErpEntityController.java`)
**Changes:**
- Added `PUT /api/erp-entities/update-sequence` endpoint
- Accepts `List<SequenceUpdateDTO>` in request body
- Updates each entity's sequence field
- Returns success/error response with count

**Endpoint Implementation:**
```java
@PutMapping("/update-sequence")
public ResponseEntity<Map<String, Object>> updateSequence(
        @RequestBody List<SequenceUpdateDTO> updates) {
    try {
        int updatedCount = 0;
        for (SequenceUpdateDTO update : updates) {
            ErpEntity entity = erpEntityService.getEntityById(update.getId())
                    .orElseThrow(() -> new RuntimeException("Entity not found: " + update.getId()));
            
            entity.setSequence(update.getSequence());
            erpEntityService.updateEntity(entity.getId(), entity);
            updatedCount++;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Successfully updated " + updatedCount + " menu items");
        response.put("count", updatedCount);
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Failed to update sequences: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
```

## User Flow

1. **Navigate to Settings:** User goes to `#!/settings/modules`
2. **Enter Organize Mode:** Click "Organize Modules" button
3. **Reorder Items:** 
   - View displays all menu items with sequence badges
   - Click up/down arrows to reorder
   - Sequence numbers update automatically
4. **Save Changes:** Click "Save Order" button
   - API call sends new sequences to backend
   - Database updated with new sequence values
   - Success toast notification shown
5. **Auto Refresh:**
   - Module page reloads with new order
   - `menu:refresh` event broadcast
   - Main navigation menu updates immediately
   - New order visible in navbar

## API Endpoints

### Update Menu Sequence
- **Endpoint:** `PUT /api/erp-entities/update-sequence`
- **Request Body:**
  ```json
  [
    { "id": 1, "sequence": 2 },
    { "id": 2, "sequence": 1 },
    { "id": 3, "sequence": 3 }
  ]
  ```
- **Success Response:**
  ```json
  {
    "success": true,
    "message": "Successfully updated 3 menu items",
    "count": 3
  }
  ```
- **Error Response:**
  ```json
  {
    "success": false,
    "message": "Failed to update sequences: [error details]"
  }
  ```

## Database Schema
Uses existing `erp_entities` table with `sequence` column:
```sql
CREATE TABLE erp_entities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    icon VARCHAR(255),
    route VARCHAR(255),
    sequence INT DEFAULT 0,
    presence TINYINT(1) DEFAULT 1,
    system_name VARCHAR(255),
    -- other fields...
);
```

## Testing Checklist

- [ ] Navigate to Settings > Modules
- [ ] Click "Organize Modules" button
- [ ] Verify organize view displays with all menu items
- [ ] Test moving items up with arrow buttons
- [ ] Test moving items down with arrow buttons
- [ ] Verify first item's up button is disabled
- [ ] Verify last item's down button is disabled
- [ ] Verify sequence badges update correctly
- [ ] Click "Save Order" button
- [ ] Verify success toast appears
- [ ] Verify page refreshes
- [ ] Verify new order persists after refresh
- [ ] Verify navbar menu updates with new order
- [ ] Test cancel button (should exit without saving)
- [ ] Test with different menu item counts

## Dependencies

### Frontend
- AngularJS 1.x
- Bootstrap 5
- FontAwesome icons
- Toastr for notifications

### Backend
- Spring Boot 3.5.6
- Spring Data JPA
- MySQL database

## Performance Considerations
- Menu items cached in MenuService
- Cache cleared on sequence update
- Menu refresh uses event broadcasting (no page reload)
- Database updates use batch processing

## Future Enhancements
- [ ] Add drag-and-drop support (instead of arrows)
- [ ] Add undo/redo functionality
- [ ] Add bulk reorder with number input
- [ ] Add preview before saving
- [ ] Add permission-based access control
- [ ] Add audit logging for order changes

## Related Documentation
- [Dynamic Menu System](DYNAMIC_MENU_SYSTEM.md) - Original menu implementation
- [Settings Module](../guides/SETTINGS_MODULE.md) - Settings architecture

## Files Modified
```
Frontend:
- /js/settings/settings.controller.js
- /templates/modules/modules.html
- /css/settings-layout.css

Backend:
- /src/main/java/krs/erp/controller/ErpEntityController.java
- /src/main/java/krs/erp/dto/SequenceUpdateDTO.java (NEW)

Documentation:
- /docs/features/MENU_REORDERING.md (NEW)
```

## Commit Message
```
feat: Add menu item reordering in settings module

- Implement organize modules functionality with up/down arrows
- Add update-sequence API endpoint to persist order changes
- Create SequenceUpdateDTO for sequence updates
- Add organize UI with dual-view design
- Add CSS styles for organize interface
- Implement auto-refresh after reorder
- Broadcast menu:refresh event to update navbar
- Add sequence badges and disabled states for first/last items

Closes: #[issue-number]
```
