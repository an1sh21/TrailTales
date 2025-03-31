# Unity AR Integration Guide
See the complete guide for integration steps between your Trail Tales app and Unity AR project.
## Step 1: Launch Unity from Android
Add code to HomeScreen.kt to launch Unity AR app via Intent, passing location data.
## Step 2: Receive Data in Unity
Create AndroidIntentHandler.cs script to get data from Intent and update AR content.
## Step 3: Send Data Back to Android
Implement method in Unity to send discovery data back to Android when AR session completes.
## Step 4: Handle Results in Trail Tales App
Add method to your Activity to receive and process data sent from Unity.
