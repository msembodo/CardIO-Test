package com.idemia.tec.jkt.cardiotest;

import com.idemia.tec.jkt.cardiotest.controller.CardiotestController;
import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class CardiotestApplication extends Application {

	private ConfigurableApplicationContext springContext;

	private BorderPane rootLayout;
	private Stage primaryStage;

	private ObservableList<AdvSaveVariable> advSaveVariables = FXCollections.observableArrayList();
	private ObservableList<VariableMapping> mappings = FXCollections.observableArrayList();

	@Autowired
	private RootLayoutController root;

	public CardiotestApplication() {}

	public static void main(String[] args) {
		launch(CardiotestApplication.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BasicConfigurator.configure();
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("CardIO Test");

		initRootLayout();
		showCardioTest();
	}

	public ObservableList<AdvSaveVariable> getAdvSaveVariables() {
		return advSaveVariables;
	}

	public ObservableList<VariableMapping> getMappings() {
		return mappings;
	}

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(CardiotestApplication.class);
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RootLayout.fxml"));
			loader.setControllerFactory(springContext::getBean);
			rootLayout = loader.load();

			// give controller access to main application
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);

			Scene scene = new Scene(rootLayout);

			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showCardioTest() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Cardiotest.fxml"));
			loader.setControllerFactory(springContext::getBean);
			AnchorPane cardiotest = loader.load();

			rootLayout.setCenter(cardiotest);

			// give controller access to main application
			CardiotestController controller = loader.getController();
			controller.setMainApp(this);

			controller.setObservableList();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

}
