package de.hbrs.ia.model;

import static de.hbrs.ia.util.Constants.FIRST_NAME;
import static de.hbrs.ia.util.Constants.LAST_NAME;
import static de.hbrs.ia.util.Constants.SID;

import org.bson.Document;

public class SalesMan {
    private String firstname;
    private String lastname;
    private Integer sid;

    public SalesMan(String firstname, String lastname, Integer sid) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.sid = sid;
    }

    public SalesMan(Document document) {
        this.firstname = document.getString(FIRST_NAME);
        this.lastname = document.getString(LAST_NAME);
        this.sid = document.getInteger(SID);
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getId() {
        return this.sid;
    }

    public void setId(Integer sid) {
        this.sid = sid;
    }

    public Document toDocument() {
        return new Document()
                .append(SID, this.sid)
                .append(FIRST_NAME, this.firstname)
                .append(LAST_NAME, this.lastname);
    }

    @Override
    public String toString() {
        return String.format(
            "SalesMan [firstname=%s, lastname=%s, sid=%s]",
            this.firstname,
            this.lastname,
            this.sid);
    }

}
