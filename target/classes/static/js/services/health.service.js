// Health Service - Handles health record-related API operations
angular.module('studentApp').service('HealthService', [
    'ApiService', '$q',
    function(ApiService, $q) {
        
        const self = this;
        const baseUrl = '/health-records';
        
        // Get all health records with pagination
        self.getAllHealthRecords = function(page, size, sort) {
            return ApiService.getPage(baseUrl, page, size, sort);
        };
        
        // Get health record by ID
        self.getHealthRecordById = function(id) {
            if (!id) {
                return $q.reject({ message: 'Health record ID is required' });
            }
            return ApiService.get(baseUrl + '/' + id);
        };
        
        // Get health record by student ID
        self.getHealthRecordByStudentId = function(studentId) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/student/' + studentId);
        };
        
        // Create new health record
        self.createHealthRecord = function(healthData) {
            if (!healthData) {
                return $q.reject({ message: 'Health record data is required' });
            }
            
            const validationError = self.validateHealthRecordData(healthData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl, healthData);
        };
        
        // Update health record
        self.updateHealthRecord = function(id, healthData) {
            if (!id) {
                return $q.reject({ message: 'Health record ID is required' });
            }
            if (!healthData) {
                return $q.reject({ message: 'Health record data is required' });
            }
            
            const validationError = self.validateHealthRecordData(healthData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/' + id, healthData);
        };
        
        // Delete health record
        self.deleteHealthRecord = function(id) {
            if (!id) {
                return $q.reject({ message: 'Health record ID is required' });
            }
            return ApiService.delete(baseUrl + '/' + id);
        };
        
        // Get immunizations by student
        self.getImmunizationsByStudent = function(studentId) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/student/' + studentId + '/immunizations');
        };
        
        // Add immunization
        self.addImmunization = function(studentId, immunizationData) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!immunizationData) {
                return $q.reject({ message: 'Immunization data is required' });
            }
            
            const validationError = self.validateImmunizationData(immunizationData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl + '/student/' + studentId + '/immunizations', immunizationData);
        };
        
        // Update immunization
        self.updateImmunization = function(immunizationId, immunizationData) {
            if (!immunizationId) {
                return $q.reject({ message: 'Immunization ID is required' });
            }
            if (!immunizationData) {
                return $q.reject({ message: 'Immunization data is required' });
            }
            
            const validationError = self.validateImmunizationData(immunizationData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/immunizations/' + immunizationId, immunizationData);
        };
        
        // Delete immunization
        self.deleteImmunization = function(immunizationId) {
            if (!immunizationId) {
                return $q.reject({ message: 'Immunization ID is required' });
            }
            return ApiService.delete(baseUrl + '/immunizations/' + immunizationId);
        };
        
        // Get allergies by student
        self.getAllergiesByStudent = function(studentId) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/student/' + studentId + '/allergies');
        };
        
        // Add allergy
        self.addAllergy = function(studentId, allergyData) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!allergyData) {
                return $q.reject({ message: 'Allergy data is required' });
            }
            
            const validationError = self.validateAllergyData(allergyData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl + '/student/' + studentId + '/allergies', allergyData);
        };
        
        // Update allergy
        self.updateAllergy = function(allergyId, allergyData) {
            if (!allergyId) {
                return $q.reject({ message: 'Allergy ID is required' });
            }
            if (!allergyData) {
                return $q.reject({ message: 'Allergy data is required' });
            }
            
            const validationError = self.validateAllergyData(allergyData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/allergies/' + allergyId, allergyData);
        };
        
        // Delete allergy
        self.deleteAllergy = function(allergyId) {
            if (!allergyId) {
                return $q.reject({ message: 'Allergy ID is required' });
            }
            return ApiService.delete(baseUrl + '/allergies/' + allergyId);
        };
        
        // Get medical conditions by student
        self.getMedicalConditionsByStudent = function(studentId) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/student/' + studentId + '/medical-conditions');
        };
        
        // Add medical condition
        self.addMedicalCondition = function(studentId, conditionData) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!conditionData) {
                return $q.reject({ message: 'Medical condition data is required' });
            }
            
            const validationError = self.validateMedicalConditionData(conditionData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl + '/student/' + studentId + '/medical-conditions', conditionData);
        };
        
        // Update medical condition
        self.updateMedicalCondition = function(conditionId, conditionData) {
            if (!conditionId) {
                return $q.reject({ message: 'Medical condition ID is required' });
            }
            if (!conditionData) {
                return $q.reject({ message: 'Medical condition data is required' });
            }
            
            const validationError = self.validateMedicalConditionData(conditionData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/medical-conditions/' + conditionId, conditionData);
        };
        
        // Delete medical condition
        self.deleteMedicalCondition = function(conditionId) {
            if (!conditionId) {
                return $q.reject({ message: 'Medical condition ID is required' });
            }
            return ApiService.delete(baseUrl + '/medical-conditions/' + conditionId);
        };
        
        // Get emergency contacts by student
        self.getEmergencyContactsByStudent = function(studentId) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            return ApiService.get(baseUrl + '/student/' + studentId + '/emergency-contacts');
        };
        
        // Add emergency contact
        self.addEmergencyContact = function(studentId, contactData) {
            if (!studentId) {
                return $q.reject({ message: 'Student ID is required' });
            }
            if (!contactData) {
                return $q.reject({ message: 'Emergency contact data is required' });
            }
            
            const validationError = self.validateEmergencyContactData(contactData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.post(baseUrl + '/student/' + studentId + '/emergency-contacts', contactData);
        };
        
        // Update emergency contact
        self.updateEmergencyContact = function(contactId, contactData) {
            if (!contactId) {
                return $q.reject({ message: 'Emergency contact ID is required' });
            }
            if (!contactData) {
                return $q.reject({ message: 'Emergency contact data is required' });
            }
            
            const validationError = self.validateEmergencyContactData(contactData);
            if (validationError) {
                return $q.reject({ message: validationError });
            }
            
            return ApiService.put(baseUrl + '/emergency-contacts/' + contactId, contactData);
        };
        
        // Delete emergency contact
        self.deleteEmergencyContact = function(contactId) {
            if (!contactId) {
                return $q.reject({ message: 'Emergency contact ID is required' });
            }
            return ApiService.delete(baseUrl + '/emergency-contacts/' + contactId);
        };
        
        // Get students with missing immunizations
        self.getStudentsWithMissingImmunizations = function(page, size) {
            return ApiService.getPage(baseUrl + '/reports/missing-immunizations', page, size);
        };
        
        // Get students with severe allergies
        self.getStudentsWithSevereAllergies = function(page, size) {
            return ApiService.getPage(baseUrl + '/reports/severe-allergies', page, size);
        };
        
        // Get health statistics
        self.getHealthStatistics = function() {
            return ApiService.get(baseUrl + '/statistics');
        };
        
        // Export health records
        self.exportToCsv = function(filters) {
            let url = baseUrl + '/export/csv';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToCsv(url, 'health_records_export.csv');
        };
        
        self.exportToPdf = function(filters) {
            let url = baseUrl + '/export/pdf';
            if (filters) {
                const queryString = ApiService.buildQueryString(filters);
                if (queryString) {
                    url += '?' + queryString;
                }
            }
            return ApiService.exportToPdf(url, 'health_records_report.pdf');
        };
        
        // Validation functions
        self.validateHealthRecordData = function(healthData) {
            if (!healthData.studentId) {
                return 'Student ID is required';
            }
            
            if (!healthData.bloodType) {
                return 'Blood type is required';
            }
            
            if (!self.isValidBloodType(healthData.bloodType)) {
                return 'Please select a valid blood type';
            }
            
            return null; // No validation errors
        };
        
        self.validateImmunizationData = function(immunizationData) {
            if (!immunizationData.vaccineName || immunizationData.vaccineName.trim() === '') {
                return 'Vaccine name is required';
            }
            
            if (!immunizationData.dateGiven) {
                return 'Date given is required';
            }
            
            if (!self.isValidDate(immunizationData.dateGiven)) {
                return 'Please enter a valid date';
            }
            
            return null; // No validation errors
        };
        
        self.validateAllergyData = function(allergyData) {
            if (!allergyData.allergen || allergyData.allergen.trim() === '') {
                return 'Allergen name is required';
            }
            
            if (!allergyData.severity) {
                return 'Severity is required';
            }
            
            if (!self.isValidSeverity(allergyData.severity)) {
                return 'Please select a valid severity level';
            }
            
            return null; // No validation errors
        };
        
        self.validateMedicalConditionData = function(conditionData) {
            if (!conditionData.conditionName || conditionData.conditionName.trim() === '') {
                return 'Condition name is required';
            }
            
            return null; // No validation errors
        };
        
        self.validateEmergencyContactData = function(contactData) {
            if (!contactData.name || contactData.name.trim() === '') {
                return 'Contact name is required';
            }
            
            if (!contactData.phoneNumber || contactData.phoneNumber.trim() === '') {
                return 'Phone number is required';
            }
            
            if (!self.isValidPhoneNumber(contactData.phoneNumber)) {
                return 'Please enter a valid phone number';
            }
            
            if (!contactData.relationship || contactData.relationship.trim() === '') {
                return 'Relationship is required';
            }
            
            return null; // No validation errors
        };
        
        // Utility validation functions
        self.isValidDate = function(dateString) {
            const date = new Date(dateString);
            return date instanceof Date && !isNaN(date);
        };
        
        self.isValidPhoneNumber = function(phone) {
            const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
            return phoneRegex.test(phone.replace(/[\s\-\(\)\.]/g, ''));
        };
        
        self.isValidBloodType = function(bloodType) {
            const validBloodTypes = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
            return validBloodTypes.includes(bloodType);
        };
        
        self.isValidSeverity = function(severity) {
            const validSeverities = ['MILD', 'MODERATE', 'SEVERE', 'CRITICAL'];
            return validSeverities.includes(severity);
        };
        
        // Helper functions for UI
        self.getBloodTypeOptions = function() {
            return [
                { value: 'A+', label: 'A+' },
                { value: 'A-', label: 'A-' },
                { value: 'B+', label: 'B+' },
                { value: 'B-', label: 'B-' },
                { value: 'AB+', label: 'AB+' },
                { value: 'AB-', label: 'AB-' },
                { value: 'O+', label: 'O+' },
                { value: 'O-', label: 'O-' }
            ];
        };
        
        self.getSeverityOptions = function() {
            return [
                { value: 'MILD', label: 'Mild' },
                { value: 'MODERATE', label: 'Moderate' },
                { value: 'SEVERE', label: 'Severe' },
                { value: 'CRITICAL', label: 'Critical' }
            ];
        };
        
        self.getSeverityDisplayName = function(severity) {
            const severityMap = {
                'MILD': 'Mild',
                'MODERATE': 'Moderate',
                'SEVERE': 'Severe',
                'CRITICAL': 'Critical'
            };
            return severityMap[severity] || severity;
        };
        
        self.getSeverityBadgeClass = function(severity) {
            const severityClasses = {
                'MILD': 'bg-success',
                'MODERATE': 'bg-warning',
                'SEVERE': 'bg-danger',
                'CRITICAL': 'bg-dark'
            };
            return severityClasses[severity] || 'bg-secondary';
        };
        
        // Create empty objects with default values
        self.createEmptyHealthRecord = function() {
            return {
                studentId: null,
                bloodType: '',
                height: null,
                weight: null,
                physicalLimitations: '',
                medicationsCurrently: '',
                physicianName: '',
                physicianPhone: '',
                hospitalPreference: '',
                insuranceProvider: '',
                insurancePolicyNumber: '',
                notes: ''
            };
        };
        
        self.createEmptyImmunization = function() {
            return {
                vaccineName: '',
                dateGiven: null,
                boosterRequired: false,
                nextBoosterDate: null,
                administeredBy: '',
                batchNumber: '',
                notes: ''
            };
        };
        
        self.createEmptyAllergy = function() {
            return {
                allergen: '',
                severity: 'MILD',
                reaction: '',
                treatment: '',
                notes: ''
            };
        };
        
        self.createEmptyMedicalCondition = function() {
            return {
                conditionName: '',
                diagnosisDate: null,
                treatment: '',
                medication: '',
                physicianName: '',
                notes: ''
            };
        };
        
        self.createEmptyEmergencyContact = function() {
            return {
                name: '',
                relationship: '',
                phoneNumber: '',
                alternatePhoneNumber: '',
                address: '',
                isPrimary: false,
                canPickup: false
            };
        };
        
        return self;
    }
]);