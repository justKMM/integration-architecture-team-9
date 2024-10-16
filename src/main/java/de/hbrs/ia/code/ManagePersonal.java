package de.hbrs.ia.code;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

import java.util.List;

/**
 * Code lines are commented for suppressing compile errors.
 * Are there any CRUD-operations missing?
 */
public interface ManagePersonal {
    public void createSalesMan( SalesMan salesMan );

    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord , SalesMan salesMan );
    // Remark: an SocialPerformanceRecord corresponds to part B of a bonus sheet

    public SalesMan readSalesMan( int sid );

    public List<SalesMan> readAllSalesMen();

    public List<SocialPerformanceRecord> readSocialPerformanceRecord( SalesMan salesMan );
    // Remark: How do you integrate the year?
    // The year will be an attribute of the social performance records

    public SocialPerformanceRecord readSocialPerformanceRecord( SalesMan salesMan, int year );

    // U - Update
    public void updateSalesMan( int sid, SalesMan newSalesMan );

    public void updateSocialPerformanceRecord( SocialPerformanceRecord newPerformanceRecord, SalesMan SalesMan, int year );

    // D - Delete
    public void deleteSalesMan( int sid );

    public void deleteSocialPerformanceRecord( int sid, int year );
}
