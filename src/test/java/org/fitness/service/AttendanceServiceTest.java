package org.fitness.service;

import lombok.val;
import org.fitness.VerifitApplication;
import org.fitness.UserDetailsHelper;
import org.fitness.model.UserDetails;
import org.fitness.repository.UserDetailsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = VerifitApplication.class)
@ExtendWith(SpringExtension.class)
public class AttendanceServiceTest {

    private static final String ERROR_EXISTING_DATE = "Cannot add attendance. "
            + "Attendance for this date has already been added.";
    private UserDetails userDetails;

    @Autowired
    AttendanceService underTest;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @AfterEach
    void setup() {
        userDetailsRepository.delete(userDetails);
    }

    @Test
    void whenValidAttendanceIsSuppliedThenAttendanceIsAdded() {
        userDetails = UserDetailsHelper.getUserDetails();
        val result = underTest.addAttendance(userDetails);
        assertThat(result.getDate()).isToday();
        assertThat(result.getName()).isEqualTo(userDetails.getName());
    }

    @Test()
    void whenAttendanceDateExistsThenEntryIsNotAdded() throws ResponseStatusException {
        userDetails = UserDetailsHelper.getUserDetails();
        underTest.addAttendance(userDetails);
        assertThatThrownBy(() -> underTest.addAttendance(userDetails))
                .hasMessageContaining(ERROR_EXISTING_DATE);
    }

    @Test
    void whenStreakIsLessThanThreeThenNoDiscountIsAvailable() {
        userDetails = UserDetailsHelper.getUserDetails();
        val result = underTest.checkDiscountEligibility(userDetails.getName());
        assertThat(result).isFalse();
    }

    @Test
    void whenStreakIsAtLeastThreeThenDiscountIsAvailable() {
        userDetails = UserDetailsHelper.getUserDetails();
        userDetails.setDate(LocalDate.now().minusWeeks(2)); // get streak to 3 weeks
        underTest.addAttendance(userDetails);
        val result = underTest.checkDiscountEligibility(userDetails.getName());
        assertThat(result).isTrue();
    }

    @Test
    void verifyThatStreakIsReturnedForValidUser() {
        userDetails = UserDetailsHelper.getUserDetails();
        val result = underTest.getCurrentStreak(userDetails.getName());
        assertThat(result).isEqualTo(2); // 2 weeks in a row
    }
}
