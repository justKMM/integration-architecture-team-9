package de.hbrs.ia;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.hbrs.ia.code.ManagePersonal;
import de.hbrs.ia.code.PersonalManager;
import de.hbrs.ia.model.SalesMan;
import de.hbrs.ia.model.SocialPerformanceRecord;

public class PersonalManagerTest {

    private AutoCloseable closeable;

    private ManagePersonal personalManager;

    @Mock
    private MongoDatabase mockDatabase;

    @Mock
    private MongoCollection<Document> mockSalesmanCollection;

    @Mock
    private MongoCollection<Document> mockPerformanceCollection;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Set up expectations for collection retrieval
        when(mockDatabase.getCollection("salesmen")).thenReturn(mockSalesmanCollection);
        when(mockDatabase.getCollection("performancerecords")).thenReturn(mockPerformanceCollection);

        personalManager = new PersonalManager(mockDatabase);

        assertAll(
            () -> assertNotNull(mockDatabase),
            () -> assertNotNull(mockSalesmanCollection),
            () -> assertNotNull(mockPerformanceCollection),
            () -> assertNotNull(personalManager)
        );
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testCreateSalesman() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);
        personalManager.createSalesMan(salesMan);

        verify(mockSalesmanCollection).insertOne(any(Document.class));
    }

    @Test
    public void testReadSalesMan() {
        when(mockSalesmanCollection.find(any(Document.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.first()).thenReturn(new Document("sid", 1).append("firstname", "John").append("lastname", "Doe"));

        SalesMan salesMan = personalManager.readSalesMan(1);

        assertNotNull(salesMan);
        assertAll(
            () -> assertEquals(1, salesMan.getId()),
            () -> assertEquals("John", salesMan.getFirstname()),
            () -> assertEquals("Doe", salesMan.getLastname())
        );
    }

    @Test
    public void testAddSocialPerformanceRecord() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);
        SocialPerformanceRecord record = new SocialPerformanceRecord(1, "Description", 4, 3, 2024);

        personalManager.addSocialPerformanceRecord(record, salesMan);

        verify(mockPerformanceCollection).insertOne(any(Document.class));
    }

    @Test
    public void testReadSocialPerformanceRecord() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);

        Document recordDoc = new Document("sid", 1)
            .append("goalid", 1)
            .append("description", "Description")
            .append("targetValue", 4)
            .append("actualValue", 3)
            .append("year", 2024);

        when(mockPerformanceCollection.find(any(Document.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.into(any())).thenReturn(List.of(recordDoc));

        List<SocialPerformanceRecord> records = personalManager.readSocialPerformanceRecord(salesMan);

        assertNotNull(records);
        assertAll(
            () -> assertEquals(1, records.size()),
            () -> assertEquals(1, records.get(0).getGoalid()),
            () -> assertEquals("Description", records.get(0).getDescription()),
            () -> assertEquals(100, records.get(0).getTargetValue()),
            () -> assertEquals(90, records.get(0).getActualValue()),
            () -> assertEquals(2024, records.get(0).getYear())
        );
    }

    @Test
    public void testReadSocialPerformanceRecordByYear() {
        SalesMan salesMan = new SalesMan("John", "Doe", 1);

        Document recordDoc = new Document("sid", 1)
            .append("goalid", 1)
            .append("description", "Description")
            .append("targetValue", 4)
            .append("actualValue", 3)
            .append("year", 2024);

        when(mockPerformanceCollection.find(any(Document.class))).thenReturn(mockFindIterable);
        when(mockFindIterable.into(any())).thenReturn(Arrays.asList(recordDoc));

        List<SocialPerformanceRecord> records = personalManager.readSocialPerformanceRecord(salesMan, 2024);

        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(2024, records.get(0).getYear());
    }

}
