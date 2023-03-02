package org.fitness.service;

import lombok.val;
import org.fitness.model.UserDetails;
import org.fitness.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final String ERROR_FUTURE_DATE = "Cannot add attendance. Given date is in the future.";
    private static final String ERROR_EXISTING_DATE = "Cannot add attendance. "
            + "Attendance for this date has already been added.";

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    public List<UserDetails> getAllAttendances() {
        return userDetailsRepository.findAll();
    }

    public UserDetails addAttendance(final UserDetails userDetails) {
        if (userDetails.getDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_FUTURE_DATE);
        }

        val userExists = userDetailsRepository.findAll()
                .stream()
                .filter(user -> user.getName().equalsIgnoreCase(userDetails.getName())
                        && user.getDate().equals(userDetails.getDate()))
                .findAny();

        if (userExists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_EXISTING_DATE);
        }

        return userDetailsRepository.save(userDetails);
    }

    public boolean checkDiscountEligibility(final String username) {
        val userAttendanceList = retrieveUserAttendance(username);

        return isEligibleForDiscount(userAttendanceList);
    }

    public int getCurrentStreak(final String username) {
        val userAttendanceList = retrieveUserAttendance(username);

        return checkCurrentStreak(userAttendanceList);
    }

    private int checkCurrentStreak(final List<LocalDate> dateList) {
        int count = 0;
        for(int i=0; i<52; i++) {
            if (checkWeeklyAttendance(dateList, i)) {
                count = count + 1;
            } else {
                return count;
            }
        }
        return count;
    }

    private boolean checkWeeklyAttendance(final List<LocalDate> attendedDates, final int weekNumber) {
        val startDate = LocalDate.now().plusDays(1).minusWeeks(weekNumber);
        return hasAttended(startDate, attendedDates);
    }

    private List<LocalDate> retrieveUserAttendance(final String username) {
        val userAttendanceList = userDetailsRepository.findAll()
                .stream()
                .filter(u -> u.getName().equalsIgnoreCase(username))
                .collect(Collectors.toList());

        if (userAttendanceList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.OK,
                    "No records exist for user " + username);
        }

        val dateList = userAttendanceList
                .stream()
                .map(UserDetails::getDate)
                .collect(Collectors.toList());

        return dateList;
    }

    private boolean isEligibleForDiscount(final List<LocalDate> dateList) {
        int count = 0;
        if (dateList.stream().count() > 3) {
            for(int i=0; i<52; i++) {
                if (checkWeeklyAttendance(dateList, i)) {
                    count = count + 1;
                    if (count < 3) {
                        continue;
                    }
                    if (count == 3) {
                        return true;
                    } else {
                        // reset
                        count = 0;
                    }
                } else {
                    // reset
                    count = 0;
                }
            }
        } else {
            // no need to check further
        }
        return false;
    }

    private boolean hasAttended(final LocalDate endDate, final List<LocalDate> userDateList) {
        LocalDate startDate = endDate.minusWeeks(1);
        List<LocalDate> dateRange = startDate.datesUntil(endDate).collect(Collectors.toList());
        val commonDates = dateRange.stream().filter(userDateList::contains).collect(Collectors.toList());

        return !commonDates.isEmpty();
    }

    private void deleteUserDetail(final UserDetails userDetails) {
        userDetailsRepository.delete(userDetails);
    }
}
