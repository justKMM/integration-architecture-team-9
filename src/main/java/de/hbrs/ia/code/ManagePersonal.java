package de.hbrs.ia.code;
import java.util.List;

import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public interface ManagePersonal {
    // C - Create
    public void createSalesMan(SalesMan salesMan);

    // R - Read
    public SalesMan readSalesMan(int sid);

    public List<SalesMan> readAllSalesMen();

    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan);

    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan, int year);

    // U - Update
    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord , SalesMan salesMan);

    // D - Delete
    public void deleteSalesMan(int sid);

    public void deleteSocialPerformanceRecord(int sid, int goalid, int year);
}
