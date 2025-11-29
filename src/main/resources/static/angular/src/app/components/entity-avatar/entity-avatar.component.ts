import { Component, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-entity-avatar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entity-avatar.component.html',
  styleUrls: ['./entity-avatar.component.css']
})
export class EntityAvatarComponent {
  @Input() entityId!: string | number;
  @Input() entityType!: string; // 'USER', 'COMPANY', 'STUDENT', 'STAFF', etc.
  @Input() entityName: string = '';
  @Input() organizationId!: number;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() showControls: boolean = true; // Show upload/view/delete buttons
  @Input() clickable: boolean = true; // Make avatar clickable to open view modal
  @Input() hasAvatarData: boolean = false; // Whether entity has an avatar (to prevent unnecessary API calls)

  @Output() avatarUpdated = new EventEmitter<string>();
  @Output() avatarDeleted = new EventEmitter<void>();

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('cropCanvas') cropCanvas!: ElementRef<HTMLCanvasElement>;

  avatarUrl: string = '';
  hasAvatar: boolean = false;
  avatarLoadError: boolean = false;

  // View modal
  showViewModal: boolean = false;

  // Upload modal
  showUploadModal: boolean = false;
  selectedFile: File | null = null;
  uploadProgress: number = 0;

  // Crop modal
  showCropModal: boolean = false;
  cropImageSrc: string = '';
  croppedImageBlob: Blob | null = null;
  zoom: number = 1;
  rotation: number = 0;

  // Messages
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.loadAvatar();
  }

  ngOnChanges() {
    this.loadAvatar();
  }

  /**
   * Load avatar URL for the entity
   */
  loadAvatar() {
    // Only load avatar if entity has avatar data or if controls are shown (detail page)
    if (this.entityId && this.organizationId && (this.hasAvatarData || this.showControls)) {
      // Normalize entity type to uppercase for API consistency
      const normalizedEntityType = this.entityType ? this.entityType.toUpperCase() : 'USER';
      this.avatarUrl = `/api/attachments/avatar/${this.entityId}?entityType=${normalizedEntityType}&t=${Date.now()}`;
      this.hasAvatar = this.hasAvatarData;
      this.avatarLoadError = false;
    } else {
      // No avatar data, show initials
      this.hasAvatar = false;
      this.avatarLoadError = true;
    }
  }

  /**
   * Handle avatar load error
   */
  onAvatarError() {
    this.avatarLoadError = true;
    this.hasAvatar = false;
  }

  /**
   * Get user initials from name
   */
  getInitials(): string {
    if (!this.entityName) return '??';
    const names = this.entityName.split(' ');
    if (names.length >= 2) {
      return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
    }
    return this.entityName.substring(0, 2).toUpperCase();
  }

  /**
   * Get avatar size class
   */
  getSizeClass(): string {
    return `avatar-${this.size}`;
  }

  /**
   * Open view modal
   */
  viewAvatar() {
    if (this.hasAvatar && !this.avatarLoadError) {
      this.showViewModal = true;
    }
  }

  /**
   * Close view modal
   */
  closeViewModal() {
    this.showViewModal = false;
  }

  /**
   * Open file picker for upload
   */
  openUploadDialog() {
    this.fileInput.nativeElement.click();
  }

  /**
   * Handle file selection
   */
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;

      // Validate file
      if (!file.type.startsWith('image/')) {
        this.errorMessage = 'Please select an image file';
        setTimeout(() => this.errorMessage = '', 3000);
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = 'File size must be less than 5MB';
        setTimeout(() => this.errorMessage = '', 3000);
        return;
      }

      // Open crop modal
      this.openCropModal();
    }
  }

  /**
   * Open crop modal with selected image
   */
  openCropModal() {
    if (!this.selectedFile) return;

    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.cropImageSrc = e.target.result;
      this.showCropModal = true;
      this.zoom = 1;
      this.rotation = 0;

      // Draw initial image on canvas
      setTimeout(() => this.drawImageOnCanvas(), 100);
    };
    reader.readAsDataURL(this.selectedFile);
  }

  /**
   * Close crop modal
   */
  closeCropModal() {
    this.showCropModal = false;
    this.cropImageSrc = '';
    this.selectedFile = null;
    this.zoom = 1;
    this.rotation = 0;
  }

  /**
   * Draw image on canvas with zoom and rotation
   */
  drawImageOnCanvas() {
    if (!this.cropCanvas) return;

    const canvas = this.cropCanvas.nativeElement;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const img = new Image();
    img.onload = () => {
      // Clear canvas
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // Save context state
      ctx.save();

      // Move to center
      ctx.translate(canvas.width / 2, canvas.height / 2);

      // Apply rotation
      ctx.rotate((this.rotation * Math.PI) / 180);

      // Apply zoom
      const scale = this.zoom;
      ctx.scale(scale, scale);

      // Draw image centered
      ctx.drawImage(img, -img.width / 2, -img.height / 2);

      // Restore context state
      ctx.restore();
    };
    img.src = this.cropImageSrc;
  }

  /**
   * Handle zoom change
   */
  onZoomChange() {
    this.drawImageOnCanvas();
  }

  /**
   * Handle rotation change
   */
  onRotationChange() {
    this.drawImageOnCanvas();
  }

  /**
   * Save cropped avatar
   */
  saveCroppedAvatar() {
    if (!this.cropCanvas) return;

    const canvas = this.cropCanvas.nativeElement;
    canvas.toBlob((blob) => {
      if (blob) {
        this.croppedImageBlob = blob;
        this.uploadAvatar();
      }
    }, 'image/jpeg', 0.9);
  }

  /**
   * Upload avatar to server
   */
  uploadAvatar() {
    if (!this.croppedImageBlob) return;

    // Validate required parameters
    if (!this.entityId) {
      this.errorMessage = 'Entity ID is required';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    if (!this.organizationId) {
      this.errorMessage = 'Organization ID is required';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    const formData = new FormData();
    formData.append('file', this.croppedImageBlob, 'avatar.jpg');
    formData.append('entityType', this.entityType ? this.entityType.toUpperCase() : 'USER');
    formData.append('entityId', this.entityId.toString());
    formData.append('organizationId', this.organizationId.toString());

    this.http.post('/api/attachments/avatar/upload', formData).subscribe({
      next: (response: any) => {
        this.successMessage = 'Avatar uploaded successfully!';
        setTimeout(() => this.successMessage = '', 3000);

        // Mark as having avatar data
        this.hasAvatarData = true;

        // Update avatar URL
        this.loadAvatar();
        this.closeCropModal();

        // Emit event
        this.avatarUpdated.emit(this.avatarUrl);
      },
      error: (error) => {
        console.error('Error uploading avatar:', error);
        this.errorMessage = error.error?.message || 'Failed to upload avatar';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  /**
   * Delete avatar
   */
  deleteAvatar() {
    if (!confirm('Are you sure you want to delete this avatar?')) {
      return;
    }

    const normalizedEntityType = this.entityType ? this.entityType.toUpperCase() : 'USER';
    this.http.delete(
      `/api/attachments/avatar/${this.entityId}?organizationId=${this.organizationId}&entityType=${normalizedEntityType}`
    ).subscribe({
      next: () => {
        this.successMessage = 'Avatar deleted successfully!';
        setTimeout(() => this.successMessage = '', 3000);

        this.hasAvatar = false;
        this.hasAvatarData = false;
        this.avatarLoadError = true;
        this.avatarUrl = '';

        // Emit event
        this.avatarDeleted.emit();
      },
      error: (error) => {
        console.error('Error deleting avatar:', error);
        this.errorMessage = error.error?.message || 'Failed to delete avatar';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  /**
   * Handle avatar click
   */
  onAvatarClick(event: Event) {
    if (this.clickable && this.hasAvatar && !this.avatarLoadError) {
      event.stopPropagation();
      this.viewAvatar();
    }
  }
}
