package com.sploot.api.dao.find;

import com.sploot.api.model.entity.find.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

}