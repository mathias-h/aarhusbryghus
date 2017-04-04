package gui;

import javax.security.sasl.AuthenticationException;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.Service;

public class Login extends GridPane {
	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final TextField tfUsername = new TextField();
	private final TextField tfPassword = new PasswordField();
	private final Label lError = new Label();
	private final Handler<?> loginHandler;

	public Login(Handler<?> loginHandler) {
		this.loginHandler = loginHandler;

		setHgap(10);
		setVgap(10);
		setAlignment(Pos.CENTER);

		// automatisk login mens vi udvikler
		tfUsername.setText("test");
		tfPassword.setText("test");
		controller.login();

		add(new Label("Brugernavn"), 0, 0);
		add(tfUsername, 1, 0);

		add(new Label("Kodeord"), 0, 1);
		add(tfPassword, 1, 1);

		Button bLogin = new Button("Login");
		bLogin.setOnAction(e -> controller.login());
		GridPane.setHalignment(bLogin, HPos.RIGHT);
		add(bLogin, 1, 2);

		lError.setStyle("-fx-text-fill: red");
		add(lError, 0, 2, 2, 1);
	}

	class Controller {
		public void login() {
			String username = tfUsername.getText().trim();
			String password = tfPassword.getText().trim();

			try {
				service.login(username, password);

				if (loginHandler != null)
					loginHandler.exec(null);
			} catch (AuthenticationException e) {
				lError.setText("Brugernavn eller kodeord er forkert");
			}
		}
	}
}
