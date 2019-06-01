package za.co.a101apps.cognitologinapp;
import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.dynamodbv2.document.DeleteItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.PutItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBEntry;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBList;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbAccess {
    private final String COGNITO_IDENTITY_POOL_ID = "us-east-2:b5e78aad-7888-42f7-b940-e8830b83eb9a";

    private final Regions COGNITO_IDENTITY_POOL_REGION =  Regions.US_EAST_2;
    private final String DYNAMODB_TABLE = "ddb-cloud2app";
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private static volatile DbAccess instance;
    private String TAG = "TomekJestSuper";

    private DbAccess(Context context) {
        this.context = context;
        final CognitoSettings cognitoSettings = new CognitoSettings(this.context);

        //credentialsProvider = new CognitoCachingCredentialsProvider(context, "us-east-2:59abf0c1-9231-45f4-bb7f-714113f76dc7", COGNITO_IDENTITY_POOL_REGION);
        credentialsProvider = cognitoSettings.getCredentialsProvider();
        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();

        Log.v("userid:",currentUser.getUserId()+"");
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_EAST_2));
        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);

    }
    public static synchronized DbAccess getInstance(Context context) {
        if (instance == null) {
             Log.i("Kasia tez", "tworzenie nowej instancji bazy ? ");
             instance = new DbAccess(context);
        }
        return instance;
    }
    public Document getItem(String ID) {
        Document result = dbTable.getItem(new Primitive(ID));
        return result;
    }
    public List<Document> getAllContacts() {
        /*using scan to get all contacts*/
        ScanOperationConfig scanConfig = new ScanOperationConfig();
        List<String> attributeList = new ArrayList<>();
        attributeList.add("ID");
        attributeList.add("number");
        attributeList.add("date");
        scanConfig.withAttributesToGet(attributeList);
        Search searchResult = dbTable.scan(scanConfig);
        return searchResult.getAllResults();
    }

    /*add a single item*/
    public Document addContact(Document cloudCon) {
        Log.i(TAG, "adding contact...");

        String newName = "drink";


        /*new attributes*/
        cloudCon.put("type", newName);

        /*boolean*/
        cloudCon.put("vodka", false);

        /*integer*/
        cloudCon.put("number", 5);


        /*Set<String> mySet = new HashSet<String>();
        mySet.add("abc");
        mySet.add("set item 2");
        cloudCon.put("Set", mySet);*/

        // An Ordered List
        DynamoDBEntry item1 = new Primitive("orderedlist item 75");
        DynamoDBEntry item2 = new Primitive("orderedlist item 57");

        DynamoDBList dynamoList = new DynamoDBList();
        dynamoList.add(item1);
        dynamoList.add(item2);
        cloudCon.put("Ordered_list", dynamoList);


        /*PutItem only supports ReturnValues All old and None*/
        PutItemOperationConfig putItemOperationConfig = new PutItemOperationConfig();
        putItemOperationConfig.withReturnValues(ReturnValue.ALL_OLD);
//        putItemOperationConfig.withReturnValues(ReturnValue.NONE);

        Document result = dbTable.putItem(cloudCon, putItemOperationConfig);

        return result;
    }
}
