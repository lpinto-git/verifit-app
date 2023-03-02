package org.fitness.controller;

import lombok.val;
import org.fitness.UserDetailsHelper;
import org.fitness.model.UserDetails;
import org.fitness.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerifitControllerTest {

    private static final String USERNAME = "username";

    @Mock
    AttendanceService attendanceServiceMock;

    @InjectMocks
    VerifitController verifitController;

    @BeforeTestMethod
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenAttendanceIsCalledThenUserIsAdded() {
        when(attendanceServiceMock.addAttendance(any(UserDetails.class))).thenReturn(mock(UserDetails.class));
        val response = verifitController.addAttendance(UserDetailsHelper.getUserDetails());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void whenCurrentStreakIsCalledThenCurrentStreakIsReturned() {
        when(attendanceServiceMock.getCurrentStreak(any(String.class))).thenReturn(1);
        val response = verifitController.getCurrentStreak(USERNAME);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getCurrentStreak()).isEqualTo(1);
        assertThat(response.getBody().getName()).isEqualTo(USERNAME);
    }

    @Test
    void whenCheckDiscountEligibilityIsCalledThenDiscountEligibilityIsChecked() {
        when(attendanceServiceMock.checkDiscountEligibility(any(String.class))).thenReturn(true);
        val response = verifitController.checkDiscountEligibility(USERNAME);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        response.getBody().equals(String.format("Gym user '%s' is eligible for discount.", USERNAME));
    }
}
