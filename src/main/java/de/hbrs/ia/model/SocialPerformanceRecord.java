package de.hbrs.ia.model;

import org.bson.Document;

public class SocialPerformanceRecord {
    private Integer goalid;
    private String description;
    private Integer targetValue;
    private Integer actualValue;
    private Integer year;

    public SocialPerformanceRecord(Integer goalid, String descrption, Integer targetValue, Integer actualValue, Integer year) {
        this.goalid = goalid;
        this.description = descrption;
        this.targetValue = targetValue;
        this.actualValue = actualValue;
        this.year = year;
    }

    public Integer getGoalid() {
        return goalid;
    }

    public void setGoalid(Integer goalid) {
        this.goalid = goalid;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getActualValue() {
        return actualValue;
    }

    public void setActualValue(Integer actualValue) {
        this.actualValue = actualValue;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Document toDocument() {
        return new Document()
                .append("goalid", this.goalid)
                .append("description", this.description)
                .append("targetValue", this.targetValue)
                .append("actualValue", this.actualValue)
                .append("year", this.year);
    }
}
