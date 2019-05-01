import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;



import com.google.gson.*;
import org.mortbay.util.IO;

import java.io.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Array;
import java.util.*;


public class NetworksController {

    @FXML
    private JFXButton btnDeleteScene;

    @FXML
    private JFXButton btnDeleteNetwork;

    @FXML
    private JFXListView<Network> lwNetworkList;

    @FXML
    private Label lblNetworkName;

    @FXML
    private Label lblNetworkAddress;

    @FXML
    private JFXListView<Groups> lvGroups;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane apNetworkInfo;

    @FXML
    private JFXListView<Scenes> lvScenes;

    @FXML
    private JFXListView<String> lvSharedInstallers;

    @FXML
    private JFXButton btnRemoveInstaller;


    //List with all networks
    ArrayList<Network> networks = new ArrayList<Network>();


    public Network networkChosen = null;
    public Object chosenGroup = null;
    private String appFolderId = "";
    private String netFolderId = "";
    private String currentNetId = "";


    @FXML
    private void initialize() throws IOException{
        ArrayList<String> idOfFiles = DriveQuickstart.getJSONfiles(LoginController.getService()); //Finner JSON-filer på Driven, må flyttes vekk fra DriveQuickstart
        networks.addAll(DriveQuickstart.downloadFiles(LoginController.getService(),idOfFiles)); //Generates the network list
        showNetworks();
        showChosenNetwork();
        showChosenNetwork();

    }

    /**
     * newNetwork: Method for adding a new network to the networks list
     *
     * */
    public void newNetwork(String meshName){

        networks.add(new DriveQuickstart().createFreshNetwork(meshName));
    }

    public void newGroup(Network network, String groupName) {
        int address = network.getGroups().size()+1+49152;
        int parentAddress = 0;
        Groups group = new Groups(groupName, address, parentAddress);
        network.getGroups().add(group);

        //Adding default scenes
        Scenes onOffScene = new Scenes("Lights off", Integer.toHexString(address), network.getScenes().size());
        network.getScenes().add(onOffScene);
        Scenes dimFast = new Scenes("Lights on", Integer.toHexString(address), network.getScenes().size());
        network.getScenes().add(dimFast);
        Scenes dimSlow = new Scenes("Light dim", Integer.toHexString(address), network.getScenes().size());
        network.getScenes().add(dimSlow);
    }


    /**
     * showNetworks: Method for showing the existing networks in the listview
     * @author Julie
     *
     * */
    public void showNetworks(){
        try{
            //Put all the networks in the network list view
            lwNetworkList.getItems().addAll(networks);

            //Custom cell factory for showing the name of the network in the network list view
            lwNetworkList.setCellFactory(param -> new JFXListCell<Network>() {
                protected void updateItem(Network item, boolean empty){
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getMeshName() == null) {
                        setText(null);
                    } else {
                        setText(item.getMeshName());
                    }
                }
            });
        }
		catch (Exception e) {
            System.out.println("Exception: Something wrong with showing the networks ---> "+ e);
        }
    }


    /**
     * showNewNetworkDialog: Method for creating a new network with name and address
     * @author Julie & Kaja
     * */
    public void showNewNetworkDialog(){
        //Create a JFXDialog
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("New network"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        JFXTextField networkName = new JFXTextField();

        grid.add(new Label("Network name:"), 0, 0);
        grid.add(networkName, 0, 1);

        content.setBody(grid);

        JFXDialog dialogNewGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
        JFXButton close = new JFXButton("Close");
        JFXButton add = new JFXButton("Add");
        add.setDefaultButton(true);

        close.setOnAction(event -> dialogNewGroup.close());

        add.setOnAction(event -> {
            try{
                if(!networkName.getText().isEmpty()){
                    String name = networkName.getText();

                    //Adding the new network to the list of networks
                    newNetwork(name);

                    //Updating the listview
                    lwNetworkList.getItems().clear();
                    lwNetworkList.getItems().addAll(networks);

                    dialogNewGroup.close();
                }
            }
            catch (NullPointerException e){
                System.out.println("Exception when creating a network --> " + e);
            }
        });

        content.setActions(close, add);
        dialogNewGroup.show();
    }


    /**
     * chooseNetwork: Method for choosing and showing info about network
     * @author Julie
     * */
    public void showChosenNetwork(){
        networkChosen = lwNetworkList.getSelectionModel().getSelectedItem();
        lwNetworkList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Network>) (observable, oldValue, newValue) -> {
            try {
                showNetworkInfo(newValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }


    /**
     * getNetworkInfo: Method for getting and showing the roght info of the chosen network
     * @author Julie
     * */
    public void showNetworkInfo(Network network) throws IOException{
        apNetworkInfo.setVisible(true);
        btnDeleteScene.setDisable(true);
        btnDeleteNetwork.setDisable(false);
        lvGroups.getItems().clear();
        lvSharedInstallers.getItems().clear();

        System.out.println("Nettverk: " + network.getMeshName() + "\n"
                + "meshUUID: " + network.getMeshUUID());

        try {
            String name = network.getMeshName();
            String meshUUID = network.getMeshUUID();
            ArrayList<Groups> groups = network.getGroups();

            lblNetworkName.setText(name);
            lblNetworkAddress.setText(meshUUID);
            lvGroups.getItems().addAll(groups);

            //Custom cell factory for showing the name of the groups in the groups list view
            lvGroups.setCellFactory(param -> new JFXListCell<Groups>() {
                protected void updateItem(Groups item, boolean empty){
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            showScenesForChosenGroup(network);
            showSharedInstallers(network);
        }
        catch (NullPointerException e){
            System.out.println("Exception loading the info for the network --> "+ e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * showScenesForChosenGroup: Method for showing the right scenes for the different groups
     * */
    public void showScenesForChosenGroup(Network network) {
        chosenGroup = lvGroups.getSelectionModel().getSelectedItem();

        lvGroups.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Groups>) (observable, oldValue, newValue) -> showScenes(network, newValue));

    }

    /**
     * showScenes: Help method for showScenesForChosenGroup(Network network)
     * */
    public void showScenes(Network network, Groups group){
        lvScenes.getItems().clear();
        btnDeleteScene.setDisable(false);

        try{
            String chosenGroup = group.getName();
            ArrayList <Scenes> scenes = network.getScenesById(group);
            lvScenes.getItems().addAll(scenes);

            //Custom cell factory for showing the name of the scene in the scene list view
            lvScenes.setCellFactory(param -> new JFXListCell<Scenes>() {
                protected void updateItem(Scenes item, boolean empty){
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            System.out.println("Scenes for group: " + chosenGroup + "\nScenes:  " + (network.getScenesNameByGroup(network.getScenesById(group))));
        } catch(NullPointerException e){
            System.out.println("Exception loading scenes to listview --->  "+ e);
        }


    }


    /**
     * deleteScene: Method for deleting a scene from a group
     * @author Julie
     * */
    public void deleteScene(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {
            //Getting the chosen group to delete from, and the chosen scene to delete
            Groups group = lvGroups.getSelectionModel().getSelectedItem();
            Scenes scene = lvScenes.getSelectionModel().getSelectedItem();


            if (!network.getScenes().isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delete scene '" + scene.getName() + "' from group '" + group.getName() + "'?"));

                JFXDialog dialogDeleteScene = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteScene.close());

                //Button action for deleting the scene from the group
                yes.setOnAction(event -> {
                    try {
                        ArrayList <Scenes> allScenes = network.getScenes();
                        allScenes.remove(scene);
                        lvScenes.getItems().clear();
                        lvScenes.getItems().addAll(network.getScenesById(group));
                        dialogDeleteScene.close();

                        System.out.println("Deleted scene: " + scene.getName());
                        System.out.println("Current scenes: " + (network.getScenesNameByGroup(network.getScenesById(group))));
                    } catch (NullPointerException e) {
                        System.out.println("Exception deleting a scene from a group --> " + e);
                    }
                });

                content.setActions(close, yes);
                dialogDeleteScene.show();

            }

        } catch (NullPointerException e){
            System.out.println("Exception getting scene or group when clicking 'Delete group' ---> "+e);
        }

    }



    /**
     * showNewGroupDialog: Method for creating a new group in a Network
     * @author Julie & Kaja
     * */
    public void showNewGroupDialog(){
        //Create a JFXDialog
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Name of new group:"));

        JFXTextField group = new JFXTextField();
        Label status = new Label();

        //Create a grid layout for the dialog
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 0, 0, 0));

        grid.add(group,0,0,2,1);
        grid.add(status,0,1,1,1);
        group.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        content.setBody(grid);


        //content.setBody(group);

        JFXDialog dialogNewGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
        JFXButton add = new JFXButton("Add");
        JFXButton close = new JFXButton("Close");
        add.setDefaultButton(true);

        //Button for closing the dialog
        close.setOnAction(event -> dialogNewGroup.close());


        //Button for adding the new group to the network
        add.setOnAction(event -> {
            try{
                if (!group.getText().isEmpty()){
                    String groupName = group.getText();
                    Network network = lwNetworkList.getSelectionModel().getSelectedItem();

                    //Er det Network-objekter på lwNetworkList?

                    //Add the new group to the network
                    newGroup(network, groupName);
                    //network.addGroup(groupName);

                    //Update the list view
                    updateGroupListView(network); //network.getName? Grupper addes ikke. Må fikses. Eller vises feil hvertfall.


                    //Sjekk
                    System.out.println("Group name: " + groupName);
                    System.out.println("Network: " + network.getMeshName());
                    System.out.println("Groups: " + network.getGroupsName());


                    group.clear();
                    status.setText("Added successfully!");
                }


            }
            catch (NullPointerException e){
                System.out.println("Exception adding a group --> " + e);
            }
        });

        content.setActions(close, add);
        dialogNewGroup.show();
    }


    //Help method for updating the list view with the groups
    public void updateGroupListView(Network network){
        lvGroups.getItems().clear();
        lvGroups.getItems().addAll(network.getGroups());

    }

    public void updateSharedNetworkView(Network network) throws IOException{
        lvSharedInstallers.getItems().clear();
        lvSharedInstallers.getItems().addAll(getSharedEmails(network));

        lvSharedInstallers.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            try {
                showSharedInstallers(network);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /*
        //Custom cell factory for showing the name of the scene in the scene list view
        lvSharedInstallers.setCellFactory(param -> new JFXListCell<ArrayList<String>>() {
            protected void updateItem(Scenes item, boolean empty){
                super.updateItem(network.getSharedEmails(), empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        lvSharedInstallers.setCellFactory(param -> new JFXListCell<Network>() {
            protected void updateItem(Network item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null || item.getSharedEmails() == null) {
                    setText(null);
                } else {
                    setText(item.getMeshName());
                }
            }
        });
*/

    }

    /**
     * Just for helping updateSharedNetworkView
     * @param network
     */
    public void showSharedInstallers(Network network) throws IOException {
        lvSharedInstallers.getItems().clear();
        btnRemoveInstaller.setDisable(false);

        try{
            ArrayList<String> installers = getSharedEmails(network);
            lvSharedInstallers.getItems().addAll(installers);
            System.out.println("Installers with copy/access in "+ network.getMeshName() +":\n-" + installers);
        } catch(NullPointerException e){
            System.out.println("Exception loading installers with access to listview --->  "+ e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * deleteNetwork: Method for deleting a network
     * @author Julie/Seb
     * */
    public void deleteNetwork(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {

            if (!networks.isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delete network '" + network.getMeshName() + "'?"));

                JFXDialog dialogDeleteScene = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteScene.close());

                //Button action for deleting the network
                yes.setOnAction(event -> {
                    try {
                        networks.remove(network);
                        // SLETTE NETTVERK FRA GDRIVE HER!
                        lwNetworkList.getItems().clear();
                        lwNetworkList.getItems().addAll(networks);
                        dialogDeleteScene.close();

                    } catch (NullPointerException e) {
                        System.out.println("Exception deleting a network --> " + e);
                    }
                });

                content.setActions(close, yes);
                dialogDeleteScene.show();

            }

        } catch (NullPointerException e){
            System.out.println("Exception getting scene or group when clicking 'Delete group' ---> "+e);
        }
    }

    /**
     * Dialogbox for adding new installer.
     * @author Seb
     */

    public void showNewInstallerDialog(){
        //Create a JFXDialog
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("E-mail of installer:"));

        JFXTextField email = new JFXTextField();
        Label status = new Label();

        //Create a grid layout for the dialog
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 0, 0, 0));

        grid.add(email,0,0,2,1);
        grid.add(status,0,1,1,1);
        email.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        content.setBody(grid);

        JFXDialog dialogNewInstaller = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
        JFXButton share = new JFXButton("Share");
        JFXButton close = new JFXButton("Close");
        share.setDefaultButton(true);

        //Button for closing the dialog
        close.setOnAction(event -> dialogNewInstaller.close());


        //Button for sharing the network
        share.setOnAction(event -> {
            try{
                if (!email.getText().isEmpty()) {
                    String sharedEmail = email.getText();
                    Network network = lwNetworkList.getSelectionModel().getSelectedItem();
                    System.out.println(sharedEmail);



                    //Adding the email address to the network TRENGER IKKE DETTE
                    //network.addSharedEmail(sharedEmail);
                    //Update the list view of shared emails ENDRE DENNE!
                    //updateSharedNetworkView(network);

                    //Check
                    //System.out.println("Shared e-mails: " + network.getSharedEmails());
                    System.out.println("Network: " + network.getMeshName());

                    //Generates provision info in the Network object
                    String username = sharedEmail.substring(0, sharedEmail.indexOf('@'));
                    network.setProvisioner(username);


                    convertToJson(network);
                    putFileintoGDrive(network);

                    makeJsonForInstaller(network, sharedEmail, netFolderId, currentNetId);
                    updateSharedNetworkView(network);


                    //TODO: Make copy and share network to the new installer! DONE ish
                    //TODO: List the shared installers





                    email.clear();
                    status.setText("Added successfully!");
                }
            }
            catch (NullPointerException e) {
                System.out.println("Exception adding a installer email --> ");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

        content.setActions(close, share);
        dialogNewInstaller.show();
    }

    /**
     * Make copy to new installer
     */

    private void makeJsonForInstaller (Network network, String email, String netFolderId, String currentNetId) throws IOException{
        String meshName = network.getMeshName().replaceAll("\\s+","");
        String username = email.substring(0, email.indexOf('@'));

        if (!existenceOfinstaller(meshName, username)) {
            File fileMetadata = new File();
            fileMetadata.setName(meshName + "_" + username + ".json");
            fileMetadata.setParents(Collections.singletonList(netFolderId));
            java.io.File filePath = new java.io.File("src/main/java/JSON_networks/" + meshName + "/" + meshName + ".json");
            FileContent mediaContent = new FileContent("application/json", filePath);
            File newFile = LoginController.getService().files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();
            System.out.println("File ID: " + newFile.getId());
            this.currentNetId = newFile.getId();
            // Setting writer permission for new installer
            shareJSONfile(newFile.getId(), email);
        }
        // Feilmelding til bruker kanskje greit å ha her?
    }
//TODO: Fikser her!!!
    public ArrayList<String> getSharedEmails(Network network) throws IOException {
        String netFolderId = getNetFolderId(network);
        ArrayList<String> sharedEmails = new ArrayList<>();
        FileList result = LoginController.getService().files().list()
                .setQ("'" + netFolderId + "' in parents") //Child of netfolderid
                .setFields("nextPageToken, files(id, name)") //files(id, name), nextPageToken
                .execute();
        List<File> files = result.getFiles();
        for (File file : files) {
            if(file.getName().contains("_")){
                System.out.println("Navn på delt fil: " + file.getName());                         //Test


                PermissionList filePermissions = LoginController.getService().permissions()
                        .list(file.getId())
                        .setFields("*")
                        .execute();

                List<Permission> permissions = filePermissions.getPermissions();

                for (Permission per : permissions) {
                    sharedEmails.add(per.getEmailAddress());
                    System.out.println("Her skal det komme en epost: " + per.getEmailAddress());
                }
            } else {
                System.out.println("Not shared with any installers");
            }
        }
        return sharedEmails;
    }

    /**
     * Just for getting the ID of the network folder in GDrive
     * @param network
     * @return String
     */
    public String getNetFolderId(Network network) throws IOException {
        String folderId = "";
        String meshName = network.getMeshName().replaceAll("\\s+", "");
        FileList result = LoginController.getService().files().list()
                .setQ("name contains '" + meshName + "'" + " and mimeType contains 'application/vnd.google-apps.folder'")
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("Can\'t find Network folder");
        } else {
            for (File file : files) {
                folderId = file.getId();
                System.out.println(file.getName() + ": " + folderId);
            }
            return folderId;
        }
        return "";
    }

    /**
     * Sharing the installer-file on GDrive
     * @param fileId
     * @throws IOException
     */
    private void shareJSONfile(String fileId, String email) throws IOException{
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
        BatchRequest batch = LoginController.getService().batch();
        Permission userPermission = new Permission()
                .setType("user")
                .setRole("writer")
                .setEmailAddress(email);
        LoginController.getService().permissions().create(fileId, userPermission)
                .setFields("id")
                .queue(batch, callback);

        batch.execute();
    }

    /**
     * Downloads file from GDrive
     * @param fileId
     * @return
     * @throws IOException
     */

    public String downloadFile(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LoginController.getService().files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        String out = outputStream.toString("UTF-8");
        return out;
    }

    public void deleteSharedInstaller() throws IOException{
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {
            //Getting the chosen email to delete
            String installer = lvSharedInstallers.getSelectionModel().getSelectedItem();


            if (!getSharedEmails(network).isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to remove installer '" + installer + "'?"));

                JFXDialog dialogDeleteSharedInstaller = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteSharedInstaller.close());

                //Button action for deleting the shared installer
                yes.setOnAction(event -> {
                    try {
                        ArrayList <String> installers = getSharedEmails(network);
                        installers.remove(installer);

                        lvSharedInstallers.getItems().clear();
                        lvSharedInstallers.getItems().addAll(installers);
                        dialogDeleteSharedInstaller.close();

                        System.out.println("Deleted shared installer: " + installer);
                        System.out.println("Current shared installers: " + installers);
                    } catch (NullPointerException e) {
                        System.out.println("Exception deleting a shared installer from a network --> " + e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                content.setActions(close, yes);
                dialogDeleteSharedInstaller.show();

            }

        } catch (NullPointerException e){
            System.out.println("Exception getting scene or group when 'Delete shared installer' ---> "+e);
        }

    }

    /**
     * Make this a merge button instead?
     */
    public void exportNetwork(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try{
            JFXDialogLayout content = new JFXDialogLayout();
            Label txt = new Label("Are you sure you want to export network '" + network.getMeshName() + "'?");
            Label status = new Label();
            content.setBody(txt,status);

            JFXDialog dialogExportNetwork = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
            JFXButton yes = new JFXButton("Yes");
            JFXButton close = new JFXButton("Close");

            //Button action for closing the dialog
            close.setOnAction(event -> dialogExportNetwork.close());

            //Button action for deleting the network
            yes.setOnAction(event -> {
                try {
                    //Saving network as JSON-file locally
                    convertToJson(network);

                    //Inserting file in GDrive folder
                    //TODO: HER!
                    putFileintoGDrive(network);

                    txt.setText("");
                    status.setText("Network exported succsessfully.");
                    yes.setDisable(true);
                } catch (NullPointerException e) {
                    System.out.println("Exception exporting the network --> " + e);
                } catch (IOException e){
                    e.printStackTrace();
                }
            });

            content.setActions(close, yes);
            dialogExportNetwork.show();

        }catch (NullPointerException e){
            System.out.println("Exception exporting the network ---> "+e);
        }

    }
    public void deleteGroup(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {
            //Getting the chosen group to delete
            Groups group = lvGroups.getSelectionModel().getSelectedItem();


            if (!network.getGroups().isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delete group '" + group.getName() + "'?"));

                JFXDialog dialogDeleteGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteGroup.close());

                //Button action for deleting the group
                yes.setOnAction(event -> {
                    try {
                        ArrayList <Groups> groups = network.getGroups();
                        network.getScenesById(group).remove(group);
                        groups.remove(group);

                        lvGroups.getItems().clear();
                        lvGroups.getItems().addAll(groups);
                        dialogDeleteGroup.close();

                        System.out.println("Deleted group: " + group.getName());
                        System.out.println("Current groups: " + network.getGroupsName());
                    } catch (NullPointerException e) {
                        System.out.println("Exception deleting a scene from a group --> " + e);
                    }
                });

                content.setActions(close, yes);
                dialogDeleteGroup.show();

            }

        } catch (NullPointerException e){
            System.out.println("Exception getting scene or group when 'Delete group' ---> "+e);
        }

    }

    /**
     * Removes a char from a string
     * @param text
     * @param place
     * @return
     */
    private String removeChar (String text, int place) {
        String newText = text.substring(0,place) + text.substring(place+1);
        return newText;
    }

    /**
     * Convert the Network object to JSON
     * @param network
     */
    private void convertToJson (Network network) {

        Gson gsonBuilder = new GsonBuilder().create();
        String jsonFromNetwork = gsonBuilder.toJson(network);

        //Fixing small issues with netKeys/appKeys
        String jsonFromNetworkTemp = removeChar(jsonFromNetwork, jsonFromNetwork.indexOf("appKeyss")+7);
        String jsonFromNetworkToWrite = removeChar(jsonFromNetworkTemp, jsonFromNetworkTemp.lastIndexOf("netKeyss")+7);

        String meshName = network.getMeshName().replaceAll("\\s+","");

        String fileName = "src/main/java/JSON_networks/" + meshName;
        Path path = Paths.get(fileName);
        //Creates directory
        try {
            if (!Files.exists(path)) {

                Files.createDirectory(path);
                System.out.println("Directory created");
            } else {

                System.out.println("Directory already exists");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Writes the JSON-file to the directory
        try (FileWriter file = new FileWriter(fileName + "/" + meshName + ".json")) {
            file.write(jsonFromNetworkToWrite);
            System.out.println("Successfully Copied JSON to File...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(jsonFromNetwork);
    }

    /**
     * Creating GDrive folder and uploads network as JSON-file to GDrive
     * Update network only works for owner of file not installers atm
     * @param network
     */

    private void putFileintoGDrive(Network network) throws IOException {

        String meshName = network.getMeshName().replaceAll("\\s+","");
        String appFolderId = "";


            //Checks if "Nordic_Home_AppData"-folder exists
            if (!existenceOfAppFolder()) {
                File fileMetadata = new File();
                fileMetadata.setName("Nordic_Home_AppData");
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                File file = LoginController.getService().files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                System.out.println("Folder ID: " + file.getId());
                appFolderId = file.getId();
            } else {
                appFolderId = this.appFolderId; //Sets existent appdata folder
            }

            //Creating folder for network inside of app data folder and generates network file
            if (getNetFolderId(network).equals("")) {
                File netFileMetadata = new File();
                netFileMetadata.setName(meshName);
                netFileMetadata.setMimeType("application/vnd.google-apps.folder");
                netFileMetadata.setParents(Collections.singletonList(appFolderId));

                File file = LoginController.getService().files().create(netFileMetadata)
                        .setFields("id, parents")
                        .execute();
                System.out.println("Folder ID: " + file.getId());
                this.netFolderId = file.getId();

                //Putting file into folder
                File newFileMetadata = new File();
                newFileMetadata.setName(meshName + ".json");
                newFileMetadata.setParents(Collections.singletonList(netFolderId));
                java.io.File filePath = new java.io.File("src/main/java/JSON_networks/" + meshName + "/" + meshName + ".json");
                FileContent mediaContent = new FileContent("application/json", filePath);
                File newFile = LoginController.getService().files().create(newFileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute();
                System.out.println("File ID: " + newFile.getId());
                this.currentNetId = newFile.getId();
            }
            //If network folder and file already exist
            else {
                FileList result = LoginController.getService().files().list()
                        .setQ("'" + getNetFolderId(network) + "' in parents")
                        .setFields("nextPageToken, files(id, name)") //files(id, name), nextPageToken
                        .execute();
                List<File> files = result.getFiles();
                for (File file : files){
                    if (!file.getName().contains("_")){
                        //Update locally first
                        convertToJson(network);
                        File newFileMetadata = new File();
                        newFileMetadata.setName(meshName + ".json");
                        newFileMetadata.setParents(Collections.singletonList(netFolderId));
                        java.io.File filePath = new java.io.File("src/main/java/JSON_networks/" + meshName + "/" + meshName + ".json");
                        FileContent mediaContent = new FileContent("application/json", filePath);
                        //Deleting the JSON file
                        LoginController.getService().files().delete(file.getId()).execute();
                        //Generates and publish the new one
                        File newFile = LoginController.getService().files().create(newFileMetadata, mediaContent)
                                .setFields("*")
                                .execute();
                        System.out.println("File ID: " + newFile.getId());
                        this.currentNetId = newFile.getId();

                        System.out.println("Network: " + newFile.getName());
                        System.out.println("Updated: " + newFile.getCreatedTime());

                    }
                }

            }
            //TODO: Klarer ikke å generer den nye filen. ----- Feil i filepath!!

    }

    /**
     * Check if there exist an Nordic_Home_AppData-folder and store the GDrive-id if it exist.
     * @return boolean
     * @throws IOException
     */
    private boolean existenceOfAppFolder()throws IOException {
        //Check if "Nordic Home app data"-folder exists in GDrive
        FileList result = LoginController.getService().files().list()
                .setQ("name contains 'Nordic_Home_AppData'")
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            return false;
        } else {
            System.out.println("Should be appfolder name and id:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                this.appFolderId = file.getId();
            }
            return true;
        }
    }

    private boolean existenceOfinstaller(String meshName, String username) throws IOException {
        FileList result = LoginController.getService().files().list()
                .setQ("name contains '"+ meshName + "_" + username + ".json'")
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            return false;
        } else {
            System.out.println("Should be appfolder name and id:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                System.out.println("Exists already"); //Put an alert in the UI maybe?
            }
            return true;
        }
    }

    /**
     * Function for merging installers JSON into the original network JSON
     */
    private void mergInstallersJson(Network network) throws IOException{
        ArrayList<Network> installerNets = new ArrayList<>();
        //Finding the JSON files in network folder
        FileList result = LoginController.getService().files().list()
                .setQ("'" + getNetFolderId(network) + "' in parents")
                .setFields("nextPageToken, files(id, name)") //files(id, name), nextPageToken
                .execute();
        List<File> files = result.getFiles();
        ArrayList<String> fileIds = new ArrayList<>();
        for (File fil : files) {
            //From Json to java object, then merge
            fileIds.add(fil.getId());
        }
        ArrayList<Network>listOfNets = DriveQuickstart.downloadFiles(LoginController.getService(), fileIds);
        convertToJson(mergeObjects(listOfNets)); //Merge and convert to JSON. JSON saved locally.
        putFileintoGDrive(network); //Updates the JSON-file on GDrive
    }


    /**
     * Help function for mergInstallersJson function
     */

    public Network mergeObjects(ArrayList<Network> listOfNets){
        //First generate new Network object
        Network mergedNet = listOfNets.get(0);
        for (Network net : listOfNets) {
            mergedNet.setNodes(net.getNodes());
        }
        return mergedNet;
    }

    //TODO: createFreshNetwork må flyttes ut av DriveQuickStart!
    //TODO: ikke lage nye json-filer om den allerede lå der
    //TODO: Dobler opp med mapper ved opprettelse av installatør-filer!!
    //TODO: Må på et eller annet tidspunkt fikse "delete network"
    //TODO: Fjerne eposter fra JSON og heller sjekke filer fra Nettverksmappe i GDrive
    //TODO: Ikke skanne hele driven ved login. Sjekk etter appdata-mappe.
    //TODO: Add provisioners i JSON-filen
    //TODO: NOE GALT MED NODES!


}