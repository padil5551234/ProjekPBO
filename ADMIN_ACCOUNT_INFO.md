# Admin Account Information

## Default Admin Credentials

**Username:** `admin`  
**Password:** `admin123`  
**Phone:** `08123456789`  
**Role:** `admin`

## How to Access Admin Dashboard

1. Run the application using: `mvnw.cmd javafx:run`
2. Login with the admin credentials above
3. You will be automatically redirected to the Admin Dashboard

## Admin Account Management

### To Create/Restore Admin Account
```bash
cd uji_coba
mvnw.cmd exec:java -Dexec.mainClass="com.example.uji_coba.AdminAccountSetup"
```

### To List All Admin Accounts
The AdminAccountSetup class includes a `listAdminAccounts()` method that can be called to see all admin users.

### To Reset Admin Password
The AdminAccountSetup class includes methods to reset admin passwords:
- `resetAdminPassword(username)` - resets to default password
- `resetAdminPassword(username, newPassword)` - sets custom password

## Security Recommendations

⚠️ **IMPORTANT:** Change the default password after first login for security!

1. Login with default credentials
2. Navigate to user profile/settings
3. Change password to something secure
4. Keep the new password safe

## Admin Dashboard Features

The admin dashboard provides access to:
- **Dashboard View:** Overview with statistics (total kos, tenants, users)
- **Kos Management:** View, detail, and delete kos listings
- **Tenant Management:** View and manage tenant accounts
- **User Management:** View and manage all user accounts
- **Reports:** (Feature placeholder for future implementation)

## Troubleshooting

If you accidentally delete the admin account again:
1. Run the AdminAccountSetup utility as shown above
2. It will recreate the admin account with default credentials
3. Login and change the password immediately

## File Locations

- Admin setup utility: `src/main/java/com/example/uji_coba/AdminAccountSetup.java`
- Admin dashboard controller: `src/main/java/com/example/uji_coba/AdminDashboardController.java`
- Admin dashboard FXML: `src/main/resources/com/example/uji_coba/AdminDashboard.fxml`