package com.pgoogol.searchservice;

import com.pgoogol.elasticsearch.data.config.ElkConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = {
	"com.pgoogol.elasticsearch.data.repository",
	"com.pgoogol.searchservice"
})
@Import({
	ElkConfig.class
})
public class SearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchServiceApplication.class, args);
	}

}
