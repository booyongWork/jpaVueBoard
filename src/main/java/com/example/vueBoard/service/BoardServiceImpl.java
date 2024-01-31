package com.example.vueBoard.service;

import com.example.vueBoard.domain.Board;
import com.example.vueBoard.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BoardServiceImpl implements BoardService {
    @Autowired
    private BoardRepository repository;

    @Transactional(readOnly = true)
    public List<Board> list() throws Exception {
        return this.repository.findByUseYn("Y", Sort.by(Sort.Direction.DESC, "no"));
    }
    @Transactional(readOnly = true)
    public Board read(Long no) throws Exception {
        return (Board)this.repository.getOne(no);
    }

    @Transactional
    public void regist(Board board) throws Exception {
        this.repository.save(board);
    }
    public Board mapItemToDto(Board board) {
        Board boardDto = new Board();
        boardDto.setNo(board.getNo());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setRegDate(board.getRegDate());
        boardDto.setCnt(board.getCnt());
        boardDto.setWriter(board.getWriter());
        boardDto.setUseYn(board.getUseYn());
        boardDto.setPicture(board.getPicture());
        boardDto.setPictureUrl(board.getPictureUrl());
        return boardDto;
    }
    @Transactional
    public void modify(Board board) throws Exception {
        Board itemEntity = (Board)this.repository.getOne(board.getNo());
        itemEntity.setTitle(board.getTitle());
        itemEntity.setContent(board.getContent());
        itemEntity.setPictureUrl(board.getPictureUrl());
    }

    @Transactional
    public void remove(Long no) throws Exception {
        Optional<Board> optionalBoard = this.repository.findById(no);

        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setUseYn("N");
            this.repository.save(board);
        }
    }

    public String getPicture(Long no) throws Exception {
        Board item = (Board)this.repository.getOne(no);
        return item.getPictureUrl();
    }
}
