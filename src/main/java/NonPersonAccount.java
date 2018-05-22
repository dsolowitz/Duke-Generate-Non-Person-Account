import edu.duke.oit.idms.idmws.client.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NonPersonAccount {

    private static final Log LOG = LogFactory.getLog(NonPersonAccount.class.getName());
    static Scanner scan;
    static String firstName;
    static String lastName;
    static String sponsorNetID;
    static String sponsorDUID;
    static String expirationDate;
    static String dukeNum;
    static String lineForCard;
    static Properties propAttribute;
    static File firstFile = new File("sample.txt");
    static ArrayList<Properties> listOfProps;
    static ArrayList<Properties> listOfFailedLines;

    public void run() {
        BasicConfigurator.configure();
        LOG.info("Starting task.");
        try {
            scan = new Scanner(firstFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOG.fatal("No valid file");
        }
        while (scan.hasNextLine()) {
            lineForCard = scan.nextLine(); //get line from text file
            if (lineForCard.equals("")) continue;
            listOfProps = new ArrayList<>();
            createDUIDInfo(lineForCard);            //send each line individualy to createduidinfo
            propAttribute = createPropList(firstName,lastName,sponsorDUID,expirationDate,dukeNum);   //send first line to be created as property object
            if(propAttribute.containsValue("error")||propAttribute.containsValue(null)){
                    if(listOfFailedLines.size() == 0) {
                        listOfFailedLines = new ArrayList<>();
                        listOfFailedLines.add(propAttribute);
                    }
                listOfFailedLines.add(propAttribute);
                LOG.error("DukeCardNumber is missing the proper attributes: " + propAttribute.toString());
                continue;   //if any property contains a null value it wont be added to our list that will go get duid's. This is then stored in a seperate list that will be printed so an analyst can troubleshoot why certain id's aren't being created.
            }
            listOfProps.add(propAttribute);         //add all properties to list that have valid key value pairs.
        }
            setDUID(listOfProps);
            setDukeCardNum(listOfProps);
            logIdsThatDidntCreate(listOfFailedLines);

        scan.close();
    }


    public static void createDUIDInfo(String lineInfo) {
        if(lineInfo!=null) {                                  //check that line isn't empty
            String[] splited = lineInfo.split(",");     //split line by commas into columns
            if (splited.length == 7 || splited.length == 6) {   //check to make sure we have the right data
                String checkCardNum = splited[0].concat(splited[1]);      //combine first to columns for complete duke card number
                if(checkCardNum.length()!=10){
                    dukeNum = "error";
                    LOG.error("Incorrect length of Duke Card Number");
                }       //check to make sure cardnum is correct lengh, if not send error
                else dukeNum = checkCardNum;
                firstName = splited[2];                                    //store appropriate date in variables
                lastName = splited[3];
                sponsorNetID = splited[4];
                sponsorDUID = getDUIDForNetID(sponsorNetID);              //get DUID for sponsor netid
                if(splited[6].isEmpty()) expirationDate = null;           // check for ex. date, or store null
                else expirationDate = splited[6];

            }
        }
    }

    public static Properties createPropList(String firstName, String lastName, String sponsorNetID, String expirationDate, String dukeNum){
        propAttribute = new Properties();
        propAttribute.setProperty("DukeCardNumber",dukeNum);         //store each attribute as a property object for one card number.
        propAttribute.setProperty("firstName",firstName);
        propAttribute.setProperty("lastName", lastName);
        propAttribute.setProperty("sponsorDukeID",sponsorDUID);
        propAttribute.setProperty("expirationDate", expirationDate);
        return propAttribute;
    }

    public static String getDUIDForNetID(String netId){        //search database for net id, if this doesn't work this line of text won't process
         String duid;
         Result uid = UserMethods.findByIdentifierAsAdmin("USR_UDF_UID", netId, new String[]{"USR_LOGIN"}, false);
         if(uid.getError()){
             LOG.error("No info in result while getting DUID for NETID.");
             throw new RuntimeException("Error:" + uid.getErrorMessage());
         }
         UserQueryResult userQueryResult = uid.getUserQueryResult();
         List<User> userList = userQueryResult.getUsers();      //check to make sure we have a return value, if not store error.
         if(userList.isEmpty()){
           LOG.error("No duid for sponsor netid");
           return duid = "error";
         }
         return duid = userList.get(0).getUserId();
    }

    public static void setDUID(ArrayList<Properties> list){
         for(Properties prop :list) {                              //getting duids for each property and updating property with new duid.
             if(!prop.containsValue("error")) {
                 Map<String, String> propMap = new HashMap<>();
                 for (final String name: prop.stringPropertyNames()){
                     propMap.put(name, prop.getProperty(name));}
                 Result result = UserReconciliationMethods.generateDUIDAsSuperAdmin(propMap, false);

                 if(result.getError()){
                     LOG.error("Error generating new DUID number");
                     throw new RuntimeException("Error:" + result.getErrorMessage());
                 }
                 UserReconciliationResult userReconciliationResult = result.getUserReconciliationResult();
                 String duid = userReconciliationResult.getNewDukeID();
                 prop.setProperty("DUID",duid);
                 LOG.info("Success creating and storing new DUID " + duid);
             }
         }
    }

    public static void setDukeCardNum(ArrayList<Properties> listOfCardNums){    //finding identity of duid num and storing the dukecardnum in the database with it.
          for(Properties prop: listOfCardNums){
              String cardNum = prop.getProperty("DukeCardNumber");
              String duid = prop.getProperty("DUID");
              int duidInt = Integer.parseInt(duid);
              UserReconciliationMethods.updateAsSuperAdmin(duidInt,"USR_UDF_DUKECARDNBR",cardNum,false);
          }
    }

    public static ArrayList<Properties> getProperties(){
        return listOfProps;
    }

    private void logIdsThatDidntCreate(ArrayList<Properties> badIds) { //Logging all of the bad information that couldn't generate duke id's.
        for(Properties prop: badIds){
            LOG.error("Could not create duid for: " + prop.toString());
        }
    }
}