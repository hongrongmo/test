package org.ei.gui;

public class PagingComponents
{
	private static StringBuffer getSelectOptions(int docIndex, int pageSize, int resultCount)
	{
		StringBuffer finalBuf = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		boolean done = false;
		int originalPageIndex = getPageIndex(pageSize, docIndex);
		int startPage = getStartPage(pageSize, docIndex);
		if(resultCount>4000)
		{
			resultCount = 4000;
		}
        if (startPage > 1)
		{
			buf.append("<option value=\"1\"/>First page</option>");

		}

		int endPage = startPage + 20;
		int i = -1;
		for (i = startPage; i <= endPage; ++i)
		{

			int beginIndex = ((i - 1) * pageSize) + 1;
			if (beginIndex > resultCount)
			{
				break;
			}

			int endIndex = beginIndex + (pageSize - 1);
			if (endIndex > resultCount)
			{
				endIndex = resultCount;

				done = true;

			}

			if((i == endPage)&&(endIndex < resultCount))
            {
                buf.append("<option value=\"").append(endIndex).append("\">Next</option>");
            }
            else
            {
                buf.append("<option ");
                if (i == originalPageIndex)
                {
                    buf.append("selected ");
                }

                buf.append("value=\"");
                buf.append(Integer.toString(beginIndex));
                buf.append("\"");

                buf.append(">");
                buf.append(Integer.toString(beginIndex));
                buf.append("-");
                buf.append(Integer.toString(endIndex));
                buf.append("</option>");
            }
			if (done)
			{
				break;
			}
		}

		return buf;
	}

	public static String getPagingComponent(String selectName, int docIndex, int pageSize, int resultCount)
	{
		StringBuffer finalBuf = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		buf.append(getSelectOptions(docIndex, pageSize, resultCount));
		if (buf.length() > 0)
		{
			finalBuf.append("<a CLASS=\"MedBlackText\"><SELECT NAME=\"").append(selectName).append("\">");
			finalBuf.append(buf);
			finalBuf.append("</SELECT></a>");
		}

		return finalBuf.toString();
	}


    private static int getStartPage(int pageSize, int docIndex)
    {
        int pageIndex = getPageIndex(pageSize, docIndex);
        if (pageIndex == 1)
        {
            return pageIndex;
        }
        else
        {
            int s = pageIndex - 10;
            if (s > 0)
            {
                return s;
            }
            else
            {
                return 1;
            }
        }

    }

    private static int getPageIndex(int pSize, int dIndex)
    {
        int pageIndex;

        if ((dIndex % pSize) == 0)
        {
            // if the remainder is zero the page index will find in the following way
            pageIndex = (dIndex / pSize);
        }
        else
        {
            // otherwise find the page index in the following way
            pageIndex = (dIndex / pSize) + 1;
        }

        return pageIndex;

    }

}