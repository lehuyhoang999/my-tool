package com.hoangcode.mytool.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.hoangcode.mytool.common.Constants;
import com.hoangcode.mytool.common.exception.HCIOException;
import com.hoangcode.mytool.dto.ColumnDto;
import com.hoangcode.mytool.dto.ConversionRequestDto;
import com.hoangcode.mytool.dto.ConversionResponseDto;
import com.hoangcode.mytool.util.Util;



@Service
public class ConvertService extends BaseService implements ConvertServiceRemote {

  @Autowired
  @Qualifier("codeGenerator")
  private TemplateEngine templateEngine;

  @Override
  public ConversionResponseDto convert(ConversionRequestDto conversion) {
    ConversionResponseDto responseDto = new ConversionResponseDto();

    responseDto.setServiceId(conversion.getServiceId());

    responseDto.setInDto(generateInDto(conversion));
    responseDto.setOutDto(generateOutDto(conversion));
    responseDto.setServiceRemote(generateServiceRemote(conversion));
    responseDto.setService(generateService(conversion));

    return responseDto;
  }

  @Override
  public void createZipFile(final ConversionResponseDto conversion, String outputFile) {

    final String path = "com/hoangcode/tool";
    File file = new File(outputFile);
    String serviceId = conversion.getServiceId();

    Map<String, String> map = new HashMap<>();
    map.put("/dto/" + serviceId + "InDto.java", conversion.getInDto());
    map.put("/dto/" + serviceId + "OutDto.java", conversion.getOutDto());
    map.put("/service/" + serviceId + "Service.java", conversion.getService());
    map.put("/service/" + serviceId + "ServiceRemote.java", conversion.getServiceRemote());

    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
      for (Entry<String, String> entry : map.entrySet()) {
        String filePath = path + entry.getKey();
        String content = entry.getValue();

        ZipEntry zipFile = new ZipEntry(filePath);
        out.putNextEntry(zipFile);
        byte[] data = content.getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
      }
    } catch (IOException ioe) {
      throw new HCIOException(ioe);
    }
  }

  private String generateInDto(ConversionRequestDto conversion) {

    Map<String, Object> modelData = new HashMap<>();
    modelData.put("serviceId", conversion.getServiceId());
    modelData.put("author", conversion.getAuthor());
    modelData.put("currentDate", Util.formatDate(new Date(), Constants.DateFormat.YYYY_MM_DD));
    modelData.put("fields", conversion.getWhereColumns()
        .stream()
        .map(ColumnDto::getFieldDeclaration)
        .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS2)));

    modelData.put("getterSetters", conversion.getWhereColumns()
        .stream()
        .map(ColumnDto::getGetterSetterString)
        .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS2)));

    return generateFile("files/dto/inDto", modelData);
  }

  private String generateOutDto(ConversionRequestDto conversion) {
    Map<String, Object> modelData = new HashMap<>();
    modelData.put("serviceId", conversion.getServiceId());
    modelData.put("author", conversion.getAuthor());
    modelData.put("currentDate", Util.formatDate(new Date(), Constants.DateFormat.YYYY_MM_DD));
    modelData.put("fields", conversion.getSelectColumns()
            .stream()
            .map(ColumnDto::getFieldDeclaration)
            .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS2)));

        modelData.put("getterSetters", conversion.getSelectColumns()
            .stream()
            .map(ColumnDto::getGetterSetterString)
            .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS2)));
    return generateFile("files/dto/outDto", modelData);
  }

  private String generateService(ConversionRequestDto conversion) {

    Map<String, Object> modelData = new HashMap<>();
    modelData.put("serviceId", conversion.getServiceId());
    modelData.put("author", conversion.getAuthor());
    modelData.put("currentDate", Util.formatDate(new Date(), Constants.DateFormat.YYYY_MM_DD));

    modelData.put("outParamIndex", conversion.getSelectColumns().size() + 1);

    modelData.put("procedureName", conversion.getProcedureName());
    
    String registerInParametersString = conversion.getWhereColumns()
        .stream()
        .map(column -> String.format("			procedureQuery.setParameter(i++, inDto.get%s());",
            column.getColumnNamePascal()))
        .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS));
    modelData.put("registerInParametersString", registerInParametersString);

    String registerOutParametersString = conversion.getSelectColumns()
        .stream()
        .map(column -> "			procedureQuery.registerStoredProcedureParameter(i++, String.class, ParameterMode.OUT);")
        .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS));
    modelData.put("registerOutParametersString", registerOutParametersString);

    String copyDbToRetDtoString = conversion.getSelectColumns()
        .stream()
        .map(column -> String.format("			outDto.set%s((String)procedureQuery.getOutputParameterValue(i++));",
            column.getColumnNamePascal()))
        .collect(Collectors.joining(Constants.LINE_SEPARATOR_WINDOWS));
    modelData.put("copyDbToRetDtoString", copyDbToRetDtoString);

    return generateFile("files/service/servicePro", modelData);
  }

  private String generateServiceRemote(ConversionRequestDto conversion) {
    Map<String, Object> modelData = new HashMap<>();
    modelData.put("serviceId", conversion.getServiceId());
    modelData.put("author", conversion.getAuthor());
    modelData.put("currentDate", Util.formatDate(new Date(), Constants.DateFormat.YYYY_MM_DD));

    return generateFile("files/service/serviceProRemote", modelData);
  }
 
  /**
   * Sinh source code từ file template và model data
   */
  private String generateFile(String templateName, Map<String, Object> modelData) {
    Context context = new Context(Locale.getDefault());
    modelData.forEach(context::setVariable);

    return templateEngine.process(templateName, context);
  }
}
