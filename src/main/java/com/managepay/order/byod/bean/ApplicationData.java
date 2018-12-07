package com.managepay.order.byod.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="application-data")
public class ApplicationData {
	private String appName;
	private String mainLogoPath;
	private String shortcutLogoPath;
	private String mainBackgroundPath;
	private String landingLogoPath;
	private String mainColor;
	private String subColor;
	private String mainTextColor;
	private String subTextColor;
	private String mainButtonTextColor;
	private String mainButtonBackgroundColor;
	private String mainButtonBackgroundHoverColor;
	private String mainButtonBackgroundFocusColor;
	private String localeButtonColor;

	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getMainLogoPath() {
		return mainLogoPath;
	}
	public void setMainLogoPath(String mainLogoPath) {
		this.mainLogoPath = mainLogoPath;
	}
	public String getShortcutLogoPath() {
		return shortcutLogoPath;
	}
	public void setShortcutLogoPath(String shortcutLogoPath) {
		this.shortcutLogoPath = shortcutLogoPath;
	}
	public String getMainBackgroundPath() {
		return mainBackgroundPath;
	}
	public void setMainBackgroundPath(String mainBackgroundPath) {
		this.mainBackgroundPath = mainBackgroundPath;
	}
	public String getLandingLogoPath() {
		return landingLogoPath;
	}
	public void setLandingLogoPath(String landingLogoPath) {
		this.landingLogoPath = landingLogoPath;
	}
	public String getMainColor() {
		return mainColor;
	}
	public void setMainColor(String mainColor) {
		this.mainColor = mainColor;
	}
	public String getSubColor() {
		return subColor;
	}
	public void setSubColor(String subColor) {
		this.subColor = subColor;
	}
	public String getMainTextColor() {
		return mainTextColor;
	}
	public void setMainTextColor(String mainTextColor) {
		this.mainTextColor = mainTextColor;
	}
	public String getSubTextColor() {
		return subTextColor;
	}
	public void setSubTextColor(String subTextColor) {
		this.subTextColor = subTextColor;
	}
	public String getMainButtonTextColor() {
		return mainButtonTextColor;
	}
	public void setMainButtonTextColor(String mainButtonTextColor) {
		this.mainButtonTextColor = mainButtonTextColor;
	}
	public String getMainButtonBackgroundColor() {
		return mainButtonBackgroundColor;
	}
	public void setMainButtonBackgroundColor(String mainButtonBackgroundColor) {
		this.mainButtonBackgroundColor = mainButtonBackgroundColor;
	}
	public String getMainButtonBackgroundHoverColor() {
		return mainButtonBackgroundHoverColor;
	}
	public void setMainButtonBackgroundHoverColor(String mainButtonBackgroundHoverColor) {
		this.mainButtonBackgroundHoverColor = mainButtonBackgroundHoverColor;
	}
	public String getMainButtonBackgroundFocusColor() {
		return mainButtonBackgroundFocusColor;
	}
	public void setMainButtonBackgroundFocusColor(String mainButtonBackgroundFocusColor) {
		this.mainButtonBackgroundFocusColor = mainButtonBackgroundFocusColor;
	}
	public String getLocaleButtonColor() {
		return localeButtonColor;
	}
	public void setLocaleButtonColor(String localeButtonColor) {
		this.localeButtonColor = localeButtonColor;
	}
}
