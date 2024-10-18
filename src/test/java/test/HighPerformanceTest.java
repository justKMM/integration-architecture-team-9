package test;

import de.hbrs.ia.code.ManagePersonal;
import de.hbrs.ia.code.PersonalManager;
import de.hbrs.ia.model.SalesMan;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighPerformanceTest {

    private ManagePersonal managePersonal;
    private SalesMan salesManAlda;
    private SalesMan salesManLuca;
    /**
     * Attention: You might update the version of the Driver
     * for newer version of MongoDB!
     * This tests run with MongoDB 4.2.17 Community
     */
    @BeforeEach
    void setUp() {
        // Setting up the connection to a local MongoDB with standard port 27017
        // must be started within a terminal with command 'mongod'.
        this.managePersonal = new PersonalManager();
        this.salesManAlda = new SalesMan("Sascha", "Alda", 90133);
        this.salesManLuca = new SalesMan("Luca", "Ringhausen", 90134);
    }

    @AfterEach
    void tearDown() {
        this.managePersonal.deleteAllSalesMen();
        this.managePersonal.deleteAllSocialPerformanceRecord();
    }

    // Test CREATE function
    @Test
    void insertSalesManTest() {
        // ... now storing the object
        this.managePersonal.createSalesMan(salesManAlda);

        // Query the added salesman - and assertEquals
        assertEquals( salesManAlda.getId(), managePersonal.readSalesMan(salesManAlda.getId()).getId()  );
    }

    // ?
    @Test
    void insertSalesManMoreObjectOrientedTest() {
        // CREATE (Storing) the salesman business object
        // Using setter instead
        SalesMan salesMan = new SalesMan( "Leslie" , "Malton" , 90444 );

        // ... now storing the object
        this.managePersonal.createSalesMan(salesMan);

        // READ (Finding) the stored Documnent
        // Mapping Document to business object would be fine...
        Document newDocument = this.managePersonal.readSalesMan(salesMan.getId()).toDocument();
        System.out.println("Printing the object (JSON): " + newDocument );

        // Assertion
        Integer sid = (Integer) newDocument.get("sid");
        assertEquals( 90444 , sid );
    }

    // Test READ (all) function
    @Test
    void readSalesManTest() {
        // List of all Salesmen
        List<SalesMan> salesManList = new ArrayList<>();
        salesManList.add(salesManAlda);
        salesManList.add(salesManLuca);

        // Adding the salesmen to the database
        this.managePersonal.createSalesMan(salesManAlda);
        this.managePersonal.createSalesMan(salesManLuca);

        List<SalesMan> salesManQueried = this.managePersonal.readAllSalesMen();
        for (int i = 0; i < salesManQueried.size(); i++) {
            assertEquals( salesManList.get(i).getId(), salesManQueried.get(i).getId() );
        }
    }

    // Test UPDATE functions
    @Test
    void updateSalesManTest() {
        this.managePersonal.createSalesMan(salesManAlda);
        this.managePersonal.updateSalesMan(salesManAlda.getId(), salesManLuca);

        assertEquals( salesManLuca.getId(), this.managePersonal.readAllSalesMen().get(0).getId() );
    }
}