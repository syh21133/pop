package com.sss.pop.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Alias("itemComments")
public class icDTO {
    private int icNum;          // 게시글 번호
    private int icItemNum;      // 물건 번호
    private String icWriter;    // 작성자
    private Date icDate;        // 날짜


    private String icmtDate;      // 댓글날짜

    private String icContent;   // 내용
}
