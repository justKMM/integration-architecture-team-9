package de.hbrs.ia.model;

import org.bson.Document;

public class SocialPerformanceRecord {
    private String feedback;
    private Integer year;

    public SocialPerformanceRecord(String feedback, Integer year) {
        this.feedback = feedback;
        this.year = year;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("feedback", this.feedback);
        document.append("year", this.year);
        return document;
    }
}
