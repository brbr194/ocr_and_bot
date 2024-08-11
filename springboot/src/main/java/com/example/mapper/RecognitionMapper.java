package com.example.mapper;

import com.example.entity.Recognize;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecognitionMapper {

    @Insert("insert into recognize(image_url, recognition_result, remarks) " +
            "VALUES(#{imageUrl},#{recognitionResult}, #{remarks})")
    void insert(Recognize recognize);

    @Select("select * from recognize where remarks like concat('%',#{remarks}, '%') and recognition_result like concat('%',#{recognitionResult}, '%')")
    List<Recognize> selectAll(Recognize recognize);


    @Delete("delete from  recognize  where  id = #{id}")
    void deleteById(Integer id);
}

