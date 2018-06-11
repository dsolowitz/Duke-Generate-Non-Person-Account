import edu.duke.oit.idms.generatenonperson.NonPersonAccount;
import edu.duke.oit.idms.idmws.client.UserMethods;
import edu.duke.oit.idms.idmws.client.UserReconciliationMethods;
import edu.duke.oit.idms.test.idmws.client.result.ResultBuilder;
import edu.duke.oit.idms.test.idmws.client.result.UserQueryResultBuilder;
import edu.duke.oit.idms.test.idmws.client.result.UserReconciliationResultBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserMethods.class,UserReconciliationMethods.class})
public class TestParseFile {

    public Scanner scan = getScanner();
    public String netid = "hodge012";
    public String dukeid = "0427503";
    public String lineForCard = scan.nextLine();
    public static ArrayList<Properties> props = new ArrayList<>();
    public static Map<String, String> propMap = new HashMap<>();

    @Before
    public void setUp() {

        //Creating a mocked Result that will be passed back from the test
        ResultBuilder resultBuilder = UserQueryResultBuilder.getInstance()
                .addUserToUserQueryResult(dukeid, Collections.emptyMap());
        ResultBuilder resultBuilder1 = UserReconciliationResultBuilder.getInstance().setUserReconciliationResultNewDukeID("1234567");

        PowerMockito.mockStatic(UserMethods.class);
        PowerMockito.mockStatic(UserReconciliationMethods.class);


        PowerMockito.when(UserMethods.findByIdentifierAsAdmin("USR_UDF_UID", netid, new String[]{"USR_LOGIN"}, false)).thenReturn(resultBuilder.getResult(200));
        //PowerMockito.when(UserReconciliationMethods.generateDUIDAsSuperAdmin(propMap, false)).thenReturn(resultBuilder1.getResult(200));
        PowerMockito.when(UserReconciliationMethods.generateDUIDAsSuperAdmin(Mockito.any(HashMap.class), Mockito.anyBoolean())).thenReturn(resultBuilder1.getResult(200));

    }

    @Test
    public void testLineForCard() {


        assertEquals(lineForCard, "980176234,9,EMILY,PARSLEY,hodge012,TIP Field Studies II Participants,7/25/2017");
    }

    @Test
    public void testCreateDuidInfo() {
        ;

        NonPersonAccount.createDUIDInfo(lineForCard);
        assertEquals("9801762349", NonPersonAccount.dukeNum);
        assertEquals("EMILY", NonPersonAccount.firstName);
        assertEquals("PARSLEY", NonPersonAccount.lastName);
        assertEquals("7/25/2017", NonPersonAccount.expirationDate);
        assertEquals("hodge012", NonPersonAccount.sponsorNetID);
        assertEquals("0427503", NonPersonAccount.sponsorDUID);
    }


    @Test
    public void testCreatePropList() {

        NonPersonAccount.createDUIDInfo(lineForCard);
        props.add(NonPersonAccount.createPropList(NonPersonAccount.firstName, NonPersonAccount.lastName, NonPersonAccount.sponsorDUID, NonPersonAccount.expirationDate, NonPersonAccount.dukeNum));
        assertEquals("EMILY", props.get(0).getProperty("firstName"));
        assertEquals("PARSLEY", props.get(0).getProperty("lastName"));
        assertEquals("7/25/2017", props.get(0).getProperty("expirationDate"));
        assertEquals("9801762349", props.get(0).getProperty("DukeCardNumber"));
        assertEquals("0427503", props.get(0).getProperty("sponsorDukeID"));
    }

    @Test
    public void testSetDuid() {

        NonPersonAccount.setDUID(props);
        assertEquals("1234567", props.get(0).getProperty("DUID"));
    }



    public static Scanner getScanner() {
        InputStream input;
        String filename = "sample.txt";
        input = TestParseFile.class.getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            System.out.println("Sorry, unable to find " + filename);

        }
        File firstFile = new File("sample.txt");

        Scanner scan = new Scanner(input);
        return scan;
    }

    public static void setMap() {
        for (Properties prop : props) {
            for (final String name : prop.stringPropertyNames()) {
                propMap.put(name, prop.getProperty(name));
            }
        }
    }
}

