package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.CloseCashDTO;
import com.smartpos.application.services.CloseCashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/close-cash")
@RequiredArgsConstructor
@Tag(name = "Close Cash", description = "Cash closing session management endpoints")
public class CloseCashController {
    
    private final CloseCashService closeCashService;
    
    @PostMapping("/open")
    @Operation(summary = "Open a new cash closing session")
    public ResponseEntity<CloseCashDTO> openCloseCash(@RequestParam(required = false) String cashierName) {
        return ResponseEntity.status(HttpStatus.CREATED).body(closeCashService.openCloseCash(cashierName));
    }
    
    @PostMapping("/{id}/close")
    @Operation(summary = "Close a cash session")
    public ResponseEntity<CloseCashDTO> closeCloseCash(@PathVariable Long id) {
        return ResponseEntity.ok(closeCashService.closeCloseCash(id));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get close cash session by ID")
    public ResponseEntity<CloseCashDTO> getCloseCashById(@PathVariable Long id) {
        return ResponseEntity.ok(closeCashService.getCloseCashById(id));
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get all pending (unreconciled) close cash sessions")
    public ResponseEntity<List<CloseCashDTO>> getPendingCloseCash() {
        return ResponseEntity.ok(closeCashService.getPendingCloseCash());
    }
    
    @PostMapping("/{id}/reconcile")
    @Operation(summary = "Reconcile a close cash session")
    public ResponseEntity<Void> reconcileCloseCash(@PathVariable Long id) {
        closeCashService.reconcileCloseCash(id);
        return ResponseEntity.noContent().build();
    }
}
