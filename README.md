# XMPP-Java

## Description

XMPP-Java is a Java-based client for the Extensible Messaging & Presence Protocol (XMPP) with a command-line interface (CLI). It uses Smack
(version 4.4.5) for Java VMs to connect to a server specified by the user. Some of the features this client provides:

- Register/Delete User from the server
- Login to server using username & password
- See other User/s Status Type, Message & Availability
- Send/Receive Subscription requests to other Users on the server
- Change User Status type & Message
- DM/Room chat with other User/s
- Send/Receive Files

## Installation

Requirements:

- `Java JDK` >= 20.0.1
- `Maven` >= 20 (In case of not using an IDE)

To be able to run this Client:

1. Clone this repo to a local directory
2. Open Project from an IDE (i.e. Visual Studio or IntelliJ IDEA)
3. Enable Maven dependencies
   
   - [Visual Studio](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
   - [Intellij IDEA](https://www.jetbrains.com/help/idea/convert-a-regular-project-into-a-maven-project.html)

## Instructions

To run this program correctly:

1. Setup new Configuration in IDE for `Main.java` with `Server Name` as first & only argument
2. Run program
3. Enter input as requested
   
   - `Int` when asked for an option
   - `String` otherwise

## *** NOTE ***

Client's Functionalities & Author's conclusions are inside the `Documentation.pdf` file

## Author

[Javier Ramirez Cospin](https://github.com/JavRamCos)
