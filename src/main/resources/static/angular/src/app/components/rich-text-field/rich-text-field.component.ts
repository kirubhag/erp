import { Component, Input, Output, EventEmitter, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ErpField } from '../../models/erp-field.model';

/**
 * Rich Text Editor Component (ui_type 120)
 * Provides rich text editing capabilities with formatting toolbar
 * Note: This is a basic implementation. For production, consider using Quill or TinyMCE
 */
@Component({
    selector: 'app-rich-text-field',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
        <div class="rich-text-container">
            <label class="field-label">
                {{ field.fieldLabel }}
                <span *ngIf="field.isRequired" class="required">*</span>
            </label>
            
            <!-- View Mode -->
            <div *ngIf="mode === 'view'" class="rich-text-display" [innerHTML]="sanitizedValue"></div>
            
            <!-- Edit/Create Mode -->
            <div *ngIf="mode !== 'view'" class="rich-text-editor">
                <!-- Toolbar -->
                <div class="toolbar">
                    <button type="button" (click)="execCommand('bold')" title="Bold" [disabled]="disabled">
                        <i class="fas fa-bold"></i>
                    </button>
                    <button type="button" (click)="execCommand('italic')" title="Italic" [disabled]="disabled">
                        <i class="fas fa-italic"></i>
                    </button>
                    <button type="button" (click)="execCommand('underline')" title="Underline" [disabled]="disabled">
                        <i class="fas fa-underline"></i>
                    </button>
                    <span class="separator"></span>
                    <button type="button" (click)="execCommand('insertUnorderedList')" title="Bullet List" [disabled]="disabled">
                        <i class="fas fa-list-ul"></i>
                    </button>
                    <button type="button" (click)="execCommand('insertOrderedList')" title="Numbered List" [disabled]="disabled">
                        <i class="fas fa-list-ol"></i>
                    </button>
                    <span class="separator"></span>
                    <button type="button" (click)="execCommand('justifyLeft')" title="Align Left" [disabled]="disabled">
                        <i class="fas fa-align-left"></i>
                    </button>
                    <button type="button" (click)="execCommand('justifyCenter')" title="Align Center" [disabled]="disabled">
                        <i class="fas fa-align-center"></i>
                    </button>
                    <button type="button" (click)="execCommand('justifyRight')" title="Align Right" [disabled]="disabled">
                        <i class="fas fa-align-right"></i>
                    </button>
                    <span class="separator"></span>
                    <button type="button" (click)="execCommand('removeFormat')" title="Clear Formatting" [disabled]="disabled">
                        <i class="fas fa-remove-format"></i>
                    </button>
                </div>
                
                <!-- Editor Area -->
                <div #editor
                     class="editor-content"
                     contenteditable="true"
                     [attr.disabled]="disabled ? true : null"
                     (input)="onContentChange()"
                     (blur)="onBlur()"
                     [innerHTML]="value">
                </div>
                
                <small *ngIf="field.fieldDescription" class="help-text">
                    {{ field.fieldDescription }}
                </small>
            </div>
        </div>
    `,
    styles: [`
        .rich-text-container {
            margin-bottom: 1rem;
        }
        
        .field-label {
            display: block;
            font-weight: 500;
            margin-bottom: 0.5rem;
        }
        
        .required {
            color: #dc3545;
        }
        
        .rich-text-editor {
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            overflow: hidden;
        }
        
        .toolbar {
            display: flex;
            gap: 0.25rem;
            padding: 0.5rem;
            background: #f8f9fa;
            border-bottom: 1px solid #dee2e6;
        }
        
        .toolbar button {
            padding: 0.375rem 0.5rem;
            border: 1px solid #dee2e6;
            background: white;
            border-radius: 0.25rem;
            cursor: pointer;
            color: #495057;
        }
        
        .toolbar button:hover:not(:disabled) {
            background: #e9ecef;
        }
        
        .toolbar button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        .separator {
            width: 1px;
            background: #dee2e6;
            margin: 0 0.25rem;
        }
        
        .editor-content {
            min-height: 150px;
            max-height: 400px;
            overflow-y: auto;
            padding: 0.75rem;
            background: white;
            outline: none;
        }
        
        .editor-content[disabled] {
            background: #e9ecef;
            cursor: not-allowed;
        }
        
        .editor-content:focus {
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        
        .rich-text-display {
            padding: 0.75rem;
            border: 1px solid #dee2e6;
            border-radius: 0.25rem;
            background: #f8f9fa;
            min-height: 50px;
        }
        
        .help-text {
            display: block;
            margin-top: 0.25rem;
            color: #6c757d;
            font-size: 0.875rem;
            padding: 0 0.75rem 0.5rem;
        }
    `]
})
export class RichTextFieldComponent implements OnInit {
    @ViewChild('editor') editorElement!: ElementRef;

    @Input() field!: ErpField;
    @Input() value: string = '';
    @Input() mode: 'create' | 'edit' | 'view' = 'edit';
    @Input() disabled: boolean = false;

    @Output() valueChange = new EventEmitter<string>();

    sanitizedValue: string = '';

    ngOnInit(): void {
        this.sanitizedValue = this.value || '';
    }

    execCommand(command: string, value: string | null = null): void {
        document.execCommand(command, false, value || undefined);
        this.onContentChange();
    }

    onContentChange(): void {
        if (this.editorElement) {
            const content = this.editorElement.nativeElement.innerHTML;
            this.value = content;
            this.valueChange.emit(content);
        }
    }

    onBlur(): void {
        // Emit final value on blur
        this.onContentChange();
    }
}
