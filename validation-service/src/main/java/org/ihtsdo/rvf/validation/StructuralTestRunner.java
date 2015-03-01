package org.ihtsdo.rvf.validation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ihtsdo.rvf.util.ZipFileUtils;
import org.ihtsdo.rvf.validation.impl.CsvMetadataResultFormatter;
import org.ihtsdo.rvf.validation.impl.CsvResultFormatter;
import org.ihtsdo.rvf.validation.impl.StreamTestReport;
import org.ihtsdo.rvf.validation.log.ValidationLog;
import org.ihtsdo.rvf.validation.log.ValidationLogFactory;
import org.ihtsdo.rvf.validation.model.ManifestFile;
import org.ihtsdo.rvf.validation.resource.ResourceManager;
import org.ihtsdo.rvf.validation.resource.ZipFileResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class StructuralTestRunner implements InitializingBean{

	private final Logger logger = LoggerFactory.getLogger(StructuralTestRunner.class);
	protected String reportFolderLocation;
	protected File reportDataFolder;
	protected int failureThreshold;

	@Autowired
	private ValidationLogFactory validationLogFactory;

	public TestReportable execute(ResourceManager resourceManager, PrintWriter writer, boolean writeSuccesses,
			ManifestFile manifest) {

		// the information for the manifest testing
		StreamTestReport testReport = new StreamTestReport(new CsvMetadataResultFormatter(), writer, true);
		// run manifest tests
		runManifestTests(resourceManager, testReport, manifest, validationLogFactory.getValidationLog(ManifestPatternTester.class));
		testReport.addNewLine();

		// run column tests
		testReport.setFormatter(new CsvResultFormatter());
		testReport.setWriteSuccesses(writeSuccesses);
		ValidationLog validationLog = validationLogFactory.getValidationLog(ColumnPatternTester.class);
		runColumnTests(resourceManager, testReport, validationLog);

		String summary = testReport.writeSummary();
		validationLog.info(summary);

		return testReport;
	}

	public TestReportable execute(ResourceManager resourceManager, PrintWriter writer, boolean writeSuccesses) {

		StreamTestReport testReport = new StreamTestReport(new CsvResultFormatter(), writer, writeSuccesses);
		ValidationLog validationLog = validationLogFactory.getValidationLog(ColumnPatternTester.class);
		runColumnTests(resourceManager, testReport, validationLog);
		String summary = testReport.writeSummary();
		validationLog.info(summary);
		return testReport;
	}

	private void runManifestTests(ResourceManager resourceManager, TestReportable report,
			ManifestFile manifest, ValidationLog validationLog) {
		ManifestPatternTester manifestPatternTester = new ManifestPatternTester(validationLog, resourceManager, manifest, report);
		manifestPatternTester.runTests();
	}

	private void runColumnTests(ResourceManager resourceManager, TestReportable report, ValidationLog validationLog) {

		ColumnPatternTester columnPatternTest = new ColumnPatternTester(validationLog, resourceManager, report);
		columnPatternTest.runTests();
	}
	
	public boolean verifyZipFileStructure(final Map<String, Object> responseMap, final File tempFile, final Long runId, final MultipartFile manifestFile, 
			final boolean writeSucceses, final String urlPrefix ) throws IOException {
		 boolean isFailed = false;
		 logger.debug("Start verifying zip file structure of {} against manifest", tempFile.getName());
		// convert groups which is passed as string to assertion groups
		// set up the response in order to stream directly to the response
		final File manifestTestReport = new File(getReportDataFolder(), "manifest_validation_"+runId+".txt");
		try (PrintWriter writer = new PrintWriter(manifestTestReport)) {
			final ResourceManager resourceManager = new ZipFileResourceProvider(tempFile);

			TestReportable report;

			if (manifestFile == null) {
				report = execute(resourceManager, writer, writeSucceses);
			} else {
				final String originalFilename = manifestFile.getOriginalFilename();
				final File tempManifestFile = File.createTempFile(originalFilename, ".xml");
				tempManifestFile.deleteOnExit();
				ZipFileUtils.copyUploadToDisk(manifestFile, tempManifestFile);

				final ManifestFile mf = new ManifestFile(tempManifestFile);
				report = execute(resourceManager, writer, writeSucceses, mf);
			}

			// verify if manifest is valid
			if(report.getNumErrors() > 0){

				logger.error("No Errors expected but got " + report.getNumErrors() + " errors");
				responseMap.put("type", "pre");
				responseMap.put("assertionsRun", report.getNumTestRuns());
				responseMap.put("assertionsFailed", report.getNumErrors());
				logger.info("reportPhysicalUrl : " + manifestTestReport.getAbsolutePath());
				// pass file name without extension - we add this back when we retrieve using controller
				responseMap.put("reportUrl", urlPrefix+"/reports/"+ FilenameUtils.removeExtension(manifestTestReport.getName()));

				logger.info("report.getNumErrors() = " + report.getNumErrors());
				logger.info("report.getNumTestRuns() = " + report.getNumTestRuns());
				final double threshold = report.getNumErrors() / report.getNumTestRuns();
				logger.info("threshold = " + threshold);
				// bail out only if number of test failures exceeds threshold
				if(threshold > getFailureThreshold()){
					isFailed = true;
				}
			}
		}
		logger.debug("Finished verifying zip file structure of {} against manifest", tempFile.getName());		
		return isFailed;
	}

	/**
	 * The init method that sets up the data folder where all reports are stored. This method must always be called
	 * immediately after instantiating this class outside of Spring context.
	 * //todo move to an async process at some time!
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Sct Data Location passed = " + reportFolderLocation);
		if (reportFolderLocation == null || reportFolderLocation.length() == 0) {
			reportFolderLocation = FileUtils.getTempDirectoryPath() + System.getProperty("file.separator") + "rvf-reports";
		}

		reportDataFolder = new File(reportFolderLocation);
		if(!reportDataFolder.exists()){
			if(reportDataFolder.mkdirs()){
				logger.info("Created report folder at : " + reportFolderLocation);
			}
			else{
				logger.error("Unable to create data folder at path : " + reportFolderLocation);
				throw new IllegalArgumentException("Bailing out because report folder location can not be set to : " + reportFolderLocation);
			}
		}

		logger.info("Using report folder location as :" + reportDataFolder.getAbsolutePath());
	}

	public void setReportFolderLocation(String reportFolderLocation) {
		this.reportFolderLocation = reportFolderLocation;
	}

	public File getReportDataFolder() {
		return reportDataFolder;
	}

	public int getFailureThreshold() {
		return failureThreshold;
	}

	public void setFailureThreshold(int failureThreshold) {
		this.failureThreshold = failureThreshold;
	}
}
