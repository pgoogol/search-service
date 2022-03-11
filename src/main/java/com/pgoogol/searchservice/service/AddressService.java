package com.pgoogol.searchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.pgoogol.searchservice.config.properties.IndexConfigProperties;
import com.pgoogol.searchservice.model.AddressSearchCriteria;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends BaseService<Object> {

    public AddressService(ElasticsearchClient client, CriteriaConfigService criteriaConfigService, IndexConfigProperties indexConfigProperties) {
        super(client, criteriaConfigService, indexConfigProperties.getAddressIndex(), Object.class);

        //customer_read
        //translations.put("powNazwa", "addresses.city");
        //policy_read
        super.translations.put("powNazwa", "customers.customer.addresses.addressType");
        //translations.put("ulNazwaGlowna", "customers.customer.addresses.streetName");
        //translations.put("ulNazwaGlowna", "customers.customer.id");
        super.translations.put("ulNazwaGlowna", "customers.customer.contacts.contactType");
        super.translations.put("ulNazwaGlown", "id");
        super.translations.put("ulNazwaGlow", "insuranceItems.insuranceItemTraits.code");
        //translations.put("ulNazwaGlowna", "id");
    }
}
