// Simple StudentController for testing
angular.module('erpApp').controller('SimpleStudentController', ['$scope', '$http', function($scope, $http) {
    
    // Initialize basic properties
    $scope.students = [];
    $scope.loading = false;
    $scope.isPlaceholder = false;
    
    // Pagination
    $scope.pagination = {
        currentPage: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0
    };
    
    // Load students function
        // Load students from API
    $scope.loadStudents = function() {
        $scope.loading = true;
        
        $http.get('/api/students')
            .then(function(response) {
                $scope.students = response.data.content || response.data;
                if (response.data.totalElements !== undefined) {
                    $scope.pagination.totalElements = response.data.totalElements;
                    $scope.pagination.totalPages = response.data.totalPages;
                    $scope.pagination.currentPage = response.data.number;
                }
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('❌ Error loading students:', error);
                $scope.loading = false;
            });
    };
    
    // Field visibility - show all fields by default
    $scope.isFieldVisible = function(fieldName) {
        // For now, show all fields
        return true;
    };
    
    // Grade level display mapping
    $scope.getGradeDisplayName = function(gradeLevel) {
        const gradeMap = {
            'KINDERGARTEN': 'Kindergarten',
            'GRADE_1': '1st Grade',
            'GRADE_2': '2nd Grade',
            'GRADE_3': '3rd Grade',
            'GRADE_4': '4th Grade',
            'GRADE_5': '5th Grade',
            'GRADE_6': '6th Grade',
            'GRADE_7': '7th Grade',
            'GRADE_8': '8th Grade',
            'GRADE_9': '9th Grade',
            'GRADE_10': '10th Grade',
            'GRADE_11': '11th Grade',
            'GRADE_12': '12th Grade'
        };
        return gradeMap[gradeLevel] || gradeLevel;
    };
    
    // Calculate age from birth date
    $scope.calculateAge = function(dateOfBirth) {
        if (!dateOfBirth) return '';
        try {
            const today = new Date();
            const birthDate = new Date(dateOfBirth);
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            return age;
        } catch (e) {
            return '';
        }
    };
    
    // Format student name
    $scope.formatStudentName = function(student) {
        if (!student) return '';
        const parts = [];
        if (student.firstName) parts.push(student.firstName);
        if (student.middleName) parts.push(student.middleName);
        if (student.lastName) parts.push(student.lastName);
        return parts.join(' ');
    };
    
    // Action button functions (placeholder)
    $scope.editStudent = function(student) {
        // TODO: Implement edit functionality
    };
    
    $scope.deleteStudent = function(student) {
        // TODO: Implement delete functionality
    };
    
    $scope.sendEmail = function(student) {
        // TODO: Implement email functionality
    };
    
    // Initialize
    $scope.loadStudents();
}]);