package com.smartpos.application.services;

import com.smartpos.application.dtos.DashboardStatsDTO;
import com.smartpos.domain.repositories.DashboardStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for dashboard operations using the v_dashboard_stats view
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    
    private final DashboardStatsRepository dashboardStatsRepository;
    
    /**
     * Get daily stats for the last N days
     */
    public List<DashboardStatsDTO> getDailyStats(int days) {
        return dashboardStatsRepository.getRecentStats(days);
    }
    
    /**
     * Get aggregated totals for the last N days
     * Perfect for the frontend dashboard summary
     */
    public DashboardStatsDTO getAggregatedStats(int days) {
        return dashboardStatsRepository.getAggregatedStats(days);
    }
    
    /**
     * Get stats for a specific cash session
     */
    public List<DashboardStatsDTO> getSessionStats(Long sessionId) {
        return dashboardStatsRepository.getStatsBySession(sessionId);
    }
}
