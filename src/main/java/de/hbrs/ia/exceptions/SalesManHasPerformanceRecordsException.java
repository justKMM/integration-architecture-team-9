package de.hbrs.ia.exceptions;

public class SalesManHasPerformanceRecordsException extends RuntimeException {

    public SalesManHasPerformanceRecordsException(int sid) {
        super(
            String.format(
                "The SalesMan \"%s\" has PerformanceRecords in the PerformanceRecord collection.",
                sid
            )
        );
    }

}
