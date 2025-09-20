// src/main/java/vn/iotstar/config/WebConfig.java
package vn.iotstar.config;

import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final StorageProperties props; // storage.location=uploads

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String fileRoot = Paths.get(props.getLocation()).toAbsolutePath().toUri().toString();
    registry.addResourceHandler("/files/**")
            .addResourceLocations(fileRoot);  // vd: file:/D:/project/uploads/
  }
}
