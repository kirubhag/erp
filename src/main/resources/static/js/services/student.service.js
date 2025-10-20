// Student Service - Handles student-related API operations
angular.module('studentApp').service('StudentService', [
    'ApiService', '$q',
    function(ApiService, $q) {
        
        const self = this;
        const baseUrl = '/students';
        
        // Get all students with pagination
        self.getAllStudents = function(page, size, sort) {
            return ApiService.getPage(baseUrl, page, size, sort);
        };
        
        // Search students
        self.searchStudents = function(searchTerm, page, size, sort) {
            return ApiService.search(baseUrl + '/search', searchTerm, page, size, sort);
        };
        
        // Get student by ID
        self.getStudentById = function(id) {
            if (!id) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/' + id);
        };
        
        // Create new student
        self.createStudent = function(studentData) {
            if (!studentData) {
                return $q.reject({ message: 'Student data is required' });
            }
            
            // Validate required fields
            const validationError = self.validateStudentData(studentData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl, studentData);
        };
        
        // Update student
        self.updateStudent = function(id, studentData) {
            if (!id) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!studentData) {
                return $q.reject({ message: 'Student data is required' });
            }
            
            const validationError = self.validateStudentData(studentData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/' + id, studentData);
        };
        
        // Delete student
        self.deleteStudent = function(id) {
            if (!id) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.delete(baseUrl + '/' + id);
        };
        
        // Get students by grade level
        self.getStudentsByGrade = function(gradeLevel, page, size) {
            if (!gradeLevel) {
                return $q.reject({ message: 'Grade level is required' });
            }
            return ApiService.getPage(baseUrl + '/grade/' + gradeLevel, page, size);
        };
        
        // Get students by enrollment status
        self.getStudentsByStatus = function(status, page, size) {
            if (!status) {
                return $q.reject({ message: 'Status is required' });
            }
            return ApiService.getPage(baseUrl + '/status/' + status, page, size);
        };
        
        // Get student summary (lightweight data)
        self.getStudentSummary = function(id) {
            if (!id) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/' + id + '/summary');
        };
        
        // Get student count
        self.getStudentCount = function() {
            return ApiService.get(baseUrl + '/count');
        };
        
        // Get students enrolled between dates
        self.getStudentsEnrolledBetween = function(startDate, endDate, page, size) {
            if (!startDate || !endDate) {
                return $q.reject({ message: 'Start date and end date are required' });
            }
            
            const params = {
                startDate: startDate,
                endDate: endDate
            };
            
            return ApiService.getPage(baseUrl + '/enrolled-between', page, size, null, params);
        };
        
        // Update student status
        self.updateStudentStatus = function(id, status) {
            if (!id) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!status) {
                return $q.reject({ message: 'Status is required' });
            }
            
            return ApiService.patch(baseUrl + '/' + id + '/status', { status: status });
        };
        
        // Bulk operations
        self.bulkUpdateStatus = function(studentIds, status) {
            if (!studentIds || !studentIds.length) {
                return $q.reject({ message: 'Student IDs are required' });
            }
            if (!status) {
                return $q.reject({ message: 'Status is required' });
            }
            
            return ApiService.post(baseUrl + '/bulk/status', {
                studentIds: studentIds,
                status: status
            });
        };
        
        self.bulkDelete = function(studentIds) {
            if (!studentIds || !studentIds.length) {
                return $q.reject({ message: 'Student IDs are required' });
            }
            
            return ApiService.post(baseUrl + '/bulk/delete', { studentIds: studentIds });
        };
        
        // Export operations
        self.exportToCsv = function(filters) {
            let url = baseUrl + '/export/csv';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToCsv(url, 'students_export.csv');
        };
        
        self.exportToPdf = function(filters) {
            let url = baseUrl + '/export/pdf';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToPdf(url, 'students_report.pdf');
        };
        
        // Validation functions
        self.validateStudentData = function(studentData) {
            if (!studentData.firstName || studentData.firstName.trim() === '') {
                return 'First name is required';
            }
            
            if (!studentData.lastName || studentData.lastName.trim() === '') {
                return 'Last name is required';
            }
            
            if (!studentData.email || studentData.email.trim() === '') {
                return 'Email is required';
            }
            
            if (!self.isValidEmail(studentData.email)) {
                return 'Please enter a valid email address';
            }
            
            if (!studentData.gradeLevel) {
                return 'Grade level is required';
            }
            
            if (!studentData.dateOfBirth) {
                return 'Date of birth is required';
            }
            
            if (!self.isValidDate(studentData.dateOfBirth)) {
                return 'Please enter a valid date of birth';
            }
            
            if (studentData.phoneNumber && !self.isValidPhoneNumber(studentData.phoneNumber)) {
                return 'Please enter a valid phone number';
            }
            
            return null; // No validation errors
        };
        
        // Utility validation functions
        self.isValidEmail = function(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        };
        
        self.isValidPhoneNumber = function(phone) {
            const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
            return phoneRegex.test(phone.replace(/[\s\-\(\)\.]/g, ''));
        };
        
        self.isValidDate = function(dateString) {
            const date = new Date(dateString);
            return date instanceof Date && !isNaN(date) && date <= new Date();
        };
        
        // Helper functions for UI
        self.getGradeLevelDisplayName = function(gradeLevel) {
            const gradeMap = {
                'KINDERGARTEN': 'Kindergarten',
                'FIRST': '1st Grade',
                'SECOND': '2nd Grade',
                'THIRD': '3rd Grade',
                'FOURTH': '4th Grade',
                'FIFTH': '5th Grade',
                'SIXTH': '6th Grade',
                'SEVENTH': '7th Grade',
                'EIGHTH': '8th Grade',
                'NINTH': '9th Grade',
                'TENTH': '10th Grade',
                'ELEVENTH': '11th Grade',
                'TWELFTH': '12th Grade'
            };
            return gradeMap[gradeLevel] || gradeLevel;
        };
        
        self.getStatusDisplayName = function(status) {
            const statusMap = {
                'ACTIVE': 'Active',
                'INACTIVE': 'Inactive',
                'GRADUATED': 'Graduated',
                'TRANSFERRED': 'Transferred',
                'SUSPENDED': 'Suspended',
                'EXPELLED': 'Expelled'
            };
            return statusMap[status] || status;
        };
        
        self.getStatusBadgeClass = function(status) {
            const statusClasses = {
                'ACTIVE': 'bg-success',
                'INACTIVE': 'bg-secondary',
                'GRADUATED': 'bg-primary',
                'TRANSFERRED': 'bg-info',
                'SUSPENDED': 'bg-warning',
                'EXPELLED': 'bg-danger'
            };
            return statusClasses[status] || 'bg-secondary';
        };
        
        // Calculate age from date of birth
        self.calculateAge = function(dateOfBirth) {
            if (!dateOfBirth) return null;
            
            const today = new Date();
            const birthDate = new Date(dateOfBirth);
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            
            return age;
        };
        
        // Format student name
        self.formatStudentName = function(student) {
            if (!student) return '';
            
            let name = '';
            if (student.firstName) name += student.firstName;
            if (student.middleName) name += ' ' + student.middleName;
            if (student.lastName) name += ' ' + student.lastName;
            
            return name.trim();
        };
        
        // Create empty student object with default values
        self.createEmptyStudent = function() {
            return {
                firstName: '',
                middleName: '',
                lastName: '',
                email: '',
                phoneNumber: '',
                dateOfBirth: null,
                gradeLevel: '',
                enrollmentDate: new Date().toISOString().split('T')[0],
                status: 'ACTIVE',
                address: {
                    street: '',
                    city: '',
                    state: '',
                    zipCode: '',
                    country: 'USA'
                },
                emergencyContacts: []
            };
        };
        
        return self;
    }
]);