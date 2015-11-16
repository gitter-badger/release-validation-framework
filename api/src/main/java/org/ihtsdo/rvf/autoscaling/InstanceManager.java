package org.ihtsdo.rvf.autoscaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

public class InstanceManager {
	
	private Logger logger = LoggerFactory.getLogger(InstanceManager.class);
	private  AmazonEC2Client amazonEC2Client;
	private static int counter;
	private String imageId = "ami-f8524399";
	//parameterized these
	private String instanceType = "t2.micro";
	private String keyName = "WestDevops";
	private String securityGroupName = "SSH_HTTPS";
	private String securityGroupId = "sg-5624cd32";
	
	public InstanceManager(AWSCredentials credentials) {
		amazonEC2Client = new AmazonEC2Client(credentials);
		amazonEC2Client.setEndpoint("ec2.us-west-2.amazonaws.com");
	}
	
	public RunInstancesResult createInstance() {
		RunInstancesRequest runInstancesRequest = 
				  new RunInstancesRequest();
			
			runInstancesRequest.withImageId(imageId)
			                     .withInstanceType(instanceType)
			                     .withMinCount(1)
			                     .withMaxCount(1)
			                     .withKeyName(keyName)
			                     .withSecurityGroups(securityGroupName)
			  					 .withSecurityGroupIds(securityGroupId);
			  					 
			  RunInstancesResult runInstancesResult = 
					  amazonEC2Client.runInstances(runInstancesRequest);

			  String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
			  logger.info("RVF worker new instance created with id:" + instanceId);
			  CreateTagsRequest createTagsRequest = new CreateTagsRequest();
			  createTagsRequest.withResources(instanceId);
			  createTagsRequest.withTags(new Tag ( "Name", "RVF_Worker" + counter++));
			  amazonEC2Client.createTags(createTagsRequest);
			  return runInstancesResult;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getSecurityGroupName() {
		return securityGroupName;
	}

	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}

	public String getSecurityGroupId() {
		return securityGroupId;
	}

	public void setSecurityGroupId(String securityGroupId) {
		this.securityGroupId = securityGroupId;
	}
}
