package de.hbrs.ia;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.hbrs.ia.code.PersonalManager;
import de.hbrs.ia.exceptions.DuplicatePerformanceRecordExcpetion;
import de.hbrs.ia.exceptions.DuplicateSidException;
import de.hbrs.ia.exceptions.SidNotFoundException;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PersonalManagerJTest {

    private MongoDatabase testDatabase;
    private PersonalManager personalManager;

    @BeforeEach
    void setUp() {
        // Set up MongoDB connection and create a test database
        testDatabase = MongoClients.create("mongodb://localhost:27017/").getDatabase("test");
        personalManager = new PersonalManager(testDatabase);
    }

    @AfterEach
    void tearDown() {
        // Clean up test database after each test
        testDatabase.drop();
    }

    @Test
    void testCreateAndReadSalesMan() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);

        personalManager.createSalesMan(salesMan);
        SalesMan retrievedSalesMan = personalManager.readSalesMan(1);

        assertEquals(salesMan.getId(), retrievedSalesMan.getId());
        assertEquals(salesMan.getFirstname(), retrievedSalesMan.getFirstname());
        assertEquals(salesMan.getLastname(), retrievedSalesMan.getLastname());
    }

    @Test
    void testCreateDuplicateSalesManThrowsException() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);
        personalManager.createSalesMan(salesMan);

        assertThrows(DuplicateSidException.class, () -> {
            personalManager.createSalesMan(new SalesMan("Jane", "Doe", 1)); // Same SID
        });
    }

    @Test
    void testAddSocialPerformanceRecord() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);
        personalManager.createSalesMan(salesMan);

        SocialPerformanceRecord record = new SocialPerformanceRecord(1, "Goal Description", 4, 4, 2024);

        personalManager.addSocialPerformanceRecord(record, salesMan);

        assertEquals(1, personalManager.readSocialPerformanceRecord(salesMan).size());
    }

    @Test
    void testAddDuplicatePerformanceRecordThrowsException() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);
        personalManager.createSalesMan(salesMan);

        SocialPerformanceRecord record = new SocialPerformanceRecord(2, "Goal Description", 4, 4, 2024);
        SocialPerformanceRecord duplicateRecord = new SocialPerformanceRecord(2, "Goal Description", 4, 3, 2024);
        personalManager.addSocialPerformanceRecord(record, salesMan);

        assertThrows(DuplicatePerformanceRecordExcpetion.class, () -> {
            personalManager.addSocialPerformanceRecord(duplicateRecord, salesMan); // Same record
        });
    }

    @Test
    void testReadSalesManNotFoundThrowsException() {
        assertThrows(SidNotFoundException.class, () -> {
            personalManager.readSalesMan(999); // Non-existent SID
        });
    }
}
