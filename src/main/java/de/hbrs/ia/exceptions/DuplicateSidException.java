package de.hbrs.ia.exceptions;

import de.hbrs.ia.model.SalesMan;

public class DuplicateSidException extends RuntimeException {

    public DuplicateSidException(SalesMan salesMan) {
        super(
            String.format(
                "A salesman with the id \"%s\" already exists.",
                salesMan.getId()
            )
        );
    }

}
