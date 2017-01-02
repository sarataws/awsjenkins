package com.aws.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@SuppressWarnings("restriction")
@Service
public class S3Communicator {

	private static final Logger logger = Logger.getLogger(S3Communicator.class);
	private static final String defaultbucketName = "CloudConfigs";
	private static AmazonS3 s3Client;
	public static final String AWS_CREDENTIALS_PROPERTIES = "AwsCredentials.properties";

	private static String accesskey, secretkey;
	public static String bucketName;

	static {

		URL resource = S3Communicator.class.getClassLoader().getResource(
				AWS_CREDENTIALS_PROPERTIES);
		if (resource != null) {
			logger.warn("************ remove credential files and move to roles ****************");
			initializeCredentialEnv();
		} else {
			logger.warn("************ using roles ****************");
			initializeRoleEnv();
		}
	}

	private static void initializeCredentialEnv() {
		InputStream is = S3Communicator.class.getClassLoader()
				.getResourceAsStream(AWS_CREDENTIALS_PROPERTIES);
		Properties env = new Properties();
		try {
			env.load(is);
			accesskey = env.getProperty("accessKey");
			secretkey = env.getProperty("secretKey");
			bucketName = env.getProperty("s3configbucket") == null ? defaultbucketName
					: env.getProperty("s3configbucket");
			s3Client = new AmazonS3Client(new BasicAWSCredentials(
					getProperty(accesskey), getProperty(secretkey)));
			/*
			 * Region region = RegionUtils.getRegion("us-east-1");
			 * s3Client.setRegion(region);
			 */
		} catch (Exception e) {
			logger.fatal("error loading aws properties", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static String getProperty(String key) {
		String decryptedValue;

		decryptedValue = key;
		return decryptedValue;
	}

	private static void initializeRoleEnv() {
		InputStream is = S3Communicator.class.getClassLoader()
				.getResourceAsStream("env.properties");
		Properties env = new Properties();
		try {
			env.load(is);
			bucketName = env.getProperty("s3configbucket") == null ? defaultbucketName
					: env.getProperty("s3configbucket");
			s3Client = new AmazonS3Client();
		} catch (Exception e) {
			logger.fatal("error loading aws properties", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@PostConstruct
	public void loadProperties() {
		s3Client = new AmazonS3Client();
	}

	public InputStream getS3Stream(String key, Boolean env)
			throws ApplicationException {
		return getS3Stream(key, env, bucketName);
	}

	public InputStream getS3Stream(String key, Boolean env, String s3BucketName)
			throws ApplicationException {
		try {

			S3Object s3object = s3Client.getObject(new GetObjectRequest(
					s3BucketName, key));
			return s3object.getObjectContent();
		} catch (AmazonServiceException ase) {
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			throw new IllegalArgumentException("Connection exception occurred",
					ase);
		} catch (AmazonClientException ace) {
			logger.error("Error Message: " + ace.getMessage());
			throw new IllegalArgumentException("Connection exception occurred",
					ace);
		}
	}

	public void putS3Object(InputStream payload, String key, Boolean env,
			int length) throws ApplicationException {
		try {
			ObjectMetadata omd = new ObjectMetadata();
			omd.setContentType("text/plain");
			omd.setContentLength(length);
			s3Client.putObject(bucketName, key, payload, omd);
		} catch (AmazonServiceException ase) {
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			throw new IllegalArgumentException("Connection exception occurred",
					ase);
		} catch (AmazonClientException ace) {
			logger.error("Error Message: " + ace.getMessage());
			throw new IllegalArgumentException("Connection exception occurred",
					ace);
		}
	}

	public void putS3Object(String bucketName, String key, File file)
			throws ApplicationException {
		try {
			s3Client.putObject(new PutObjectRequest(bucketName, key, file));
		} catch (AmazonServiceException ase) {
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			throw new IllegalArgumentException("Connection exception occurred",
					ase);
		} catch (AmazonClientException ace) {
			logger.error("Error Message: " + ace.getMessage());
			throw new IllegalArgumentException("Connection exception occurred",
					ace);
		}
	}

	public void copyS3Object(String sourceBucketName, String sourceKey,
			String destinationBucketName, String destinationKey) {
		try {
			s3Client.copyObject(sourceBucketName, sourceKey,
					destinationBucketName, destinationKey);
		} catch (AmazonServiceException ase) {
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			throw ase;
		} catch (AmazonClientException ace) {
			logger.error("Error Message: " + ace.getMessage());
			throw ace;
		}
	}
}
