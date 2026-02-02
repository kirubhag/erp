import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';

@Component({
    selector: 'app-library-search',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './library-search.component.html',
    styleUrls: ['./library-search.component.css']
})
export class LibrarySearchComponent {
    searchQuery = '';
    results: any[] = [];
    loading = false;
    searchSubject = new Subject<string>();

    constructor(private http: HttpClient) {
        this.searchSubject.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap(query => {
                this.loading = true;
                return this.http.get<any[]>(`/api/library/search?query=${query}`);
            })
        ).subscribe(results => {
            this.results = results;
            this.loading = false;
        });
    }

    onSearch(query: string) {
        this.searchSubject.next(query);
    }
}
