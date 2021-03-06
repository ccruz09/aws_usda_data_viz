package com.aws.codestar.projecttemplates.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.harmonia.properties.AppProperties;

/**
 * Basic Spring web service controller that handles all GET requests.
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

	//private static final String PLOT_OUTPUT_DIR = AppProperties.RSCRIPT_OUTPUT_DIR;
	private static final String PLOT_OUTPUT_DIR = "/var/www/html";
	private static final String GENETIC_ENGINEERING_ADOPTION_CSV = "geneticEngineeringAdoption.csv";
	private static final String PROTOTYPE_R = "prototype.R";
	private static final String COLUMNS = "columns";
	private static final String REGRESSION = "regression";
	private static final String SUMMARY = "summary";
	private static final String PLOT = "plot";
	private static final String RSCRIPT = "Rscript ";
	//private static final String RSCRIPT = AppProperties.RSCRIPT_LOCATION;
	
	private String scriptLoc;
	
	public AnalyticsController() {
		scriptLoc = RSCRIPT + " " + getPathToFile(PROTOTYPE_R);
	}

	@RequestMapping(path = "plot", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity plot(@RequestParam(value = "inputFile") String inputFile, @RequestParam(value = "columns") String columns) {
		String cmd = scriptLoc;
		String inputLoc = getInputPath(inputFile);
		try {
			//String response = runR(cmd, PLOT, inputLoc, PLOT_OUTPUT_DIR + " \"" + columns + "\"");
			String response = runR(cmd, PLOT, inputLoc, PLOT_OUTPUT_DIR + " " + columns);
			return ResponseEntity.ok(response);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "regression", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity regression(@RequestParam(value = "inputFile") String inputFile) {
		String cmd = scriptLoc;
		String inputLoc = getInputPath(inputFile);
		try {
			String response = runR(cmd, REGRESSION, inputLoc, "");
			return ResponseEntity.ok(response);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "summary", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity summary(@RequestParam(value = "inputFile") String inputFile) {
		String cmd = scriptLoc;
		String inputLoc = getInputPath(inputFile);
		try {
			String response = runR(cmd, SUMMARY, inputLoc, "");
			return ResponseEntity.ok(response);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@RequestMapping(path = "columns", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity columns(@RequestParam(value = "inputFile") String inputFile) {
		String cmd = scriptLoc;
		String inputLoc = getInputPath(inputFile);
		try {
			String response = runR(cmd, COLUMNS, inputLoc, "");
			return ResponseEntity.ok(response);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	private String runR(String cmd, String action, String inputFile, String params) throws IOException, InterruptedException {
		Runtime run = Runtime.getRuntime();
		String exec = cmd + " " + action + " " + inputFile + " " + params;
		System.out.println(exec);
		Process pr = run.exec(exec);
		pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		
		//print errors
		BufferedReader errBuf = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		while ((line = errBuf.readLine()) != null) {
			System.err.println(line);
		}
		
		String data = "";
		while ((line = buf.readLine()) != null) {
			System.out.println(line);
			data = line.substring(0, line.length());
		}
		return data;
		// return ResponseEntity.ok(createResponse(name));

	}

	private String getInputPath(String inputFile) {
		String inputLoc = "";
		if(GENETIC_ENGINEERING_ADOPTION_CSV.equals(inputFile)) {
			inputLoc = getPathToFile(GENETIC_ENGINEERING_ADOPTION_CSV);
		}
		return inputLoc;
	}
	
	private String getPathToFile(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(fileName);
		File file = new File(url.getFile());
		return file.getAbsolutePath();
	}

}
