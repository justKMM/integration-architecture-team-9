package de.hbrs.ia.code;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import de.hbrs.ia.exceptions.DuplicatePerformanceRecordExcpetion;
import de.hbrs.ia.exceptions.DuplicateSidException;
import de.hbrs.ia.exceptions.SidNotFoundException;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class PersonalManager implements ManagePersonal {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> salesmenCollection;
    private final MongoCollection<Document> performanceRecordsCollection;

    public PersonalManager() {
        // Connect to the local MongoDB instance
        this.mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("highperformance");

        this.salesmenCollection = this.database.getCollection("salesmen");
        this.performanceRecordsCollection = this.database.getCollection("performancerecords");

        // Create Index for Salesman sid and PerformanceRecord sid+goalid
        this.salesmenCollection.createIndex(new Document("sid", 1), new IndexOptions().unique(true));
        this.performanceRecordsCollection.createIndex(new Document("sid", 1).append("goalid", 1).append("year", 1), new IndexOptions().unique(true));
    }

    @Override
    public void createSalesMan(SalesMan salesMan) {
        try {
            this.salesmenCollection.insertOne(salesMan.toDocument());
        } catch (MongoWriteException mwe) {
            throw new DuplicateSidException(salesMan);
        }
    }

    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord, SalesMan salesMan) {
        try {
            this.performanceRecordsCollection.insertOne(
                performanceRecord.toDocument()
                    .append("sid", salesMan.getId())
            );
        } catch (MongoWriteException mwe) {
            throw new DuplicatePerformanceRecordExcpetion(performanceRecord, salesMan);
        }
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        return Optional.ofNullable(
            this.salesmenCollection
                .find(eq("sid", sid))
                .map(SalesMan::new)
                .first()
        ).orElseThrow(() -> new SidNotFoundException(sid));
    }

    @Override
    public List<SalesMan> readAllSalesMen() {
        return this.salesmenCollection
                .find()
                .map(SalesMan::new)
                .into(new ArrayList<>());
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
        return this.performanceRecordsCollection
            .find(eq("sid", salesMan.getId()))
            .map(SocialPerformanceRecord::new)
            .into(new ArrayList<>());
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan, int year) {
        return this.readSocialPerformanceRecord(salesMan)
            .stream()
            .filter(r -> r.getYear() == year)
            .toList();
    }

    @Override
    public void deleteSalesMan(int sid) {
        this.salesmenCollection.deleteOne(
                eq("sid", sid)
        );
    }

    @Override
    public void deleteSocialPerformanceRecord(int sid, int goalid, int year) {
        this.performanceRecordsCollection.deleteOne(
                and(
                    eq("sid", sid),
                    eq("goalid", goalid),
                    eq("year", year)
                )
        );
    }

}
