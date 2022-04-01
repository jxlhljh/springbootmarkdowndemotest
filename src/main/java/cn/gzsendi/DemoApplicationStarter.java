package cn.gzsendi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//测试地址：http://localhost:8080/articleList.html
@SpringBootApplication
@MapperScan(basePackages = "cn.gzsendi.**.mapper")
public class DemoApplicationStarter {

	public static void main(String[] args) {
		
		SpringApplication.run(DemoApplicationStarter.class, args);
		
	}

}
