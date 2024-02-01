package com.example.vueBoard.service;

import com.example.vueBoard.domain.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    List<Board> list() throws Exception;
    Board read(Long no) throws Exception;
    void regist(Board board, MultipartFile picture) throws Exception;
    Board modify(Board board, MultipartFile picture) throws Exception;
    void remove(Long itemId) throws Exception;
//    String getPicture(Long itemId) throws Exception;
    Board mapItemToDto(Board board) throws Exception;
    ResponseEntity<byte[]> displayFile(Long no) throws Exception;
    void updateCnt(Board item) throws Exception;
}