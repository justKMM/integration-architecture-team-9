package de.hbrs.ia.exceptions;

import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class DuplicatePerformanceRecordExcpetion extends RuntimeException {

    public DuplicatePerformanceRecordExcpetion(SocialPerformanceRecord record, SalesMan salesMan) {
        super(
            String.format(
                "A SocialPerformanceRecord for the Salesman \"%s\" with the goalId \"%s\" in the year \"%s\" already exists.",
                salesMan.getId(),
                record.getGoalid(),
                record.getYear()
            )
        );
    }

}
