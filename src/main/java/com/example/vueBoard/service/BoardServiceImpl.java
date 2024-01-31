package com.example.vueBoard.service;

import com.example.vueBoard.domain.Board;
import com.example.vueBoard.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository repository;

    @Autowired
    public BoardServiceImpl(BoardRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Board> list() throws Exception {
        return this.repository.findAll(Sort.by(Sort.Direction.DESC, new String[]{"no"}));
    }
    @Transactional(readOnly = true)
    public Board read(Long no) throws Exception {
        return (Board)this.repository.getOne(no);
    }

    @Transactional
    public void regist(Board board) throws Exception {
        this.repository.save(board);
    }

    @Transactional
    public void modify(Board board) throws Exception {
        Board itemEntity = (Board)this.repository.getOne(board.getNo());
        itemEntity.setTitle(board.getTitle());
        itemEntity.setContent(board.getContent());
        itemEntity.setPictureUrl(board.getPictureUrl());
    }

    @Transactional
    public void remove(Long itemId) throws Exception {
        this.repository.deleteById(itemId);
    }

    public String getPicture(Long no) throws Exception {
        Board item = (Board)this.repository.getOne(no);
        return item.getPictureUrl();
    }
}
