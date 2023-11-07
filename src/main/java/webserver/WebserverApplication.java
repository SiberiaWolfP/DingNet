package webserver;

import gui.MainGUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WebserverApplication {

	public static void main(String[] args) {
        MainGUI.AppMain(args);
		SpringApplication.run(WebserverApplication.class, args);
	}

}
