package com.sploot.api.dao;


import com.sploot.api.constant.enums.CMSEntryType;
import com.sploot.api.model.entity.CMSEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CMSRepository extends JpaRepository<CMSEntry, Long> {
    List<CMSEntry> findByType(CMSEntryType type);

    List<CMSEntry> findByTypeAndStatus(CMSEntryType type, Boolean status);

    @Query("Select c from CMSEntry c where c.type in :typeList and c.status=1 order by c.dateCreated desc")
    List<CMSEntry> findByTypeList(@Param("typeList") List<CMSEntryType> typeList);

    List<CMSEntry> findByTypeInAndStatus(List<CMSEntryType> types, Boolean status);

    CMSEntry findByIdAndType(long cmsDashboardId, CMSEntryType type);
}
