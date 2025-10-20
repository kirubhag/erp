// Attendance Service - Handles attendance-related API operations
angular.module('erpApp').service('AttendanceService', [
    'ApiService', '$q',
    function(ApiService, $q) {
        
        const self = this;
        const baseUrl = '/attendance';
        
        // Get all attendance records with pagination
        self.getAllAttendance = function(page, size, sort) {
            return ApiService.getPage(baseUrl, page, size, sort);
        };
        
        // Get attendance by ID
        self.getAttendanceById = function(id) {
            if (!id) {
                return $q.reject({ message: 'Attendance ID is required' });
            }
            return ApiService.get(baseUrl + '/' + id);
        };
        
        // Create attendance record
        self.createAttendance = function(attendanceData) {
            if (!attendanceData) {
                return $q.reject({ message: 'Attendance data is required' });
            }
            
            const validationError = self.validateAttendanceData(attendanceData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl, attendanceData);
        };
        
        // Update attendance record
        self.updateAttendance = function(id, attendanceData) {
            if (!id) {
                return $q.reject({ message: 'Attendance ID is required' });
            }
            if (!attendanceData) {
                return $q.reject({ message: 'Attendance data is required' });
            }
            
            const validationError = self.validateAttendanceData(attendanceData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/' + id, attendanceData);
        };
        
        // Delete attendance record
        self.deleteAttendance = function(id) {
            if (!id) {
                return $q.reject({ message: 'Attendance ID is required' });
            }
            return ApiService.delete(baseUrl + '/' + id);
        };
        
        // Get attendance by date
        self.getAttendanceByDate = function(date, page, size) {
            if (!date) {
                return $q.reject({ message: 'Date is required' });
            }
            return ApiService.getPage(baseUrl + '/date/' + date, page, size);
        };
        
        // Get attendance by date range
        self.getAttendanceByDateRange = function(startDate, endDate, page, size) {
            if (!startDate || !endDate) {
                return $q.reject({ message: 'Start date and end date are required' });
            }
            
            const params = {
                startDate: startDate,
                endDate: endDate
            };
            
            return ApiService.getPage(baseUrl + '/date-range', page, size, null, params);
        };
        
        // Get student attendance
        self.getStudentAttendance = function(studentId, page, size) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.getPage(baseUrl + '/student/' + studentId, page, size);
        };
        
        // Get student attendance by date range
        self.getStudentAttendanceByDateRange = function(studentId, startDate, endDate, page, size) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!startDate || !endDate) {
                return $q.reject({ message: 'Start date and end date are required' });
            }
            
            const params = {
                startDate: startDate,
                endDate: endDate
            };
            
            return ApiService.getPage(baseUrl + '/student/' + studentId + '/date-range', page, size, null, params);
        };
        
        // Get staff attendance
        self.getStaffAttendance = function(staffId, page, size) {
            if (!staffId) {
                return $q.reject({ message: 'Staff ID is required' });
            }
            return ApiService.getPage(baseUrl + '/staff/' + staffId, page, size);
        };
        
        // Get attendance by status
        self.getAttendanceByStatus = function(status, date, page, size) {
            if (!status) {
                return $q.reject({ message: 'Status is required' });
            }
            
            let url = baseUrl + '/status/' + status;
            const params = {};
            
            if (date) {
                params.date = date;
            }
            
            return ApiService.getPage(url, page, size, null, params);
        };
        
        // Get attendance by grade level
        self.getAttendanceByGrade = function(gradeLevel, date, page, size) {
            if (!gradeLevel) {
                return $q.reject({ message: 'Grade level is required' });
            }
            
            let url = baseUrl + '/grade/' + gradeLevel;
            const params = {};
            
            if (date) {
                params.date = date;
            }
            
            return ApiService.getPage(url, page, size, null, params);
        };
        
        // Get attendance statistics
        self.getAttendanceStatistics = function(date) {
            let url = baseUrl + '/statistics';
            if (date) {
                url += '/date/' + date;
            }
            return ApiService.get(url);
        };
        
        // Get attendance statistics by date range
        self.getAttendanceStatisticsByDateRange = function(startDate, endDate) {
            if (!startDate || !endDate) {
                return $q.reject({ message: 'Start date and end date are required' });
            }
            
            const params = {
                startDate: startDate,
                endDate: endDate
            };
            
            return ApiService.get(baseUrl + '/statistics/date-range', params);
        };
        
        // Get irregular attendance (students with attendance issues)
        self.getIrregularAttendance = function(days, page, size) {
            const params = {};
            if (days) {
                params.days = days;
            }
            
            return ApiService.getPage(baseUrl + '/irregular', page, size, null, params);
        };
        
        // Mark attendance (bulk operation for a class/grade)
        self.markAttendance = function(attendanceList) {
            if (!attendanceList || !attendanceList.length) {
                return $q.reject({ message: 'Attendance list is required' });
            }
            
            // Validate each attendance record
            for (let i = 0; i < attendanceList.length; i++) {
                const validationError = self.validateAttendanceData(attendanceList[i]);
                if (validationError) {
                    return $q.reject({ message: 'Invalid data for record ' + (i + 1) + ': ' + validationError });
                }
            }
            
            return ApiService.post(baseUrl + '/mark', attendanceList);
        };
        
        // Mark individual attendance
        self.markIndividualAttendance = function(studentId, date, status, notes) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!date) {
                return $q.reject({ message: 'Date is required' });
            }
            if (!status) {
                return $q.reject({ message: 'Status is required' });
            }
            
            const attendanceData = {
                studentId: studentId,
                date: date,
                status: status,
                notes: notes || '',
                markedAt: new Date().toISOString()
            };
            
            return ApiService.post(baseUrl + '/mark/individual', attendanceData);
        };
        
        // Update attendance status
        self.updateAttendanceStatus = function(id, status, notes) {
            if (!id) {
                return $q.reject({ message: 'Attendance ID is required' });
            }
            if (!status) {
                return $q.reject({ message: 'Status is required' });
            }
            
            return ApiService.patch(baseUrl + '/' + id + '/status', {
                status: status,
                notes: notes || ''
            });
        };
        
        // Export attendance data
        self.exportToCsv = function(filters) {
            let url = baseUrl + '/export/csv';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToCsv(url, 'attendance_export.csv');
        };
        
        self.exportToPdf = function(filters) {
            let url = baseUrl + '/export/pdf';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToPdf(url, 'attendance_report.pdf');
        };
        
        // Validation functions
        self.validateAttendanceData = function(attendanceData) {
            if (!attendanceData.studentId && !attendanceData.staffId) {
                return 'Either student ID or staff ID is required';
            }
            
            if (!attendanceData.date) {
                return 'Date is required';
            }
            
            if (!self.isValidDate(attendanceData.date)) {
                return 'Please enter a valid date';
            }
            
            if (!attendanceData.status) {
                return 'Status is required';
            }
            
            if (!self.isValidStatus(attendanceData.status)) {
                return 'Please select a valid status';
            }
            
            return null; // No validation errors
        };
        
        // Utility validation functions
        self.isValidDate = function(dateString) {
            const date = new Date(dateString);
            return date instanceof Date && !isNaN(date);
        };
        
        self.isValidStatus = function(status) {
            const validStatuses = ['PRESENT', 'ABSENT', 'LATE', 'EXCUSED'];
            return validStatuses.includes(status);
        };
        
        // Helper functions for UI
        self.getStatusDisplayName = function(status) {
            const statusMap = {
                'PRESENT': 'Present',
                'ABSENT': 'Absent',
                'LATE': 'Late',
                'EXCUSED': 'Excused'
            };
            return statusMap[status] || status;
        };
        
        self.getStatusBadgeClass = function(status) {
            const statusClasses = {
                'PRESENT': 'bg-success',
                'ABSENT': 'bg-danger',
                'LATE': 'bg-warning',
                'EXCUSED': 'bg-info'
            };
            return statusClasses[status] || 'bg-secondary';
        };
        
        self.getStatusIcon = function(status) {
            const statusIcons = {
                'PRESENT': 'check-circle',
                'ABSENT': 'times-circle',
                'LATE': 'clock',
                'EXCUSED': 'info-circle'
            };
            return statusIcons[status] || 'question-circle';
        };
        
        // Calculate attendance rate
        self.calculateAttendanceRate = function(presentDays, totalDays) {
            if (!totalDays || totalDays === 0) return 0;
            return Math.round((presentDays / totalDays) * 100);
        };
        
        // Get attendance rate class for styling
        self.getAttendanceRateClass = function(rate) {
            if (rate >= 95) return 'text-success';
            if (rate >= 85) return 'text-info';
            if (rate >= 75) return 'text-warning';
            return 'text-danger';
        };
        
        // Format attendance summary
        self.formatAttendanceSummary = function(statistics) {
            if (!statistics) return 'No data available';
            
            const total = statistics.totalStudents || 0;
            const present = statistics.presentCount || 0;
            const absent = statistics.absentCount || 0;
            const late = statistics.lateCount || 0;
            const excused = statistics.excusedCount || 0;
            
            return `Total: ${total}, Present: ${present}, Absent: ${absent}, Late: ${late}, Excused: ${excused}`;
        };
        
        // Create empty attendance object
        self.createEmptyAttendance = function() {
            return {
                studentId: null,
                staffId: null,
                date: new Date().toISOString().split('T')[0],
                status: 'PRESENT',
                notes: '',
                markedAt: null,
                markedBy: null
            };
        };
        
        // Create attendance template for class
        self.createClassAttendanceTemplate = function(students, date) {
            if (!students || !students.length) return [];
            if (!date) date = new Date().toISOString().split('T')[0];
            
            return students.map(function(student) {
                return {
                    studentId: student.id,
                    studentName: student.firstName + ' ' + student.lastName,
                    date: date,
                    status: 'PRESENT',
                    notes: ''
                };
            });
        };
        
        // Get attendance trends
        self.getAttendanceTrends = function(studentId, days) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            
            const params = {};
            if (days) {
                params.days = days;
            }
            
            return ApiService.get(baseUrl + '/trends/student/' + studentId, params);
        };
        
        // Get class attendance summary
        self.getClassAttendanceSummary = function(gradeLevel, date) {
            const params = {};
            if (date) {
                params.date = date;
            }
            
            let url = baseUrl + '/summary';
            if (gradeLevel) {
                url += '/grade/' + gradeLevel;
            }
            
            return ApiService.get(url, params);
        };
        
        return self;
    }
]);