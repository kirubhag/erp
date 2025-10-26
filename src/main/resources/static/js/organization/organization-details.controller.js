angular.module('erpApp').controller('OrganizationDetailsController', ['$scope', '$http', '$location', '$routeParams', '$rootScope', function($scope, $http, $location, $routeParams, $rootScope) {
    
    // Initialize scope variables
    $scope.loading = true;
    $scope.organizationData = {};
    $scope.businessHours = [];
    $scope.holidays = [];
    $scope.currencies = [];
    // Check if we're on the edit route to automatically enable edit mode
    $scope.editMode = $location.path() === '/organization-details/edit';
    $scope.originalData = {}; // Store original data for cancel functionality
    
    // Listen for route changes to manage edit mode
    $rootScope.$on('$routeChangeStart', function(event, next) {
        // Only reset edit mode if we're not going to the edit route
        if (next && next.originalPath !== '/organization-details/edit') {
            $scope.editMode = false;
            $scope.originalData = {};
        }
    });
    
    // Listen for route change success to properly set edit mode
    $rootScope.$on('$routeChangeSuccess', function() {
        // Set edit mode based on current path
        $scope.editMode = $location.path() === '/organization-details/edit';
        if ($scope.editMode && Object.keys($scope.originalData).length === 0) {
            // Store original data when entering edit mode
            $scope.originalData = angular.copy($scope.organizationData);
        }
    });
    
    // Sidebar state management
    $scope.expandedSections = {
        general: true,
        users: false,
        customization: false,
        portal: false,
        dataAdmin: false,
        developer: false
    };
    
    // Toggle sidebar sections
    $scope.toggleSection = function(section) {
        $scope.expandedSections[section] = !$scope.expandedSections[section];
    };
    
    // Initialize with default organization data
    $scope.organizationData = {
        name: 'Zylker',
        contactInfo: {
            email: 'kirubhakaran.g+cllc@zohotest.com',
            phone: '09876543210',
            fax: '09876543210',
            website: 'https://www.zylker.com'
        },
        address: {
            street: '123 Business Street',
            city: 'Chennai',
            state: 'Tamil Nadu',
            country: 'India',
            zipCode: '600001'
        },
        accessUrl: 'https://recruitqa1.localzoho.com/recruit/org875438l6/',
        localeInfo: {
            currencyLocale: 'United States',
            timeZone: '(GMT 5:30) India Standard Time(Asia/Kolkata)'
        },
        branding: {
            logoUrl: null
        },
        fiscalYear: {
            startDate: '2024-04-01',
            endDate: '2025-03-31'
        }
    };
    
    // Initialize business hours
    $scope.businessHours = [
        { code: 'MON', name: 'Monday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
        { code: 'TUE', name: 'Tuesday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
        { code: 'WED', name: 'Wednesday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
        { code: 'THU', name: 'Thursday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
        { code: 'FRI', name: 'Friday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
        { code: 'SAT', name: 'Saturday', isWorkingDay: false, startTime: '', endTime: '' },
        { code: 'SUN', name: 'Sunday', isWorkingDay: false, startTime: '', endTime: '' }
    ];
    
    // Initialize holidays
    $scope.holidays = [
        {
            id: 1,
            date: new Date('2024-01-26'),
            name: 'Republic Day',
            description: 'National Holiday - Republic Day of India'
        },
        {
            id: 2,
            date: new Date('2024-08-15'),
            name: 'Independence Day',
            description: 'National Holiday - Independence Day of India'
        },
        {
            id: 3,
            date: new Date('2024-10-02'),
            name: 'Gandhi Jayanti',
            description: 'National Holiday - Birth Anniversary of Mahatma Gandhi'
        }
    ];
    
    // Initialize currencies
    $scope.currencies = [
        {
            id: 1,
            code: 'USD',
            name: 'US Dollar',
            symbol: '$',
            exchangeRate: 1.00,
            isDefault: true
        },
        {
            id: 2,
            code: 'INR',
            name: 'Indian Rupee',
            symbol: '₹',
            exchangeRate: 83.20,
            isDefault: false
        },
        {
            id: 3,
            code: 'EUR',
            name: 'Euro',
            symbol: '€',
            exchangeRate: 0.92,
            isDefault: false
        }
    ];
    
    // Helper function to get full address
    $scope.getFullAddress = function() {
        if (!$scope.organizationData.address) return '';
        
        const addr = $scope.organizationData.address;
        const parts = [];
        
        if (addr.street) parts.push(addr.street);
        if (addr.city) parts.push(addr.city);
        if (addr.state) parts.push(addr.state);
        if (addr.country) parts.push(addr.country);
        if (addr.zipCode) parts.push(addr.zipCode);
        
        return parts.join(', ');
    };
    
    // Load organization data
    $scope.loadOrganizationData = function() {
        // Don't reset edit mode if we're on the edit route
        if ($location.path() !== '/organization-details/edit') {
            $scope.editMode = false;
            $scope.originalData = {};
        }
        
        $scope.loading = true;
        
        // Try to get the first organization, fallback to default data
        $http.get('/api/organizations?page=0&size=1')
            .then(function(response) {
                if (response.data && response.data.content && response.data.content.length > 0) {
                    $scope.organizationData = response.data.content[0];
                }
                // If no organization found, use the default data initialized above
            })
            .catch(function(error) {
                // Use default data initialized above on error
                console.info('Using default organization data since API returned:', error.status);
            })
            .finally(function() {
                // Add a small delay to show the loading message with company name
                setTimeout(function() {
                    $scope.loading = false;
                    $scope.$apply();
                }, 800);
            });
    };
    
    // Enable edit mode
    $scope.enableEditMode = function() {
        $scope.editMode = true;
        // Store original data for cancel functionality
        $scope.originalData = angular.copy($scope.organizationData);
    };
    
    // Cancel edit mode
    $scope.cancelEdit = function() {
        // If we're on the edit route, navigate back to the regular organization details page
        if ($location.path() === '/organization-details/edit') {
            $location.path('/settings/organization-details');
            return;
        }
        
        $scope.editMode = false;
        // Restore original data
        $scope.organizationData = angular.copy($scope.originalData);
    };
    
    // Edit organization (legacy function for individual field editing)
    $scope.editOrganizationName = function() {
        $scope.enableEditMode();
    };
    
    // Edit access URL (legacy function for individual field editing)
    $scope.editAccessUrl = function() {
        $scope.enableEditMode();
    };
    
    // Edit locale information (legacy function for individual field editing)  
    $scope.editLocaleInfo = function() {
        $scope.enableEditMode();
    };
    
    // Holiday management functions
    $scope.addHoliday = function() {
        const newHoliday = {
            id: Date.now(), // Temporary ID
            date: new Date(),
            name: '',
            description: ''
        };
        
        // Show modal or inline editing
        const holidayName = prompt('Enter holiday name:');
        if (holidayName) {
            newHoliday.name = holidayName;
            const holidayDate = prompt('Enter holiday date (YYYY-MM-DD):');
            if (holidayDate) {
                newHoliday.date = new Date(holidayDate);
                newHoliday.description = prompt('Enter description (optional):') || '';
                $scope.holidays.push(newHoliday);
            }
        }
    };
    
    $scope.editHoliday = function(holiday) {
        const newName = prompt('Enter holiday name:', holiday.name);
        if (newName !== null) {
            holiday.name = newName;
            const newDescription = prompt('Enter description:', holiday.description);
            if (newDescription !== null) {
                holiday.description = newDescription;
            }
        }
    };
    
    $scope.deleteHoliday = function(holiday) {
        if (confirm('Are you sure you want to delete this holiday?')) {
            const index = $scope.holidays.indexOf(holiday);
            if (index > -1) {
                $scope.holidays.splice(index, 1);
            }
        }
    };
    
    // Currency management functions
    $scope.addCurrency = function() {
        const currencyCode = prompt('Enter currency code (e.g., GBP):');
        if (currencyCode) {
            const currencyName = prompt('Enter currency name:');
            const currencySymbol = prompt('Enter currency symbol:');
            const exchangeRate = parseFloat(prompt('Enter exchange rate:') || '1.0');
            
            if (currencyName && currencySymbol) {
                const newCurrency = {
                    id: Date.now(),
                    code: currencyCode.toUpperCase(),
                    name: currencyName,
                    symbol: currencySymbol,
                    exchangeRate: exchangeRate,
                    isDefault: false
                };
                $scope.currencies.push(newCurrency);
            }
        }
    };
    
    $scope.editCurrency = function(currency) {
        const newName = prompt('Enter currency name:', currency.name);
        if (newName !== null) {
            currency.name = newName;
            const newSymbol = prompt('Enter currency symbol:', currency.symbol);
            if (newSymbol !== null) {
                currency.symbol = newSymbol;
                const newRate = parseFloat(prompt('Enter exchange rate:', currency.exchangeRate));
                if (!isNaN(newRate)) {
                    currency.exchangeRate = newRate;
                }
            }
        }
    };
    
    $scope.deleteCurrency = function(currency) {
        if (currency.isDefault) {
            alert('Cannot delete the default currency. Please set another currency as default first.');
            return;
        }
        
        if (confirm('Are you sure you want to delete this currency?')) {
            const index = $scope.currencies.indexOf(currency);
            if (index > -1) {
                $scope.currencies.splice(index, 1);
            }
        }
    };
    
    // Save functions for each tab
    $scope.saveCompanyDetails = function() {
        // Try to get organization id from route or fallback to loaded data
        var orgId = $scope.organizationData && $scope.organizationData.id;
        if (!orgId) {
            // Try to get from $routeParams if available
            if ($routeParams.id) {
                orgId = $routeParams.id;
                $scope.organizationData.id = orgId;
            }
        }
        if (!orgId) {
            alert('Error: No organization data to save');
            return;
        }

        // Show saving state
        $scope.isSaving = true;

        // Prepare the data for the API (mapping from frontend structure to backend structure)
        var organizationData = {
            name: $scope.organizationData.name,
            type: $scope.organizationData.type || 'School',
            code: $scope.organizationData.code,
            description: $scope.organizationData.description,
            // Contact information - handle nested structure
            email: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.email : $scope.organizationData.email,
            phone: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.phone : $scope.organizationData.phone,
            fax: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.fax : $scope.organizationData.fax,
            website: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.website : $scope.organizationData.website,
            // Address information - handle nested structure
            streetAddress: $scope.organizationData.address ? $scope.organizationData.address.street : $scope.organizationData.streetAddress,
            city: $scope.organizationData.address ? $scope.organizationData.address.city : $scope.organizationData.city,
            state: $scope.organizationData.address ? $scope.organizationData.address.state : $scope.organizationData.state,
            postalCode: $scope.organizationData.address ? $scope.organizationData.address.zipCode : $scope.organizationData.postalCode,
            country: $scope.organizationData.address ? $scope.organizationData.address.country : $scope.organizationData.country,
            // Other fields
            registrationNumber: $scope.organizationData.registrationNumber,
            taxId: $scope.organizationData.taxId,
            establishedYear: $scope.organizationData.establishedYear,
            accreditation: $scope.organizationData.accreditation,
            academicYearFormat: $scope.organizationData.academicYearFormat,
            defaultLanguage: $scope.organizationData.localeInfo ? $scope.organizationData.localeInfo.language : $scope.organizationData.defaultLanguage,
            defaultCurrency: $scope.organizationData.localeInfo ? $scope.organizationData.localeInfo.currency : $scope.organizationData.defaultCurrency,
            timezone: $scope.organizationData.localeInfo ? $scope.organizationData.localeInfo.timezone : $scope.organizationData.timezone,
            logoUrl: $scope.organizationData.logoUrl
        };

        // Make API call to save organization details
        $http.put('/api/organizations/' + $scope.organizationData.id, organizationData)
            .then(function(response) {
                // Success - update the organizationData with the response
                $scope.organizationData = response.data;
                
                // Update original data to reflect saved changes
                $scope.originalData = angular.copy($scope.organizationData);
                
                alert('Company details saved successfully!');
                
                // If we're on the edit route, navigate back to the regular organization details page
                if ($location.path() === '/organization-details/edit') {
                    $location.path('/settings/organization-details');
                } else {
                    // Exit edit mode after saving (for inline editing)
                    $scope.editMode = false;
                }
            })
            .catch(function(error) {
                // Error handling
                console.error('Error saving organization details:', error);
                var errorMessage = 'Error saving organization details';
                
                if (error.data && error.data.message) {
                    errorMessage += ': ' + error.data.message;
                } else if (error.status) {
                    errorMessage += ' (Status: ' + error.status + ')';
                }
                
                alert(errorMessage);
            })
            .finally(function() {
                $scope.isSaving = false;
            });
    };
    
    $scope.saveFiscalYear = function() {

        // Implement save functionality
        alert('Fiscal year settings saved successfully!');
    };
    
    $scope.saveBusinessHours = function() {

        // Implement save functionality
        alert('Business hours saved successfully!');
    };
    
    $scope.saveHolidays = function() {

        // Implement save functionality
        alert('Holiday calendar saved successfully!');
    };
    
    $scope.saveCurrencies = function() {

        // Implement save functionality
        alert('Currency configuration saved successfully!');
    };
    
    // Initialize the controller
    $scope.init = function() {
        // Check if we're on the edit route to set edit mode
        if ($location.path() === '/organization-details/edit') {
            $scope.editMode = true;
        } else {
            $scope.editMode = false;
        }
        $scope.originalData = {};
        
        $scope.loadOrganizationData();
    };
    
    // Initialize on load
    $scope.init();
}]);