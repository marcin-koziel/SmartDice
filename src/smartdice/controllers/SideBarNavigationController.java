package smartdice.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Marcin Koziel
 */
public class SideBarNavigationController extends ClassController<SideBarNavigationController> {

    @FXML
    private VBox sideBarNavPane;
    @FXML
    private AnchorPane navBarSideHead;
    @FXML
    private VBox navBarSideItems;
    @FXML
    private AnchorPane navBarSideHome;
    @FXML
    private Rectangle navBarSideSelectedLeftBox;
    @FXML
    private Rectangle navBarSideSelectetBackground;
    @FXML
    private ImageView navBarSideImgHome;
    @FXML
    private ToggleButton navBarSideToggleHome;
    @FXML
    private AnchorPane navBarSideDashboard;
    @FXML
    private ImageView navBarSideImgDashboard;
    @FXML
    private ToggleButton navBarSideToggleDashboard;
    @FXML
    private AnchorPane navBarSideNotifications;
    @FXML
    private ImageView navBarSideImgNotifications;
    @FXML
    private ToggleButton navBarSideToggleNotifications;
    @FXML
    private AnchorPane navBarSideCalendar;
    @FXML
    private ImageView navBarSideImgCalendar;
    @FXML
    private ToggleButton navBarSideToggleCalendar;
    @FXML
    private AnchorPane navBarSideSettings;
    @FXML
    private ImageView navBarSideImgSettings;
    @FXML
    private ToggleButton navBarSideToggleSettings;

    // Used for initializing Side Navigation ( initSideNavBar() )
    private ArrayList<AnchorPane> navBarPaneList;
    private ArrayList<ToggleButton> navBarList;
    private ArrayList<TranslateTransition> ttEnterList;
    private ArrayList<TranslateTransition> ttExitList;
    private ObservableList<AnchorPane> navBarSideObserv;
    private ListView<AnchorPane> navBarSideList;

    @FXML
    SmartDiceController smartDiceController;

    public SideBarNavigationController(){
        super();
    }

    public SideBarNavigationController(String fxmlPath) {
        super(fxmlPath);
    }

    public SideBarNavigationController(String fxmlPath, SmartDiceController smartDiceController) {
        super(fxmlPath);
    }

    /**
     * Initializes the controller class
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        classControllers.put("home", new HomeController("/smartdice/fxml/HomeFXML.fxml"));
        classControllers.put("dashboard", new DashboardController("/smartdice/fxml/DashboardFXML.fxml"));
        classControllers.put("notifications", new NotificationsController("/smartdice/fxml/NotificationsFXML.fxml"));
        classControllers.put("calendar", new CalendarController("/smartdice/fxml/CalendarFXML.fxml"));
        classControllers.put("settings", new SettingsController("/smartdice/fxml/SettingsFXML.fxml"));
        initSideNavBar();
    }

    private void initSideNavBar() {
        SmartDiceController smartDiceController = SmartDiceController.getInstance();

//        SmartDiceController smartDiceController = (SmartDiceController) classControllers.get("smartDice");


        ToggleGroup toggleGroup = new ToggleGroup();

        navBarPaneList = new ArrayList<>();
        navBarList = new ArrayList<>();
        ttEnterList = new ArrayList<>();
        ttExitList = new ArrayList<>();
        navBarSideObserv = FXCollections.observableArrayList();
        navBarSideList = new ListView<>();

        // Add all nav. button nodes to an arraylist
        navBarList.add(navBarSideToggleHome);
        navBarList.add(navBarSideToggleDashboard);
        navBarList.add(navBarSideToggleNotifications);
        navBarList.add(navBarSideToggleCalendar);
        navBarList.add(navBarSideToggleSettings);

        for (ToggleButton toggleSelected : navBarList) {
            AnchorPane toggleParentBox = (AnchorPane) toggleSelected.getParent();
            TranslateTransition ttEnter = new TranslateTransition();
            TranslateTransition ttExit = new TranslateTransition();
            // Add all nav. button parent nodes to an arraylist
            navBarPaneList.add(toggleParentBox);
//            System.out.println(navBarPaneList);
            // Set all ToggleButton in nav to a group
            toggleSelected.setToggleGroup(toggleGroup);

            ttEnterList.add(ttEnter);
            ttExitList.add(ttExit);

            ttEnter.setDuration(new Duration(50));
            ttEnter.setFromX(0);
            ttEnter.setToX(10);

            ttExit.setDuration(new Duration(50));
            ttExit.setFromX(10);
            ttExit.setToX(0);

            toggleParentBox.hoverProperty().addListener(l -> {
                if (toggleParentBox.hoverProperty().getValue().equals(Boolean.TRUE)) {
                    if (ttEnter.getStatus() == Animation.Status.RUNNING) {
                        ttEnter.stop();
                    }
                    ttEnter.setNode(toggleSelected);
                    ttEnter.play();
                } else {
                    if (ttExit.getStatus() == Animation.Status.RUNNING) {
                        ttExit.stop();
                    }
                    ttExit.setNode(toggleSelected);
                    ttExit.play();
                }
            });

            toggleSelected.selectedProperty().addListener(l -> {
                if (toggleParentBox.getChildren().contains(navBarSideSelectedLeftBox) == true) {
                    toggleParentBox.getChildren().remove(navBarSideSelectedLeftBox);
                    toggleParentBox.getChildren().remove(navBarSideSelectetBackground);
                }
                toggleParentBox.getChildren().add(0, navBarSideSelectedLeftBox);
                toggleParentBox.getChildren().add(0, navBarSideSelectetBackground);
            });
        }

        // Toggles on-select
        navBarSideToggleHome.selectedProperty().addListener(l->{
            smartDiceController.setMainPane(getHomePane());
        });
        navBarSideToggleDashboard.selectedProperty().addListener(l->{
            smartDiceController.setMainPane(getDashboardPane());
        });
        navBarSideToggleNotifications.selectedProperty().addListener(l->{
            smartDiceController.setMainPane(getNotificationsPane());
        });
        navBarSideToggleCalendar.selectedProperty().addListener(l->{
            smartDiceController.setMainPane(getCalendarPane());
        });
        navBarSideToggleSettings.selectedProperty().addListener(l->{
            smartDiceController.setMainPane(getSettingsPane());
        });

        // Pane(s)/tabs clicked on in sidebar nav.
        navBarSideHome.setOnMousePressed(l -> {
            navBarSideToggleHome.setSelected(true);
        });
        navBarSideDashboard.setOnMousePressed(l -> {
            navBarSideToggleDashboard.setSelected(true);
        });
        navBarSideNotifications.setOnMousePressed(l -> {
            navBarSideToggleNotifications.setSelected(true);
        });
        navBarSideCalendar.setOnMousePressed(l -> {
            navBarSideToggleCalendar.setSelected(true);
        });
        navBarSideSettings.setOnMousePressed(l -> {
            navBarSideToggleSettings.setSelected(true);
        });

//        // TODO: Check the purpose
//        navBarSideHome.getStyleClass().add("ipane");

        // Set Default selected toggleButton
        navBarSideToggleHome.setSelected(true);

    }

    public HomeController getHomeController() {
        HomeController homeController = (HomeController) classControllers.get("home");
        return homeController.getController();
    }

    public AnchorPane getHomePane() {
        HomeController homeController = (HomeController) classControllers.get("home");
        return (AnchorPane) homeController.getPane();
    }

    public DashboardController getDashboardController() {
        DashboardController dashboardController = (DashboardController) classControllers.get("dashboard");
        return dashboardController.getController();
    }

    public AnchorPane getDashboardPane() {
        DashboardController dashboardController = (DashboardController) classControllers.get("dashboard");
        return (AnchorPane) dashboardController.getPane();
    }

    public NotificationsController getNotificationsController() {
        NotificationsController notificationsController = (NotificationsController) classControllers.get("notifications");
        return notificationsController.getController();
    }

    public AnchorPane getNotificationsPane() {
        NotificationsController notificationsController = (NotificationsController) classControllers.get("notifications");
        return (AnchorPane) notificationsController.getPane();
    }

    public CalendarController getCalendarController() {
        CalendarController calendarController = (CalendarController) classControllers.get("calendar");
        return calendarController.getController();
    }

    public AnchorPane getCalendarPane() {
        CalendarController calendarController = (CalendarController) classControllers.get("calendar");
        return (AnchorPane) calendarController.getPane();
    }

    public SettingsController getSettingsController() {
        SettingsController settingsController = (SettingsController) classControllers.get("settings");
        return settingsController.getController();
    }

    public AnchorPane getSettingsPane() {
        SettingsController settingsController = (SettingsController) classControllers.get("settings");
        return (AnchorPane) settingsController.getPane();
    }



}
