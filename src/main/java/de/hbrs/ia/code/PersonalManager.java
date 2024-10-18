package de.hbrs.ia.code;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Updates;

import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class PersonalManager implements ManagePersonal {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> salesmenCollection;

    public PersonalManager() {
        // Connect to the local MongoDB instance
        this.mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("highperformance");
        this.salesmenCollection = this.database.getCollection("salesmen");
        //Create Index for Salesman sid
        salesmenCollection.createIndex(new Document("sid", 1), new IndexOptions().unique(true));
    }

    @Override
    public void createSalesMan(SalesMan salesMan) {
        try {
            salesmenCollection.insertOne(salesMan.toDocument());
        } catch (MongoWriteException exception) {
            throw new IllegalArgumentException("Ein SalesMan mit der SID " + salesMan.getId() + " existiert bereits.");
        }
    }

    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord, SalesMan salesMan) {
        Document existingRecord = salesmenCollection.find(
                and(
                eq("sid", salesMan.getId()),
                eq("performancerecords.goalid", performanceRecord.getGoalid()))
        ).first();

        if (existingRecord != null)
            throw new IllegalArgumentException("Ein PerformanceRecord mit der GoalID " + performanceRecord.getGoalid() + " existiert bereits für den SalesMan mit der SID " + salesMan.getId() + ".");

        Document salesManDocument = salesmenCollection.find(eq("sid", salesMan.getId())).first();
        if (salesManDocument == null)
            throw new IllegalArgumentException("Ein SalesMan mit der SID " + salesMan.getId() + " existiert nicht.");

        Bson update = Updates.addToSet("performancerecords", performanceRecord.toDocument());
        salesmenCollection.updateOne(salesManDocument, update);
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        Document salesMan = salesmenCollection.find(eq("sid", sid)).first();

        if (salesMan == null)
            throw new IllegalArgumentException("Ein SalesMan mit der SID " + sid + " existiert nicht.");

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
        Document salesManDocument = salesmenCollection.find(eq("sid", salesMan.getId())).first();

        if (salesManDocument == null) {
            throw new IllegalArgumentException("SalesMan mit SID " + salesMan.getId() + " nicht gefunden.");
        }

        List<Document> performanceRecords = (List<Document>) salesManDocument.get("performancerecords");

        List<SocialPerformanceRecord> records = new ArrayList<>();
        for (Document document : performanceRecords) {
            SocialPerformanceRecord record = new SocialPerformanceRecord(
                    document.getInteger("goaldid"),
                    document.getString("description"),
                    document.getInteger("targetValue"),
                    document.getInteger("actualValue"),
                    document.getInteger("year")
            );
            records.add(record);
        }
        return records;
    }


    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan, int year) {
        Document salesManDocument = salesmenCollection.find(eq("sid", salesMan.getId())).first();

        if (salesManDocument == null) {
            throw new IllegalArgumentException("SalesMan mit SID " + salesMan.getId() + " nicht gefunden.");
        }

        List<Document> performanceRecords = (List<Document>) salesManDocument.get("performancerecords");

        List<SocialPerformanceRecord> records = new ArrayList<>();
        for (Document document : performanceRecords) {
            if(document.getInteger("year") == year) {
                SocialPerformanceRecord record = new SocialPerformanceRecord(
                        document.getInteger("goaldid"),
                        document.getString("description"),
                        document.getInteger("targetValue"),
                        document.getInteger("actualValue"),
                        document.getInteger("year")
                );
                records.add(record);
            }
        }
        return records;
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
