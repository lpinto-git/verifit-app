package org.fitness;

import lombok.val;
import org.fitness.model.UserDetails;

import java.time.LocalDate;

public final class UserDetailsHelper {

    public static UserDetails getUserDetails() {
        val userDetails = new UserDetails();
        userDetails.setDate(LocalDate.now());
        userDetails.setName("Lionel Pinto");
        return userDetails;
    }
}
