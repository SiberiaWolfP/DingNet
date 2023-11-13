package webserver;

import gui.MainGUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WebserverApplication {

	public static void main(String[] args) {
//        System.setProperty("java.awt.headless", "true");
//		args= new String[]{"basic_graph.xml", "Signal-based", "ReliableEfficient", "5"};
//        for (String arg : args) {
//            System.out.println(arg);
//        }
        MainGUI.AppMain(args);
		SpringApplication.run(WebserverApplication.class, args);
	}

}
