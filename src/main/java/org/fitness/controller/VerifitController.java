package org.fitness.controller;

import lombok.val;
import org.fitness.model.UserCurrentStreak;
import org.fitness.model.UserDetails;
import org.fitness.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VerifitController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/attendance")
    public ResponseEntity<UserDetails> addAttendance(@RequestBody UserDetails user) {
        val created = attendanceService.addAttendance(user);
        return new ResponseEntity<>(created, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping("/currentStreak")
    public ResponseEntity<UserCurrentStreak> getCurrentStreak(@RequestParam String username) {
        final int currentStreak = attendanceService.getCurrentStreak(username);

        val userEntity = new UserCurrentStreak();
        userEntity.setName(username);
        userEntity.setCurrentStreak(currentStreak);

        return new ResponseEntity<>(userEntity, HttpStatus.OK);
    }

    @GetMapping("/discountEligibility")
    public ResponseEntity<String> checkDiscountEligibility(@RequestParam String username) {
        final boolean isEligibleForDiscount = attendanceService.checkDiscountEligibility(username);
        if (isEligibleForDiscount) {
            return new ResponseEntity<>(String.format("Gym user '%s' is eligible for discount.", username),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(String.format("Gym user '%s' is ineligible for discount.", username),
                    HttpStatus.OK);
        }
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<UserDetails>> getAllAttendances() {
        return new ResponseEntity<>(attendanceService.getAllAttendances(), HttpStatus.OK);
    }
}
