package com.avensys.rts.jobservice.enums;

/**
 * Author: Rahul Sahu This enum is used to specify the permissions
 */
public enum Permission {
	JOB_READ("Jobs:Read"), JOB_WRITE("Jobs:Write"), JOB_DELETE("Jobs:Delete"), JOB_EDIT("Jobs:Edit"),
	JOB_NOACCESS("Jobs:NoAccess");

	private final String permission;

	Permission(String permission) {
		this.permission = permission;
	}

	public String toString() {
		return this.permission;
	}
}
