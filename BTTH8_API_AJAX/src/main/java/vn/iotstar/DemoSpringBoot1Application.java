package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import vn.iotstar.config.StorageProperties;
import vn.iotstar.service.IStorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DemoSpringBoot1Application {

  public static void main(String[] args) {
    SpringApplication.run(DemoSpringBoot1Application.class, args);
  }

  // Khởi tạo thư mục storage khi app chạy
  @Bean
  CommandLineRunner initStorage(IStorageService storageService) {
    return args -> storageService.init();
  }
}
