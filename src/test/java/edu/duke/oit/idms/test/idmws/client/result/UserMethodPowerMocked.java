package edu.duke.oit.idms.test.idmws.client.result;

import edu.duke.oit.idms.idmws.client.Result;
import edu.duke.oit.idms.idmws.client.UserMethods;

public class UserMethodPowerMocked {

    public String getUserDukeIDFromNetID(String netid){

        Result myResult = UserMethods.findByIdentifierAsAdmin("USR_UDF_UID", netid, new String[]{}, false);
        return  myResult.getUserQueryResult().getUsers().get(0).getUserId();

    }


    public int getUserCount(String identifer, String identiferValue){

        Result myResult = UserMethods.findByIdentifierAsAdmin("USR_UDF_IS_STAFF", identiferValue, new String[]{"USR_UDF_UID"}, false);
        return  myResult.getUserQueryResult().getMetadata().getNumberResults();

    }
}
