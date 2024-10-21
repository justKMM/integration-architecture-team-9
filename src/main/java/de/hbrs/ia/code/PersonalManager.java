package de.hbrs.ia.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

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
        if (!collectionExists(database, "salesmen")) {
            Document validator = new Document("$jsonSchema", new Document()
                    .append("bsonType", "object")
                    .append("required", List.of("sid", "firstname", "lastname"))
                    .append("properties", new Document()
                            .append("sid", new Document("bsonType", "int"))
                            .append("firstname", new Document("bsonType", "string"))
                            .append("lastname", new Document("bsonType", "string"))
                    ));
            database.createCollection("salesmen",
                    new CreateCollectionOptions().validationOptions(
                                    new ValidationOptions().validator(validator)
                    )
            );
        }
        this.salesmenCollection = database.getCollection("salesmen");

        if (!collectionExists(database, "performancerecords")) {

            Document validator = new Document("$jsonSchema", new Document("bsonType", "object")
                            .append("required", List.of("sid", "goalid", "description", "targetValue", "actualValue", "year"))
                            .append("properties", new Document("sid", new Document("bsonType", "int"))
                                    .append("goalid", new Document("bsonType", "int"))
                                    .append("description", new Document("bsonType", "string"))
                                    .append("targetValue", new Document("bsonType", "int"))
                                    .append("actualValue", new Document("bsonType", "int"))
                                    .append("year", new Document("bsonType", "int"))
                            ));
            database.createCollection("performancerecords",
                    new CreateCollectionOptions().validationOptions(
                            new ValidationOptions().validator(validator)
                    )
            );
        }

        this.performanceRecordsCollection = database.getCollection("performancerecords");

        // Set up indexes
        this.salesmenCollection.createIndex(
            Indexes.ascending("sid"),
            new IndexOptions().unique(true)
        );
        this.performanceRecordsCollection.createIndex(
            Indexes.compoundIndex(
                Indexes.ascending("sid"),
                Indexes.ascending("goalid"),
                Indexes.ascending("year")
            ),
            new IndexOptions().unique(true)
        );
    }

    private boolean collectionExists(MongoDatabase database, String collectionName) {
        for (String name : database.listCollectionNames()) {
            System.out.println("Collection Name: " + name);
            if (name.equalsIgnoreCase(collectionName))
                return true;
        }
        return false;
        // return database.listCollectionNames()
        //     .into(new ArrayList<>())
        //     .stream()
        //     .anyMatch(name -> name.equalsIgnoreCase(collectionName));
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
            .find(eq("sid", salesMan.getId()))
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
