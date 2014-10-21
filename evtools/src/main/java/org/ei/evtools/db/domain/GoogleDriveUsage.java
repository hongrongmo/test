package org.ei.evtools.db.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Entity
@Table(name="GOOGLE_DRIVE_USAGE")
public class GoogleDriveUsage implements Serializable{

	private static final long serialVersionUID = -1698013895185051439L;
	
	@Column(name="ID")
	@Id
	private String id;
	
	@Temporal(TemporalType.DATE)
	@Column(name="TS")
	private Date timestamp;
	
	@Column(name="IP")
	private String ip;
	
	@Column(name="FORMAT")
	private String format;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getAcctno() {
		return acctno;
	}

	public void setAcctno(String acctno) {
		this.acctno = acctno;
	}

	@Column(name="ACCTNO")
	private String acctno;

}
