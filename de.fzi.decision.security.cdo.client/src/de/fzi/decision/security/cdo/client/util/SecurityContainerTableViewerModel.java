package de.fzi.decision.security.cdo.client.util;

public class SecurityContainerTableViewerModel {
	
	private String name;
	private boolean opened;
	
	public SecurityContainerTableViewerModel(String name) {
		this.name = name;
		this.opened = false;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public String getName() {
		return name;
	}

}
