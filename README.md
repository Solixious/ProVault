# ProVault
## Overview
ProVault is a Java application that is used to encrypt files in local machine and keep it in a vault like ecosystem which needs a password for access. The first time a user launches the application, s/he would be prompted for a password. The password entered would be used for subsequent access as well as encryption of the files present in the vault.
## Technologies Used
- JDK 17
- Java Swing
- AED 256 bit encryption
## Using the application
![ProVault Screenshot](https://i.postimg.cc/761vR9zw/image.png)
- The toolbar menu on the left side can be used for adding file to the vault, deleting file from the vault, and exitting the application.
- Each row on the table represents a file in the vault
- The green lock on the file indicates that it is encrypted and cannot be viewed. Clicking on the lock would decrypt it, symbolized by the red lock.
- Double clicking on the row of a red (unlocked) icon will open the file using the default app of the local machine.
- Clicking on the red lock will encrypt it again and lock status will change accordingly.
## Reporting Issues
If you face any issue while using this application, or if you have any question, suggestion or enhancements for the application, please feel free to raise it in the *Issues* section of this repository with appropriate labels so that it may be tracked properly.
