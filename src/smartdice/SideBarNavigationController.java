package smartdice;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
public class SideBarNavigationController implements Initializable {

    @FXML
    private VBox sideBarNavMain;
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

    private HomeController homeController;
    private DashboardController dashboardController;
    private NotificationsController notificationsController;
    private CalendarController calendarController;
    private SettingsController settingsController;

    private AnchorPane homePane;
    private AnchorPane dashboardPane;
    private AnchorPane notificationsPane;
    private AnchorPane calendarPane;
    private AnchorPane settingsPane;

    private static SideBarNavigationController instance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        initHome();
        initDashboard();
        initNotifications();
        initCalendar();
        initSettings();
        initSideNavBar();
    }

    public static SideBarNavigationController getInstance(){
        return instance;
    }

    private void initSideNavBar() {
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
            SmartDiceController.getInstance().setMainPane(getHomePane());
        });
        navBarSideToggleDashboard.selectedProperty().addListener(l->{
            SmartDiceController.getInstance().setMainPane(getDashboardPane());
        });
        navBarSideToggleNotifications.selectedProperty().addListener(l->{
            SmartDiceController.getInstance().setMainPane(getNotificationsPane());
        });
        navBarSideToggleCalendar.selectedProperty().addListener(l->{
            SmartDiceController.getInstance().setMainPane(getCalendarPane());
        });
        navBarSideToggleSettings.selectedProperty().addListener(l->{
            SmartDiceController.getInstance().setMainPane(getSettingsPane());
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

        // Set Default selected toggleButton
        navBarSideToggleHome.setSelected(true);
        // TODO: Check the purpose
        navBarSideHome.getStyleClass().add("ipane");

    }

    public FXMLLoader getFXMLLoader(String path) {
        FXMLLoader loader = new FXMLLoader();
        URL location = getClass().getResource(path);
        loader.setLocation(location);
        return loader;
    }

    // TODO: Implement Enum for state of window (ex. Home, Dashboard...etc)
    private void initHome() {
        try {
            FXMLLoader loader = getFXMLLoader("HomeFXML.fxml");
            homePane = loader.load(loader.getLocation().openStream());
            homeController = loader.getController();
        } catch (IOException e) {
            //TODO: Show error window
            e.printStackTrace();
        }
    }

    public AnchorPane getHomePane() {
        return homePane;
    }

    public HomeController getHomeController() {
        return homeController;
    }

    private void initDashboard() {
        try {
            FXMLLoader loader = getFXMLLoader("DashboardFXML.fxml");
            dashboardPane = loader.load(loader.getLocation().openStream());
            dashboardController = loader.getController();
        } catch (IOException e) {
            //TODO: Show error window
            System.out.println(e.getMessage());
        }
    }

    public AnchorPane getDashboardPane() {
        return dashboardPane;
    }

    public DashboardController getDashboardController() {
        return dashboardController;
    }

    private void initNotifications() {
        try {
            FXMLLoader loader = getFXMLLoader("NotificationsFXML.fxml");
            notificationsPane = loader.load(loader.getLocation().openStream());
            notificationsController = loader.getController();
        } catch (IOException e) {
            //TODO: Show error window
            System.out.println(e.getMessage());
        }
    }

    public AnchorPane getNotificationsPane() {
        return notificationsPane;
    }

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    private void initCalendar() {
        try {
            FXMLLoader loader = getFXMLLoader("CalendarFXML.fxml");
            calendarPane = loader.load(loader.getLocation().openStream());
            calendarController = loader.getController();
        } catch (IOException e) {
            //TODO: Show error window
            System.out.println(e.getMessage());
        }
    }

    public AnchorPane getCalendarPane() {
        return calendarPane;
    }

    public CalendarController getCalendarController() {
        return calendarController;
    }

    private void initSettings() {
        try {
            FXMLLoader loader = getFXMLLoader("SettingsFXML.fxml");
            settingsPane = loader.load(loader.getLocation().openStream());
            settingsController = loader.getController();
        } catch (IOException e) {
            //TODO: Show error window
            System.out.println(e.getMessage());
        }
    }

    public AnchorPane getSettingsPane() {
        return settingsPane;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

}
