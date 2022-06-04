package com.sploot.api.dao;

import com.sploot.api.model.entity.EmailSentRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailSentRecordRepository extends CommonRepositoryInterface<EmailSentRecord, Long> {
}
