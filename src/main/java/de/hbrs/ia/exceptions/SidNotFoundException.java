package de.hbrs.ia.exceptions;

import de.hbrs.ia.model.SalesMan;

public class SidNotFoundException extends RuntimeException {

    public SidNotFoundException(SalesMan salesMan) {
        super(
            String.format(
                "A salesman with the id \"%s\" does not exist.",
                salesMan.getId()
            )
        );
    }

    public SidNotFoundException(int sid) {
        super(
            String.format(
                "A salesman with the id \"%s\" does not exist.",
                sid
            )
        );
    }

}
