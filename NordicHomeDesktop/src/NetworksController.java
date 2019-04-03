import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import java.util.Set;
import java.util.ArrayList;
import javafx.util.Callback;


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
    private JFXListView lvGroups;

    @FXML
    private JFXListView lvParentGroups;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane apNetworkInfo;

    @FXML
    private JFXListView lvScenes;


    //List with all networks
    ArrayList<Network> networks = new ArrayList<Network>();


    public Network networkChosen = null;
    public Object chosenGroup = null;


    @FXML
    private void initialize() {
        showNetworks();
        showChosenNetwork();
    }

    /**
     * newNetwork: Method for adding a new network to the networks list*/
    public void newNetwork(String name, String address){
        networks.add(new Network(name, address));
    }

    /**
     * showNetworks: Method for showing the existing networks in the listview
     * @author Julie
     * */
    public void showNetworks(){
        try{
            //Put all the networks in the network list view
            lwNetworkList.getItems().addAll(networks);

            //Custom cell factory for showing the name of the network in the network list view
            lwNetworkList.setCellFactory(param -> new JFXListCell<Network>() {
                protected void updateItem(Network item, boolean empty){
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
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
        JFXTextField addressName = new JFXTextField();

        grid.add(new Label("Network name:"), 0, 0);
        grid.add(networkName, 0, 1);
        grid.add(new Label("Network address:"), 0, 3);
        grid.add(addressName, 0, 4);

        content.setBody(grid);

        JFXDialog dialogNewGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
        JFXButton close = new JFXButton("Close");
        JFXButton add = new JFXButton("Add");
        add.setDefaultButton(true);

        close.setOnAction(event -> dialogNewGroup.close());

        add.setOnAction(event -> {
            try{
                if(!networkName.getText().isEmpty() && !addressName.getText().isEmpty() ){
                    String name = networkName.getText();
                    String address = addressName.getText();

                    //Adding the new network to the list of networks
                    newNetwork(name,address);

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
    public void showChosenNetwork() {
        networkChosen = lwNetworkList.getSelectionModel().getSelectedItem();

        lwNetworkList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Network>) (observable, oldValue, newValue) -> showNetworkInfo(newValue));
    }


    /**
     * getNetworkInfo: Method for getting and showing the roght info of the chosen network
     * @author Julie
     * */
    public void showNetworkInfo(Network network){
        apNetworkInfo.setVisible(true);
        btnDeleteScene.setDisable(true);
        btnDeleteNetwork.setDisable(false);
        lvGroups.getItems().clear();

        System.out.println("Nettverk: "+networks);

        try {
            String name = network.getName();
            String address = network.getAddress();
            Set<String> groups = network.getGroups();
            Set<String> parentGroups = (Set<String>) network.getParentGroups();

            lblNetworkName.setText(name);
            lblNetworkAddress.setText(address);
            lvGroups.getItems().addAll(groups);
            lvParentGroups.getItems().addAll(parentGroups);

            showScenesForChosenGroup(network);
        }
        catch (NullPointerException e){
            System.out.println("Exception loading the info for the network --> "+ e);
        }
    }


    /**
     * showScenesForChosenGroup: Method for showing the right scenes for the different groups
     * */
    public void showScenesForChosenGroup(Network network) {
        chosenGroup = lvGroups.getSelectionModel().getSelectedItem();

        lvGroups.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> showScenes(network, newValue));
    }

    /**
     * showScenes: Help method for showScenesForChosenGroup(Network network)
     * */
    public void showScenes(Network network, Object group){
        lvScenes.getItems().clear();
        btnDeleteScene.setDisable(false);

        try{
            String groupChosen = group.toString();
            ArrayList<String> scenes = network.getFullGroups().get(groupChosen);
            lvScenes.getItems().addAll(scenes);
            System.out.println("Scener for gruppe: "+groupChosen+"\n-"+scenes);
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
            String group = (String) lvGroups.getSelectionModel().getSelectedItem();
            String scene = (String) lvScenes.getSelectionModel().getSelectedItem();


            if (!scene.isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delet scene '" + scene + "' from group '" + group + "'?"));

                JFXDialog dialogDeleteScene = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteScene.close());

                //Button action for deleting the scene from the group
                yes.setOnAction(event -> {
                    try {
                        ArrayList<String> scenes = network.getFullGroups().get(group);
                        scenes.remove(scene);
                        lvScenes.getItems().clear();
                        lvScenes.getItems().addAll(scenes);
                        dialogDeleteScene.close();

                        System.out.println("Deleted scene: " + scene);
                        System.out.println("Scener nå: " + network.getFullGroups().get(group));
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
        content.setHeading(new Text("New group:"));

        JFXTextField group = new JFXTextField();
        Label status = new Label();

        //Create a grid layout for the dialog
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0, 0, 0, 0));

        grid.add(group,0,0,2,1);
        grid.add(status,0,1,1,1);
        group.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        content.setBody(grid);

        JFXDialog dialogNewGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
        JFXButton add = new JFXButton("Add");
        JFXButton close = new JFXButton("Close");
        add.setDefaultButton(true);

        //Button for closing the dialog
        close.setOnAction(event -> dialogNewGroup.close());


        //Button for adding the new group to the network
        add.setOnAction(event -> {
            try{
                if (!group.getText().isEmpty() && !groupAlreadyExists(group.getText())){
                    String newGroup = group.getText();
                    Network network = lwNetworkList.getSelectionModel().getSelectedItem();

                    //Sjekk
                    System.out.println("Gruppe: "+newGroup);
                    System.out.println("Nettverk: "+ network);
                    System.out.println("Groups:"+network.getGroups());

                    //Add the new group to the network
                    network.addGroup(newGroup);

                    //Update the list view
                    updateGroupListView(network);

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

    //Help method to find out if the group already exists
    public boolean groupAlreadyExists(String group){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();
        if (!network.getGroups().contains(group)){
            return false;
        }
        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(new Label("The group already exists."));
        JFXDialog dialog= new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
        JFXButton close = new JFXButton("Close");
        close.setOnAction(event -> {
            dialog.close();
        });
        content.setActions(close);
        dialog.show();
        return true;
    }


    /**
     * deleteNetwork: Method for deleting a network
     * @author Julie
     * */
    public void deleteNetwork(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {

            if (!networks.isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delet network '"+network.getName()+"'?"));

                JFXDialog dialogDeleteScene = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteScene.close());

                //Button action for deleting the network
                yes.setOnAction(event -> {
                    try {
                        networks.remove(network);
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

    public void exportNetwork(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try{
            JFXDialogLayout content = new JFXDialogLayout();
            Label txt = new Label("Are you sure you want to export network '"+network.getName()+"'?");
            Label status = new Label();
            content.setBody(txt,status);

            JFXDialog dialogDeleteScene = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
            JFXButton yes = new JFXButton("Yes");
            JFXButton close = new JFXButton("Close");

            //Button action for closing the dialog
            close.setOnAction(event -> dialogDeleteScene.close());

            //Button action for deleting the network
            yes.setOnAction(event -> {
                try {
                    
                    //Her er det mye som mangler, her skal funksjon/funksjoner for å generere JSON-fila

                    //txt.setText("");
                    //status.setText("Network exported succsessfully.");
                    yes.setDisable(true);
                } catch (NullPointerException e) {
                    System.out.println("Exception exporting the network --> " + e);
                }
            });

            content.setActions(close, yes);
            dialogDeleteScene.show();

        }catch (NullPointerException e){
            System.out.println("Exception exporting the network ---> "+e);
        }

    }

    /**
     * deleteNetwork: Method for deleting a group from a network
     * @author Julie
     * */
    public void deleteGroup(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {
            //Getting the chosen group to delete
            String group = (String) lvGroups.getSelectionModel().getSelectedItem();


            if (!group.isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delet group '"+ group + "'?"));

                JFXDialog dialogDeleteGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteGroup.close());

                //Button action for deleting the group
                yes.setOnAction(event -> {
                    try {
                        //Deleting the group from the network
                        Set<String> groups = network.getGroups();
                        groups.remove(group);

                        //Updating the listview
                        lvGroups.getItems().clear();
                        lvGroups.getItems().addAll(groups);
                        dialogDeleteGroup.close();

                        System.out.println("Deleted gruppe: " + group);
                        System.out.println("Grupper nå: " + network.getGroups());
                    } catch (NullPointerException e) {
                        System.out.println("Exception deleting a group --> " + e);
                    }
                });

                content.setActions(close, yes);
                dialogDeleteGroup.show();

            }

        } catch (NullPointerException e){
            System.out.println("Exception getting group when 'Delete group' ---> "+e);
        }

    }


    /**
     * createParentGroup: Method for creating a new parent group
     * */
    public void createParentGroup(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {
            //Create a JFXDialog
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("New parent group: "));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 10, 10, 10));

            JFXTextField parentGroupName = new JFXTextField();
            JFXTextField groups = new JFXTextField();

            grid.add(new Label("Parent group name:"), 0, 0);
            grid.add(parentGroupName, 0, 1);
            grid.add(new Label("Child groups (group names separated with comma):"), 0, 3);
            grid.add(groups, 0, 4);

            content.setBody(grid);


            JFXDialog dialogCreateParent = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
            JFXButton add = new JFXButton("Add");
            JFXButton close = new JFXButton("Close");
            add.setDefaultButton(true);

            //Button action for closing the dialog
            close.setOnAction(event -> dialogCreateParent.close());

            //Button action for creating the new parent group
            add.setOnAction(event -> {
                try {
                    if(!parentGroupName.getText().isEmpty() && !groups.getText().isEmpty() && multipleGroups(groups.getText()) && knownGroups(groups.getText()) ) {
                        //Getting the groups for the text field to add to the parent group
                        ArrayList<String> resultGroups = findGroupsToParent(groups.getText());
                        String parentName = parentGroupName.getText();

                        //Adding new parent group to the network
                        network.addParentGroup(parentName, resultGroups);

                        //Updating the listview
                        lvParentGroups.getItems().clear();
                        lvParentGroups.getItems().addAll(network.getParentGroups());

                        dialogCreateParent.close();
                    }
                } catch (NullPointerException e) {
                    System.out.println("Exception when creating a parent group --> " + e);
                }
            });

                content.setActions(close, add);
                dialogCreateParent.show();


        } catch (NullPointerException e){
            System.out.println("Exception getting group when 'Create parent group' ---> "+e);
        }

    }

    //Help method for finding the groups to add to the parent group
    public ArrayList<String> findGroupsToParent(String groupString){
        String [] groups = groupString.split(", ");
        ArrayList<String> groupResult = new ArrayList<>();

        for (String group: groups) {
            groupResult.add(group);
        }

        return groupResult;
    }

    //Help method for checking that all groups listed exists
    public boolean knownGroups(String groupString){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();
        String [] groups = groupString.split(", ");

        for(String group: groups){
            if(!network.getGroups().contains(group)){
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("There are one or more groups that does not exist."));

                JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialog.close());
                content.setActions(close);
                dialog.show();
                return false;
            }
        }
        return true;
    }

    //Help method for checking that more than one group are listed
    public boolean multipleGroups(String groupString){
        String [] groups = groupString.split(",");

        if (groups.length <= 1){
            //Dialog
            JFXDialogLayout content = new JFXDialogLayout();
            content.setBody(new Label("A parent group has to consist of more than one group."));

            JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
            JFXButton close = new JFXButton("Close");

            //Button action for closing the dialog
            close.setOnAction(event -> dialog.close());
            content.setActions(close);
            dialog.show();
            return false;
        }

        return true;
    }

    /**
     * deleteParentGroup: Method for deleting a parent group
     * */
    public void deleteParentGroup(){
        Network network = lwNetworkList.getSelectionModel().getSelectedItem();

        try {
            //Getting the chosen group to delete
            String parentGroup = (String) lvParentGroups.getSelectionModel().getSelectedItem();


            if (!parentGroup.isEmpty()) {
                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(new Label("Are you sure you want to delet group '"+ parentGroup + "'?"));

                JFXDialog dialogDeleteParentGroup = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER, true);
                JFXButton yes = new JFXButton("Yes");
                JFXButton close = new JFXButton("Close");

                //Button action for closing the dialog
                close.setOnAction(event -> dialogDeleteParentGroup.close());

                //Button action for deleting the group
                yes.setOnAction(event -> {
                    try {
                        //Delete from the networks parent groups
                        Set<String> parentGroups = network.getParentGroups();
                        parentGroups.remove(parentGroup);

                        //Delete from listview
                        lvParentGroups.getItems().clear();
                        lvParentGroups.getItems().addAll(parentGroups);
                        dialogDeleteParentGroup.close();

                    } catch (NullPointerException e) {
                        System.out.println("Exception deleting a parent group --> " + e);
                    }
                });

                content.setActions(close, yes);
                dialogDeleteParentGroup.show();

            }

        } catch (NullPointerException e){
            System.out.println("Exception getting group when 'Delete parent group' ---> "+e);
        }

    }

}