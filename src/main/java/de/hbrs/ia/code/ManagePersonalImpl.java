package de.hbrs.ia.code;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Updates;

import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class ManagePersonalImpl implements ManagePersonal {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> salesmenCollection;

    public ManagePersonalImpl() {
        // Connect to the local MongoDB instance
        this.mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("highperformance");
        this.salesmenCollection = this.database.getCollection("salesmen");
    }

    @Override
    public void createSalesMan(SalesMan salesMan) {
        salesmenCollection.insertOne(salesMan.toDocument());
    }

    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord, SalesMan salesMan) {
        Document salesManDocument = salesmenCollection.find(eq("sid", salesMan.getId())).first();

        if (salesManDocument == null)
            throw new IllegalArgumentException();

        Bson update = Updates.combine(
                Updates.addToSet("performancerecords", performanceRecord.toDocument())
        );
        salesmenCollection.updateOne(salesManDocument, update);
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        Document salesMan = salesmenCollection.find(eq("sid", sid)).first();

        if (salesMan == null)
            return null;

        return new SalesMan(
            salesMan.getString("firstname"),
            salesMan.getString("lastname"),
            salesMan.getInteger("sid")
        );
    }

    @Override
    public List<SalesMan> readAllSalesMen() {
        return salesmenCollection
            .find()
            .map(
                d -> new SalesMan(
                    d.getString("firstname"),
                    d.getString("lastname"),
                    d.getInteger("sid")
                )
            )
            .into(new ArrayList<>());
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
        return salesmenCollection
            .find(eq("sid", salesMan.getId()))
            .map(d -> d.get("performancerecords"))
            .map(
                d -> new SocialPerformanceRecord(
                    d.getInteger("goaldid"),
                    d.getString("description"),
                    d.getInteger("targetValue"),
                    d.getInteger("actualValue"),
                    d.getInteger("year")
                )
            )
            .into(new ArrayList<>());
    }

    @Override
    public SocialPerformanceRecord readSocialPerformanceRecord(SalesMan salesMan, int year) {
        return readSocialPerformanceRecord(salesMan)
            .stream()
            .filter(r -> r.getYear() == year)
            .findFirst()
            .get();
    }

    @Override
    public void updateSalesMan(int sid, SalesMan newSalesMan) {
        Document salesManDocument = salesmenCollection.find(eq("sid", sid)).first();
        if (salesManDocument != null) {
            salesmenCollection.replaceOne(salesManDocument, newSalesMan.toDocument());
        }
    }

    @Override
    public void updateSocialPerformanceRecord(SocialPerformanceRecord newPerformanceRecord, SalesMan salesMan, int year) {
        /*Document currentSocialPerformanceRecordDocument;
        Document filterCriteria = new Document();
        filterCriteria.put("sid", salesMan.getId());
        filterCriteria.append("year", year);
        currentSocialPerformanceRecordDocument = socialPerformanceRecordsCollection.find(filterCriteria).first();
        if (currentSocialPerformanceRecordDocument == null) {
            socialPerformanceRecordsCollection.insertOne(newPerformanceRecord.toDocument());
            return;
        }
        socialPerformanceRecordsCollection.updateOne(currentSocialPerformanceRecordDocument, newPerformanceRecord.toDocument());
        */
    }


    @Override
    public void deleteSalesMan(int sid) {
        Document currentSalesManDocument = salesmenCollection.find(eq("sid", sid)).first();
        salesmenCollection.deleteOne(currentSalesManDocument);
    }

    @Override
    public void deleteSocialPerformanceRecord(int sid, int year) {
        /*Document filterCriteria = new Document();
        filterCriteria.put("sid", sid);
        filterCriteria.append("year", year);
        Document currentSocialPerformanceRecordDocument = socialPerformanceRecordsCollection.find(filterCriteria).first();*/
    }
}
