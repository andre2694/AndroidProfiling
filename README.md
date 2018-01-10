# Android Profiling

Hi, my name is **André Faustino**, I'm from Lisbon, Portugal and I have a *Bologna Master Degree in Information Systems and Computer Engineering* from *Instituto Superior Técnico*. This repository contains all the work I done in my Master Degree Thesis.

In the root folder you can see my **Thesis** final document and the **Extended Abstract**. You can also take a look at the code I used to test my system. 

Under **AndroidProfiling** folder you will find the Android application prototype with a **README** that contains the most relevant information.

Similarly, you can find the code for both the **Profiling Server** and the **Service Authentication Server** under the **server** folder. I also provided a **README** file with some instructions on how to run the servers.

## Thesis Abstract
Online authentication using only a username and a textual password has been proven flawed over years. Some solutions have been adopting different types of passwords (for example, Graphical Passwords) or adding more authentication factors such as biometrics, location and object possession. 

Despite these solutions being secure, they sometimes require expensive hardware that is hard to distribute, or demand several actions from the user. In the particular case of authentication in mobile devices, where users want a fast authentication, some of these solutions are unusable.

The solution described in this work is an additional authentication factor that uses the smartphone characteristics and its user configuration in order to create user profiles that will be used to authenticate the user, requiring no actions from the user. This way the proposed solution provides a fast authentication that does not requires the user to perform any actions. The proposed solution is also as secure as existing solutions.

Since mobile devices are subject to changes such as the installed applications, memorized networks and accounts, operating system upgrades among others, profile verification must be made allowing small changes.

Therefore, the Authentication Server should evaluate whether the freshly received profile is similar enough to the stored profile using defined thresholds. If static attributes (such as the IMEI, screen resolution among others) are changed, or if a large number of attributes have been changed, then the verification fails immediately, activating the fallback mechanism defined by the third-party application.

### Keywords
Online authentication, Two-Factor Authentication, mobile devices, user profile