package com.example.service.impl;

import com.example.entity.Recognize;
import com.example.mapper.RecognitionMapper;
import com.example.service.RecognitionService;
import jakarta.annotation.Resource;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecognitionServiceImpl implements RecognitionService {

    @Resource
    private RecognitionMapper recognitionMapper;

    @Override
    public void add(Recognize recognize) {
        recognitionMapper.insert(recognize);
    }

    @Override
    public PageInfo<Recognize> selectPage(Integer pageNum, Integer pageSize, Recognize recognize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Recognize> recognizes = recognitionMapper.selectAll(recognize);
        return PageInfo.of(recognizes);
    }

    @Override
    public void deleteById(Integer id) {
        recognitionMapper.deleteById(id);
    }
}
