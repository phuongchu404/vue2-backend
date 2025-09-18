package vn.mk.eid.web.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.mk.eid.web.dto.report.OverviewStatistics;
import vn.mk.eid.web.dto.report.ReportResponse;

import java.time.LocalDate;

@Service
public class ReportCacheService {
    @Cacheable(value = "overview-stats", key = "'current'")
    public OverviewStatistics getCachedOverviewStatistics() {
        // This will be cached for faster access
        return null; // Will be populated by the actual service
    }

    @Cacheable(value = "detainee-status", key = "#fromDate + '_' + #toDate")
    public ReportResponse getCachedDetaineeStatusReport(LocalDate fromDate, LocalDate toDate) {
        return null; // Will be populated by the actual service
    }

    @CacheEvict(value = {"overview-stats", "detainee-status", "monthly-reports"}, allEntries = true)
    public void clearAllReportCache() {
        // Clear cache when data is updated
    }

    @Scheduled(fixedRate = 3600000) // Clear cache every hour
    public void scheduledCacheClear() {
        clearAllReportCache();
    }
}
