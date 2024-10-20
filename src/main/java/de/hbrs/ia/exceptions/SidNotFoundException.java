package de.hbrs.ia.exceptions;

import de.hbrs.ia.model.SalesMan;

public class SidNotFoundException extends RuntimeException {

    public SidNotFoundException(SalesMan salesMan) {
        super("A salesman with the id: " + salesMan.getId() + " does not exist.");
    }

    public SidNotFoundException(int sid) {
        super("A salesman with the id: " + sid + " does not exist.");
    }

}
