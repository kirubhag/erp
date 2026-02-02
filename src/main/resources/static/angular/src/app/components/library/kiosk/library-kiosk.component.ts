import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-library-kiosk',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './library-kiosk.component.html',
    styleUrls: ['./library-kiosk.component.css']
})
export class LibraryKioskComponent {
    mode: 'SELECT' | 'ISSUE' | 'RETURN' = 'SELECT';
    step: 'USER_SCAN' | 'BOOK_SCAN' | 'SUCCESS' | 'ERROR' = 'USER_SCAN';

    userBarcode: string = '';
    bookBarcode: string = '';

    currentUser: any = null;
    scannedBook: any = null;

    successMessage: string = '';
    errorMessage: string = '';

    constructor(private http: HttpClient) { }

    selectMode(mode: 'ISSUE' | 'RETURN') {
        this.mode = mode;
        this.step = mode === 'ISSUE' ? 'USER_SCAN' : 'BOOK_SCAN'; // Return starts with book scan
        this.resetState();
    }

    resetState() {
        this.userBarcode = '';
        this.bookBarcode = '';
        this.currentUser = null;
        this.scannedBook = null;
        this.errorMessage = '';
        setTimeout(() => {
            const input = document.getElementById('barcodeInput');
            if (input) input.focus();
        }, 100);
    }

    onUserScan() {
        // Simulate user fetch
        if (this.userBarcode.length > 3) {
            // Mock user fetch
            this.currentUser = { id: 101, name: 'John Doe', photoUrl: '', booksOut: 2 };
            this.step = 'BOOK_SCAN';
            this.userBarcode = ''; // Clear for next input but keep currentUser
        }
    }

    onBookScan() {
        if (this.bookBarcode.length > 3) {
            if (this.mode === 'ISSUE') {
                this.processIssue();
            } else {
                this.processReturn();
            }
        }
    }

    processIssue() {
        // API call to issue
        const payload = new FormData();
        payload.append('itemId', '1'); // Mock ID, real implementation needs lookup
        payload.append('userId', this.currentUser.id.toString());

        // For Demo purposes, let's just show success
        this.step = 'SUCCESS';
        this.successMessage = `Book Issued Successfully!`;
    }

    processReturn() {
        // API call to return
        const payload = new FormData();
        payload.append('itemId', '1'); // Mock ID

        this.step = 'SUCCESS';
        this.successMessage = `Book Returned Successfully!`;
    }

    goHome() {
        this.mode = 'SELECT';
        this.step = 'USER_SCAN';
        this.resetState();
    }
}
