package de.hbrs.ia.code;

import java.util.List;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class Main {
    public static void main(String[] args) {
        // Connect to the local MongoDB instance
        MongoDatabase database = MongoClients.create("mongodb://localhost:27017/").getDatabase("highperformance");

        // Instantiate the control class
        PersonalManager managePersonal = new PersonalManager(database);

        // Create a new Salesman
        SalesMan salesMan1 = new SalesMan("John", "Doe", 101);
        managePersonal.createSalesMan(salesMan1);

        SalesMan salesMan2 = new SalesMan("Jane", "Smith", 102);
        managePersonal.createSalesMan(salesMan2);

        // Add social performance records for salesMan1
        SocialPerformanceRecord record1 = new SocialPerformanceRecord(1, "Leadership Competence", 4, 3, 2023);
        managePersonal.addSocialPerformanceRecord(record1, salesMan1);

        SocialPerformanceRecord record2 = new SocialPerformanceRecord(2, "Openness Employee", 4, 2, 2023);
        managePersonal.addSocialPerformanceRecord(record2, salesMan1);

        SocialPerformanceRecord record3 = new SocialPerformanceRecord(3, "Leadership Competence", 4, 3, 2024);
        managePersonal.addSocialPerformanceRecord(record3, salesMan1);

        SocialPerformanceRecord record4 = new SocialPerformanceRecord(4, "Openness Employee", 4, 2, 2024);
        managePersonal.addSocialPerformanceRecord(record4, salesMan1);
        /*
        // Add social performance record for salesMan2
        SocialPerformanceRecord record3 = new SocialPerformanceRecord("Strong leadership", 2023);
        managePersonal.addSocialPerformanceRecord(record3, salesMan2);

        // Read a single Salesman by ID
        SalesMan readSalesMan = managePersonal.readSalesMan(101);
        System.out.println("Salesman read: " + readSalesMan.getFirstname() + " " + readSalesMan.getLastname());
        */

        // Read all Salesmen
        List<SalesMan> salesmen = managePersonal.readAllSalesMen();
        System.out.println("\nAll Salesmen:");
        for (SalesMan sm : salesmen) {
            System.out.println(sm.getFirstname() + " " + sm.getLastname());
        }

        // Read all Social Performance Records for salesMan1
        List<SocialPerformanceRecord> socialRecords = managePersonal.readSocialPerformanceRecord(salesMan1);
        System.out.println("\nSocial Performance Records for Salesman ID 101:");
        for (SocialPerformanceRecord sr : socialRecords) {
            System.out.println(sr.getYear() + ": " + sr.getDescription() + " Target Value: " + sr.getTargetValue() + " Actual Value: " + sr.getActualValue());
        }

        // Read all Social Performance Records for salesMan1 for Year 2024
        List<SocialPerformanceRecord> socialRecords2024 = managePersonal.readSocialPerformanceRecord(salesMan1, 2024);
        System.out.println("\nSocial Performance Records for Salesman ID 101:");
        for (SocialPerformanceRecord sr : socialRecords2024) {
            System.out.println(sr.getYear() + ": " + sr.getDescription() + " Target Value: " + sr.getTargetValue() + " Actual Value: " + sr.getActualValue());
        }

    }

}
