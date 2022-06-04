package com.sploot.api.service;

import com.sploot.api.constant.enums.EmailTemplateType;
import com.sploot.api.dao.EmailSentRecordRepository;
import com.sploot.api.dao.EmailTemplateRepository;
import com.sploot.api.model.dto.EmailSubjectAndBodyDto;
import com.sploot.api.model.entity.EmailSentRecord;
import com.sploot.api.model.entity.EmailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

import static com.sploot.api.util.Utility.getTextAfterFillingParameters;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Autowired
	EmailSentRecordRepository emailSentRecordRepository;
	@Autowired
	EmailTemplateRepository emailTemplateDao;
	@Value("${send.email}")
	private Boolean emailToBeSent;

	@Autowired
	JavaMailSender emailSender;

	@Value("${sender.email}")
	private String senderEmail;
	@Value("${sender.alias}")
	private String senderAlias;

	//TODO ... have to save the sent email in case the isEMailToBeSaved param is true
	@Override
//	@Transactional
	public void sendEmail(String email, String emailSubject, String emailBody, Boolean isEmailToBeSaved) throws Exception {
//		JavaMailSender emailSender = mailDispatcher.getJavaMailSender();

		MimeMessage message = emailSender.createMimeMessage();
		String encodingOptions = "text/html; charset=UTF-8";
		message.setHeader("Content-Type", encodingOptions);
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		message.setSubject(emailSubject, "UTF-8");
		message.setContent(emailBody, "text/html; charset=utf-8");
//        helper.setText(emailBody);
		helper.setFrom(senderEmail, senderAlias);
		helper.setTo(email);
		emailSender.send(message);
	}


	@Override
	public Boolean sendEmailWithAttachment(File file, String email, String subject, String emailBody, boolean isEmailToBeSaved, EmailTemplateType templateType) throws Exception {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			String encodingOptions = "text/html; charset=UTF-8";
			message.setHeader("Content-Type", encodingOptions);
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			message.setSubject(subject, "UTF-8");
			helper.setText(emailBody, true);
			helper.setFrom(senderEmail, senderAlias);
			if (email.contains(","))
				helper.setTo(email.split(","));
			else
				helper.setTo(email);
			helper.addAttachment(file.getName(), file);
			EmailSentRecord emailSentRecord = getEmailSentRecord(senderEmail, subject, emailBody, email);
			try {
				emailSender.send(message);
			} catch (Exception e) {
				log.error("Error in sending email: " + e);
				saveEmailSentRecord(emailSentRecord, false);
				return false;
			}
			if (isEmailToBeSaved) {
				saveEmailSentRecord(emailSentRecord, true);
			}
		} catch (Exception ex) {
			log.error("Error in sending email with attachment and saving email : " + ex);
			return false;
		}
		return true;
	}

	@Override
	public EmailSubjectAndBodyDto fetchEmailSubjectAndBody(EmailTemplateType type, Map<String, String> emailTemplateBodyFieldsValue, Map<String, String> emailTemplateSubjectFieldsValue) {
		EmailTemplate emailTemplate = emailTemplateDao.findByType(type);//should be there else exception thrown
		String emailSubject = emailTemplate.getTemplateEmailSubject();
		String emailBody = emailTemplate.getTemplateText();
		for (String subjectKey : emailTemplateSubjectFieldsValue.keySet()) {
			emailSubject = getTextAfterFillingParameters(emailSubject, subjectKey, emailTemplateSubjectFieldsValue.get(subjectKey));
		}
		for (String bodyKey : emailTemplateBodyFieldsValue.keySet()) {
			emailBody = getTextAfterFillingParameters(emailBody, bodyKey, emailTemplateBodyFieldsValue.get(bodyKey));
		}
		return new EmailSubjectAndBodyDto(emailSubject, emailBody);
	}

	private void saveEmailSentRecord(EmailSentRecord emailSentRecord, boolean isEmailSent) {
		emailSentRecord.setSent(isEmailSent);
		emailSentRecordRepository.save(emailSentRecord);
	}

	private EmailSentRecord getEmailSentRecord(String senderEmail, String subject, String emailBody, String recepientEmail) {
		EmailSentRecord emailSentRecord = new EmailSentRecord();
		emailSentRecord.setText(emailBody);
		emailSentRecord.setTarget(recepientEmail);
		emailSentRecord.setSource(senderEmail);
		emailSentRecord.setSubject(subject);
		return emailSentRecord;
	}

	private String[] getAllSubsribersEmails() {
		return null;
	}

}
