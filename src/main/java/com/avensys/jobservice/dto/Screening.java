package com.avensys.jobservice.dto;

public class Screening {

	private String technicalScreening;
	private String selectTechnicalScreeningTemplate;
	private String selectRecruitmentScreeningTemplate;
	public String getTechnicalScreening() {
		return technicalScreening;
	}
	public void setTechnicalScreening(String technicalScreening) {
		this.technicalScreening = technicalScreening;
	}
	public String getSelectTechnicalScreeningTemplate() {
		return selectTechnicalScreeningTemplate;
	}
	public void setSelectTechnicalScreeningTemplate(String selectTechnicalScreeningTemplate) {
		this.selectTechnicalScreeningTemplate = selectTechnicalScreeningTemplate;
	}
	public String getSelectRecruitmentScreeningTemplate() {
		return selectRecruitmentScreeningTemplate;
	}
	public void setSelectRecruitmentScreeningTemplate(String selectRecruitmentScreeningTemplate) {
		this.selectRecruitmentScreeningTemplate = selectRecruitmentScreeningTemplate;
	}
}
