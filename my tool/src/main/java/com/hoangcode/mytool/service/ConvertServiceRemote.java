package com.hoangcode.mytool.service;

import com.hoangcode.mytool.dto.ConversionRequestDto;
import com.hoangcode.mytool.dto.ConversionResponseDto;

public interface ConvertServiceRemote {

  ConversionResponseDto convert(ConversionRequestDto conversion);

  void createZipFile(ConversionResponseDto conversion, String outputFile);
}
