package com.example.vueBoard.service;

import com.example.vueBoard.domain.Board;

import java.util.List;

public interface BoardService {
    List<Board> list() throws Exception;

    Board read(Long no) throws Exception;

    void regist(Board board) throws Exception;

    void modify(Board board) throws Exception;

    void remove(Long itemId) throws Exception;

    String getPicture(Long itemId) throws Exception;
}
