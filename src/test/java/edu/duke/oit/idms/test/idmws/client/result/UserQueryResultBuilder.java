package edu.duke.oit.idms.test.idmws.client.result;

import edu.duke.oit.idms.idmws.client.*;

import java.util.*;

public class UserQueryResultBuilder implements ResultBuilder {

    private Result result;
    private UserQueryResult userQueryResult;

    private UserQueryResultBuilder(){
        this.result = new Result(false,"No Error");
    }


    public static UserQueryResultBuilder getInstance(){
        return new UserQueryResultBuilder();
    }

    /**
     * This gets the Result object back that you can use for testing
     *
     * @param statusCode is the HTTP code that you want the Result to have
     * @return the Result object
     */
    public Result getResult(int statusCode){
        result.setUserQueryResult(this.userQueryResult);
        result.setStatusCode(statusCode);
        return result;
    }



    public UserQueryResultBuilder addUserToUserQueryResult(User user){

        if(this.userQueryResult == null){
            //No UserQueryResult exists, create a new one (along with its UserQueryMetaData Object)
            this.userQueryResult = new UserQueryResult();
            List<User> userList = new ArrayList<>();
            userList.add(user);
            this.userQueryResult.setUsers(userList);

            UserQueryMetadata userQueryMetadata = new UserQueryMetadata();
            userQueryMetadata.setNumberResults(1);

            this.userQueryResult.setMetadata(userQueryMetadata);
        } else {
            //There is already a UserQueryResult, append the give user
            this.userQueryResult.getUsers().add(user);

            //Adding one to the number of results the MetaData is reporting
            this.userQueryResult.getMetadata().setNumberResults(
                    this.userQueryResult.getMetadata().getNumberResults() + 1);
        }

        return this;
    }

    public UserQueryResultBuilder addUserToUserQueryResult(String dukeid, Map<String, List<Object>> attributes, String ... groups){

        //Converting the String[] of group names to Set
        Set<String> userGroups = new HashSet<>(Arrays.asList(groups));

        //Adding the User object to the UserQueryResults
        this.addUserToUserQueryResult(new User(dukeid, attributes, userGroups));

        return this;
    }
}
