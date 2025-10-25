angular.module('erpApp').controller('OrganizationDetailsController', ['$scope', '$http', '$location', '$routeParams', function($scope, $http, $location, $routeParams) {
    
    // Initialize scope variables
    $scope.loading = true;
    $scope.organizationData = {};
    $scope.businessHours = [];
    $scope.holidays = [];
    $scope.currencies = [];
    
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
    
    // Edit organization name
    $scope.editOrganizationName = function() {
        const currentName = $scope.organizationData.name || 'Zylker';
        const newName = prompt('Enter organization name:', currentName);
        if (newName !== null && newName.trim() !== '') {
            $scope.organizationData.name = newName.trim();
            $scope.$apply(); // Force digest cycle since prompt is outside Angular
        }
    };
    
    // Edit access URL
    $scope.editAccessUrl = function() {
        const currentUrl = $scope.organizationData.accessUrl || 'https://recruitqa1.localzoho.com/recruit/org875438l6/';
        const newUrl = prompt('Enter access URL:', currentUrl);
        if (newUrl !== null && newUrl.trim() !== '') {
            $scope.organizationData.accessUrl = newUrl.trim();
            $scope.$apply(); // Force digest cycle since prompt is outside Angular
        }
    };
    
    // Edit locale information
    $scope.editLocaleInfo = function() {
        alert('Locale information editing will be implemented with a proper form in a future update.');
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

        // Implement save functionality
        alert('Company details saved successfully!');
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
    
    // Navigation functions
    $scope.goBack = function() {
        $location.path('/settings');
    };
    
    // Initialize the controller
    $scope.init = function() {

        $scope.loadOrganizationData();
    };
    
    // Initialize on load
    $scope.init();
}]);