package com.xc0ffeelabs.taxicabdriver.activities;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.securepreferences.SecurePreferences;
import com.xc0ffeelabs.taxicabdriver.models.Driver;
import com.xc0ffeelabs.taxicabdriver.models.Location;
import com.xc0ffeelabs.taxicabdriver.models.Trip;
import com.xc0ffeelabs.taxicabdriver.models.User;
import com.xc0ffeelabs.taxicabdriver.network.AccountManager;
import com.xc0ffeelabs.taxicabdriver.states.StateManager;

public class TaxiDriverApplication extends Application {

    private static final String APP_ID = "gotaxi";
    private static final String PARSE_URL = "https://gotaxi.herokuapp.com/parse/";

//    private static final String APP_ID = "chariottaxi";
//    private static final String PARSE_URL = "https://chariottaxi.herokuapp.com/parse/";

//    private static final String APP_ID = "chariotapp";
//    private static final String PARSE_URL = "https://chariotapp.herokuapp.com/parse/";
    private static TaxiDriverApplication mApp;

    private SecurePreferences mSecurePrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initializeParse();
    }

    public static TaxiDriverApplication get(){
        return mApp;
    }

    public static AccountManager getAccountManager() {
        return AccountManager.getInstance();
    }

    public static StateManager getStateManager() {
        return StateManager.getInstance();
    }

    private void initializeParse() {

        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(Driver.class);
        ParseObject.registerSubclass(Trip.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
//                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(PARSE_URL).build());
        ParseInstallation.getCurrentInstallation().saveInBackground();

//        setupData();
    }

    public SecurePreferences getSecureSharedPreferences() {
        if (mSecurePrefs == null){
            mSecurePrefs = new SecurePreferences(this, "", "my_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }


    /**
     * Data Setup Functions. Dont delete
     */

//    private void setupData() {
//        setUpUserData();
//        setUpDriverData();
//    }
//
//    public void setUpUserData(){
//        String names[] = {"Erick Ferencz", "Dyan Oldroyd", "Blair Malet", "Karl Nowski", "Penney Weight"};
//        String emails[] = {"erick@gmail.com", "dyan@gmail.com", "blair@gmail.com", "karl@gmail.com", "penney@gmail.com"};
//        String phones[] = {"2349874563", "345671234", "34514567", "4567890123", "3425678190"};
//        String picUrl = "https://randomuser.me/api/portraits/med/men/"; // 1.jpg
//        String pictureUrls[] = {picUrl+"1.jpg", picUrl+ "2.jpg", picUrl+"3.jpg", picUrl+"4.jpg", picUrl+"5.jpg"};
//
//
//        for (int i=0; i< 5; i++) {
//            try {
//                createUser(names[i], phones[i], emails[i], "password", pictureUrls[i], null);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public void setUpDriverData(){
//        String names[] = {"Lai Gato", "Herman Demesa", "Howard Paulas", "Daren Weirather", "Karan Karpin"};
//        String emails[] = {"lai@gmail.com", "herman@gmail.com", "howard@gmail.com", "daren@gmail.com", "karan@gmail.com"};
//        String phones[] = {"2349874563", "345671234", "34514567", "4567890123", "3425678190"};
//        String picUrl = "https://randomuser.me/api/portraits/med/men/"; // 1.jpg
//        String pictureUrls[] = {picUrl+"6.jpg", picUrl+ "7.jpg", picUrl+"8.jpg", picUrl+"9.jpg", picUrl+"10.jpg"};
//
//
//        for (int i=0; i< 5; i++) {
//            try {
//                createDriver(names[i], phones[i], emails[i], pictureUrls[i], "password", "126723cab1234", "honda", "3we245");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void createUser(String name, String phone, String email, String password, String profileImageUrl, SaveCallback cb) throws ParseException {
//
//        final User user = (User)ParseObject.create("_User");
////        User user = new User();
//        user.setRole(User.USER_ROLE);
//        user.setName(name);
//        user.setPhone(phone);
//        user.setEmail(email);
//        user.setPassword(password);
//        user.put("profileImage", profileImageUrl);
//        user.setUsername(email);
//        user.signUp();
//        user.signUpInBackground(new SignUpCallback() {
//            @Override
//            public void done(ParseException e) {
//                try {
//                    user.save();
//                } catch (ParseException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void createDriver(String name, String phone, String email, String profileImageUrl,  String password, String licenseNumber, String carModel, String carNumber ) throws ParseException {
//        final Driver user = (Driver)ParseObject.create("_User");
//        user.setRole(User.DRIVER_ROLE);
//        user.setName(name);
//        user.setPhone(phone);
//        user.setEmail(email);
//        user.setPassword(password);
//        user.setLicense(licenseNumber);
//        user.setCarModel(carModel);
//        user.setCarNumber(carNumber);
//        user.put("profileImage", profileImageUrl);
//        user.setUsername(email);
//        user.signUp();
//        user.signUpInBackground(new SignUpCallback() {
//            @Override
//            public void done(ParseException e) {
//                try {
//                    user.save();
//                } catch (ParseException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//    }
//
//
//    private void setupTripHistory() throws ParseException {
//        String userEmails[] = {"erick@gmail.com", "dyan@gmail.com", "blair@gmail.com", "karl@gmail.com", "penney@gmail.com"};
//        String driverEmails[] = {"lai@gmail.com", "herman@gmail.com", "howard@gmail.com", "daren@gmail.com", "karan@gmail.com"};
//        String sourceAddress[] = {"1 Facebook Way, Menlo Park, CA", "Googleplex, Amphitheatre Parkway, Mountain View, CA", "Microsoft SVC Building 1, La Avenida Street, Mountain View, CA"};
//        String destinationAddress[] = {"Apple Campus, Cupertino, CA", "LinkedIn, Stierlin Court, Mountain View, CA", "Twitter HQ, Market Street, San Francisco, CA"};
//
//        for(int i=0 ; i<userEmails.length; i++){
//            User user = (User)ParseUser.getQuery().whereEqualTo("email", userEmails[i]).find();
//            if (user!=null){
//                for(int j=0; j<driverEmails.length; j++) {
//                    Driver driver = (Driver)ParseUser.getQuery().whereEqualTo("email", driverEmails[i]).find();
//                    if (driver!=null) {
//                        for (int k=0; k<sourceAddress.length; k++) {
//                            //get source and destination address then post trip data
//                        }
//
//                    }
//                }
//            }
//        }
//    }
}