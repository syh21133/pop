package com.sss.pop.dao;

import com.sss.pop.dto.itemDTO;
import com.sss.pop.dto.rvDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface rvDAO {
    itemDTO idto(int itemNum);

    int rvWrite(rvDTO review);
}
