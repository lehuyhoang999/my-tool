package com.hoangcode.mytool.dto;

import java.util.List;
import lombok.Data;

@Data
public class ConversionRequestDto {

  private String serviceId;
  private String procedureName; // STORE PROC NAME
  private String author;
  private List<ColumnDto> selectColumns;
  private List<ColumnDto> whereColumns;

}
