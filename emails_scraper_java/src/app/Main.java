package app;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// gasenje logiranja
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

	    Map<String, String> emails = new HashMap<String, String>();	
	    WebClient client = null;
	    String outputFileName = null;
	    try (InputStream input = Main.class.getResourceAsStream(Constants.PATH_APPLICATION_PROPERTIES)) {  
	    	
	    	// kreiranje output datoteke
			int outputFilesTotal = new File(Constants.PATH_OUTPUT_FOLDER).list().length;
			outputFileName = (outputFilesTotal + 1) + Constants.DOT + Constants.FILE_EXTENSION;
			File outputFile = new File(Constants.PATH_OUTPUT_FOLDER + Constants.FORWARD_SLASH + outputFileName);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			
			// dohvacanje enkriptiranih kredencijala
    		Properties prop = new Properties();
    		prop.load(input);
    	    String encryptedUser = prop.getProperty(Constants.PROPERTY_USERNAME);
    	    String encryptedPass = prop.getProperty(Constants.PROPERTY_PASSWORD);
    	    byte[] decryptedUserBytes = Base64.getDecoder().decode(encryptedUser.getBytes());
    	    byte[] decryptedPassBytes = Base64.getDecoder().decode(encryptedPass.getBytes());

    	    // dohvacanje login stranice
    	    client = new WebClient();
    	    client.getOptions().setRedirectEnabled(true);
    	    client.getOptions().setJavaScriptEnabled(true);
    	    client.getOptions().setThrowExceptionOnFailingStatusCode(false);
    	    client.getOptions().setThrowExceptionOnScriptError(false);
    	    client.getOptions().setCssEnabled(false);
    	    client.getOptions().setUseInsecureSSL(true);
    	    client.addRequestHeader("Accept-Charset", "utf-8");
			HtmlPage page = client.getPage(Constants.URL_LOGIN);

    	    // ulogiravanje u moj.tvz.hr
			HtmlInput usernameInput = page.getFirstByXPath(Constants.XPATH_USERNAME_INPUT);
			HtmlInput passwordInput = page.getFirstByXPath(Constants.XPATH_PASSWORD_INPUT);
			usernameInput.setValueAttribute(new String(decryptedUserBytes));
			passwordInput.setValueAttribute(new String(decryptedPassBytes));
			HtmlButton htmlButton =  page.getFirstByXPath(Constants.XPATH_LOGIN_SUBMIT);
			HtmlPage pageAfterLogin = (HtmlPage) htmlButton.click();

			// dohvacanje svih smjerova
			HtmlDivision div = pageAfterLogin.getFirstByXPath(Constants.XPATH_SMJEROVI_DIV);
			int totalSmjer = div.getChildElementCount();
			int countSmjer = 0;
			
			// iteracija po svim smjerovima
			int counter = 0;
			Map<String, Integer> map = new HashMap<>();
    	    Iterable<DomElement> parent = div.getChildElements();
			for (DomElement child : parent) {

				countSmjer++;
				HtmlDivision childDiv = (HtmlDivision) child;
				HtmlButton smjerButton = (HtmlButton) childDiv.getChildNodes().get(0);
				System.out.println("  " + countSmjer + "/" + totalSmjer + " id:" + smjerButton.getAttribute("id"));
				
				String smjer = smjerButton.getAttribute("id");
				if (!smjer.equals("inf") && !smjer.equals("iinf") && !smjer.equals("rac") && !smjer.equals("irac")) {
					continue;
				}
				String upis;
				if (smjer.startsWith("i")) {
					if (smjer.charAt(1) == 'i') {
						upis = "izvanredni";
					} else if (smjer.charAt(1) == 'n' && smjer.charAt(2) == 'f') {
						upis = "redovni";
					} else {
						upis = "izvanredni";
					}
				} else {
					upis = "redovni";
				}
				
				// dohvacanje trenutnog smjera i kolegija na tom smjeru
				pageAfterLogin = client.getPage("https://moj.tvz.hr/studij" + smjer + "?TVZ=MOJ5ee398a7a26d6");
				HtmlSelect select = (HtmlSelect) pageAfterLogin.getElementById(Constants.ID_POPIS_KOLEGIJA_ZA_SMJER);
				int total = select.getChildElementCount();
				int count = 0;
				
				// iteracija po svim kolegijima trenutnog smjera
	    	    Iterable<DomElement> parentSmjer = select.getChildElements();
				for (DomElement element : parentSmjer) {
					String id = element.getAttribute("value");
					count++;
					if (id != null && !id.equals("")) {
						// dohvacanje trenutnog kolegija trenutnog smjera
						String url = "https://moj.tvz.hr/studij" + smjer + "/predmet/" + id + "?TVZ=MOJ5ee398a7a26d6";
						System.out.println(count + "/" + total + " url:" + url);
						HtmlPage pg = client.getPage(url);
						
						// javascript koji oponasa klikanje na gumb "Popis studenata"
						ScriptResult sr = pg.executeJavaScript(Constants.JS_POPIS_STUDENATA_SUBMIT_FORM);
						HtmlPage newPage = (HtmlPage) sr.getNewPage();
						
						// provjera postoji li ijedan student na kolegiju
						ScriptResult n = newPage.executeJavaScript(Constants.JS_BROJ_UKUPNO_STRANICA_POPISA_STUDENATA);
						if (n.getJavaScriptResult() == null) {
							continue;
						}
						
						// dohvacanje indexa gumba za sljedecu stranicu liste studenata
						String result = n.getJavaScriptResult().toString();
						int index = Integer.parseInt(result) - 1;
						String SLJEDECA_STRANICA_JS = "document.querySelector(\"#podaci_paginate > ul\").children[" + index + "]";
						boolean firstIterationFinished = false;
			    	    
						// iteracija po studentima
						while (true) {
	    					ScriptResult nsr = newPage.executeJavaScript(SLJEDECA_STRANICA_JS);
	    					String res = nsr.getJavaScriptResult().toString();

				    	    if (firstIterationFinished) {
		    					HtmlAnchor anchor = newPage.getFirstByXPath(Constants.XPATH_SLJEDECA_STRANICA_POPIS);
								newPage = (HtmlPage) anchor.click();
							}
				    	    
				    	    // ispisivanje mejlova studenata
							final HtmlTable table = newPage.getHtmlElementById(Constants.ID_POPIS_STUDENATA);
							for (int i = 1; i < table.getRows().size(); i++) {
								final HtmlTableRow tableRow = table.getRows().get(i);
								HtmlTableCell emailCell = tableRow.getCells().get(6);
								HtmlTableCell nameCell = tableRow.getCells().get(4);
								HtmlTableCell surnameCell = tableRow.getCells().get(3);
								HtmlTableCell jmbagCell = tableRow.getCells().get(2);
						    	String email = emailCell.asText().trim();
						    	if (emails.get(email) == null) {
						    		emails.put(email, email);
						    		String name = new String(nameCell.asText().getBytes(Charset.forName("UTF-8"))).trim();
						    		String jmbag = jmbagCell.asText().trim();
						    		String surname = new String(surnameCell.asText().getBytes(Charset.forName("UTF-8"))).trim();
									
						    		String fullname = name + " " + surname;
						    		Integer val = map.get(fullname);
						    		String surnameAppend = "";
						    		if (val != null) {
										map.put(fullname, val + 1);
										surnameAppend = String.valueOf(val + 1);
									} else {
										map.put(fullname, 1);
									}
									
						    		String smjerString = smjer.equals("inf") || smjer.equals("iinf") ? "informatika" : "racunarstvo";
						    		String sql = (++counter) + "~" + jmbag + "~" + email + "~" + name + "~" + surname + surnameAppend + "~" + upis + "~" + smjerString + "\n";
							    	Files.write(Paths.get(Constants.PATH_OUTPUT_FOLDER + Constants.FORWARD_SLASH + outputFileName), sql.getBytes(), StandardOpenOption.APPEND);
						    	}
							}
							
							firstIterationFinished = true;
	    					if (res.contains("disabled")) {
	    						break;
	    					}      
						}
					}
				}
	    	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	client.close();
        }
	    System.out.println("Success!");
	    System.out.println("Output file: " + Constants.PATH_OUTPUT_FOLDER + Constants.FORWARD_SLASH + outputFileName);
	}
}
