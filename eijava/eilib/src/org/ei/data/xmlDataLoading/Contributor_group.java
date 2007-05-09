package org.ei.data.xmlDataLoading;

import java.util.*;

public class Contributor_group extends BaseElement
{

	List contributors = new ArrayList();
	List collaborations = new ArrayList();
	String et_al;
	Affiliation affiliation;


	public void setContributors(List contributors)
	{
		this.contributors = contributors;
	}

	public void addContributor(Contributor contributor)
	{
		contributors.add(contributor);
	}

	public List getContributors()
	{
		return this.contributors;
	}

	public void setCollaborations(List collaborations)
	{
		this.collaborations = collaborations;
	}

	public void addCollaboration(Collaboration collaboration)
	{
		collaborations.add(collaboration);
	}

	public List getCollaborations()
	{
		return this.collaborations;
	}

	public void setEt_al(String et_al)
	{
		this.et_al = et_al;
	}

	public String getEt_al()
	{
		return this.et_al;
	}

	public void setAffiliation(Affiliation affiliation)
	{
		this.affiliation = affiliation;
	}

	public Affiliation getAffiliation()
	{
		return this.affiliation;
	}
}
