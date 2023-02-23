package com.sss.pop.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("auctionLike")
public class alDTO {
    private int alNum;          // 게시글 번호
    private int alItem;      // 물건 번호
    private String alUser;    // 작성자
}
