package com.pgoogol.searchservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("com.pgoogol.searchservice.criteria-config")
public class CriteriaConfigProperties {

    private String type;
    private String file;
    private String indexName;

}
