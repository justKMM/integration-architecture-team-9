package de.hbrs.ia.code;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ManagePersonalImpl implements ManagePersonal {

    private MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> salesmenCollection;
    private final MongoCollection<Document> socialPerformanceRecordsCollection;

    public ManagePersonalImpl() {
        // Connect to the local MongoDB instance
        this.mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("highperformance");
        this.salesmenCollection = this.database.getCollection("salesmen");
        this.socialPerformanceRecordsCollection = this.database.getCollection("socialPerformanceRecords");
    }

    @Override
    public void createSalesMan(SalesMan salesMan) {
        salesmenCollection.insertOne(salesMan.toDocument());
    }

    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord, SalesMan salesMan) {
        Document performanceRecordDocument = performanceRecord.toDocument();
        performanceRecordDocument.append("sid", salesMan.getId());
        socialPerformanceRecordsCollection.insertOne(performanceRecordDocument);
        System.out.println("Social Performance Record added for: " + salesMan.getFirstname());
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        Document salesMan = salesmenCollection.find(eq("sid", sid)).first();
        if (salesMan != null) {
            return new SalesMan(salesMan.getString("firstname"), salesMan.getString("lastname"), salesMan.getInteger("sid"));
        }
        return null;
    }

    @Override
    public List<SalesMan> readAllSalesMen() {
        List<SalesMan> salesMen = new ArrayList<>();
        for (Document salesManDocument : salesmenCollection.find()) {
            SalesMan salesMan = new SalesMan(salesManDocument.getString("firstname"), salesManDocument.getString("lastname"), salesManDocument.getInteger("sid"));
            salesMen.add(salesMan);
        }
        return salesMen;
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
        List<SocialPerformanceRecord> records = new ArrayList<>();
        for (Document socialPerformanceRecordDocument : socialPerformanceRecordsCollection.find(eq("salesManId", salesMan.getId()))) {
            SocialPerformanceRecord record = new SocialPerformanceRecord(socialPerformanceRecordDocument.getString("feedback"), socialPerformanceRecordDocument.getInteger("year"));
            records.add(record);
        }
        return records;
    }

    @Override
    public SocialPerformanceRecord readSocialPerformanceRecord(SalesMan salesMan, int year) {
        for (Document socialPerformanceRecordDocument : socialPerformanceRecordsCollection.find(eq("salesManId", salesMan.getId()))) {
            if (socialPerformanceRecordDocument.getInteger("year") == year) {
                return new SocialPerformanceRecord(socialPerformanceRecordDocument.getString("feedback"), socialPerformanceRecordDocument.getInteger("year"));
            }
        }
        return null;
    }

    @Override
    public void updateSalesMan(int sid, SalesMan newSalesMan) {
        Document salesManDocument = salesmenCollection.find(eq("sid", sid)).first();
        if (salesManDocument != null) {
            salesmenCollection.replaceOne(salesManDocument, newSalesMan.toDocument());
        }
        System.out.println("Can't find salesman in database");
    }

    @Override
    public void updateSocialPerformanceRecord(SocialPerformanceRecord newPerformanceRecord, SalesMan salesMan, int year) {
        Document currentSocialPerformanceRecordDocument;
        Document filterCriteria = new Document();
        filterCriteria.put("sid", salesMan.getId());
        filterCriteria.append("year", year);
        currentSocialPerformanceRecordDocument = socialPerformanceRecordsCollection.find(filterCriteria).first();
        if (currentSocialPerformanceRecordDocument == null) {
            socialPerformanceRecordsCollection.insertOne(newPerformanceRecord.toDocument());
            return;
        }
        socialPerformanceRecordsCollection.updateOne(currentSocialPerformanceRecordDocument, newPerformanceRecord.toDocument());
    }

    @Override
    public void deleteSalesMan(int sid) {
        Document currentSalesManDocument = salesmenCollection.find(eq("sid", sid)).first();
        salesmenCollection.deleteOne(currentSalesManDocument);
    }

    @Override
    public void deleteSocialPerformanceRecord(int sid, int year) {
        Document filterCriteria = new Document();
        filterCriteria.put("sid", sid);
        filterCriteria.append("year", year);
        Document currentSocialPerformanceRecordDocument = socialPerformanceRecordsCollection.find(filterCriteria).first();
    }

    @Override
    public void deleteAllSalesMen() {
        salesmenCollection.drop();
    }

    @Override
    public void deleteAllSocialPerformanceRecord() {
        socialPerformanceRecordsCollection.drop();
    }
}
