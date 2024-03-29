package com.sheldon.springbootinit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 主类（项目启动入口）
 *
 * @author sheldon<a href="https://github.com/sheldon-3601e"></a>
 * @from <a href="https://github.com/sheldon-3601e">github</a>
 */
@SpringBootApplication
@MapperScan("com.sheldon.springbootinit.mapper")
@EnableTransactionManagement
@EnableScheduling
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
