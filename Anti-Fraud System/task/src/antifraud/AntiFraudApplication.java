package antifraud;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;

@SpringBootApplication
public class AntiFraudApplication {
    //to push to git
    public static void main(String[] args) {
        SpringApplication.run(AntiFraudApplication.class, args);
    }
}