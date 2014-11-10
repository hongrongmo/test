package org.ei.ane.fences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.ANEServiceHelper;
import org.ei.service.CSWebService;
import org.ei.service.CSWebServiceImpl;

import com.elsevier.webservices.schemas.csas.constants.types.v7.AppMetaDataStatusCodeType;
import com.elsevier.webservices.schemas.csas.types.v13.AppMetaDataResponseStatusType;
import com.elsevier.webservices.schemas.csas.types.v13.FenceListType;
import com.elsevier.webservices.schemas.csas.types.v13.FenceType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPlatformFenceInfoRespPayloadType;

public class FencesDataPopulator {

	private final static Logger log4j = Logger.getLogger(ANEServiceHelper.class);
	private static final String FENCE_DATA_ERR = "Load exception - Unable to load fence data : Status from CSWS = ";

	/**
	 * Attempts to retrieve Platform-level fence information from CS
	 * 
	 * @return
	 * @throws ServiceException 
	 * @throws CSDefaultFencesDataException
	 */
	public static Map<String, String> buildDefaultFencesData() throws ServiceException  {
		Map<String, String> platformFencesMap = null;
		AppMetaDataResponseStatusType status;
		CSWebService service = new CSWebServiceImpl();

		log4j.info("Building default fences data.");
		final GetPlatformFenceInfoRespPayloadType payload = service.getPlatformFenceInfo();
		status = payload.getStatus();

		if (AppMetaDataStatusCodeType.OK.equals(status.getStatusCode())) {
			FenceListType fenceList = payload.getFenceList();

			if (fenceList != null && fenceList.getFence() != null) {
				List<FenceType> allFences = fenceList.getFence();
				platformFencesMap = new HashMap<String, String>();

				for (FenceType fence : allFences) {
					if (fence.getName() != null) {
						platformFencesMap.put(fence.getId(), fence.getName());
					} else {
						log4j.warn("Fence name structure is invalid");
					}
				}
			}
		} else {
			log4j.error(FENCE_DATA_ERR + status.getStatusCode().value(), new Throwable());
			throw new ServiceException(SystemErrorCodes.CS_DEFAULT_FENCE_FETCH_ERROR, FENCE_DATA_ERR + status.getStatusCode().value(), null);
		}

		return platformFencesMap;
	}
}
