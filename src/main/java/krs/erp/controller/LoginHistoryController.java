package krs.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krs.erp.model.LoginHistory;
import krs.erp.repository.LoginHistoryRepository;

@RestController
@RequestMapping("/api/login-history")
public class LoginHistoryController {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @GetMapping
    public ResponseEntity<Page<LoginHistory>> getAllLoginHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "loginTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(loginHistoryRepository.findAll(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LoginHistory>> getUserLoginHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("loginTime").descending());
        return ResponseEntity.ok(loginHistoryRepository.findByUserId(userId, pageable));
    }
}
