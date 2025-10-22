// Settings Controller - System and user settings management
angular.module('erpApp').controller('SettingsController', [
    '$scope', '$rootScope', 'ApiService', 'SettingsService', 'OrganizationService',
    function($scope, $rootScope, ApiService, SettingsService, OrganizationService) {
        
        // Initialize controller
        $scope.init = function() {
            $scope.loading = false;
            $scope.activeSettingsTab = ''; // Default to welcome message
            $scope.settings = SettingsService.getDefaultSettings();
            
            // Initialize dropdown options
            $scope.availableLanguages = SettingsService.getAvailableLanguages();
            $scope.availableThemes = SettingsService.getAvailableThemes();
            $scope.availableTimezones = SettingsService.getAvailableTimezones();
            $scope.availableCurrencies = SettingsService.getAvailableCurrencies();
            
            // Initialize organization-related data
            $scope.organizationData = null;
            $scope.currentYear = new Date().getFullYear();
            $scope.organizationTypes = [
                { value: 'School', label: 'School' },
                { value: 'College', label: 'College' },
                { value: 'University', label: 'University' },
                { value: 'Institute', label: 'Institute' },
                { value: 'Academy', label: 'Academy' },
                { value: 'Center', label: 'Center' },
                { value: 'Foundation', label: 'Foundation' },
                { value: 'Corporation', label: 'Corporation' },
                { value: 'Department', label: 'Department' }
            ];
            
            $scope.loadOrganizationData();
            
            // Initialize settings structure
            $scope.initializeSettings();
            $scope.loadSettings();
        };
        
        // Load organization data for view
        $scope.loadOrganizationData = function() {
            ApiService.get('/organizations?size=10').then(function(response) {
                if (response.data && response.data.content && response.data.content.length > 0) {
                    // Find the organization with the most complete data (prioritize those with email/phone)
                    var org = response.data.content.find(function(o) {
                        return o.email || o.phone || o.streetAddress;
                    }) || response.data.content[0];
                    
                    // Map the organization data to the expected structure
                    $scope.organizationData = {
                        id: org.id,
                        name: org.name || '',
                        type: org.type || '',
                        contactInfo: {
                            email: org.email || '',
                            phoneNumber: org.phone || '',
                            website: org.website || ''
                        },
                        address: {
                            line1: org.streetAddress || '',
                            line2: '',
                            city: org.city || '',
                            state: org.state || '',
                            postalCode: org.postalCode || '',
                            country: org.country || ''
                        },
                        branding: {
                            logoUrl: org.logoUrl || '',
                            primaryColor: '#007bff', // Default color
                            motto: '',
                            description: org.description || ''
                        },
                        establishedYear: org.establishedYear || new Date().getFullYear()
                    };
                    console.log('Organization data loaded in settings:', $scope.organizationData);
                } else {
                    // No organization found, use default structure
                    console.log('No organization found, using default structure');
                    $scope.organizationData = OrganizationService.createDefaultOrganization();
                }
            }).catch(function(error) {
                console.error('Error loading organization data:', error);
                $scope.organizationData = OrganizationService.createDefaultOrganization();
            });
        };
        
        // Save organization function for edit form
        $scope.saveOrganization = function() {
            if (!$scope.organizationData) {
                console.error('No organization data to save');
                return;
            }
            
            console.log('Saving organization:', $scope.organizationData);
            
            // Convert frontend structure to backend structure for API call
            var orgForApi = {
                id: $scope.organizationData.id,
                name: $scope.organizationData.name,
                type: $scope.organizationData.type,
                email: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.email : '',
                phone: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.phoneNumber : '',
                website: $scope.organizationData.contactInfo ? $scope.organizationData.contactInfo.website : '',
                streetAddress: $scope.organizationData.address ? $scope.organizationData.address.line1 : '',
                city: $scope.organizationData.address ? $scope.organizationData.address.city : '',
                state: $scope.organizationData.address ? $scope.organizationData.address.state : '',
                postalCode: $scope.organizationData.address ? $scope.organizationData.address.postalCode : '',
                country: $scope.organizationData.address ? $scope.organizationData.address.country : '',
                description: $scope.organizationData.branding ? $scope.organizationData.branding.description : '',
                logoUrl: $scope.organizationData.branding ? $scope.organizationData.branding.logoUrl : '',
                establishedYear: $scope.organizationData.establishedYear,
                isActive: true
            };
            
            var promise;
            if (orgForApi.id) {
                // Update existing organization
                promise = ApiService.put('/organizations/' + orgForApi.id, orgForApi);
            } else {
                // Create new organization
                promise = ApiService.post('/organizations', orgForApi);
            }
            
            promise.then(function(response) {
                console.log('Organization saved successfully:', response.data);
                // Refresh the organization data
                $scope.loadOrganizationData();
                // Show success message (you can add UI feedback here)
                alert('Organization saved successfully!');
                // Redirect to view page
                window.location.href = '#!/settings/organisation';
            }).catch(function(error) {
                console.error('Error saving organization:', error);
                alert('Error saving organization. Please try again.');
            });
        };
        
        // Reset organization to defaults
        $scope.resetToDefaults = function() {
            $scope.organizationData = OrganizationService.createDefaultOrganization();
            console.log('Reset organization to defaults');
        };
        
        // Helper functions to get labels for dropdown values
        $scope.getLanguageLabel = function(value) {
            var lang = $scope.availableLanguages.find(function(l) { return l.value === value; });
            return lang ? lang.label : value;
        };
        
        $scope.getThemeLabel = function(value) {
            var theme = $scope.availableThemes.find(function(t) { return t.value === value; });
            return theme ? theme.label : value;
        };
        
        $scope.getTimezoneLabel = function(value) {
            var tz = $scope.availableTimezones.find(function(t) { return t.value === value; });
            return tz ? tz.label : value;
        };
        
        $scope.getCurrencyLabel = function(value) {
            var curr = $scope.availableCurrencies.find(function(c) { return c.value === value; });
            return curr ? curr.label : value;
        };
        
        $scope.formatDateTime = function(dateTime) {
            if (!dateTime) return '';
            try {
                return new Date(dateTime).toLocaleString();
            } catch (e) {
                return dateTime;
            }
        };
        
        // Tab navigation
        $scope.setActiveSettingsTab = function(tab) {
            $scope.activeSettingsTab = tab;
            
            // Auto-expand the appropriate submenu based on the selected tab
            $scope.$evalAsync(function() {
                if (tab.includes('organisation') || tab === 'view-organisation') {
                    // Show organisation submenu
                    var orgSubmenu = document.getElementById('organisationSubmenu');
                    if (orgSubmenu && !orgSubmenu.classList.contains('show')) {
                        var bsCollapse = new bootstrap.Collapse(orgSubmenu, {show: true});
                    }
                    // Hide user submenu
                    var userSubmenu = document.getElementById('userSubmenu');
                    if (userSubmenu && userSubmenu.classList.contains('show')) {
                        var bsCollapse = new bootstrap.Collapse(userSubmenu, {hide: true});
                    }
                } else if (tab.includes('user') || tab === 'view-user') {
                    // Show user submenu
                    var userSubmenu = document.getElementById('userSubmenu');
                    if (userSubmenu && !userSubmenu.classList.contains('show')) {
                        var bsCollapse = new bootstrap.Collapse(userSubmenu, {show: true});
                    }
                    // Hide organisation submenu
                    var orgSubmenu = document.getElementById('organisationSubmenu');
                    if (orgSubmenu && orgSubmenu.classList.contains('show')) {
                        var bsCollapse = new bootstrap.Collapse(orgSubmenu, {hide: true});
                    }
                }
            });
        };
        
        // Helper function for templates
        $scope.Object = Object;
        
        // Initialize default settings structure
        $scope.initializeSettings = function() {
            $scope.settings = {
                // General Settings
                general: {
                    applicationName: 'Student Information System',
                    applicationVersion: '1.0.0',
                    theme: 'light',
                    language: 'en',
                    dateFormat: 'MM/dd/yyyy',
                    timeFormat: '12-hour',
                    currency: 'USD',
                    timezone: 'America/New_York'
                },
                
                // User Interface Settings
                ui: {
                    sidebarCollapsed: false,
                    showTooltips: true,
                    showAnimations: true,
                    compactMode: false,
                    showBreadcrumbs: true,
                    itemsPerPage: 10,
                    autoSave: true,
                    confirmDialogs: true
                },
                
                // Notification Settings
                notifications: {
                    email: {
                        enabled: true,
                        newStudentRegistration: true,
                        attendanceReminders: true,
                        gradeUpdates: true,
                        parentCommunication: true,
                        systemMaintenance: true
                    },
                    sms: {
                        enabled: false,
                        emergencyAlerts: true,
                        attendanceAlerts: false,
                        eventReminders: true
                    },
                    desktop: {
                        enabled: true,
                        soundEnabled: true,
                        showPreview: true
                    }
                },
                
                // Security Settings
                security: {
                    passwordPolicy: {
                        minLength: 8,
                        requireUppercase: true,
                        requireLowercase: true,
                        requireNumbers: true,
                        requireSpecialChars: false,
                        passwordExpiry: 90
                    },
                    session: {
                        timeoutMinutes: 30,
                        multipleLogins: false,
                        rememberMe: true
                    },
                    twoFactor: {
                        enabled: false,
                        method: 'email' // email, sms, app
                    }
                },
                
                // System Settings
                system: {
                    maintenance: {
                        enabled: false,
                        message: 'System is under maintenance. Please try again later.',
                        startTime: '',
                        endTime: ''
                    },
                    backups: {
                        enabled: true,
                        frequency: 'daily', // daily, weekly, monthly
                        retentionDays: 30,
                        includeLogs: true
                    },
                    logging: {
                        level: 'INFO', // DEBUG, INFO, WARN, ERROR
                        maxFileSize: '10MB',
                        maxFiles: 5
                    }
                }
            };
        };
        
        // Load current settings
        $scope.loadSettings = function() {
            $scope.loading = true;
            
            ApiService.get('/settings/user').then(function(response) {
                if (response.data) {
                    $scope.settings = angular.merge($scope.settings, response.data);
                }
            }).catch(function(error) {
                console.error('Error loading settings:', error);
                // Use default settings if none exist
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Save settings
        $scope.saveSettings = function() {
            $scope.loading = true;
            
            ApiService.put('/settings/user', $scope.settings).then(function(response) {
                $scope.showToast('success', 'Settings Saved', 'Your settings have been saved successfully.');
                $scope.applySettings();
                // Redirect to user view page
                window.location.href = '#!/settings/user';
            }).catch(function(error) {
                console.error('Error saving settings:', error);
                $scope.showToast('error', 'Error', 'Failed to save settings. Please try again.');
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Apply settings to the application
        $scope.applySettings = function() {
            // Apply theme
            document.body.className = document.body.className.replace(/theme-\w+/g, '');
            document.body.classList.add('theme-' + $scope.settings.general.theme);
            
            // Apply other UI settings
            if ($scope.settings.ui.compactMode) {
                document.body.classList.add('compact-mode');
            } else {
                document.body.classList.remove('compact-mode');
            }
            
            // Update root scope for global access
            $rootScope.settings = $scope.settings;
        };
        
        // Reset settings to default
        $scope.resetSettings = function() {
            if (confirm('This will reset all settings to their default values. Are you sure?')) {
                $scope.initializeSettings();
                $scope.showToast('info', 'Settings Reset', 'All settings have been reset to default values.');
            }
        };
        
        // Export settings
        $scope.exportSettings = function() {
            const dataStr = JSON.stringify($scope.settings, null, 2);
            const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
            
            const exportFileDefaultName = 'erp-settings-' + new Date().toISOString().split('T')[0] + '.json';
            
            const linkElement = document.createElement('a');
            linkElement.setAttribute('href', dataUri);
            linkElement.setAttribute('download', exportFileDefaultName);
            linkElement.click();
            
            $scope.showToast('success', 'Settings Exported', 'Settings have been exported successfully.');
        };
        
        // Import settings
        $scope.importSettings = function() {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = '.json';
            
            input.onchange = function(event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        try {
                            const importedSettings = JSON.parse(e.target.result);
                            $scope.settings = angular.merge($scope.settings, importedSettings);
                            $scope.$apply();
                            $scope.showToast('success', 'Settings Imported', 'Settings have been imported successfully.');
                        } catch (error) {
                            $scope.showToast('error', 'Import Error', 'Invalid settings file. Please check the file format.');
                        }
                    };
                    reader.readAsText(file);
                }
            };
            
            input.click();
        };
        
        // Clear cache
        $scope.clearCache = function() {
            if (confirm('This will clear all cached data. Continue?')) {
                // Clear browser cache programmatically (limited support)
                if ('serviceWorker' in navigator) {
                    navigator.serviceWorker.getRegistrations().then(function(registrations) {
                        for (let registration of registrations) {
                            registration.unregister();
                        }
                    });
                }
                
                // Clear localStorage
                localStorage.clear();
                sessionStorage.clear();
                
                $scope.showToast('success', 'Cache Cleared', 'Application cache has been cleared.');
            }
        };
        
        // Test email settings
        $scope.testEmailSettings = function() {
            $scope.loading = true;
            
            ApiService.post('/settings/test-email', {}).then(function(response) {
                $scope.showToast('success', 'Email Test', 'Test email sent successfully!');
            }).catch(function(error) {
                $scope.showToast('error', 'Email Test Failed', 'Failed to send test email. Please check your email configuration.');
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Test SMS settings
        $scope.testSMSSettings = function() {
            $scope.loading = true;
            
            ApiService.post('/settings/test-sms', {}).then(function(response) {
                $scope.showToast('success', 'SMS Test', 'Test SMS sent successfully!');
            }).catch(function(error) {
                $scope.showToast('error', 'SMS Test Failed', 'Failed to send test SMS. Please check your SMS configuration.');
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Check for updates
        $scope.checkForUpdates = function() {
            $scope.loading = true;
            
            ApiService.get('/settings/check-updates').then(function(response) {
                if (response.data && response.data.updateAvailable) {
                    $scope.showToast('info', 'Update Available', 'Version ' + response.data.latestVersion + ' is available for download.');
                } else {
                    $scope.showToast('success', 'Up to Date', 'You are running the latest version of the application.');
                }
            }).catch(function(error) {
                $scope.showToast('error', 'Update Check Failed', 'Failed to check for updates. Please try again later.');
            }).finally(function() {
                $scope.loading = false;
            });
        };
        
        // Settings tab management
        $scope.setActiveSettingsTab = function(tab) {
            $scope.activeSettingsTab = tab;
        };
        
        // Available options
        $scope.themeOptions = [
            { value: 'light', label: 'Light Theme' },
            { value: 'dark', label: 'Dark Theme' },
            { value: 'auto', label: 'Auto (System)' }
        ];
        
        $scope.languageOptions = [
            { value: 'en', label: 'English' },
            { value: 'es', label: 'Spanish' },
            { value: 'fr', label: 'French' },
            { value: 'de', label: 'German' },
            { value: 'zh', label: 'Chinese' }
        ];
        
        $scope.dateFormatOptions = [
            { value: 'MM/dd/yyyy', label: 'MM/DD/YYYY (US)' },
            { value: 'dd/MM/yyyy', label: 'DD/MM/YYYY (UK)' },
            { value: 'yyyy-MM-dd', label: 'YYYY-MM-DD (ISO)' },
            { value: 'MMM dd, yyyy', label: 'MMM DD, YYYY' }
        ];
        
        $scope.timeFormatOptions = [
            { value: '12-hour', label: '12-hour (AM/PM)' },
            { value: '24-hour', label: '24-hour' }
        ];
        
        $scope.timezoneOptions = [
            { value: 'America/New_York', label: 'Eastern Time (US & Canada)' },
            { value: 'America/Chicago', label: 'Central Time (US & Canada)' },
            { value: 'America/Denver', label: 'Mountain Time (US & Canada)' },
            { value: 'America/Los_Angeles', label: 'Pacific Time (US & Canada)' }
        ];
        
        $scope.logLevelOptions = [
            { value: 'DEBUG', label: 'Debug' },
            { value: 'INFO', label: 'Information' },
            { value: 'WARN', label: 'Warnings' },
            { value: 'ERROR', label: 'Errors Only' }
        ];
        
        // Utility functions
        $scope.showToast = function(type, title, message) {
            $rootScope.$broadcast('app:' + type, message);
        };
        
        // Initialize controller when page loads
        $scope.init();
    }
]);