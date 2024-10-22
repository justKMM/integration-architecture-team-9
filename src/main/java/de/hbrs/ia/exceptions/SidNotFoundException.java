package de.hbrs.ia.exceptions;

import de.hbrs.ia.model.SalesMan;

public class SidNotFoundException extends RuntimeException {

    public SidNotFoundException(int sid) {
        super(
            String.format(
                "A salesman with the id \"%s\" does not exist.",
                sid
            )
        );
    }
    public SidNotFoundException(SalesMan salesMan) {
        this(salesMan.getId());
    }

}
