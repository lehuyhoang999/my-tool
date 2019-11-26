package com.hoangcode.mytool.dto;

import lombok.Data;

@Data
public class ConversionResponseDto {

  private String serviceId;

  private String inDto;
  private String outDto;
  private String service;
  private String serviceRemote;

}
