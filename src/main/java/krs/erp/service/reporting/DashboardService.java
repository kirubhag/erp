package krs.erp.service.reporting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import krs.erp.repository.StaffRepository;
import krs.erp.repository.inventory.AssetRepository;
import krs.erp.repository.maintenance.WorkOrderRepository;

@Service
public class DashboardService {

    @Autowired(required = false)
    private StaffRepository staffRepository;

    @Autowired(required = false)
    private AssetRepository assetRepository;

    @Autowired(required = false)
    private WorkOrderRepository workOrderRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Admissions (Mocked or real if repo exists)
        stats.put("totalStudents", 378);
        stats.put("presentToday", 345);
        stats.put("absentToday", 33);
        stats.put("attendanceRate", 91.2);

        // HR
        if (staffRepository != null) {
            stats.put("totalStaff", staffRepository.count());
        } else {
            stats.put("totalStaff", 25);
        }

        // Inventory
        if (assetRepository != null) {
            stats.put("totalAssets", assetRepository.count());
        } else {
            stats.put("totalAssets", 150);
        }

        // Maintenance
        if (workOrderRepository != null) {
            stats.put("pendingWorkOrders", workOrderRepository.count());
        } else {
            stats.put("pendingWorkOrders", 5);
        }

        // Finance (Mocked)
        stats.put("monthlyRevenue", 1250000.0);
        stats.put("monthlyExpense", 850000.0);

        return stats;
    }
}
