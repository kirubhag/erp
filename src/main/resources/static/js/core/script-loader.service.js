// Dynamic Script Loader Service for Lazy Loading
angular.module('erpApp').service('ScriptLoaderService', ['$q', '$timeout', function($q, $timeout) {
    var loadedScripts = {};
    var loadingPromises = {};

    // Load a single script file
    this.loadScript = function(src) {

        
        // Return existing promise if already loading
        if (loadingPromises[src]) {

            return loadingPromises[src];
        }

        // Return resolved promise if already loaded
        if (loadedScripts[src]) {

            return $q.resolve();
        }

        var deferred = $q.defer();
        loadingPromises[src] = deferred.promise;

        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = src;

        script.onload = function() {

            loadedScripts[src] = true;
            delete loadingPromises[src];
            $timeout(function() {
                deferred.resolve();
            }, 50); // Small delay to allow script execution
        };

        script.onerror = function() {
            console.error('❌ Script failed to load:', src);
            delete loadingPromises[src];
            $timeout(function() {
                deferred.reject('Failed to load script: ' + src);
            });
        };

        document.head.appendChild(script);
        return deferred.promise;
    };

        // Load multiple scripts in sequence (not parallel)
    this.loadScripts = function(scripts) {
        var self = this;
        var deferred = $q.defer();
        

        
        // Try to get performance monitor (may not be available during bootstrap)
        var performanceMonitor = null;
        try {
            var $injector = angular.injector(['erpApp']);
            performanceMonitor = $injector.get('PerformanceMonitorService');
            if (performanceMonitor && scripts.length > 0) {
                performanceMonitor.startTimer('scripts-loading-' + scripts.length);
            }
        } catch(e) {
            // Performance monitor not available yet
        }
        
        // Load scripts one by one sequentially
        function loadNextScript(index) {
            if (index >= scripts.length) {
                // All scripts loaded successfully

                try {
                    if (performanceMonitor && scripts.length > 0) {
                        performanceMonitor.endTimer('scripts-loading-' + scripts.length);
                    }
                } catch(e) {
                    // Performance monitor not available
                }
                deferred.resolve();
                return;
            }
            
            var scriptUrl = scripts[index];

            
            self.loadScript(scriptUrl).then(function() {

                // Wait before loading next script to ensure proper execution order
                $timeout(function() {
                    loadNextScript(index + 1);
                }, 100); // Increased to 100ms for better reliability
            }).catch(function(error) {
                console.error('❌ Failed to load script:', scriptUrl, error);
                deferred.reject(error);
            });
        }
        
        // Start loading from first script
        loadNextScript(0);
        return deferred.promise;
    };

    // Load scripts for a specific module/entity
    this.loadModule = function(moduleName) {

        var moduleScripts = this.getModuleScripts(moduleName);

        
        if (moduleScripts.length > 0) {
            return this.loadScripts(moduleScripts).then(function(results) {

                
                // Force AngularJS to fully process the controller registration

                
                // Create a deferred promise that we'll resolve manually
                var deferred = $q.defer();
                
                // Force multiple digest cycles to ensure registration is processed
                var cycleCount = 0;
                var maxCycles = 5;
                
                function forceDigestCycles() {
                    cycleCount++;

                    
                    $timeout(function() {
                        // Try to get the controller name for this module
                        var controllerName = '';
                        switch(moduleName) {
                            case 'students': controllerName = 'StudentController'; break;
                            case 'settings': controllerName = 'SettingsController'; break;
                            case 'parents': controllerName = 'ParentController'; break;
                            case 'attendance': controllerName = 'AttendanceController'; break;
                            case 'health': controllerName = 'HealthController'; break;
                        }
                        
                        if (controllerName) {

                            
                            // Check if we have more cycles to run
                            if (cycleCount < maxCycles) {
                                // Continue with more cycles
                                forceDigestCycles();
                                return;
                            } else {
                                // Final verification after all cycles

                            }
                        }
                        

                        deferred.resolve(results);
                    }, 300); // 300ms between each cycle
                }
                
                // Start the digest cycle process
                forceDigestCycles();
                
                return deferred.promise;
            });
        }
        return $q.resolve();
    };

    // Define module-specific scripts
    this.getModuleScripts = function(moduleName) {
        var moduleMap = {
            'settings': [
                '/js/modules/settings.module.js?v=2',
                '/js/constants/ui-field-types.js?v=1',
                '/js/settings/settings.service.js?v=2',
                '/js/settings/organization.service.js?v=2',
                '/js/email/email.service.js?v=2',
                '/js/settings/settings.controller.js?v=3',
                '/js/settings/entity-field-customization.controller.js?v=1',
                '/js/settings/organization.controller.js?v=2',
                '/js/email/email-template.controller.js?v=2',
                '/js/email/email-log.controller.js?v=2',
                '/js/email/entity-email.controller.js?v=2'
            ],
            'students': [
                '/js/modules/students.module.js?v=2',
                '/js/student/student.service.js?v=4',
                '/js/student/student.controller.js?v=18'
            ],
            'parents': [
                '/js/modules/parents.module.js?v=2',
                '/js/parent/parent.service.js?v=3',
                '/js/parent/parent.controller.js?v=3'
            ],
            'attendance': [
                '/js/modules/attendance.module.js?v=2',
                '/js/attendance/attendance.service.js?v=4',
                '/js/attendance/attendance.controller.js?v=4'
            ],
            'health': [
                '/js/modules/health.module.js?v=2',
                '/js/health/health.service.js?v=3',
                '/js/health/health.controller.js?v=3'
            ],
            'organization': [
                '/js/modules/organization.module.js?v=1',
                '/js/organization/organization.service.js?v=1',
                '/js/organization/organization-details.controller.js?v=1'
            ]
        };

        return moduleMap[moduleName] || [];
    };

    // Get loading priority - core scripts load first
    this.getLoadingPriority = function() {
        return {
            core: [
                '/js/core/api.js?v=3'
            ],
            common: [
                // Common utilities that might be shared across modules
            ]
        };
    };

    // Preload frequently used modules (optional optimization)
    this.preloadModules = function(moduleNames) {
        var self = this;
        if (!angular.isArray(moduleNames)) {
            moduleNames = [moduleNames];
        }

        angular.forEach(moduleNames, function(moduleName) {
            var scripts = self.getModuleScripts(moduleName);
            if (scripts.length > 0) {
                // Preload without blocking - just get the resources in cache
                angular.forEach(scripts, function(scriptUrl) {
                    var link = document.createElement('link');
                    link.rel = 'prefetch';
                    link.href = scriptUrl;
                    document.head.appendChild(link);
                });
            }
        });
    };

    // Check if module is already loaded
    this.isModuleLoaded = function(moduleName) {
        return loadedModules.indexOf(moduleName) !== -1;
    };

    // Check if a module is loaded
    this.isModuleLoaded = function(moduleName) {
        var scripts = this.getModuleScripts(moduleName);
        return scripts.every(function(script) {
            return loadedScripts[script];
        });
    };
}]);