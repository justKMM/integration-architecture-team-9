package de.hbrs.ia;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import de.hbrs.ia.code.PersonalManager;
import de.hbrs.ia.exceptions.DuplicatePerformanceRecordExcpetion;
import de.hbrs.ia.exceptions.DuplicateSidException;
import de.hbrs.ia.exceptions.SidNotFoundException;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

class PersonalManagerTest {

    private MongoDatabase testDatabase;
    private PersonalManager personalManager;

    @BeforeEach
    public void setUp() {
        // Set up MongoDB connection and create a test database
        this.testDatabase = MongoClients.create("mongodb://localhost:27017/").getDatabase("test");
        this.personalManager = new PersonalManager(this.testDatabase);
    }

    @AfterEach
    public void tearDown() {
        // Clean up test database after each test
        this.testDatabase.drop();
    }

    @Test
    public void testCreateAndReadSalesMan() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);

        personalManager.createSalesMan(salesMan);

        SalesMan retrievedSalesMan = personalManager.readSalesMan(1);

        assertNotNull(retrievedSalesMan);
        assertAll(
            () -> assertEquals(salesMan.getId(), retrievedSalesMan.getId()),
            () -> assertEquals(salesMan.getFirstname(), retrievedSalesMan.getFirstname()),
            () -> assertEquals(salesMan.getLastname(), retrievedSalesMan.getLastname())
        );
    }

    @Test
    public void testCreateDuplicateSalesManThrowsException() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);
        personalManager.createSalesMan(salesMan);

        assertThrows(
            DuplicateSidException.class,
            () -> personalManager.createSalesMan(new SalesMan("Jane", "Doe", 1))
        );
    }

    @Test
    public void testAddAndReadSocialPerformanceRecord() {
        SocialPerformanceRecord record = new SocialPerformanceRecord(1, "Description", 4, 3, 2023);
        SalesMan salesMan = new SalesMan("", "", 1);

        personalManager.addSocialPerformanceRecord(record, salesMan);

        List<SocialPerformanceRecord> retrievedRecords = personalManager.readSocialPerformanceRecord(salesMan);

        assertNotNull(retrievedRecords);
        assertAll(
            () -> assertEquals(1, retrievedRecords.size()),
            () -> assertEquals(record.getGoalid(), retrievedRecords.get(0).getGoalid()),
            () -> assertEquals(record.getDescription(), retrievedRecords.get(0).getDescription()),
            () -> assertEquals(record.getTargetValue(), retrievedRecords.get(0).getTargetValue()),
            () -> assertEquals(record.getActualValue(), retrievedRecords.get(0).getActualValue()),
            () -> assertEquals(record.getYear(), retrievedRecords.get(0).getYear())
        );
    }

    @Test
    public void testAddAndReadSocialPerformanceRecordByYear() {
        SocialPerformanceRecord r1 = new SocialPerformanceRecord(1, "Description1", 4, 3, 2023);
        SocialPerformanceRecord r2 = new SocialPerformanceRecord(2, "Description2", 4, 4, 2024);
        SalesMan salesMan = new SalesMan("", "", 0);

        personalManager.addSocialPerformanceRecord(r1, salesMan);
        personalManager.addSocialPerformanceRecord(r2, salesMan);

        List<SocialPerformanceRecord> retrievedRecords = personalManager.readSocialPerformanceRecord(salesMan, 2023);

        assertNotNull(retrievedRecords);
        assertAll(
            () -> assertEquals(1, retrievedRecords.size()),
            () -> assertEquals(r1.getGoalid(), retrievedRecords.get(0).getGoalid()),
            () -> assertEquals(r1.getDescription(), retrievedRecords.get(0).getDescription()),
            () -> assertEquals(r1.getTargetValue(), retrievedRecords.get(0).getTargetValue()),
            () -> assertEquals(r1.getActualValue(), retrievedRecords.get(0).getActualValue()),
            () -> assertEquals(r1.getYear(), retrievedRecords.get(0).getYear())
        );
    }

    @Test
    public void testAddDuplicatePerformanceRecordThrowsException() {
        SalesMan salesMan = new SalesMan("", "", 3);

        SocialPerformanceRecord r1 = new SocialPerformanceRecord(2, "Description", 4, 4, 2024);
        SocialPerformanceRecord r2 = new SocialPerformanceRecord(2, "Description", 4, 3, 2024);

        personalManager.addSocialPerformanceRecord(r1, salesMan);

        assertThrows(
            DuplicatePerformanceRecordExcpetion.class,
            () -> personalManager.addSocialPerformanceRecord(r2, salesMan)
        );
    }

    @Test
    public void testReadSalesManNotFoundThrowsException() {
        assertThrows(
            SidNotFoundException.class,
            () -> personalManager.readSalesMan(999)
        );
    }

    @Test
    public void testDeleteSalesMan() {
        SalesMan s1 = new SalesMan("", "", 2);

        personalManager.createSalesMan(s1);

        personalManager.deleteSalesMan(s1.getId());

        assertThrows(
            SidNotFoundException.class,
            () -> personalManager.readSalesMan(s1.getId())
        );
    }

    @Test
    public void testDeleteSocialPerformanceRecord() {
        SalesMan salesMan = new SalesMan("", "", 1);

        SocialPerformanceRecord r1 = new SocialPerformanceRecord(1, "Description1", 5, 4, 2023);
        SocialPerformanceRecord r2 = new SocialPerformanceRecord(2, "Description2", 4, 4, 2024);

        personalManager.addSocialPerformanceRecord(r1, salesMan);
        personalManager.addSocialPerformanceRecord(r2, salesMan);

        personalManager.deleteSocialPerformanceRecord(1, 2, 2024);

        List<SocialPerformanceRecord> records = personalManager.readSocialPerformanceRecord(salesMan);

        assertAll(
            () -> assertEquals(1, records.size()),
            () -> assertEquals(r1.getGoalid(), records.get(0).getGoalid())
        );
    }
}
