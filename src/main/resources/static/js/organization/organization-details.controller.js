angular.module('erpApp').controller('OrganizationDetailsController', 
    ['$scope', '$location', '$routeParams', 'OrganizationService', 
    function($scope, $location, $routeParams, OrganizationService) {
    
    // Initialize scope variables
    $scope.loading = true;
    $scope.editMode = false;
    $scope.isSaving = false;
    $scope.organizationData = {};
    $scope.businessHours = [];
    $scope.holidays = [];
    $scope.currencies = [];
    
    // Sidebar section expansion state
    $scope.expandedSections = {
        general: true,
        users: false,
        customization: false,
        portal: false,
        dataAdmin: false,
        developer: false
    };
    
    // Toggle sidebar section
    $scope.toggleSection = function(section) {
        $scope.expandedSections[section] = !$scope.expandedSections[section];
    };
    
    // Check if we're in edit mode based on route
    if ($location.path().includes('/edit')) {
        $scope.editMode = true;
    }
    
    // Load organization data
    function loadOrganizationData() {
        $scope.loading = true;
        
        OrganizationService.getOrganizationDetails()
            .then(function(data) {
                $scope.organizationData = data || getDefaultOrganizationData();
                $scope.loading = false;
            })
            .catch(function(error) {
                console.error('Error loading organization data:', error);
                // Load default data if API fails
                $scope.organizationData = getDefaultOrganizationData();
                $scope.loading = false;
            });
    }
    
    // Get default organization data (fallback)
    function getDefaultOrganizationData() {
        return {
            name: 'Zylker',
            contactInfo: {
                email: 'kirubhakaran.g+cllc@zohotest.com',
                phone: '09876543210',
                fax: '09876543210',
                website: 'https://www.zylker.com'
            },
            address: {
                street: '',
                city: '',
                state: '',
                country: '',
                zipCode: ''
            },
            branding: {
                logoUrl: ''
            },
            accessUrl: 'https://recruitqa1.localzoho.com/recruit/org875438l6/',
            localeInfo: {
                language: 'en',
                currencyLocale: 'United States',
                timeZone: '(GMT 5:30) India Standard Time(Asia/Kolkata)',
                dateFormat: 'MM/DD/YYYY'
            },
            fiscalYear: {
                startDate: null,
                endDate: null
            }
        };
    }
    
    // Get full address as a formatted string
    $scope.getFullAddress = function() {
        if (!$scope.organizationData || !$scope.organizationData.address) {
            return 'No address specified';
        }
        
        var address = $scope.organizationData.address;
        var parts = [];
        
        if (address.street) parts.push(address.street);
        if (address.city) parts.push(address.city);
        if (address.state) parts.push(address.state);
        if (address.country) parts.push(address.country);
        if (address.zipCode) parts.push(address.zipCode);
        
        return parts.length > 0 ? parts.join(', ') : 'No address specified';
    };
    
    // Enable edit mode
    $scope.enableEditMode = function() {
        $location.path('/organization-details/edit');
    };
    
    // Save company details
    $scope.saveCompanyDetails = function() {
        $scope.isSaving = true;
        
        OrganizationService.updateOrganizationDetails($scope.organizationData)
            .then(function(response) {
                $scope.isSaving = false;
                alert('Organization details saved successfully!');
                $scope.editMode = false;
                $location.path('/settings/organization-details');
            })
            .catch(function(error) {
                $scope.isSaving = false;
                alert('Error saving organization details. Please try again.');
                console.error('Save error:', error);
            });
    };
    
    // Cancel edit
    $scope.cancelEdit = function() {
        if (confirm('Are you sure you want to cancel? Any unsaved changes will be lost.')) {
            $scope.editMode = false;
            $location.path('/settings/organization-details');
            loadOrganizationData(); // Reload original data
        }
    };
    
    // Load business hours
    function loadBusinessHours() {
        OrganizationService.getBusinessHours()
            .then(function(data) {
                $scope.businessHours = data || getDefaultBusinessHours();
            })
            .catch(function(error) {
                console.error('Error loading business hours:', error);
                $scope.businessHours = getDefaultBusinessHours();
            });
    }
    
    // Get default business hours
    function getDefaultBusinessHours() {
        return [
            { code: 'monday', name: 'Monday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
            { code: 'tuesday', name: 'Tuesday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
            { code: 'wednesday', name: 'Wednesday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
            { code: 'thursday', name: 'Thursday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
            { code: 'friday', name: 'Friday', isWorkingDay: true, startTime: '09:00', endTime: '17:00' },
            { code: 'saturday', name: 'Saturday', isWorkingDay: false, startTime: '09:00', endTime: '17:00' },
            { code: 'sunday', name: 'Sunday', isWorkingDay: false, startTime: '09:00', endTime: '17:00' }
        ];
    }
    
    // Save business hours
    $scope.saveBusinessHours = function() {
        OrganizationService.updateBusinessHours($scope.businessHours)
            .then(function(response) {
                alert('Business hours saved successfully!');
            })
            .catch(function(error) {
                alert('Error saving business hours. Please try again.');
                console.error('Save error:', error);
            });
    };
    
    // Load holidays
    function loadHolidays() {
        OrganizationService.getHolidays()
            .then(function(data) {
                $scope.holidays = data || [];
            })
            .catch(function(error) {
                console.error('Error loading holidays:', error);
                $scope.holidays = [];
            });
    }
    
    // Add holiday
    $scope.addHoliday = function() {
        var holiday = {
            date: new Date(),
            name: 'New Holiday',
            description: ''
        };
        $scope.holidays.push(holiday);
    };
    
    // Edit holiday
    $scope.editHoliday = function(holiday) {
        // In a real implementation, this would open a modal or navigate to edit form
        console.log('Edit holiday:', holiday);
    };
    
    // Delete holiday
    $scope.deleteHoliday = function(holiday) {
        if (confirm('Are you sure you want to delete this holiday?')) {
            var index = $scope.holidays.indexOf(holiday);
            if (index > -1) {
                if (holiday.id) {
                    OrganizationService.deleteHoliday(holiday.id)
                        .then(function() {
                            $scope.holidays.splice(index, 1);
                        })
                        .catch(function(error) {
                            alert('Error deleting holiday. Please try again.');
                            console.error('Delete error:', error);
                        });
                } else {
                    $scope.holidays.splice(index, 1);
                }
            }
        }
    };
    
    // Save holidays
    $scope.saveHolidays = function() {
        var promises = $scope.holidays.map(function(holiday) {
            if (holiday.id) {
                return OrganizationService.updateHoliday(holiday.id, holiday);
            } else {
                return OrganizationService.createHoliday(holiday);
            }
        });
        
        Promise.all(promises)
            .then(function() {
                alert('Holidays saved successfully!');
                loadHolidays(); // Reload to get IDs
            })
            .catch(function(error) {
                alert('Error saving holidays. Please try again.');
                console.error('Save error:', error);
            });
    };
    
    // Load currencies
    function loadCurrencies() {
        OrganizationService.getCurrencies()
            .then(function(data) {
                $scope.currencies = data || getDefaultCurrencies();
            })
            .catch(function(error) {
                console.error('Error loading currencies:', error);
                $scope.currencies = getDefaultCurrencies();
            });
    }
    
    // Get default currencies
    function getDefaultCurrencies() {
        return [
            { code: 'USD', name: 'US Dollar', symbol: '$', exchangeRate: 1.00, isDefault: true },
            { code: 'EUR', name: 'Euro', symbol: '€', exchangeRate: 0.85, isDefault: false },
            { code: 'GBP', name: 'British Pound', symbol: '£', exchangeRate: 0.73, isDefault: false },
            { code: 'INR', name: 'Indian Rupee', symbol: '₹', exchangeRate: 83.00, isDefault: false }
        ];
    }
    
    // Add currency
    $scope.addCurrency = function() {
        var currency = {
            code: '',
            name: '',
            symbol: '',
            exchangeRate: 1.00,
            isDefault: false
        };
        $scope.currencies.push(currency);
    };
    
    // Edit currency
    $scope.editCurrency = function(currency) {
        // In a real implementation, this would open a modal or navigate to edit form
        console.log('Edit currency:', currency);
    };
    
    // Delete currency
    $scope.deleteCurrency = function(currency) {
        if (currency.isDefault) {
            alert('Cannot delete the default currency.');
            return;
        }
        
        if (confirm('Are you sure you want to delete this currency?')) {
            var index = $scope.currencies.indexOf(currency);
            if (index > -1) {
                if (currency.id) {
                    OrganizationService.deleteCurrency(currency.id)
                        .then(function() {
                            $scope.currencies.splice(index, 1);
                        })
                        .catch(function(error) {
                            alert('Error deleting currency. Please try again.');
                            console.error('Delete error:', error);
                        });
                } else {
                    $scope.currencies.splice(index, 1);
                }
            }
        }
    };
    
    // Save currencies
    $scope.saveCurrencies = function() {
        var promises = $scope.currencies.map(function(currency) {
            if (currency.id) {
                return OrganizationService.updateCurrency(currency.id, currency);
            } else {
                return OrganizationService.createCurrency(currency);
            }
        });
        
        Promise.all(promises)
            .then(function() {
                alert('Currencies saved successfully!');
                loadCurrencies(); // Reload to get IDs
            })
            .catch(function(error) {
                alert('Error saving currencies. Please try again.');
                console.error('Save error:', error);
            });
    };
    
    // Save fiscal year
    $scope.saveFiscalYear = function() {
        if (!$scope.organizationData.fiscalYear) {
            alert('Please set fiscal year dates.');
            return;
        }
        
        OrganizationService.updateFiscalYear($scope.organizationData.fiscalYear)
            .then(function(response) {
                alert('Fiscal year settings saved successfully!');
            })
            .catch(function(error) {
                alert('Error saving fiscal year settings. Please try again.');
                console.error('Save error:', error);
            });
    };
    
    // Initialize - load all data
    function init() {
        loadOrganizationData();
        loadBusinessHours();
        loadHolidays();
        loadCurrencies();
    }
    
    // Run initialization
    init();
}]);
