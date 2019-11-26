package com.hoangcode.mytool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoangcode.mytool.common.Constants;
import com.hoangcode.mytool.common.Constants.DateFormat;
import com.hoangcode.mytool.common.Constants.Session;
import com.hoangcode.mytool.common.exception.HCIOException;
import com.hoangcode.mytool.dto.ConversionRequestDto;
import com.hoangcode.mytool.dto.ConversionResponseDto;
import com.hoangcode.mytool.dto.DownloadDto;
import com.hoangcode.mytool.dto.ResponseDto;
import com.hoangcode.mytool.service.ConvertServiceRemote;
import com.hoangcode.mytool.util.SessionUtil;
import com.hoangcode.mytool.util.Util;


@Controller
public class HomeController extends BaseController{

	@Autowired
	private Environment environment;

	@Autowired
	private ConvertServiceRemote convertService;
	
	@GetMapping(path = {"", "/", "/home"})
	public String index(Model model) {
		
		return "home/index";
	}
	
	@PostMapping("convert")
	@ResponseBody
	public ResponseDto convert(@RequestBody ConversionRequestDto conversion) {
		ConversionResponseDto responseDto = convertService.convert(conversion);
		
		String filename = String.format("%s_%s.zip", conversion.getServiceId(),
		        Util.formatDate(new Date(), DateFormat.YYYYMMDD_HHMMSS));
		
		Path path = Paths.get(environment.getProperty("application.path.download"),
		        conversion.getAuthor());
		
		String zipFilePath = Paths.get(path.toString(), filename).toString();
		
		if (!Files.exists(path)) {
	      try {
	        Files.createDirectories(path);
	      } catch (IOException e) {
	        throw new HCIOException(e);
	      }
	    }
		
		convertService.createZipFile(responseDto, zipFilePath);
		
		DownloadDto downloadDto = new DownloadDto();
	    downloadDto.setFullPath(zipFilePath);
	    downloadDto.setFileName(filename);
	    
	    SessionUtil.set(Session.DOWNLOAD_FILE, downloadDto);
	    
		return ResponseDto.success(downloadDto.getId());
	}
	
	@GetMapping(value = "download", produces = Constants.APPLICATION_ZIP)
	public void download(String downloadId, HttpServletResponse response) {
	
		DownloadDto downloadDto = SessionUtil.get(Session.DOWNLOAD_FILE);
		
		if (downloadDto == null || !downloadDto.getId().equals(downloadId)) {
	      return;
		}

		File file = new File(downloadDto.getFullPath());
	    if (!file.exists()) {
	      return;
	    }
	    
	    try (InputStream inputStream = new FileInputStream(file)) {
	      response.setContentType(Constants.APPLICATION_ZIP);
	      response.setHeader("Content-Disposition", "attachment;filename=" + downloadDto.getFileName());
	      response.setHeader("Content-Length", String.valueOf(file.length()));

	      // copy it to response's OutputStream
	      FileCopyUtils.copy(inputStream, response.getOutputStream());
	      response.flushBuffer();

	      SessionUtil.delete(Session.DOWNLOAD_FILE);
	      Files.deleteIfExists(Paths.get(downloadDto.getFullPath()));
	      
	    } catch (IOException ex) {
	      throw new HCIOException(ex);
	    }
	}
}
