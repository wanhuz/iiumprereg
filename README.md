# IIUM Pre-Registration Utility - Abandoned

Automatically register subject for IIUM university subject. Input the course code + section and click start. 
It will run at 12am the next day.

This program works, but the prereg website has bad coding that the program cannot account for:
  1. Server crash when registration open. Thus the program cannot register at 12am.
  2. Weird website coding changes over new semester.

A better advice is to brute force subject registration by using browser and fast internet. But IIUM server will probably crash and you might need to wait until 3am so yeah.. 

Library used: JavaFX for UI and Selenium for web automation.
If you really want to used this program, consider forking and fixing above issue. It can be done, however I'm done with this project.
Idea:
  1. Implement function to "try to register every X hour" to circumvent server crash issue. 
  2. Retry registration X times if fail the first time.
  3. Simplify further the program so that website coding changes will not affect this program.
