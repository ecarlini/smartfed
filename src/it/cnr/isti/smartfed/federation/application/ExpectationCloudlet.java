package it.cnr.isti.smartfed.federation.application;

import org.cloudbus.cloudsim.Cloudlet;

public class ExpectationCloudlet extends Cloudlet {

	Integer expectationTime;
	Integer expectationBw;
	static final int pesNumber = 1;
	
	/**
	 * 
	 * @param cloudletId
	 * @param cloudletLength
	 * @param pesNumber
	 * @param cloudletFileSize
	 * @param cloudletOutputSize
	 * @param expectationTime
	 * @param expBw
	 */
	public ExpectationCloudlet(	int cloudletId, long cloudletLength, long cloudletFileSize, long cloudletOutputSize, int expectationTime, int expBw ) {
		super(cloudletId, cloudletLength, pesNumber, cloudletFileSize,
				cloudletOutputSize, 
				CloudletProvider.getDefaultUtilModel(), 
				CloudletProvider.getDefaultUtilModel(),
				CloudletProvider.getDefaultUtilModel());
		this.expectationBw = expBw;
		this.expectationTime = expectationTime;
	}

}
