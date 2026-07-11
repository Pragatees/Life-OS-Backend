package com.lifeos.service;

import com.lifeos.entity.RepeatType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RecurrenceCalculator {

    /**
     * Calculates the next occurrence date based on the repeat type.
     *
     * @param currentDate Current occurrence date
     * @param repeatType  Recurrence type
     * @return Next occurrence date
     */
    public LocalDate calculateNextDate(LocalDate currentDate, RepeatType repeatType) {

        if (currentDate == null || repeatType == null) {
            return currentDate;
        }

        return switch (repeatType) {

            case DAILY -> currentDate.plusDays(1);

            case WEEKLY -> currentDate.plusWeeks(1);

            case MONTHLY -> currentDate.plusMonths(1);

            case YEARLY -> currentDate.plusYears(1);

            case NEVER -> currentDate;
        };
    }

}