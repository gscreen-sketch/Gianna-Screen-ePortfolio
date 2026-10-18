# Gianna Screen ePortfolio

This is my final project for the CS-499 Capstone at SNHU. Over the past 6 weeks I have learned a significant amount about my skills and have a clear understanding of the next phase of my professional development. I enjoyed enhancing this artifact and look forward to the feedback that will be received!

ePortfolio Website: https://gscreen-sketch.github.io/Gianna-Screen-ePortfolio/

## Navigating This Repository

Both app folders are Android Studio projects, so either one can be opened directly in Android Studio. All of the app code is inside app/src/main/java/com/example/project1/ in each folder.

**original**

The original CS-360 Inventory Android app (Project1) before any of my capstone enhancements. Use it to compare against the enhanced version. The main files to look at are:

- login_backend.java : the login screen
- database_backend.java : the inventory screen
- database_helper.java : the local database
- sms_backend.java : the SMS alert screen
- app/src/main/res/ : the screen layouts, colors, and images

**enhanced**

The enhanced version of the same app, including the Android app code, the Node/Express and MySQL server, and the unit tests. This is where the Software Design and Engineering, Algorithms and Data Structures, and Databases enhancements are showcased.

Software Design and Engineering (app/src/main/java/com/example/project1/):

- AuthRepository.java and InventoryRepository.java : the repository layer between the screens and the data
- ApiClient.java and AppConfig.java : the connection from the app to the server
- CreateAccountBackend.java : the new create account screen
- InputValidator.java : input validation for the forms
- AlertManager.java : the low stock alert logic
- InventoryAdapter.java : the inventory list display

Algorithms and Data Structures (app/src/main/java/com/example/project1/):

- DatabaseBackend.java : the HashMap duplicate checker (addOrUpdateItem)
- InventoryOrganizer.java : sorting and the low stock filter

Databases and Security:

- server/server.js : the Node/Express API that connects the app to the MySQL database
- server/schema.sql : the MySQL tables, including the unique index on item name
- server/package.json : the server dependencies
- app/src/main/java/com/example/project1/CredentialHasher.java : password hashing

Unit tests (app/src/test/java/com/example/project1/):

- InputValidatorTest.java and AlertManagerTest.java : Software Design and Engineering
- InventoryOrganizerTest.java and DuplicateCheckerTest.java : Algorithms and Data Structures
- CredentialHasherTest.java : Security

**Narratives**

The written narratives for each enhancement category: Software Design and Engineering, Algorithms and Data Structures, and Databases.
