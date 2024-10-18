package de.hbrs.ia.code;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

import java.util.List;

public interface ManagePersonal {
    // C - Create
    public void createSalesMan( SalesMan salesMan );

    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord , SalesMan salesMan );

    // R - Read
    public SalesMan readSalesMan( int sid );

    public List<SalesMan> readAllSalesMen();

    public List<SocialPerformanceRecord> readSocialPerformanceRecord( SalesMan salesMan );

    public List<SocialPerformanceRecord> readSocialPerformanceRecord( SalesMan salesMan, int year );

    // U - Update
    public void updateSalesMan( int sid, SalesMan newSalesMan );

    public void updateSocialPerformanceRecord( SocialPerformanceRecord newPerformanceRecord, SalesMan SalesMan, int year );

    // D - Delete
    public void deleteSalesMan( int sid );

    public void deleteSocialPerformanceRecord( int sid, int year );
}
