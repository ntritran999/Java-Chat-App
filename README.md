# Java-Chat-App

> [!NOTE]
> The current version of the app does not support group chat encryption on multiple devices per account.

> [!WARNING]
> Start the server before running the application by executing the following command in the server folder:
* Windows:
```
java -cp "../lib/*;." .\Server.java 
```
* Linux:
```
java -cp "../lib/*:." .\Server.java 
```

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

* Windows:
```
java -cp "build;lib/*;." Main
```
* Linux:
```
java -cp "build:lib/*:." Main
```