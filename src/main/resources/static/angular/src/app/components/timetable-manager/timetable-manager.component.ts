import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-timetable-manager',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './timetable-manager.component.html',
    styleUrls: ['./timetable-manager.component.css']
})
export class TimetableManagerComponent implements OnInit {
    classes: any[] = [];
    selectedClassId: string = '';
    timetable: any[] = []; // { day, period, subjectId, teacherId, roomId }

    // Lists for dropdowns
    subjects: any[] = [];
    teachers: any[] = [];
    rooms: any[] = [];

    // Modal State
    showModal: boolean = false;
    currentSlot: { day: string, period: number, subjectId: string, teacherId: string, roomId: string } =
        { day: '', period: 0, subjectId: '', teacherId: '', roomId: '' };

    // Grid Constraints
    days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
    periods = Array.from({ length: 8 }, (_, i) => i + 1);

    constructor(private http: HttpClient) { }

    ngOnInit() {
        this.loadMockData();
    }

    loadMockData() {
        // Mock Data simulating API response
        this.classes = [
            { id: 'C01', name: 'Class 10-A' },
            { id: 'C02', name: 'Class 10-B' }
        ];

        this.subjects = [
            { id: 'S01', name: 'Mathematics' },
            { id: 'S02', name: 'Physics' },
            { id: 'S03', name: 'Chemistry' },
            { id: 'S04', name: 'English' },
            { id: 'S05', name: 'History' }
        ];

        this.teachers = [
            { id: 'T01', name: 'John Smith', specialization: 'Math' },
            { id: 'T02', name: 'Sarah Jones', specialization: 'Science' },
            { id: 'T03', name: 'Mike Brown', specialization: 'History' }
        ];

        this.rooms = [
            { id: 'R101', name: 'Room 101', type: 'GENERAL' },
            { id: 'R102', name: 'Room 102', type: 'GENERAL' },
            { id: 'L01', name: 'Physics Lab', type: 'LAB' }
        ];
    }

    onClassChange() {
        // In a real app, fetch timetable for this class
        this.timetable = [];
        console.log('Class changed to', this.selectedClassId);
    }

    openSlot(day: string, period: number) {
        const existing = this.timetable.find(t => t.day === day && t.period === period);

        this.currentSlot = {
            day,
            period,
            subjectId: existing ? existing.subjectId : '',
            teacherId: existing ? existing.teacherId : '',
            roomId: existing ? existing.roomId : ''
        };

        this.showModal = true;
    }

    closeModal() {
        this.showModal = false;
    }

    saveSlot() {
        // Validation: Teacher Conflict
        // (Conceptual: Check if this teacher is assigned elsewhere at this time)
        // For now, we check if teacher is assigned to another class in our local mock state? 
        // Since we only load ONE class's timetable, we can't fully check cross-class conflicts here without an API.
        // We will assume server-side validation or extensive client-side caching.

        if (!this.currentSlot.subjectId || !this.currentSlot.teacherId || !this.currentSlot.roomId) {
            alert('Please select all fields.');
            return;
        }

        // Update local state
        // Remove existing for this slot
        this.timetable = this.timetable.filter(t => !(t.day === this.currentSlot.day && t.period === this.currentSlot.period));

        // Add new
        this.timetable.push({ ...this.currentSlot });
        this.closeModal();
    }

    getSlotData(day: string, period: number) {
        const slot = this.timetable.find(t => t.day === day && t.period === period);
        if (!slot) return null;

        const sub = this.subjects.find(s => s.id === slot.subjectId);
        const tea = this.teachers.find(t => t.id === slot.teacherId);
        const room = this.rooms.find(r => r.id === slot.roomId);

        return {
            subject: sub ? sub.name : '???',
            teacher: tea ? tea.name : '???',
            room: room ? room.name : '???'
        };
    }

    isSlotFilled(day: string, period: number): boolean {
        return !!this.getSlotData(day, period);
    }

    saveTimetable() {
        // TODO: Send this.timetable to backend
        console.log('Saving timetable:', this.timetable);
        alert('Timetable saved (mock)!');
    }

    clearGrid() {
        if (confirm('Are you sure you want to clear the grid?')) {
            this.timetable = [];
        }
    }
}
