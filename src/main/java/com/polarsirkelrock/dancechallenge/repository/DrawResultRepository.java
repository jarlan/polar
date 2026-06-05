package com.polarsirkelrock.dancechallenge.repository;

import com.polarsirkelrock.dancechallenge.entity.DrawResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DrawResultRepository extends JpaRepository<DrawResult, Long> {
    List<DrawResult> findAllByOrderByDrawnAtDesc();
}
