package com.example.service;

import com.example.entity.Recognize;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface RecognitionService {

    public void add(Recognize recognize);

    PageInfo<Recognize> selectPage(Integer pageNum, Integer pageSize, Recognize recognize);

    void deleteById(Integer id);
}
