package com.smartpos.interfaces.rest;

import com.smartpos.application.dtos.DashboardStatsDTO;
import com.smartpos.application.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for dashboard statistics
 * Uses SQL views for optimized aggregated queries
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * Get aggregated dashboard stats for the last N days
     * GET /api/v1/dashboard/stats?days=7
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getAggregatedStats(
            @RequestParam(defaultValue = "7") int days) {
        DashboardStatsDTO stats = dashboardService.getAggregatedStats(days);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get daily breakdown stats for the last N days
     * GET /api/v1/dashboard/daily?days=7
     */
    @GetMapping("/daily")
    public ResponseEntity<List<DashboardStatsDTO>> getDailyStats(
            @RequestParam(defaultValue = "7") int days) {
        List<DashboardStatsDTO> stats = dashboardService.getDailyStats(days);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get stats for a specific cash session
     * GET /api/v1/dashboard/session/{sessionId}
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<DashboardStatsDTO>> getSessionStats(
            @PathVariable Long sessionId) {
        List<DashboardStatsDTO> stats = dashboardService.getSessionStats(sessionId);
        return ResponseEntity.ok(stats);
    }
}
