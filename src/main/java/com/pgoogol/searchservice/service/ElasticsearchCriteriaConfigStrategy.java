package com.pgoogol.searchservice.service;

import com.pgoogol.dictionary.client.model.DictionaryConfig;
import com.pgoogol.dictionary.client.model.SearchConfig;
import com.pgoogol.elasticsearch.data.repository.ElasticsearchRepository;
import com.pgoogol.searchservice.config.properties.CriteriaConfigProperties;
import com.pgoogol.searchservice.enums.CriteriaConfigType;
import lombok.SneakyThrows;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.pgoogol.searchservice.enums.CriteriaConfigType.ELASTICSEARCH;

@Service
@RequestScope
public class ElasticsearchCriteriaConfigStrategy extends AbstractCriteriaConfigStrategy {

    private final ElasticsearchRepository repository;

    public ElasticsearchCriteriaConfigStrategy(
            ElasticsearchRepository repository,
            CriteriaConfigProperties criteriaConfigProperties
    ) {
        super(criteriaConfigProperties);
        this.repository = repository;
    }

    @Override
    public CriteriaConfigType getType() {
        return ELASTICSEARCH;
    }

    @Override
    public void prepareCriteriaConfig(String dictionaryCode) {
        Optional<DictionaryConfig> dictionaryConfigOptional = repository.getById(
                criteriaConfigProperties.getIndexName(),
                dictionaryCode,
                Arrays.asList("indexName", "searchConfig"),
                DictionaryConfig.class
        );
        if (dictionaryConfigOptional.isPresent()) {
            DictionaryConfig dictionaryConfig = dictionaryConfigOptional.get();
            indexName = dictionaryConfig.getIndexName();
            prepareCriteriaConfigMap(dictionaryConfig.getSearchConfig());
        } else {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
    }

    @SneakyThrows(ResourceNotFoundException.class)
    @Override
    public List<SearchConfig> getConfigCriteria(@NotBlank String dictionaryCode) {
        if (dictionaryCode.isBlank()) {
            throw new ResourceNotFoundException("dictionaryCode is Blank");
        }
        Optional<DictionaryConfig> dictionaryConfigOptional = repository.getById(
                criteriaConfigProperties.getIndexName(),
                dictionaryCode,
                Arrays.asList("indexName", "searchConfig"),
                DictionaryConfig.class
        );
        if (dictionaryConfigOptional.isPresent()) {
            DictionaryConfig dictionaryConfig = dictionaryConfigOptional.get();
            return dictionaryConfig.getSearchConfig();
        } else {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
    }

}
