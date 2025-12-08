package krs.erp.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import krs.erp.repository.TimetableRepository;
import krs.erp.service.DataImportService;

// @Component - Disabled for multi-tenant system if handled by generic loader, using Order for explicit run if enabled
@Component // Re-enabling for testing if multi-tenancy not strictly blocking this
@Order(11)
public class TimetableDataInitializer implements CommandLineRunner {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private DataImportService dataImportService;

    @Override
    public void run(String... args) throws Exception {
        if (timetableRepository.count() == 0) {
            System.out.println("Initializing Timetable data...");
            dataImportService.importTimetablesDataFromXml("data/timetable/timetables.xml");
        }
    }
}
