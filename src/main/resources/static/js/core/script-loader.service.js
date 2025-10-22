// Dynamic Script Loader Service for Lazy Loading
angular.module('erpApp').service('ScriptLoaderService', ['$q', '$timeout', function($q, $timeout) {
    var loadedScripts = {};
    var loadingPromises = {};

    // Load a single script file
    this.loadScript = function(src) {
        console.log('📥 Loading script:', src);
        
        // Return existing promise if already loading
        if (loadingPromises[src]) {
            console.log('⏳ Script already loading:', src);
            return loadingPromises[src];
        }

        // Return resolved promise if already loaded
        if (loadedScripts[src]) {
            console.log('✅ Script already loaded:', src);
            return $q.resolve();
        }

        var deferred = $q.defer();
        loadingPromises[src] = deferred.promise;

        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = src;

        script.onload = function() {
            console.log('✅ Script loaded successfully:', src);
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

        // Load multiple scripts in sequence
    this.loadScripts = function(scripts) {
        var promises = [];
        var self = this;
        
        // Try to get performance monitor (may not be available during bootstrap)
        try {
            var $injector = angular.injector(['erpApp']);
            var performanceMonitor = $injector.get('PerformanceMonitorService');
            if (performanceMonitor && scripts.length > 0) {
                performanceMonitor.startTimer('scripts-loading-' + scripts.length);
            }
        } catch(e) {
            // Performance monitor not available yet
        }
        
        scripts.forEach(function(script) {
            promises.push(self.loadScript(script));
        });
        
        return $q.all(promises).then(function(results) {
            // Record completion time
            try {
                if (performanceMonitor && scripts.length > 0) {
                    performanceMonitor.endTimer('scripts-loading-' + scripts.length);
                }
            } catch(e) {
                // Performance monitor not available
            }
            return results;
        });
    };

    // Load scripts for a specific module/entity
    this.loadModule = function(moduleName) {
        console.log('🔄 Loading module:', moduleName);
        var moduleScripts = this.getModuleScripts(moduleName);
        console.log('📋 Scripts to load:', moduleScripts);
        
        if (moduleScripts.length > 0) {
            return this.loadScripts(moduleScripts).then(function(results) {
                console.log('📦 Scripts loaded, waiting for registration...');
                // Add a longer delay to ensure scripts are fully executed and registered
                return $timeout(function() {
                    console.log('✅ Module loaded and registered:', moduleName);
                    
                    // Verify controller is registered for debugging
                    if (moduleName === 'students') {
                        try {
                            var $injector = angular.element(document.body).injector();
                            if ($injector && $injector.has('$controller')) {
                                var $controller = $injector.get('$controller');
                                console.log('🔍 Checking if StudentController is registered...');
                                // This will throw an error if controller is not found
                                $controller('StudentController', {$scope: {}});
                                console.log('✅ StudentController is registered!');
                            }
                        } catch (e) {
                            console.error('❌ StudentController registration check failed:', e);
                        }
                    }
                    
                    return results;
                }, 300); // Increased delay from 100ms to 300ms
            });
        }
        return $q.resolve();
    };

    // Define module-specific scripts
    this.getModuleScripts = function(moduleName) {
        var moduleMap = {
            'settings': [
                '/js/modules/settings.module.js?v=1',
                '/js/settings/settings.service.js?v=1',
                '/js/settings/organization.service.js?v=1',
                '/js/email/email.service.js?v=1',
                '/js/settings/settings.controller.js?v=1',
                '/js/settings/organization.controller.js?v=1',
                '/js/email/email-template.controller.js?v=1',
                '/js/email/email-log.controller.js?v=1',
                '/js/email/entity-email.controller.js?v=1'
            ],
            'students': [
                '/js/modules/students.module.js?v=1',
                '/js/student/student.service.js?v=3',
                '/js/student/student.controller.js?v=3'
            ],
            'parents': [
                '/js/modules/parents.module.js?v=1',
                '/js/parent/parent.service.js?v=2',
                '/js/parent/parent.controller.js?v=2'
            ],
            'attendance': [
                '/js/modules/attendance.module.js?v=1',
                '/js/attendance/attendance.service.js?v=3',
                '/js/attendance/attendance.controller.js?v=3'
            ],
            'health': [
                '/js/modules/health.module.js?v=1',
                '/js/health/health.service.js?v=2',
                '/js/health/health.controller.js?v=2'
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