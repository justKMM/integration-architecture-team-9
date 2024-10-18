package de.hbrs.ia.code;

import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class Main {
    public static void main(String[] args) {
        // Instantiate the control class
        ManagePersonalImpl managePersonal = new ManagePersonalImpl();

        // Create a new Salesman
        SalesMan salesMan1 = new SalesMan("John", "Doe", 101);
        managePersonal.createSalesMan(salesMan1);

        SalesMan salesMan2 = new SalesMan("Jane", "Smith", 102);
        managePersonal.createSalesMan(salesMan2);

        // Add social performance records for salesMan1
        SocialPerformanceRecord record1 = new SocialPerformanceRecord(1, "Leadership Competence", 4, 3, 2024);
        managePersonal.addSocialPerformanceRecord(record1, salesMan1);

        SocialPerformanceRecord record2 = new SocialPerformanceRecord(2, "Openness Employee", 4, 2, 2024);
        managePersonal.addSocialPerformanceRecord(record2, salesMan1);

        managePersonal.readSocialPerformanceRecord(salesMan1).forEach(System.out::println);
        /*
        // Add social performance record for salesMan2
        SocialPerformanceRecord record3 = new SocialPerformanceRecord("Strong leadership", 2023);
        managePersonal.addSocialPerformanceRecord(record3, salesMan2);

        // Read a single Salesman by ID
        SalesMan readSalesMan = managePersonal.readSalesMan(101);
        System.out.println("Salesman read: " + readSalesMan.getFirstname() + " " + readSalesMan.getLastname());

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
            System.out.println(sr.getYear() + ": " + sr.getFeedback());
        }*/
    }
}
