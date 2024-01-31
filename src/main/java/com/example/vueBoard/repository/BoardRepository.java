package com.example.vueBoard.repository;

import com.example.vueBoard.domain.Board;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUseYn(String useYn, Sort sort);
}
