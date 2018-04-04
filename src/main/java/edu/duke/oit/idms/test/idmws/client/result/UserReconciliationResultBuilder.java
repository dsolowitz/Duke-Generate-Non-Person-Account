package edu.duke.oit.idms.test.idmws.client.result;

import edu.duke.oit.idms.idmws.client.Result;
import edu.duke.oit.idms.idmws.client.UserReconciliationResult;

public class UserReconciliationResultBuilder implements ResultBuilder {

    private Result result;
    private UserReconciliationResult userReconciliationResult = new UserReconciliationResult();

    private UserReconciliationResultBuilder(){
        this.result = new Result(false,"No Error");
    }

    public static UserReconciliationResultBuilder getInstance(){

        return new UserReconciliationResultBuilder();
    }

    public Result getResult(int statusCode) {
        result.setUserReconciliationResult(this.userReconciliationResult);
        result.setStatusCode(statusCode);
        return  result;
    }

    public UserReconciliationResultBuilder setUserReconciliationResultNewDukeID(String newDukeID){
        this.userReconciliationResult.setNewDukeID(newDukeID);
        return this;
    }
}
