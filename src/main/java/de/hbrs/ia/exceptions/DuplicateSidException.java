package de.hbrs.ia.exceptions;

import de.hbrs.ia.model.SalesMan;

public class DuplicateSidException extends RuntimeException {

    public DuplicateSidException(SalesMan salesMan) {
        super("A salesman with the id: " + salesMan.getId() + " already exists.");
    }

}
