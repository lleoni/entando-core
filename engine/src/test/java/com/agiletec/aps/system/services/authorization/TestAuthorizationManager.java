/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.services.authorization;

import java.util.List;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.GroupManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.role.RoleManager;
import com.agiletec.aps.system.services.user.IAuthenticationProviderManager;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.MockUser;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * @author E.Santoboni
 */
public class TestAuthorizationManager extends BaseTestCase {
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        this.init();
    }
    
	public void testAuthoritiesByGroup_1() throws Throwable {
		String username = "pageManagerCoach";
		UserDetails user = this._authenticationProvider.getUser(username);
		List<IApsAuthority> autorities = this._authorizationManager.getAuthoritiesByGroup(user, "coach");
		assertNotNull(autorities);
		assertEquals(1, autorities.size());
		
		autorities = this._authorizationManager.getAuthoritiesByGroup(user, "wrong_group");
		assertNull(autorities);
	}
	
	public void testAuthoritiesByGroup_2() throws Throwable {
		String username = "admin";
		UserDetails user = this._authenticationProvider.getUser(username);
		List<IApsAuthority> autorities = this._authorizationManager.getAuthoritiesByGroup(user, Group.ADMINS_GROUP_NAME);
		assertNotNull(autorities);
		assertEquals(1, autorities.size());
		
		autorities = this._authorizationManager.getAuthoritiesByGroup(user, "coach");
		assertNotNull(autorities);
		assertEquals(1, autorities.size());
		
		autorities = this._authorizationManager.getAuthoritiesByGroup(user, "wrong_group");
		assertNull(autorities);
	}
	
	public void testAuthoritiesByRole_1() throws Throwable {
		String username = "pageManagerCoach";
		UserDetails user = this._authenticationProvider.getUser(username);
		List<IApsAuthority> autorities = this._authorizationManager.getAuthoritiesByRole(user, "pageManager");
		assertNotNull(autorities);
		assertEquals(2, autorities.size());
		
		autorities = this._authorizationManager.getAuthoritiesByRole(user, "wrong_role");
		assertNull(autorities);
	}
	
	public void testAuthoritiesByRole_2() throws Throwable {
		int allGroupSize = this._groupManager.getGroups().size();
		String username = "admin";
		UserDetails user = this._authenticationProvider.getUser(username);
		List<IApsAuthority> autorities = this._authorizationManager.getAuthoritiesByRole(user, "admin");
		assertNotNull(autorities);
		assertEquals(allGroupSize, autorities.size());
		
		autorities = this._authorizationManager.getAuthoritiesByRole(user, "pageManager");
		assertNotNull(autorities);
		assertEquals(allGroupSize, autorities.size());
		
		autorities = this._authorizationManager.getAuthoritiesByRole(user, "wrong_role");
		assertNull(autorities);
	}
	
	public void testGroupsByPermission_1() throws Throwable {
		String username = "pageManagerCoach";
		UserDetails user = this._authenticationProvider.getUser(username);
		List<Group> autorities = this._authorizationManager.getGroupsByPermission(user, Permission.MANAGE_PAGES);
		assertNotNull(autorities);
		assertEquals(2, autorities.size());
		
		autorities = this._authorizationManager.getGroupsByPermission(user, Permission.SUPERUSER);
		assertNotNull(autorities);
		assertTrue(autorities.isEmpty());
		
		autorities = this._authorizationManager.getGroupsByPermission(user, "wrong_permission");
		assertNotNull(autorities);
		assertTrue(autorities.isEmpty());
	}
	
	public void testGroupsByPermission_2() throws Throwable {
		int allGroupSize = this._groupManager.getGroups().size();
		String username = "admin";
		UserDetails user = this._authenticationProvider.getUser(username);
		List<Group> autorities = this._authorizationManager.getGroupsByPermission(user, Permission.MANAGE_PAGES);
		assertNotNull(autorities);
		assertEquals(allGroupSize, autorities.size());
		
		autorities = this._authorizationManager.getGroupsByPermission(user, Permission.SUPERUSER);
		assertNotNull(autorities);
		assertEquals(allGroupSize, autorities.size());
		
		autorities = this._authorizationManager.getGroupsByPermission(user, "wrong_permission");
		assertNotNull(autorities);
		assertEquals(allGroupSize, autorities.size());
	}
	
	/*
    public void testCheckAdminUser() throws Throwable {
    	UserDetails adminUser = this._authenticationProvider.getUser("admin", "admin");//nel database di test, username e password sono uguali
    	assertEquals("admin", adminUser.getUsername());
    	assertEquals("admin", adminUser.getPassword());
    	assertEquals(1, adminUser.getAuthorizations().size());
    	
    	List<Group> groups = this._groupManager.getGroups();
    	for (int i=0; i<groups.size(); i++) {
    		Group group = groups.get(i);
    		boolean check = this._authorizationManager.isAuth(adminUser, group);
    		assertTrue(check);
        	check = this._authorizationManager.isAuthOnGroup(adminUser, group.getName());
        	assertTrue(check);
    	}
    	
    	List<Permission> permissions = new ArrayList<Permission>(this._roleManager.getPermissions());
    	for (int i=0; i<permissions.size(); i++) {
    		Permission perm = permissions.get(i);
        	boolean check = this._authorizationManager.isAuth(adminUser, perm);
        	assertTrue(check);
        	check = this._authorizationManager.isAuthOnPermission(adminUser, perm.getName());
        	assertTrue(check);
    	}
    }
    
    public void testCheckCustomerUser() throws Throwable {
    	UserDetails extractedUser = this._authenticationProvider.getUser("pageManagerCustomers", "pageManagerCustomers");
    	assertEquals("pageManagerCustomers", extractedUser.getUsername());
    	assertEquals("pageManagerCustomers", extractedUser.getPassword());
    	assertEquals(1, extractedUser.getAuthorizations().size());
    	
    	Group group = this._groupManager.getGroup("coach");
		boolean checkGroup = this._authorizationManager.isAuth(extractedUser, group);
		assertFalse(checkGroup);
		group = this._groupManager.getGroup(Group.FREE_GROUP_NAME);
		checkGroup = this._authorizationManager.isAuth(extractedUser, group);
		assertFalse(checkGroup);
		group = this._groupManager.getGroup("customers");
		checkGroup = this._authorizationManager.isAuth(extractedUser, group);
		assertTrue(checkGroup);
		
		boolean checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, Permission.SUPERVISOR);
		assertFalse(checkPermission);
		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, Permission.SUPERUSER);
		assertFalse(checkPermission);
		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, Permission.BACKOFFICE);
		assertTrue(checkPermission);
		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, "editContents");
		assertFalse(checkPermission);
		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, "managePages");
		assertTrue(checkPermission);
    }
    
    public void testCheckNewUser() throws Throwable {
    	String username = "UserForTest";
    	String password = "PasswordForTest";
    	this.addUserForTest(username, password);
    	UserDetails extractedUser = null;
		try {
			extractedUser = this._authenticationProvider.getUser(username, password);
			assertEquals(username, extractedUser.getUsername());
			assertNotNull(extractedUser);
			assertEquals(1, extractedUser.getAuthorizations().size());
			
			Group group = this._groupManager.getGroup("coach");
    		boolean checkGroup = this._authorizationManager.isAuth(extractedUser, group);
    		assertFalse(checkGroup);
    		group = this._groupManager.getGroup(Group.FREE_GROUP_NAME);
    		checkGroup = this._authorizationManager.isAuth(extractedUser, group);
    		assertTrue(checkGroup);
    		
    		boolean checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, Permission.SUPERVISOR);
    		assertFalse(checkPermission);
    		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, Permission.SUPERUSER);
    		assertFalse(checkPermission);
    		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, Permission.BACKOFFICE);
    		assertTrue(checkPermission);
    		checkPermission = this._authorizationManager.isAuthOnPermission(extractedUser, "editContents");
    		assertTrue(checkPermission);
		} catch (Throwable t) {
			throw t;
		} finally {
			if (null != extractedUser) {
				this._userManager.removeUser(extractedUser);
			}
			extractedUser = this._userManager.getUser(username);
			assertNull(extractedUser);
		}
	}
    */
    private void init() throws Exception {
    	try {
    		this._authenticationProvider = (IAuthenticationProviderManager) this.getService(SystemConstants.AUTHENTICATION_PROVIDER_MANAGER);
    		this._authorizationManager = (IAuthorizationManager) this.getService(SystemConstants.AUTHORIZATION_SERVICE);
    		this._userManager = (IUserManager) this.getService(SystemConstants.USER_MANAGER);
    		this._roleManager = (RoleManager) this.getService(SystemConstants.ROLE_MANAGER);
    		this._groupManager = (GroupManager) this.getService(SystemConstants.GROUP_MANAGER);
		} catch (Throwable e) {
			throw new Exception(e);
		}
    }
    
    private void addUserForTest(String username, String password) throws Throwable {
    	MockUser user = new MockUser();
		user.setUsername(username);
        user.setPassword(password);
        user.setDisabled(false);
		Authorization auth = new Authorization(this._groupManager.getGroup(Group.FREE_GROUP_NAME), 
				this._roleManager.getRole("editor"));
        //user.addRole(this._roleManager.getRole("editor"));
        //user.addGroup(this._groupManager.getGroup(Group.FREE_GROUP_NAME));
		user.addAuthorization(auth);
        this._userManager.removeUser(user);
		UserDetails extractedUser = _userManager.getUser(username);
		assertNull(extractedUser);
		this._userManager.addUser(user);
		this._authorizationManager.addUserAuthorization(username, auth);
		//this._roleManager.setUserAuthorizations(username, new ArrayList<IApsAuthority>(user.getRoles()));
		//this._groupManager.setUserAuthorizations(username, new ArrayList<IApsAuthority>(user.getGroups()));
	}
	
    private IAuthorizationManager _authorizationManager;
    private IAuthenticationProviderManager _authenticationProvider;
    private IUserManager _userManager = null;
	private RoleManager _roleManager = null;
	private GroupManager _groupManager = null;
	
}
