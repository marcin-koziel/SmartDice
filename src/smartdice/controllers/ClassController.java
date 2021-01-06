package smartdice.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.ResourceBundle;


public abstract class ClassController<T> implements Initializable {

    protected FXMLLoader loader;
    protected String fxmlPath;

    protected final Hashtable<String, Object> classControllers = new Hashtable<>();

    public ClassController() {
        // Left empty
    }

    public ClassController(String fxmlPath){
        this.fxmlPath = fxmlPath;
        this.loader = initFXMLLoader(fxmlPath);
        System.out.println(loader.toString() + " : " + fxmlPath);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private FXMLLoader initFXMLLoader(String path) {
        FXMLLoader loader = new FXMLLoader();
        URL location = getClass().getResource(path);
        loader.setLocation(location);
        try {
            loader.load(loader.getLocation().openStream());
        } catch (IOException e) {
            // TODO: Throw to UI
            e.printStackTrace();
        }
        return loader;
    }

    private Pane getFXMLLoaderPane(FXMLLoader loader) {
        return loader.getRoot();
    }

    private T getFXMLLoaderController(FXMLLoader loader) {
        return loader.getController();
    }

    public T getController(){
        return getFXMLLoaderController(loader);
    }

    public Pane getPane(){
        return getFXMLLoaderPane(loader);
    }

    public void addClassTable(String key, Object value){
        classControllers.put(key, value);
    }

}
