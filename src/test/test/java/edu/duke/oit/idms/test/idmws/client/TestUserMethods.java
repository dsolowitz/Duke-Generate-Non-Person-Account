package edu.duke.oit.idms.test.idmws.client;

import edu.duke.oit.idms.idmws.client.UserMethods;
import edu.duke.oit.idms.test.idmws.client.result.ResultBuilder;
import edu.duke.oit.idms.test.idmws.client.result.UserQueryResultBuilder;
import edu.duke.oit.idms.test.idmws.client.utils.UserMethodPowerMocked;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;



@RunWith(PowerMockRunner.class)
@PrepareForTest({UserMethods.class})
public class TestUserMethods {


    /**
     * Basic test showing how to use PowerMock and MockResults together
     *
     *
     */
    @Test
    public void myResultTest(){
        String netid = "rch17";
        String dukeid = "0427503";

        //Creating a mocked Result that will be passed back from the test
        ResultBuilder resultBuilder = UserQueryResultBuilder.getInstance()
                .addUserToUserQueryResult(dukeid, Collections.emptyMap());




        //UserMethodPowerMocked.getUserDukeIDFromNetID() uses:
        //  UserMethodsfindByIdentifierAsAdmin("USR_UDF_UID", [GIVEN_NETID], new String[]{"USR_LOGIN"}, false)
        //  to search for records.
        //Setting up PowerMock to override the static UserMethods.findByIdentifierAsAdmin() method
        //  and to return my MockedResults for the myPowerMockedUserMethods.getUserDukeIDFromNetID() to use
        PowerMockito.mockStatic(UserMethods.class);


        PowerMockito.when(UserMethods.findByIdentifierAsAdmin("USR_UDF_UID", netid, new String[]{}, false))
                .thenReturn(resultBuilder.getResult(200));

        //Testing here....
        UserMethodPowerMocked myPowerMockedUserMethods = new UserMethodPowerMocked();

        //Comparing the results
        Assert.assertEquals(myPowerMockedUserMethods.getUserDukeIDFromNetID("rch17"),dukeid);

    }

    /**
     * Test showing how to use MockedResults with multiple users returned and while attribute Maps for the users.
     *
     */
    @Test
    public void myResultCountTest(){

        //Creating a mocked Result that will be passed back from the test
        ResultBuilder result = UserQueryResultBuilder.getInstance()
                .addUserToUserQueryResult("0427503", Collections.singletonMap("USR_UDF_UID", Collections.singletonList("rch17")))
                .addUserToUserQueryResult("0427504", Collections.singletonMap("USR_UDF_UID", Collections.singletonList("not_rch17")));

        //Setting up PowerMock to override the static UserMethods.findByIdentifierAsAdmin() method
        //  and to return my MockedResults for the myPowerMockedUserMethods.getUserDukeIDFromNetID() to use
        PowerMockito.mockStatic(UserMethods.class);
        PowerMockito.when(UserMethods.findByIdentifierAsAdmin(
                "USR_UDF_IS_STAFF", "1", new String[]{"USR_UDF_UID"}, false)
        ).thenReturn(result.getResult(200));

        //Testing here....
        UserMethodPowerMocked myPowerMockedUserMethods = new UserMethodPowerMocked();

        //Comparing the results
        Assert.assertEquals(myPowerMockedUserMethods.getUserCount("USR_UDF_IS_STAFF", "1"),2);

    }

}
