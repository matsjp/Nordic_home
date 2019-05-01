import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;


import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.*;

public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE); //_FILE skal ikke brukes pga deling ikke fungerer
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static ArrayList<Network> networks = new ArrayList<Network>(); // Bruker ikke den --- List of all available networks

    private static final int TAI_YEAR = 2000;
    private static final int TAI_MONTH = 1;
    private static final int TAI_DATE = 1;

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ////////
        ////////

        // Print the names and IDs for up to 10 files. Folder fungerer med in parents.

        //String folder = "1oomIQ5ai8PFdRVx7v61_ju_x2ixRApFw";


        /**
         * Har brukt denne
         */
        FileList result = service.files().list()
                .setQ("mimeType contains 'application/json'")
                //.setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }


        // Kopi av over. Fungerer ikke å gjenbruke denne.
/*
        String fileiden = "1WdvsJSsn4AkvpT_uTQ_tYi4_NLmwRegL";
        FileList result = service.files().list()
                .setQ("1WdvsJSsn4AkvpT_uTQ_tYi4_NLmwRegL")
                //.setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
*/


        /**
         * Printer filen fra drive. fungerer. (generere tekst filen)
         * Trenger denne
         */

        String fileId = "1jRPPJ7m99dRLgqS9QZwml4O5ULF3yz_Y";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        service.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        String out = outputStream.toString("UTF-8");
        //System.out.println(out);


        //Laste opp bildefil
        /*
        File fileMetadata = new File();
        fileMetadata.setName("photo.jpg");
        java.io.File filePath = new java.io.File("photo.jpg");
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        System.out.println("File ID: " + file.getId());*/

        // Lage ny fil
        /*
        File fileMetadata = new File();
        fileMetadata.setName("NordicHome");
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file = service.files().create(fileMetadata)
                .setFields("id")
                .execute();
        System.out.println("Folder ID: " + file.getId());
        */
        /**
         * Fil inn i mappe
         */
        /*
        String folderId = "1oomIQ5ai8PFdRVx7v61_ju_x2ixRApFw";
        File fileMetadata = new File();
        fileMetadata.setName("ntnui_logo.jpg");
        fileMetadata.setParents(Collections.singletonList(folderId));
        java.io.File filePath = new java.io.File("files/NTNUI_logo_stor.png");
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();
        System.out.println("File ID: " + file.getId());
        */

/*

        // Convert JSON Array String into Java Array List
        String jsonArrayString = "[\"{Russian: RUS}\",\"{English: ENG}\",\"{French: FRA}\"]";
        Gson googleJson = new Gson();
        ArrayList javaArrayListFromGSON = googleJson.fromJson(jsonArrayString, ArrayList.class);

        System.out.println(javaArrayListFromGSON);
*/


        /**
         * Share file/folders works
         */

        /*
        String fileId = "1oomIQ5ai8PFdRVx7v61_ju_x2ixRApFw";
        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                // Handle error
                System.err.println(e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                System.out.println("Permission ID: " + permission.getId());
            }
        };
        BatchRequest batch = service.batch();
        Permission userPermission = new Permission()
                .setType("user")
                .setRole("writer")
                .setEmailAddress("sebastian.torgersen@ntnui.no");
        service.permissions().create(fileId, userPermission)
                .setFields("id")
                .queue(batch, callback);

        batch.execute();

        */
        //makeJSONObject(downloadFile(service, "1XT6aRBeuQY-0pnyMrAfaah_hdu50Q0dV"));

        //System.out.println(makeObjectNetKeys(input));    //En test for å finne en feilplassert ,
    }





    // Funksjon for å håndtere alle nettverk

    public static ArrayList<Network> netsFromFolder(Drive service, String fileId) {

        //networks.add(makeJSONObject(downloadFile(service,fileId)));

        return networks;
    }


    // Download and return list of networks from Drive

    /**
     * MÅ FLYTTES UT HERIFRA!
     * @param service
     * @param fileIds
     * @return list of networks from Drive
     */

    public static ArrayList<Network> downloadFiles(Drive service, ArrayList<String> fileIds) {
        ArrayList <Network> listOfNetworks = new ArrayList<>();
        for (String fileId : fileIds) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                String file = outputStream.toString("UTF-8");
                if (file.contains("\"meshUUID\":")) {
                    listOfNetworks.add(makeJSONObject(file));
                    System.out.println("HEEEEER");
                    System.out.println(fileId);
                } else {
                    System.out.println("NEI");
                }
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
            }

        }
        return listOfNetworks;
    }

    /**
     * Denne må også ut herifra!
     * @param json
     * @return
     */

    private static Network makeJSONObject(String json) {

        Gson g = new Gson();

        Network network = g.fromJson(json, Network.class);
        return network;
        //System.out.println(network.scenes); //printer navn på meshnetverket
        //System.out.println(g.toJson(network)); // {"noe":"noe"}       //BS: Tror ikke denne fungere helt for arrays, men kan fungere om Network endres litt

    }

    /**
     * Denne må flyttes ut herifra!!
     *
     * Getting the file IDs and returns them in
     * @param service
     */

    public static ArrayList<String> getJSONfiles(Drive service){
        ArrayList<String> fileIds = new ArrayList<>();
        try{
            FileList result = service.files().list()
                    .setQ("mimeType contains 'application/json'")
                    //.setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                System.out.println("Files:");
                for (File file : files) {
                    System.out.printf("%s (%s)\n", file.getName(), file.getId());
                    // Preventing the installers JSON-files to pop up in Network-list
                    if (!file.getName().contains("_")) {
                        fileIds.add(file.getId());
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return fileIds;
    }

    // Bruker den i Network.java i stedet
    private static ArrayList makeObjectNetKeys(String json) {
        Gson g = new Gson();
        ArrayList netkeys = g.fromJson(json, ArrayList.class);
        return netkeys;
    }


    /**
     *
     * @param meshName name of the network
     * @return the new empty network
     */
    public Network createFreshNetwork(String meshName) {
        final String meshUuid = UUID.randomUUID().toString().toUpperCase(Locale.US);
        final Long timeStamp = getInternationalAtomicTime(System.currentTimeMillis());
        final NetKeys netKeys= new NetKeys();
        final AppKeys appKeys = new AppKeys();
        final String provisioners = "[]";
        final String nodes = "[]";
        final String groups = "[]";
        final String scenes = "[]";

        Network network = new Network(
                "http://json-schema.org/draft-04/schema#",
                "TBD",
                "1.0",
                meshUuid,
                meshName,
                timeStamp.toString(),
                netKeys,
                appKeys,
                provisioners,
                nodes,
                groups,
                scenes
                );
        return network;
    }

    /**
     * Returns the international atomic time (TAI) in seconds
     * <p>
     * TAI seconds and is the number of seconds after 00:00:00 TAI on 2000-01-01
     * </p>
     * @author Nordic semiconductor
     * @param currentTime current time in milliseconds
     */
    public static long getInternationalAtomicTime(final long currentTime) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(TAI_YEAR, TAI_MONTH, TAI_DATE, 0, 0, 0);
        final long millisSinceEpoch = calendar.getTimeInMillis();
        return (currentTime - millisSinceEpoch) / 1000;
    }


}