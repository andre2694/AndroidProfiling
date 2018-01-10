# Server
To run the server you need to setup some things first:

 - Install Python Fabric - Optional but really helpful to run the commands from the Fabfile 
 - Setup the MySQL databases. You can either do that by running **fab init_db** and **fab create_db** or running by yourself the corresponding MySQL commands. MySQL scripts can be found under **src/main/java/dbutils/**
 - Generate a certificate for the server or use your own certificate. Note that you'll have to change the Android Application to use your certificate string. The default keystore is called **testkey.jks** and the password is **password**. Feel free to change it in **Server.java**
 - Run the Profiling Server by typing **fab run_server** or running the command by yourself.
 - Run the Service Authentication Server by typing **fab run_auth** or running the command by yourself.
 - Both Servers run using Maven.

The default endpoints of the servers are the following:
 - Profiling Server - **8000**
 - Service Authentication Server - **8001**

I had some issues with Logging, due to that reason I used to redirect the program's output to specific files. I don't recall any other significant bugs, however the server could see some improvements.

I used Javadocs to comment the server to explain the most relevant design decisions. 
