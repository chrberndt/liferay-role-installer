package com.chberndt.liferay.role.installer;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author Christian Berndt
 *
 */
@Component(
	immediate=true,
	service=RolesInstaller.class
)
public class RolesInstaller {
	
	@Activate
	private void activate() throws Exception {
		
		_log.info("RolesInstaller.activate()");
		
		createRoles(getServiceContext());
		
	}
	
	protected void createRoles(ServiceContext serviceContext) throws Exception {
		
		_log.info("RolesInstaller.createRoles()"); 

		JSONArray jsonArray = _getJSONArray("roles.json");

		_rolesImporter.createRoles(jsonArray, serviceContext);

	}
	
	protected ServiceContext getServiceContext()
			throws PortalException {
		
		// This strategy is limited to single instance configurations
		long defaultCompanyId = _portal.getDefaultCompanyId();
		
		User user = _userLocalService.getDefaultUser(defaultCompanyId);

		Locale locale = LocaleUtil.getSiteDefault();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);		
		serviceContext.setCompanyId(defaultCompanyId);
		serviceContext.setLanguageId(_language.getLanguageId(locale));
		serviceContext.setScopeGroupId(defaultCompanyId);
		serviceContext.setTimeZone(user.getTimeZone());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}
	
	private JSONArray _getJSONArray(String name) throws Exception {
		return _jsonFactory.createJSONArray(
			_getJSON(name));
	}
	
	private String _getJSON(String name) throws IOException {
		return StringUtil.read(RolesInstaller.class.getClassLoader(), 
			_DEPENDENCIES_PATH + name);
	}

	private static final String _DEPENDENCIES_PATH =
		"com/chberndt/liferay/role/installer/dependencies/";
	
	private static final Log _log = LogFactoryUtil.getLog(RolesInstaller.class);
	
	@Reference
	private JSONFactory _jsonFactory;
	
	@Reference
	private Language _language;
	
	@Reference
	private Portal _portal;
	
	@Reference
	private RolesImporter _rolesImporter;
	
	@Reference
	private GroupLocalService _groupLocalService;
	
	@Reference
	private UserLocalService _userLocalService;

}
