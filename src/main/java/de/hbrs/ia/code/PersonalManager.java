package de.hbrs.ia.code;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static de.hbrs.ia.util.Constants.ACTUAL_VALUE;
import static de.hbrs.ia.util.Constants.DESCRIPTION;
import static de.hbrs.ia.util.Constants.FIRST_NAME;
import static de.hbrs.ia.util.Constants.GOAL_ID;
import static de.hbrs.ia.util.Constants.LAST_NAME;
import static de.hbrs.ia.util.Constants.PERFORMANCE_COLLECTION;
import static de.hbrs.ia.util.Constants.SALESMEN_COLLECTION;
import static de.hbrs.ia.util.Constants.SID;
import static de.hbrs.ia.util.Constants.TARGET_VALUE;
import static de.hbrs.ia.util.Constants.YEAR;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;

import de.hbrs.ia.exceptions.DuplicatePerformanceRecordExcpetion;
import de.hbrs.ia.exceptions.DuplicateSidException;
import de.hbrs.ia.exceptions.SidNotFoundException;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class PersonalManager implements ManagePersonal {

    private final MongoCollection<Document> salesmenCollection;
    private final MongoCollection<Document> performanceRecordsCollection;

    public PersonalManager(MongoDatabase database) {
        // Get the Collections
        if (!collectionExists(database, SALESMEN_COLLECTION)) {
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", List.of(SID, FIRST_NAME, LAST_NAME))
                    .append("properties", new Document()
                            .append(SID, new Document("bsonType", "int").append("minimum", 0))
                            .append(FIRST_NAME, new Document("bsonType", "string"))
                            .append(LAST_NAME, new Document("bsonType", "string"))
                    ));
            database.createCollection(SALESMEN_COLLECTION,
                    new CreateCollectionOptions().validationOptions(
                                    new ValidationOptions().validator(validator)
                    )
            );
        }
        this.salesmenCollection = database.getCollection(SALESMEN_COLLECTION);

        if (!collectionExists(database, PERFORMANCE_COLLECTION)) {

            Document validator = new Document("$jsonSchema", new Document("bsonType", "object")
                            .append("required", List.of(SID, GOAL_ID, DESCRIPTION, TARGET_VALUE, ACTUAL_VALUE, YEAR))
                            .append("properties", new Document(SID, new Document("bsonType", "int").append("minimum", 0))
                                    .append(GOAL_ID, new Document("bsonType", "int").append("minimum", 0))
                                    .append(DESCRIPTION, new Document("bsonType", "string"))
                                    .append(TARGET_VALUE, new Document("bsonType", "int"))
                                    .append(ACTUAL_VALUE, new Document("bsonType", "int"))
                                    .append(YEAR, new Document("bsonType", "int"))
                            ));
            database.createCollection(PERFORMANCE_COLLECTION,
                    new CreateCollectionOptions().validationOptions(
                            new ValidationOptions().validator(validator)
                    )
            );
        }

        this.performanceRecordsCollection = database.getCollection(PERFORMANCE_COLLECTION);

        // Set up indexes
        this.salesmenCollection.createIndex(
            Indexes.ascending(SID),
            new IndexOptions().unique(true)
        );
        this.performanceRecordsCollection.createIndex(
            Indexes.compoundIndex(
                Indexes.ascending(SID),
                Indexes.ascending(GOAL_ID),
                Indexes.ascending(YEAR)
            ),
            new IndexOptions().unique(true)
        );
    }

    private boolean collectionExists(MongoDatabase database, String collectionName) {
        return database.listCollectionNames()
            .into(new ArrayList<>())
            .stream()
            .anyMatch(name -> name.equalsIgnoreCase(collectionName));
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
                    .append(SID, salesMan.getId())
            );
        } catch (MongoWriteException mwe) {
            throw new DuplicatePerformanceRecordExcpetion(performanceRecord, salesMan);
        }
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        return Optional.ofNullable(
            this.salesmenCollection
                .find(eq(SID, sid))
                .first()
            )
            .map(SalesMan::new)
            .orElseThrow(() -> new SidNotFoundException(sid));
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
            .find(eq(SID, salesMan.getId()))
            .map(SocialPerformanceRecord::new)
            .into(new ArrayList<>());
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan, int year) {
        return this.readSocialPerformanceRecord(salesMan)
            .stream()
            .filter(r -> r.getYear() == year)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteSalesMan(int sid) {
        this.salesmenCollection.deleteOne(
                eq(SID, sid)
        );
    }

    @Override
    public void deleteSocialPerformanceRecord(int sid, int goalid, int year) {
        this.performanceRecordsCollection.deleteOne(
                and(
                    eq(SID, sid),
                    eq(GOAL_ID, goalid),
                    eq(YEAR, year)
                )
        );
    }

}
