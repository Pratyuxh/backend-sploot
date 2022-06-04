package com.sploot.api.dao;


import com.sploot.api.constant.enums.EmailTemplateType;
import com.sploot.api.model.entity.EmailTemplate;

public interface EmailTemplateRepository extends CommonRepositoryInterface<EmailTemplate, Long> {
    EmailTemplate findByType(EmailTemplateType type);
}
