package com.hoangcode.mytool.service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;

import com.hoangcode.mytool.dto.[( ${serviceId} )]OutDto;
import com.hoangcode.mytool.dto.[( ${serviceId} )]InDto;

/**
 * [( ${serviceId} )]Service.java
 * --------------------------------------------------------------------------------------------*
 * History<br/>
 * Version YYYY/MM/DD Author       Content
 * --------------------------------------------------------------------------------------------*
 * 1.0     [(${currentDate})] - [( ${author} )]   Create
 * --------------------------------------------------------------------------------------------*
 */
@Service
public class [( ${serviceId} )]Service extends BaseService implements [( ${serviceId} )]ServiceRemote {
	
	private static final int IN_PARAM_START_IDX = 1;
	private static final int OUT_PARAM_START_IDX_PROC1 = [(${outParamIndex})];
	private static final int PROC1_NAME = [(${procedureName})];
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public [( ${serviceId} )]OutDto procces([( ${serviceId} )]InDto inDto) {
		StoredProcedureQuery procedureQuery = null;
		[( ${serviceId} )]OutDto outDto = null;
		try {
			procedureQuery = entityManager.createStoredProcedureQuery(PROC1_NAME);
			int i = IN_PARAM_START_IDX;
			
[(${registerInParametersString})]
					
[(${registerOutParametersString})]
					
			procedureQuery.execute();
			
			outDto = new [( ${serviceId} )]OutDto();
			
			i = OUT_PARAM_START_IDX_PROC1;
			
[(${copyDbToRetDtoString})]
			
		}finally {
	      if (procedureQuery != null) {
	        try {
	        	procedureQuery.unwrap(StoredProcedureQuery.class);
	        } finally {
	        	procedureQuery = null;
	        }
		   }
		 }
		return outDto;
	}
}