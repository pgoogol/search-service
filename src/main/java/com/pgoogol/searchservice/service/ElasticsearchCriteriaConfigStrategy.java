package com.pgoogol.searchservice.service;

import com.pgoogol.elasticsearch.data.repository.ElasticsearchRepository;
import com.pgoogol.searchservice.config.properties.CriteriaConfigProperties;
import com.pgoogol.searchservice.enums.CriteriaConfigType;
import com.pgoogol.searchservice.exception.ResourceNotFoundException;
import com.pgoogol.searchservice.model.dictionary.model.DictionaryConfig;
import com.pgoogol.searchservice.model.dictionary.model.SearchConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.pgoogol.searchservice.enums.CriteriaConfigType.ELASTICSEARCH;
import static com.pgoogol.searchservice.model.dictionary.enums.Fields.INDEX_NAME;
import static com.pgoogol.searchservice.model.dictionary.enums.Fields.SEARCH_CONFIG;

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
            DictionaryConfig dictionaryConfig = getDictionaryConfig(dictionaryCode);
            indexName = dictionaryConfig.getIndexName();
            prepareCriteriaConfigMap(dictionaryConfig.getSearchConfig());
    }

    @SneakyThrows(ResourceNotFoundException.class)
    @Override
    public List<SearchConfig> getConfigCriteria(String dictionaryCode) {
        DictionaryConfig dictionaryConfig = getDictionaryConfig(dictionaryCode);
        return dictionaryConfig.getSearchConfig();
    }

    private DictionaryConfig getDictionaryConfig(String dictionaryCode) {
        if (dictionaryCode.isBlank()) {
            throw new ResourceNotFoundException("DictionaryCode is Blank");
        }
        Optional<DictionaryConfig> dictionaryConfigOptional = repository.getById(
                criteriaConfigProperties.getIndexName(),
                dictionaryCode,
                Arrays.asList(INDEX_NAME.getName(), SEARCH_CONFIG.getName()),
                DictionaryConfig.class
        );
        if (dictionaryConfigOptional.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
        return dictionaryConfigOptional.get();
    }

}
