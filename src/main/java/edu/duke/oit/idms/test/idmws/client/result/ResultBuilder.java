package edu.duke.oit.idms.test.idmws.client.result;

import edu.duke.oit.idms.idmws.client.*;


public interface ResultBuilder {

    /**
     * This gets the Result object back that you can use for testing
     *
     * @param statusCode is the HTTP code that you want the Result to have
     * @return the Result object
     */
    public Result getResult(int statusCode);
}
