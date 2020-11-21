package app;

public class Constants {
	public static final String XPATH_USERNAME_INPUT = "/html/body/div/div/div/div/div/div[2]/form/div[1]/input";
	public static final String XPATH_PASSWORD_INPUT = "/html/body/div/div/div/div/div/div[2]/form/div[2]/input";
	public static final String XPATH_LOGIN_SUBMIT = "/html/body/div/div/div/div/div/div[2]/form/div[3]/button";
	public static final String PATH_APPLICATION_PROPERTIES = "/application.properties";
	public static final String PATH_OUTPUT_FOLDER = "output";
	public static final String FORWARD_SLASH = "/";
	public static final String DOT = ".";
	public static final String FILE_EXTENSION = "txt";
	public static final String URL_LOGIN = "https://moj.tvz.hr/studijirac/predmet/200100?TVZ=MOJ5ee398a7a26d6";
	public static final String PROPERTY_USERNAME = "user";
	public static final String PROPERTY_PASSWORD = "pass";
	public static final String XPATH_SMJEROVI_DIV = "//*[@id=\"studijSel\"]/div/div/div[2]/div";
	public static final String ID_POPIS_STUDENATA = "podaci";
	public static final String JS_BROJ_UKUPNO_STRANICA_POPISA_STUDENATA = "document.querySelector(\"#podaci_paginate > ul\").childNodes.length";
	public static final String JS_POPIS_STUDENATA_SUBMIT_FORM = "document.supportform.supporttype.value = 'prij'; document.supportform.submit();";
	public static final String XPATH_SLJEDECA_STRANICA_POPIS = "//*[@id=\"podaci_next\"]/a";
	public static final String ID_POPIS_KOLEGIJA_ZA_SMJER = "e1";
}
