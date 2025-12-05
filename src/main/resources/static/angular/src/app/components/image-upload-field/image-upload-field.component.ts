import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ErpField } from '../../models/erp-field.model';

/**
 * Image Upload Component (ui_type 118)
 * Handles image uploads with preview and validation
 */
@Component({
    selector: 'app-image-upload-field',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="image-upload-container">
            <label class="field-label">
                {{ field.fieldLabel }}
                <span *ngIf="field.isRequired" class="required">*</span>
            </label>
            
            <!-- View Mode -->
            <div *ngIf="mode === 'view' && value" class="image-display">
                <img [src]="value" [alt]="field.fieldLabel">
            </div>
            <div *ngIf="mode === 'view' && !value" class="no-image">No image uploaded</div>
            
            <!-- Edit/Create Mode -->
            <div *ngIf="mode !== 'view'" class="image-upload-input">
                <input type="file"
                       #imageInput
                       (change)="onImageSelected($event)"
                       accept="image/*"
                       [disabled]="disabled || uploading"
                       style="display: none;">
                
                <div class="upload-area" (click)="imageInput.click()" [class.disabled]="disabled || uploading">
                    <div *ngIf="!previewUrl && !uploading">
                        <i class="fas fa-image fa-3x"></i>
                        <p>Click to upload image</p>
                        <small>JPG, PNG, GIF up to {{ formatFileSize(maxFileSize) }}</small>
                    </div>
                    
                    <div *ngIf="uploading" class="uploading">
                        <div class="spinner-border" role="status"></div>
                        <p>Uploading... {{ uploadProgress }}%</p>
                        <div class="progress">
                            <div class="progress-bar" [style.width.%]="uploadProgress"></div>
                        </div>
                    </div>
                    
                    <div *ngIf="previewUrl && !uploading" class="image-preview">
                        <img [src]="previewUrl" [alt]="field.fieldLabel">
                        <div class="image-overlay">
                            <button type="button" class="btn-change" (click)="imageInput.click(); $event.stopPropagation()">
                                <i class="fas fa-sync-alt"></i> Change
                            </button>
                            <button type="button" class="btn-remove" (click)="removeImage($event)">
                                <i class="fas fa-trash"></i> Remove
                            </button>
                        </div>
                    </div>
                </div>
                
                <div *ngIf="error" class="error-message">
                    <i class="fas fa-exclamation-circle"></i> {{ error }}
                </div>
                
                <small *ngIf="field.fieldDescription" class="help-text">
                    {{ field.fieldDescription }}
                </small>
            </div>
        </div>
    `,
    styles: [`
        .image-upload-container {
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
        
        .upload-area {
            border: 2px dashed #ced4da;
            border-radius: 0.25rem;
            padding: 2rem;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            min-height: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .upload-area:hover:not(.disabled) {
            border-color: #007bff;
            background: #f8f9fa;
        }
        
        .upload-area.disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        .upload-area i {
            color: #6c757d;
            margin-bottom: 0.5rem;
        }
        
        .upload-area p {
            margin: 0.5rem 0;
            color: #495057;
        }
        
        .upload-area small {
            color: #6c757d;
        }
        
        .uploading {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 0.5rem;
            width: 100%;
        }
        
        .progress {
            width: 100%;
            max-width: 300px;
            height: 0.5rem;
            background: #e9ecef;
            border-radius: 0.25rem;
            overflow: hidden;
        }
        
        .progress-bar {
            height: 100%;
            background: #007bff;
            transition: width 0.3s;
        }
        
        .image-preview {
            position: relative;
            max-width: 100%;
        }
        
        .image-preview img {
            max-width: 100%;
            max-height: 300px;
            border-radius: 0.25rem;
        }
        
        .image-overlay {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: rgba(0, 0, 0, 0.7);
            padding: 0.5rem;
            display: flex;
            gap: 0.5rem;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s;
        }
        
        .image-preview:hover .image-overlay {
            opacity: 1;
        }
        
        .btn-change, .btn-remove {
            padding: 0.375rem 0.75rem;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
            font-size: 0.875rem;
            color: white;
        }
        
        .btn-change {
            background: #007bff;
        }
        
        .btn-change:hover {
            background: #0056b3;
        }
        
        .btn-remove {
            background: #dc3545;
        }
        
        .btn-remove:hover {
            background: #c82333;
        }
        
        .image-display {
            padding: 0.5rem;
            border: 1px solid #dee2e6;
            border-radius: 0.25rem;
            background: #f8f9fa;
        }
        
        .image-display img {
            max-width: 100%;
            max-height: 400px;
            border-radius: 0.25rem;
        }
        
        .no-image {
            color: #6c757d;
            font-style: italic;
        }
        
        .error-message {
            color: #dc3545;
            margin-top: 0.5rem;
            font-size: 0.875rem;
        }
        
        .help-text {
            display: block;
            margin-top: 0.25rem;
            color: #6c757d;
            font-size: 0.875rem;
        }
    `]
})
export class ImageUploadFieldComponent {
    @Input() field!: ErpField;
    @Input() value: string = '';
    @Input() mode: 'create' | 'edit' | 'view' = 'edit';
    @Input() disabled: boolean = false;
    @Input() maxFileSize: number = 5 * 1024 * 1024; // 5MB default

    @Output() valueChange = new EventEmitter<string>();

    previewUrl: string = '';
    uploading: boolean = false;
    uploadProgress: number = 0;
    error: string = '';

    constructor(private http: HttpClient) { }

    ngOnInit(): void {
        if (this.value) {
            this.previewUrl = this.value;
        }
    }

    onImageSelected(event: any): void {
        const file = event.target.files[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            this.error = 'Please select a valid image file';
            return;
        }

        // Validate file size
        if (file.size > this.maxFileSize) {
            this.error = `Image size exceeds ${this.formatFileSize(this.maxFileSize)}`;
            return;
        }

        this.error = '';

        // Create preview
        const reader = new FileReader();
        reader.onload = (e: any) => {
            this.previewUrl = e.target.result;
        };
        reader.readAsDataURL(file);

        // Upload file
        this.uploadImage(file);
    }

    uploadImage(file: File): void {
        this.uploading = true;
        this.uploadProgress = 0;

        const formData = new FormData();
        formData.append('image', file);

        // Simulate upload progress (replace with actual upload API)
        const interval = setInterval(() => {
            this.uploadProgress += 10;
            if (this.uploadProgress >= 100) {
                clearInterval(interval);
                this.uploading = false;
                // In production, this would be the URL returned from the server
                this.value = `/uploads/images/${file.name}`;
                this.valueChange.emit(this.value);
            }
        }, 200);

        // TODO: Replace with actual upload API call
        // this.http.post('/api/upload/image', formData).subscribe({
        //   next: (response: any) => {
        //     this.value = response.url;
        //     this.valueChange.emit(this.value);
        //     this.uploading = false;
        //   },
        //   error: (error) => {
        //     this.error = 'Upload failed. Please try again.';
        //     this.uploading = false;
        //   }
        // });
    }

    removeImage(event: Event): void {
        event.stopPropagation();
        this.previewUrl = '';
        this.value = '';
        this.error = '';
        this.valueChange.emit('');
    }

    formatFileSize(bytes: number): string {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
    }
}
