package org.ei.controller;

import org.apache.log4j.Logger;

/**
 * This class records the number of "hits" from accession number
 * searches.  It will return blocked status for customer when
 * we hit defined limit.
 * 
 * @author harovetm
 *
 */
public class HitCount {
	private final static Logger log4j = Logger.getLogger(HitCount.class);

	public static final int HITCOUNT_BLOCK_THRESHOLD = 10;
	
	private String key;
	private long createTime;
	private int hits = 0;
	private boolean blocked;

	// Constructor with just key
	public HitCount(String key) throws Exception {
		log4j.info("Construction HitCount object, key = " + key);
		this.key = key;
		this.createTime = System.currentTimeMillis();
		this.hits++;
		this.blocked = false;
	}

	// Constructor with key and value
	public HitCount(String key, String value) {
		log4j.info("Construction HitCount object, key = " + key + ", value=" + value);
		this.key = key;
		String[] valueArray = value.split(":");
		this.createTime = Long.parseLong(valueArray[0]);
		this.hits = Integer.parseInt(valueArray[1]);
		this.blocked = new Boolean(valueArray[2]).booleanValue();
		this.hits++;

		if (this.hits > HITCOUNT_BLOCK_THRESHOLD) {
			this.blocked = true;
		}
	}

	public String getKey() {
		return this.key;
	}

	public long getCreateTime() {
		return this.createTime;
	}

	public boolean getBlocked() {
		return this.blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public String toString() {
		return this.createTime + ":" + this.hits + ":" + Boolean.toString(this.blocked);
	}
}