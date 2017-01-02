package com.aws.controller;

import java.io.IOException;
import java.io.InputStream;

import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.util.IOUtils;

@Service
public class S3Helper {

	private static final Logger logger = LoggerFactory.getLogger(S3Helper.class);

	private String indexPage = null;
	private String adminIndexPage = null;
	private String userIndexPage = null;

	private static final String INDEX_HTML = "index.html";
	private static final String USER_INDEX_HTML = "userindex.html";
	private static final String ADMIN_INDEX_HTML = "adminindex.html";

	@Autowired
	S3Communicator s3Communicator;
	
	public String getUserIndexPage() {
		if (StringUtils.isEmpty(userIndexPage)) {
			userIndexPage = getFile(USER_INDEX_HTML, S3Communicator.bucketName);
		}
		return userIndexPage;
	}

	public String getIndexPage() {
		if (StringUtils.isEmpty(indexPage)) {
			indexPage = getFile(INDEX_HTML, S3Communicator.bucketName);
		}
		return indexPage;
	}

	public String getAdminIndexPage() {
		if (StringUtils.isEmpty(adminIndexPage)) {
			adminIndexPage = getFile(ADMIN_INDEX_HTML, S3Communicator.bucketName);
		}
		return adminIndexPage;
	}

	private String getFile(String fileName, String bucketName) {
		InputStream s3Stream = null;
		try {
			s3Stream = s3Communicator.getS3Stream(fileName, true, bucketName);
		} catch (ApplicationException e1) {
		}
		if (s3Stream != null) {
			try {
				return IOUtils.toString(s3Stream);
			} catch (IOException e) {
				logger.error("Exception occured while loading file from s3", e);
			}
		}
		return null;
	}

}
