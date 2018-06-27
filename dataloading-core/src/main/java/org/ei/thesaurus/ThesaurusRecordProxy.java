package org.ei.thesaurus;


import org.ei.domain.ClassificationID;

public class ThesaurusRecordProxy
    implements ThesaurusRecord
{


    private ThesaurusRecordBroker tbroker;
    protected ThesaurusRecordImpl impl;
    protected ThesaurusRecordID[] useTermIDs;
    protected ThesaurusRecordID[] topTermIDs;
    protected ThesaurusRecordID[] relatedTermIDs;
    protected ThesaurusRecordID[] narrowerTermIDs;
    protected ThesaurusRecordID[] broaderTermIDs;
    protected ThesaurusRecordID[] priorTermIDs;
    protected ThesaurusRecordID[] leadinTermIDs;
    protected ThesaurusRecordID[] seeIDs;
    protected ThesaurusRecordID[] seeAlsoIDs;
    protected ThesaurusRecordID[] chemicalAspectsIDs;
    private String useTermAndOrFlag;


    public void accept(ThesaurusNodeVisitor v)
        throws ThesaurusException
    {
        v.visitWith((ThesaurusRecord)this);
    }

    public ThesaurusRecordProxy(ThesaurusRecordImpl impl)
    {
        this.impl = impl;
        tbroker = new ThesaurusRecordBroker(impl.getRecID().getDatabase());
    }

    public ThesaurusRecordID getRecID()
    {
        return this.impl.getRecID();
    }

    public void setStatus(String status)
    {
        this.impl.setStatus(status);
    }

    public String getStatus()
    {
        return this.impl.getStatus();
    }

    public void setScopeNotes(String scopeNotes)
    {
        this.impl.setScopeNotes(scopeNotes);
    }

    public String getScopeNotes()
    {
        return this.impl.getScopeNotes();
    }

    public void setSearchInfo(String searchInfo)
    {
        this.impl.setSearchInfo(searchInfo);
    }

    public String getSearchInfo()
    {
        return this.impl.getSearchInfo();
    }

    public void setHistoryScopeNotes(String historyScopeNotes)
    {
        this.impl.setHistoryScopeNotes(historyScopeNotes);
    }

    public String getHistoryScopeNotes()
    {
        return this.impl.getHistoryScopeNotes();
    }


    // UseTerm methods

    public void setUseTerms(ThesaurusPage useTerms)
    {
        this.impl.setUseTerms(useTerms);
    }

	public void setUserTermAndOrFlag(String userTermAndOrFlag)
	{
		this.impl.setUserTermAndOrFlag(userTermAndOrFlag);
	}

	public String getUserTermAndOrFlag()
	{
		return this.impl.getUserTermAndOrFlag();
	}

    public ThesaurusPage getUseTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;
        if((p=this.impl.getUseTerms()) == null)
        {
			if(useTermIDs != null && (useTermIDs[0].getDatabase().getIndexName()!="ept") && (useTermIDs[0].getDatabase().getIndexName()!="elt"))
			{
				p = tbroker.buildPage(useTermIDs,
									 0,
									 (useTermIDs.length-1));

				this.impl.setUseTerms(p);
			}
			else if(useTermIDs != null && ((useTermIDs[0].getDatabase().getIndexName()=="ept") ||(useTermIDs[0].getDatabase().getIndexName()=="elt")))
			{
				ThesaurusRecordID[] eptPlusUseTermsIDs = new ThesaurusRecordID[useTermIDs.length];
				ThesaurusRecordID[] eptNoPlusUseTermsIDs = new ThesaurusRecordID[useTermIDs.length];
				//ThesaurusPage eptLeadinTermsPage = new ThesaurusPage(2*leadinTermIDs);
				int j=0;
				int k=0;
				for(int i=0;i<useTermIDs.length;i++)
				{
					String useTerm = useTermIDs[i].getMainTerm();

					if(useTerm != null && ((useTerm.indexOf(" plus ")>0) || (useTerm.indexOf("<STRING>")>-1)))
					{
						eptPlusUseTermsIDs[j] = new ThesaurusRecordID(useTerm, useTermIDs[i].getDatabase());
						j++;
					}
					else
					{
						eptNoPlusUseTermsIDs[k] = new ThesaurusRecordID(useTerm, useTermIDs[i].getDatabase());
						k++;
					}
				}
				if(k >0)
				{
					p = tbroker.buildPage(eptNoPlusUseTermsIDs,
										 0,
										 k-1);
				}

				if(j>0)
				{
					for(int i=0;i<j;i++)
					{
						ThesaurusRecordID eptTheID = eptPlusUseTermsIDs[i];
						ThesaurusRecordImpl trec = new ThesaurusRecordImpl(eptTheID);
						ThesaurusRecordProxy proxy = new ThesaurusRecordProxy(trec);
						if(p==null)
						{
							p = new ThesaurusPage(eptPlusUseTermsIDs.length);
						}
						p.add(proxy);
					}
				}
            	this.impl.setUseTerms(p);
	   		}
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setUseTerms(p);
            }


        }

        return p;
    }

    public void setUseTermIDs(ThesaurusRecordID[] ids)
    {
        this.useTermIDs = ids;
    }

    public int countUseTerms()
    {
        if(useTermIDs == null)
        {
            return 0;
        }

        return useTermIDs.length;
    }





    // LeadinTerm methods

    public void setLeadinTerms(ThesaurusPage leadinTerms)
    {
        this.impl.setLeadinTerms(leadinTerms);
    }

    public ThesaurusPage getLeadinTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;
        if((p=this.impl.getLeadinTerms()) == null)
        {
            if(leadinTermIDs != null && leadinTermIDs[0].getDatabase().getIndexName()!="ept" && leadinTermIDs[0].getDatabase().getIndexName()!="elt")
            {
                p = tbroker.buildPage(leadinTermIDs,
                                     0,
                                     (leadinTermIDs.length-1));

                this.impl.setLeadinTerms(p);
            }
            else if(leadinTermIDs != null && ((leadinTermIDs[0].getDatabase().getIndexName()=="ept") || (leadinTermIDs[0].getDatabase().getIndexName()=="elt")))
            {
				ThesaurusRecordID[] eptPlusLeadinTermsIDs = new ThesaurusRecordID[leadinTermIDs.length];
				ThesaurusRecordID[] eptNoPlusLeadinTermsIDs = new ThesaurusRecordID[leadinTermIDs.length];
				//ThesaurusPage eptLeadinTermsPage = new ThesaurusPage(2*leadinTermIDs);
				int j=0;
				int k=0;
				for(int i=0;i<leadinTermIDs.length;i++)
				{
					String leadinTerm = leadinTermIDs[i].getMainTerm();

					if(leadinTerm != null && (leadinTerm.indexOf(" plus ")>0 || leadinTerm.indexOf(" <STRING> ")>0))
					{
						eptPlusLeadinTermsIDs[j] = new ThesaurusRecordID(leadinTerm, leadinTermIDs[i].getDatabase());
                		j++;
					}
					else
					{
						eptNoPlusLeadinTermsIDs[k] = new ThesaurusRecordID(leadinTerm, leadinTermIDs[i].getDatabase());
						k++;
					}
				}
				if(k >0)
				{
					p = tbroker.buildPage(eptNoPlusLeadinTermsIDs,
										 0,
										 k-1);
            	}

            	if(j>0)
            	{
					for(int i=0;i<j;i++)
					{
						ThesaurusRecordID eptTheID = eptPlusLeadinTermsIDs[i];
						ThesaurusRecordImpl trec = new ThesaurusRecordImpl(eptTheID);
						ThesaurusRecordProxy proxy = new ThesaurusRecordProxy(trec);
						if(p==null)
						{
							p = new ThesaurusPage(eptPlusLeadinTermsIDs.length);
						}
                		p.add(proxy);
					}
				}
            	this.impl.setLeadinTerms(p);
			}
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setLeadinTerms(p);
            }
        }

        return p;
    }

    public int countLeadinTerms()
    {
        if(leadinTermIDs == null)
        {
            return 0;
        }

        return leadinTermIDs.length;
    }

    public void setLeadinTermIDs(ThesaurusRecordID[] ids)
    {
        this.leadinTermIDs = ids;
    }


    // NarrowerTerm methods

    public void setNarrowerTerms(ThesaurusPage narrowerTerms)
    {
        this.impl.setNarrowerTerms(narrowerTerms);
    }

    public ThesaurusPage getNarrowerTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getNarrowerTerms()) == null)
        {
            if(narrowerTermIDs != null)
            {
                p = tbroker.buildPage(narrowerTermIDs,
                                     0,
                                     (narrowerTermIDs.length-1));
                this.impl.setNarrowerTerms(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setNarrowerTerms(p);
            }
        }

        return p;
    }

    public int countNarrowerTerms()
    {
        if(narrowerTermIDs == null)
        {
            return 0;
        }

        return narrowerTermIDs.length;
    }

    public void setNarrowerTermIDs(ThesaurusRecordID[] ids)
    {
        this.narrowerTermIDs = ids;
    }
    
    // Chemical Aspect methods

    public void setChemicalAspects(ThesaurusPage chemicalAspectsIDs)
    {
        this.impl.setChemicalAspects(chemicalAspectsIDs);
    }

    public ThesaurusPage getChemicalAspects()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getChemicalAspects()) == null)
        {
            if(chemicalAspectsIDs != null)
            {
                p = tbroker.buildPage(chemicalAspectsIDs,
                                     0,
                                     (chemicalAspectsIDs.length-1));
                this.impl.setChemicalAspects(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setChemicalAspects(p);
            }
        }

        return p;
    }

    public int countChemicalAspects()
    {
        if(chemicalAspectsIDs == null)
        {
            return 0;
        }

        return chemicalAspectsIDs.length;
    }

    public void setChemicalAspectsIDs(ThesaurusRecordID[] ids)
    {
        this.chemicalAspectsIDs = ids;
    }




    // BroaderTerm methods

    public void setBroaderTerms(ThesaurusPage broaderTerms)
    {
        this.impl.setBroaderTerms(broaderTerms);
    }

    public ThesaurusPage getBroaderTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getBroaderTerms()) == null)
        {
            if(broaderTermIDs != null)
            {
                p = tbroker.buildPage(broaderTermIDs,
                                     0,
                                     (broaderTermIDs.length-1));

                this.impl.setBroaderTerms(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setBroaderTerms(p);
            }
        }

        return p;
    }

    public int countBroaderTerms()
    {
        if(broaderTermIDs == null)
        {
            return 0;
        }

        return broaderTermIDs.length;
    }

    public void setBroaderTermIDs(ThesaurusRecordID[] ids)
    {
        this.broaderTermIDs = ids;
    }


    // RelatedTerm methods

    public void setRelatedTerms(ThesaurusPage relatedTerms)
    {
        this.impl.setRelatedTerms(relatedTerms);
    }

    public ThesaurusPage getRelatedTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getRelatedTerms()) == null)
        {
            if(relatedTermIDs != null)
            {
                p = tbroker.buildPage(relatedTermIDs,
                                     0,
                                     (relatedTermIDs.length-1));
                this.impl.setRelatedTerms(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setRelatedTerms(p);

            }
        }

        return p;
    }

    public int countRelatedTerms()
    {
        if(relatedTermIDs == null)
        {
            return 0;
        }

        return relatedTermIDs.length;
    }

    public void setRelatedTermIDs(ThesaurusRecordID[] ids)
    {
        this.relatedTermIDs = ids;
    }


    // TopTerm methods

    public void setTopTerms(ThesaurusPage topTerms)
    {
        this.impl.setTopTerms(topTerms);
    }

    public ThesaurusPage getTopTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getTopTerms()) == null)
        {
            if(this.topTermIDs != null)
            {
                p = tbroker.buildPage(topTermIDs,
                                     0,
                                     (topTermIDs.length-1));
                this.impl.setTopTerms(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setTopTerms(p);

            }
        }

        return p;
    }

    public int countTopTerms()
    {
        if(topTermIDs == null)
        {
            return 0;
        }

        return topTermIDs.length;
    }

    public void setTopTermIDs(ThesaurusRecordID[] ids)
    {
        this.topTermIDs = ids;
    }



    // PriorTerm methods

    public void setPriorTerms(ThesaurusPage priorTerms)
    {
        this.impl.setPriorTerms(priorTerms);
    }

    public ThesaurusPage getPriorTerms()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getPriorTerms()) == null)
        {
            if(this.priorTermIDs != null)
            {
                p = tbroker.buildPage(priorTermIDs,
                                     0,
                                     (priorTermIDs.length-1));
                this.impl.setPriorTerms(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setPriorTerms(p);
            }
        }

        return p;
    }

    public void setPriorTermIDs(ThesaurusRecordID[] ids)
    {
        this.priorTermIDs = ids;
    }

    public int countPriorTerms()
    {
        if(priorTermIDs == null)
        {
            return 0;
        }

        return priorTermIDs.length;
    }


    public ClassificationID[] getClassificationIDs()
    {
        return impl.getClassificationIDs();
    }

    public void setClassificationIDs(ClassificationID[] ids)
    {
        impl.setClassificationIDs(ids);
    }

    public void setDateOfIntro(String dateOfIntro)
    {
        this.impl.setDateOfIntro(dateOfIntro);
    }

    public String getDateOfIntro()
    {
        return this.impl.getDateOfIntro();
    }

    public void setCoordinates(String coordinates)
    {
    	impl.setCoordinates(coordinates);
    }

    public String getCoordinates()
    {
        return this.impl.getCoordinates();
    }

    public void setType(String type)
    {
    	impl.setType(type);
    }

    public String getType()
    {
        return this.impl.getType();
    }



    public void setRegistryNumber(String registryNumber)
    {
    	impl.setRegistryNumber(registryNumber);
    }

    public String getRegistryNumber()
    {
        return this.impl.getRegistryNumber();
    }

    public void setRegistryNumberBroaderTerm(String registryNumberBroaderTerm)
    {
    	impl.setRegistryNumberBroaderTerm(registryNumberBroaderTerm);
    }

    public String getRegistryNumberBroaderTerm()
    {
        return this.impl.getRegistryNumberBroaderTerm();
    }

    // See Also methods

    public void setSeeAlsoIDs(ThesaurusRecordID[] ids)
    {
        this.seeAlsoIDs = ids;
    }

    public ThesaurusPage getSeeAlso()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getSeeAlso()) == null)
        {
            if(this.seeAlsoIDs != null)
            {
                p = tbroker.buildPage(seeAlsoIDs,
                                     0,
                                     (seeAlsoIDs.length-1));
                this.impl.setSeeAlso(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setSeeAlso(p);
            }
        }

        return p;
    }

    // See methods

    public void setSeeIDs(ThesaurusRecordID[] ids)
    {
        this.seeIDs = ids;
    }

    public ThesaurusPage getSee()
        throws ThesaurusException
    {
        ThesaurusPage p = null;

        if((p=this.impl.getSee()) == null)
        {
            if(this.seeIDs != null)
            {
                p = tbroker.buildPage(seeIDs,
                                     0,
                                     (seeIDs.length-1));
                this.impl.setSee(p);
            }
            else
            {
                p = new ThesaurusPage(0);
                this.impl.setSee(p);
            }
        }

        return p;
    }

	public String getTranslatedType()
	{
		return this.impl.getTranslatedType();
	}

	@Deprecated
	// Use isInfo instead!
    public boolean hasInfo()
    {

        if(impl.hasInfo())
        {
            return true;
        }

        return false;
    }
    public boolean isInfo()
    {

        if(impl.hasInfo())
        {
            return true;
        }

        return false;
    }

}