package com.xc0ffeelabs.taxicabdriver;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xc0ffeelabs.taxicabdriver.models.User;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skammila on 3/22/16.
 */
public class DataSetup extends TestCase{

    private String[] userNames={"James Butt","Lenna Paprocki", "Sage Wieser"};
//    String user1 = "{" +
//            "name: James Butt" +
//            "phone: 3456789124" +
//            "email: james@gmail.com" +
//            "password: password" +
//            "}";
//    String
    public void setUp(){
        String baseUrl = "https://randomuser.me/api/";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(baseUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray

                try {
                    JSONObject result = (JSONObject)response.getJSONArray("results").get(0);
                    JSONObject user = result.getJSONObject("user");
                    String name = user.getJSONObject("name").getString("first") + user.getJSONObject("name").getString("last");
                    String email = user.getString("email");
                    String phone = user.getString("phone");
                    String picture = user.getJSONObject("picture").getString("thumbnail");
                    createUser(name,phone, email, "password", picture);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Pull out the first event on the public timeline

//                System.out.println(tweetText);
            }
        });

    }

    private void createUser(String name, String phone, String email, String password, String profileImageUrl) {

        User user = new User();
        user.setRole(User.USER_ROLE);
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.put("profileImage", profileImageUrl);
        user.saveInBackground();

    }

    private void createDriver(String name, String phone, String email, String password, String licenseNumber, String carModel, String carNumber ) {

        User user = new User();
        user.setRole(User.DRIVER_ROLE);
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.setLicense(licenseNumber);
        user.setCarModel(carModel);
        user.setCarNumber(carNumber);
        user.saveInBackground();

    }

    public void testAdd() {
        double result = 2;
        assertTrue(result == 2.0);
    }


}
