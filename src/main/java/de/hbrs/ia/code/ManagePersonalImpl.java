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

    private MongoDatabase database;

    public ManagePersonalImpl() {
        // Connect to the local MongoDB instance
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("highperformance");
    }

    @Override
    public void createSalesMan(SalesMan salesMan) {
        MongoCollection<Document> collection = database.getCollection("salesmen");
        collection.insertOne(salesMan.toDocument());
    }

    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord performanceRecord, SalesMan salesMan) {
        MongoCollection<Document> collection = database.getCollection("socialPerformanceRecords");
        Document document = performanceRecord.toDocument();
        document.append("salesManId", salesMan.getId());
        collection.insertOne(document);
        System.out.println("Social Performance Record added for: " + salesMan.getFirstname());
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        MongoCollection<Document> collection = database.getCollection("salesmen");
        Document document = collection.find(eq("sid", sid)).first();

        if (document != null) {
            return new SalesMan(document.getString("firstname"), document.getString("lastname"), document.getInteger("sid"));
        }
        return null;
    }

    @Override
    public List<SalesMan> readAllSalesMen() {
        MongoCollection<Document> collection = database.getCollection("salesmen");
        List<SalesMan> salesMen = new ArrayList<>();
        for (Document doc : collection.find()) {
            SalesMan salesMan = new SalesMan(doc.getString("firstname"), doc.getString("lastname"), doc.getInteger("sid"));
            salesMen.add(salesMan);
        }
        return salesMen;
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
        MongoCollection<Document> collection = database.getCollection("socialPerformanceRecords");
        List<SocialPerformanceRecord> records = new ArrayList<>();
        for (Document doc : collection.find(eq("salesManId", salesMan.getId()))) {
            SocialPerformanceRecord record = new SocialPerformanceRecord(doc.getString("feedback"), doc.getInteger("year"));
            records.add(record);
        }
        return records;
    }
}
