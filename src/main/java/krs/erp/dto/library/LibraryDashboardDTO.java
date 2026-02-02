package krs.erp.dto.library;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class LibraryDashboardDTO {
    // Top Level Cards
    private long totalInventory;
    private long totalDigitalAssets;

    private long totalBooksOut;
    private long totalBooksIn;
    private double circulationRate; // (Out / Total)*100

    private long overdueCount;
    private long todayTraffic; // Issues + Returns today

    // Charts & Lists
    private List<TopBorrowedBook> trendingBooks;
    private List<OverdueUser> overdueLeaders;
    private Map<String, Integer> collectionHealth; // Category -> Count
    private List<UpcomingDueBook> upcomingDueBooks;

    @Data
    public static class TopBorrowedBook {
        private String title;
        private int borrowCount;
    }

    @Data
    public static class OverdueUser {
        private Long userId;
        private String name;
        private int overdueItemsCount;
    }

    @Data
    public static class UpcomingDueBook {
        private String title;
        private String currentHolder;
        private String dueDate;
    }
}
