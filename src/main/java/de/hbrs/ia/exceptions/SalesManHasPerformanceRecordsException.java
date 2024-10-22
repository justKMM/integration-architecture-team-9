package de.hbrs.ia.exceptions;

public class SalesManHasPerformanceRecordsException extends RuntimeException {

    public SalesManHasPerformanceRecordsException(int sid) {
        super(
            String.format(
                "Es existieren noch SocialPerformanceRecords zu dem SalesMan mit der sid \"%s\".",
                sid
            )
        );
    }

}
