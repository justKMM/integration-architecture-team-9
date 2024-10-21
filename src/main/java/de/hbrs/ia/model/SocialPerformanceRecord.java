package de.hbrs.ia.model;

import org.bson.Document;

import static de.hbrs.ia.constants.Constants.ACTUAL_VALUE;
import static de.hbrs.ia.constants.Constants.DESCRIPTION;
import static de.hbrs.ia.constants.Constants.GOAL_ID;
import static de.hbrs.ia.constants.Constants.TARGET_VALUE;
import static de.hbrs.ia.constants.Constants.YEAR;

public class SocialPerformanceRecord {
    private Integer goalid;
    private String description;
    private Integer targetValue;
    private Integer actualValue;
    private Integer year;

    public SocialPerformanceRecord(Integer goalid, String description, Integer targetValue, Integer actualValue, Integer year) {
        this.goalid = goalid;
        this.description = description;
        this.targetValue = targetValue;
        this.actualValue = actualValue;
        this.year = year;
    }

    public SocialPerformanceRecord(Document document) {
        this.goalid = document.getInteger(GOAL_ID);
        this.description = document.getString(DESCRIPTION);
        this.targetValue = document.getInteger(TARGET_VALUE);
        this.actualValue = document.getInteger(ACTUAL_VALUE);
        this.year = document.getInteger(YEAR);
    }

    public Integer getGoalid() {
        return this.goalid;
    }

    public void setGoalid(Integer goalid) {
        this.goalid = goalid;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTargetValue() {
        return this.targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getActualValue() {
        return this.actualValue;
    }

    public void setActualValue(Integer actualValue) {
        this.actualValue = actualValue;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Document toDocument() {
        return new Document()
                .append(GOAL_ID, this.goalid)
                .append(DESCRIPTION, this.description)
                .append(TARGET_VALUE, this.targetValue)
                .append(ACTUAL_VALUE, this.actualValue)
                .append(YEAR, this.year);
    }

    @Override
    public String toString() {
        return String.format(
            "SocialPerformanceRecord [goalid=%s, description=%s, targetValue=%s, actualValue=%s, year=%s]",
            this.goalid,
            this.description,
            this.targetValue,
            this.actualValue,
            this.year
        );
    }


}
