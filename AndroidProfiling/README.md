# Android Application

- This is a simple prototype of what a real application would look like. The main focus of this prototype was to retrieve all the relevant information and send it to the server.
- The application uses Google Volley to communicate with the Server.
- You need to replace the **pem** String located in **ProfilingUtils** with your own server's certificate string. For simplicity reasons I used the same certificate for both servers, which would need to be changed to work in real world environments. 
- You also need to replace the strings **server_ip**, **auth_ip** and **hostname** in the **strings.xml** file located in **res/values**. The ones by default correspond to the machine from EC2 I used to test my application.
- The uploaded code corresponds to the version for **Android 5** with some permission requests to work in **Android 6**. However, for **Android 6** and on wards you may still need to add more permissions and make some relevant changes.