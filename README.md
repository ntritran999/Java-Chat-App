# Java-Chat-App

> [!IMPORTANT]
> Please follow the 'Set Up' section to create neccessary properites files.

> [!NOTE]
> The current version of the app does not support group chat encryption on multiple devices per account.

> [!WARNING]
> Start the server before running the application by executing the following command in the server folder:
* Windows:
```
java -cp "../lib/*;." Server.java 
```
* Linux:
```
java -cp "../lib/*:." Server.java 
```

## Set Up
Create the following **.properties** files:
#### db.properties
* DB_URL=YOUR_DB_URL_HERE
* DB_USER=YOUR_DB_USER_HERE
* DB_PASSWORD=YOUR_DB_PASSWORD_HERE

#### host.properties
* HOST=SERVER_IP

#### gmail.properties
* GMAIL_PASSWORD=YOUR_GMAIL_PASSWORD_HERE

#### llm_setting.properties
* API_URL=YOUR_API_URL_HERE
* API_KEY=YOUR_API_KEY_HERE

## Build
* Windows:
```
javac -d build -cp "lib/*;." Main.java
```
* Linux:
```
javac -d build -cp "lib/*:." Main.java
```

## Run
### User
* Windows:
```
java -cp "build;lib/*;." Main
```
* Linux:
```
java -cp "build:lib/*:." Main
```
### Admin
* Windows:
```
java -cp "build;lib/*;." admin.views.AdminDashboard
```
* Linux:
```
java -cp "build:lib/*:." admin.views.AdminDashboard
```
