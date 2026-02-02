import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-library-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './library-dashboard.component.html',
  styleUrls: ['./library-dashboard.component.css']
})
export class LibraryDashboardComponent implements OnInit {
  stats: any = {
    totalInventory: 0,
    totalBooksOut: 0,
    checkoutRate: 0,
    overdueCount: 0,
    todayTraffic: 0
  };
  
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchStats();
  }

  fetchStats() {
    this.http.get('/api/library/stats').subscribe({
      next: (data: any) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load library stats', err);
        this.loading = false;
      }
    });
  }
}
