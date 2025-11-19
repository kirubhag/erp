import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GridViewComponent, GridColumn, GridItem } from './grid-view.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

describe('GridViewComponent', () => {
  let component: GridViewComponent;
  let fixture: ComponentFixture<GridViewComponent>;

  const mockColumns: GridColumn[] = [
    { key: 'name', label: 'Name', type: 'text', sortable: true },
    { key: 'email', label: 'Email', type: 'email' },
    { key: 'status', label: 'Status', type: 'badge' }
  ];

  const mockItems: GridItem[] = [
    {
      id: 1,
      name: 'John Doe',
      email: 'john@example.com',
      status: 'Active'
    },
    {
      id: 2,
      name: 'Jane Smith',
      email: 'jane@example.com',
      status: 'Inactive'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GridViewComponent, CommonModule, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(GridViewComponent);
    component = fixture.componentInstance;
    component.columns = mockColumns;
    component.items = mockItems;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Display Mode', () => {
    it('should toggle between cards and list view', () => {
      expect(component.displayModeInternal).toBe('cards');
      component.switchMode('list');
      expect(component.displayModeInternal).toBe('list');
      component.switchMode('cards');
      expect(component.displayModeInternal).toBe('cards');
    });

    it('should render cards when displayMode is cards', () => {
      component.displayModeInternal = 'cards';
      fixture.detectChanges();
      const cardElement = fixture.nativeElement.querySelector('.grid-cards-container');
      expect(cardElement).toBeTruthy();
    });

    it('should render table when displayMode is list', () => {
      component.displayModeInternal = 'list';
      fixture.detectChanges();
      const tableElement = fixture.nativeElement.querySelector('.grid-table');
      expect(tableElement).toBeTruthy();
    });
  });

  describe('Item Selection', () => {
    it('should toggle item selection', () => {
      component.toggleItem(mockItems[0]);
      expect(component.isSelected(mockItems[0])).toBe(true);
      component.toggleItem(mockItems[0]);
      expect(component.isSelected(mockItems[0])).toBe(false);
    });

    it('should emit selectionChange event when item is toggled', () => {
      spyOn(component.selectionChange, 'emit');
      component.toggleItem(mockItems[0]);
      expect(component.selectionChange.emit).toHaveBeenCalled();
    });

    it('should toggle select all', () => {
      component.toggleSelectAll();
      expect(component.allSelected).toBe(true);
      expect(component.selectedItems.size).toBe(mockItems.length);
    });

    it('should clear selection when toggling select all again', () => {
      component.toggleSelectAll(); // Select all
      component.toggleSelectAll(); // Deselect all
      expect(component.allSelected).toBe(false);
      expect(component.selectedItems.size).toBe(0);
    });
  });

  describe('Column Operations', () => {
    it('should get first column', () => {
      const firstCol = component.getFirstColumn();
      expect(firstCol).toBe(mockColumns[0]);
    });

    it('should get card columns (skip first column)', () => {
      const cardCols = component.getCardColumns();
      expect(cardCols.length).toBe(2);
      expect(cardCols[0]).toBe(mockColumns[1]);
    });

    it('should get cell value from item', () => {
      const value = component.getCellValue(mockItems[0], mockColumns[0]);
      expect(value).toBe('John Doe');
    });

    it('should format value based on column type', () => {
      const column: GridColumn = {
        key: 'price',
        label: 'Price',
        type: 'currency'
      };
      const result = component.getCellValue({ id: 1, price: 99.99 }, column);
      expect(result).toContain('100');
    });
  });

  describe('Avatar Functions', () => {
    it('should generate avatar text from name', () => {
      const text = component.getAvatarText('John Doe');
      expect(text).toBe('JO');
    });

    it('should handle empty string for avatar text', () => {
      const text = component.getAvatarText('');
      expect(text).toBe('??');
    });

    it('should generate avatar color class', () => {
      const color = component.getAvatarClass('John');
      expect(color).toMatch(/bg-(primary|secondary|success|danger|warning|info)/);
    });
  });

  describe('Badge Functions', () => {
    it('should return success badge class for true values', () => {
      const badgeClass = component.getBadgeClass(true);
      expect(badgeClass).toBe('badge-success');
    });

    it('should return danger badge class for false values', () => {
      const badgeClass = component.getBadgeClass(false);
      expect(badgeClass).toBe('badge-danger');
    });

    it('should return appropriate badge text', () => {
      expect(component.getBadgeText(true)).toBe('Active');
      expect(component.getBadgeText(false)).toBe('Inactive');
      expect(component.getBadgeText('Custom')).toBe('Custom');
    });
  });

  describe('Events', () => {
    it('should emit itemClick when card is clicked', () => {
      spyOn(component.itemClick, 'emit');
      component.onCardClick(mockItems[0]);
      expect(component.itemClick.emit).toHaveBeenCalledWith(mockItems[0]);
    });

    it('should emit actionClick when action button is clicked', () => {
      spyOn(component.actionClick, 'emit');
      const event = new MouseEvent('click');
      component.onActionClick('edit', mockItems[0], event);
      expect(component.actionClick.emit).toHaveBeenCalledWith({
        action: 'edit',
        item: mockItems[0]
      });
    });
  });

  describe('Responsive Grid Columns', () => {
    it('should return correct column classes for different card columns', () => {
      component.cardColumns = 3;
      expect(component.getGridColClasses()).toContain('col-lg-4');

      component.cardColumns = 4;
      expect(component.getGridColClasses()).toContain('col-lg-3');

      component.cardColumns = 2;
      expect(component.getGridColClasses()).toContain('col-lg-6');
    });
  });

  describe('Text Truncation', () => {
    it('should truncate long text', () => {
      const longText = 'a'.repeat(100);
      const result = component.truncate(longText, 50);
      expect(result.length).toBeLessThanOrEqual(53); // 50 chars + '...'
      expect(result.endsWith('...')).toBe(true);
    });

    it('should not truncate short text', () => {
      const shortText = 'short';
      const result = component.truncate(shortText, 50);
      expect(result).toBe('short');
    });
  });

  describe('Loading State', () => {
    it('should display loading spinner when loading is true', () => {
      component.loading = true;
      fixture.detectChanges();
      const spinner = fixture.nativeElement.querySelector('.grid-loading');
      expect(spinner).toBeTruthy();
    });

    it('should not display items when loading is true', () => {
      component.loading = true;
      fixture.detectChanges();
      const cardsContainer = fixture.nativeElement.querySelector('.grid-cards-container');
      expect(cardsContainer).toBeFalsy();
    });
  });

  describe('Empty State', () => {
    it('should display empty message when no items', () => {
      component.items = [];
      component.loading = false;
      fixture.detectChanges();
      const emptyState = fixture.nativeElement.querySelector('.grid-empty');
      expect(emptyState).toBeTruthy();
    });

    it('should display custom empty message', () => {
      component.items = [];
      component.loading = false;
      component.emptyMessage = 'Custom empty message';
      fixture.detectChanges();
      const message = fixture.nativeElement.textContent;
      expect(message).toContain('Custom empty message');
    });
  });

  describe('Track By Function', () => {
    it('should return item id for tracking', () => {
      const result = component.trackByFn(0, mockItems[0]);
      expect(result).toBe(mockItems[0].id);
    });
  });
});
